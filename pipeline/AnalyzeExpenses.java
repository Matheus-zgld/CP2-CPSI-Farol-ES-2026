import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class AnalyzeExpenses {
  static final Charset ENC = Charset.forName("UTF-8");
  static final String[] YEARS = {"2024", "2025"};
  static final String[] MONEY = {"ValorEmpenho","ValorLiquidado","ValorPago","ValorRap"};
  static final String[] QUALITY = {"Data","ValorEmpenho","ValorLiquidado","ValorPago","CpfCnpjNis","Favorecido","TipoLicitacao","HistoricoDocumento","Documento","DocumentoEmpenho","CodigoProcesso","CodigoUnidadeGestora","UnidadeGestora","CodigoOrgao","Orgao","CodigoFuncao","Funcao","CodigoElementoDespesa","ElementoDespesa","Contrato","Id"};

  static class Agg {
    long rows, negPaid, zeroPaid, posPaid;
    double empenho, liquidado, pago, rap, paidPositive, paidNegativeAbs;
    void add(double e, double l, double p, double r) {
      rows++; empenho += e; liquidado += l; pago += p; rap += r;
      if (p > 0) { posPaid++; paidPositive += p; }
      else if (p < 0) { negPaid++; paidNegativeAbs += -p; }
      else zeroPaid++;
    }
  }
  static class TopRecord {
    String year,date,id,documento,processo,orgaoCode,orgao,funcaoCode,funcao,elementoCode,elemento,favorecido,tipoLicitacao,unidade,history;
    double pago, liquidado;
  }
  static class YearStats {
    String year;
    Agg total = new Agg();
    Map<String,Long> missing = new LinkedHashMap<>();
    Map<String,Agg> months = new TreeMap<>(), orgaos = new HashMap<>(), funcoes = new HashMap<>(), unidades = new HashMap<>(), elementos = new HashMap<>(), licitacoes = new HashMap<>(), favorecidos = new HashMap<>();
    Set<String> ids = new HashSet<>();
    long duplicateIds, malformedRows, missingId;
    PriorityQueue<TopRecord> topPayments = new PriorityQueue<>(Comparator.comparingDouble(r -> r.pago));
    PriorityQueue<TopRecord> topReversals = new PriorityQueue<>((a,b) -> Double.compare(-a.pago, -b.pago));
    YearStats(String y) { year=y; for (String q: QUALITY) missing.put(q,0L); }
  }
  static class FileStats { String file,year; long rows, malformed; }
  static Map<String,String> labels = new HashMap<>();
  static List<FileStats> files = new ArrayList<>();
  static Map<String,BufferedWriter> traceWriters = new HashMap<>();
  static Path traceDirectory;

  static String csvCell(String value){
    if(value==null)return "";
    if(value.indexOf(';')<0 && value.indexOf('"')<0 && value.indexOf('\n')<0 && value.indexOf('\r')<0)return value;
    return "\""+value.replace("\"","\"\"").replace('\n',' ').replace('\r',' ')+"\"";
  }
  static void writeTrace(String year,String month,String id,String document,String function,String organ,double paid,String source) throws IOException {
    if(!month.matches("0[1-9]|1[0-2]"))return;
    String key=year+"-"+month;
    BufferedWriter writer=traceWriters.get(key);
    if(writer==null){
      writer=Files.newBufferedWriter(traceDirectory.resolve(key+".csv"),ENC);
      writer.write("id;documento;funcao;orgao;valor_pago;arquivo\n");
      traceWriters.put(key,writer);
    }
    writer.write(csvCell(id)+";"+csvCell(document)+";"+csvCell(function)+";"+csvCell(organ)+";"+String.format(Locale.US,"%.4f",paid)+";"+csvCell(source)+"\n");
  }

  static class CsvReader implements Closeable {
    final Reader r; int pushed=-2;
    CsvReader(InputStream in) { r=new BufferedReader(new InputStreamReader(in, ENC), 1<<20); }
    int read() throws IOException { if(pushed!=-2){int c=pushed;pushed=-2;return c;} return r.read(); }
    void unread(int c){ pushed=c; }
    List<String> next() throws IOException {
      List<String> row=new ArrayList<>(); StringBuilder field=new StringBuilder();
      boolean quoted=false, atStart=true, any=false; int c;
      while((c=read())!=-1){
        any=true;
        if(quoted){
          if(c=='"'){
            int next=read();
            if(next=='"'){field.append('"');continue;}
            quoted=false;
            if(next!=-1)unread(next);
          } else field.append((char)c);
          continue;
        }
        if(c=='"' && atStart){quoted=true;atStart=false;continue;}
        if(c==';'){row.add(field.toString());field.setLength(0);atStart=true;continue;}
        if(c=='\n' || c=='\r'){
          row.add(field.toString());
          if(c=='\r'){int next=read();if(next!='\n'&&next!=-1)unread(next);}
          return row;
        }
        field.append((char)c);atStart=false;
      }
      if(!any && row.isEmpty() && field.length()==0)return null;
      row.add(field.toString());return row;
    }
    public void close() throws IOException { r.close(); }
  }

  static double num(String s){ if(s==null||s.isBlank())return 0; try{return Double.parseDouble(s.trim().replace(".","").replace(',','.'));}catch(Exception e){return 0;} }
  static String val(List<String> row, Map<String,Integer> h, String k){Integer i=h.get(k);return i==null||i>=row.size()?"":row.get(i).trim();}
  static String clean(String s){return s==null?"":s.trim().replaceAll("\\s+"," ");}
  static String shortText(String s,int n){s=clean(s);return s.length()<=n?s:s.substring(0,n-1)+"…";}
  static Agg agg(Map<String,Agg> m,String key){return m.computeIfAbsent(key,k->new Agg());}
  static void remember(String dim,String code,String name){code=clean(code);name=clean(name);if(!code.isEmpty()&&!name.isEmpty())labels.putIfAbsent(dim+":"+code,name);}
  static void pushTop(PriorityQueue<TopRecord> q, TopRecord r, boolean payment){ q.add(r); if(q.size()>20)q.poll(); }

  static void process(Path zipPath, YearStats ys) throws Exception {
    FileStats fs=new FileStats();fs.file=zipPath.getFileName().toString();fs.year=ys.year;files.add(fs);
    try(ZipInputStream zin=new ZipInputStream(new BufferedInputStream(Files.newInputStream(zipPath),1<<20))) {
      if(zin.getNextEntry()==null) return;
      CsvReader csv=new CsvReader(zin); List<String> header=csv.next(); Map<String,Integer> h=new HashMap<>();
      for(int i=0;i<header.size();i++)h.put(header.get(i).replace("\uFEFF",""),i); int expected=header.size();
      List<String> row;
      while((row=csv.next())!=null){ if(row.size()==1&&row.get(0).isBlank())continue; fs.rows++;
        if(row.size()!=expected){fs.malformed++;ys.malformedRows++; if(row.size()<expected)while(row.size()<expected)row.add(""); else continue;}
        double e=num(val(row,h,"ValorEmpenho")), l=num(val(row,h,"ValorLiquidado")), p=num(val(row,h,"ValorPago")), r=num(val(row,h,"ValorRap")); ys.total.add(e,l,p,r);
        for(String q:QUALITY)if(val(row,h,q).isBlank())ys.missing.merge(q,1L,Long::sum);
        String id=val(row,h,"Id");if(id.isBlank())ys.missingId++;else if(!ys.ids.add(id))ys.duplicateIds++;
        String date=val(row,h,"Data"), month=(date.length()>=5?date.substring(3,5):"??");agg(ys.months,month).add(e,l,p,r);
        String oc=val(row,h,"CodigoOrgao"), on=val(row,h,"Orgao"), fc=val(row,h,"CodigoFuncao"), fn=val(row,h,"Funcao"), uc=val(row,h,"CodigoUnidadeGestora"), un=val(row,h,"UnidadeGestora"), ec=val(row,h,"CodigoElementoDespesa"), en=val(row,h,"ElementoDespesa");
        writeTrace(ys.year,month,id,val(row,h,"Documento"),fc,oc,p,fs.file);
        remember("orgao",oc,on);remember("funcao",fc,fn);remember("unidade",uc,un);remember("elemento",ec,en);
        agg(ys.orgaos,oc.isBlank()?"(sem código)":oc).add(e,l,p,r); agg(ys.funcoes,fc.isBlank()?"(sem código)":fc).add(e,l,p,r); agg(ys.unidades,uc.isBlank()?"(sem código)":uc).add(e,l,p,r); agg(ys.elementos,ec.isBlank()?"(sem código)":ec).add(e,l,p,r);
        String lic=clean(val(row,h,"TipoLicitacao"));agg(ys.licitacoes,lic.isEmpty()?"(não informado)":lic).add(e,l,p,r);
        String favId=val(row,h,"IdFavorecido"), fav=clean(val(row,h,"Favorecido")), tipo=val(row,h,"TipoFavorecido"); if("1".equals(tipo))fav="PESSOA FÍSICA (identidade protegida)"; if(fav.isEmpty())fav="(não informado)"; String favKey=(favId.isBlank()?fav:favId+"|"+fav); agg(ys.favorecidos,favKey).add(e,l,p,r);
        if(p>0 || p<0){TopRecord tr=new TopRecord();tr.year=ys.year;tr.date=date;tr.id=id;tr.documento=val(row,h,"Documento");tr.processo=val(row,h,"CodigoProcesso");tr.orgaoCode=oc;tr.orgao=on;tr.funcaoCode=fc;tr.funcao=fn;tr.elementoCode=ec;tr.elemento=en;tr.favorecido=fav;tr.tipoLicitacao=lic;tr.unidade=un;tr.history=shortText(val(row,h,"HistoricoDocumento"),240);tr.pago=p;tr.liquidado=l;if(p>0)pushTop(ys.topPayments,tr,true);else pushTop(ys.topReversals,tr,false);}
      }
    }
    System.out.printf(Locale.US,"%s: %,d registros, %,d malformados%n",fs.file,fs.rows,fs.malformed);
  }

  static String esc(String s){if(s==null)return"";return s.replace("\\","\\\\").replace("\"","\\\"").replace("\r"," ").replace("\n"," ");}
  static void kv(StringBuilder b,String k,String v){b.append('"').append(esc(k)).append("\":").append(v);}
  static String q(String s){return"\""+esc(s)+"\"";}
  static String n(double d){return String.format(Locale.US,"%.4f",d);}
  static String aggJson(Agg a){return String.format(Locale.US,"{\"rows\":%d,\"empenho\":%.4f,\"liquidado\":%.4f,\"pago\":%.4f,\"rap\":%.4f,\"paidPositive\":%.4f,\"paidNegativeAbs\":%.4f,\"posPaid\":%d,\"negPaid\":%d,\"zeroPaid\":%d}",a.rows,a.empenho,a.liquidado,a.pago,a.rap,a.paidPositive,a.paidNegativeAbs,a.posPaid,a.negPaid,a.zeroPaid);}
  static String dimensionJson(Map<String,Agg> map,String dim,int limit){List<Map.Entry<String,Agg>> all=new ArrayList<>(map.entrySet());all.sort((a,b)->Double.compare(b.getValue().pago,a.getValue().pago));StringBuilder b=new StringBuilder("[");int c=0;for(var e:all){if(c++>=limit)break;if(c>1)b.append(',');String code=e.getKey(),label=labels.getOrDefault(dim+":"+code,code);if("favorecido".equals(dim)&&code.contains("|"))label=code.substring(code.indexOf('|')+1);b.append("{\"code\":").append(q(code)).append(",\"label\":").append(q(label)).append(",\"metrics\":").append(aggJson(e.getValue())).append('}');}return b.append(']').toString();}
  static String mapAggJson(Map<String,Agg> map){StringBuilder b=new StringBuilder("{");int c=0;for(var e:map.entrySet()){if(c++>0)b.append(',');b.append(q(e.getKey())).append(':').append(aggJson(e.getValue()));}return b.append('}').toString();}
  static String recordJson(TopRecord r){return "{"+"\"year\":"+q(r.year)+",\"date\":"+q(r.date)+",\"id\":"+q(r.id)+",\"documento\":"+q(r.documento)+",\"processo\":"+q(r.processo)+",\"orgaoCode\":"+q(r.orgaoCode)+",\"orgao\":"+q(r.orgao)+",\"funcaoCode\":"+q(r.funcaoCode)+",\"funcao\":"+q(r.funcao)+",\"elementoCode\":"+q(r.elementoCode)+",\"elemento\":"+q(r.elemento)+",\"favorecido\":"+q(r.favorecido)+",\"tipoLicitacao\":"+q(r.tipoLicitacao)+",\"unidade\":"+q(r.unidade)+",\"history\":"+q(r.history)+",\"pago\":"+n(r.pago)+",\"liquidado\":"+n(r.liquidado)+"}";}
  static String topJson(PriorityQueue<TopRecord> pq,boolean desc){List<TopRecord>a=new ArrayList<>(pq);a.sort(desc?Comparator.comparingDouble((TopRecord r)->r.pago).reversed():Comparator.comparingDouble(r->r.pago));StringBuilder b=new StringBuilder("[");for(int i=0;i<a.size();i++){if(i>0)b.append(',');b.append(recordJson(a.get(i)));}return b.append(']').toString();}
  static double hhi(Map<String,Agg> m){double total=m.values().stream().mapToDouble(a->Math.max(0,a.pago)).sum(),h=0;if(total==0)return 0;for(Agg a:m.values()){double s=Math.max(0,a.pago)/total;h+=s*s;}return h;}

static String yearJson(YearStats y){StringBuilder b=new StringBuilder("{");kv(b,"total",aggJson(y.total));b.append(',');kv(b,"months",mapAggJson(y.months));b.append(",\"qualityMissing\":{");int c=0;for(var e:y.missing.entrySet()){if(c++>0)b.append(',');b.append(q(e.getKey())).append(':').append(e.getValue());}b.append("},\"duplicateIds\":").append(y.duplicateIds).append(",\"malformedRows\":").append(y.malformedRows).append(",\"distinctIds\":").append(y.ids.size());b.append(",\"distinct\":{").append("\"orgaos\":").append(y.orgaos.size()).append(",\"funcoes\":").append(y.funcoes.size()).append(",\"unidades\":").append(y.unidades.size()).append(",\"elementos\":").append(y.elementos.size()).append(",\"licitacoes\":").append(y.licitacoes.size()).append(",\"favorecidos\":").append(y.favorecidos.size()).append('}');b.append(",\"hhiFavorecidos\":").append(n(hhi(y.favorecidos)));b.append(",\"top\":{");kv(b,"orgaos",dimensionJson(y.orgaos,"orgao",20));b.append(',');kv(b,"funcoes",dimensionJson(y.funcoes,"funcao",100));b.append(',');kv(b,"unidades",dimensionJson(y.unidades,"unidade",20));b.append(',');kv(b,"elementos",dimensionJson(y.elementos,"elemento",20));b.append(',');kv(b,"licitacoes",dimensionJson(y.licitacoes,"licitacao",20));b.append(',');kv(b,"favorecidos",dimensionJson(y.favorecidos,"favorecido",20));b.append('}');b.append(",\"topPayments\":").append(topJson(y.topPayments,true)).append(",\"topReversals\":").append(topJson(y.topReversals,false));return b.append('}').toString();}

  public static void main(String[] args) throws Exception {
    Path root=Path.of(args.length>0?args[0]:"."); Map<String,YearStats> years=new LinkedHashMap<>();for(String y:YEARS)years.put(y,new YearStats(y));
    traceDirectory=root.resolve("site/dist/recortes");Files.createDirectories(traceDirectory);
    try{
      for(String y:YEARS)for(int p=1;p<=4;p++){Path z=root.resolve(String.format("despesas_es_%s_completo_parte_%02d.zip",y,p));process(z,years.get(y));}
    }finally{for(BufferedWriter writer:traceWriters.values())writer.close();}
    StringBuilder out=new StringBuilder("{\"generatedAt\":").append(q(java.time.LocalDate.now().toString())).append(",\"source\":\"Portal da Transparência do Governo do Estado do Espírito Santo\",\"files\":[");for(int i=0;i<files.size();i++){if(i>0)out.append(',');FileStats f=files.get(i);out.append("{\"file\":").append(q(f.file)).append(",\"year\":").append(q(f.year)).append(",\"rows\":").append(f.rows).append(",\"malformed\":").append(f.malformed).append('}');}out.append("],\"years\":{");int c=0;for(var e:years.entrySet()){if(c++>0)out.append(',');out.append(q(e.getKey())).append(':').append(yearJson(e.getValue()));}out.append("}}");Files.writeString(root.resolve("analysis.json"),out.toString());System.out.println("analysis.json gerado: "+out.length()+" bytes");
  }
}
