// Demonstração didática: amostragem aleatória simples sobre o índice integral de 2025.
// O parâmetro de interesse é a proporção de registros com ValorPago negativo.
const fs=require('fs'),path=require('path');
const root=path.resolve(__dirname,'..'),sampleSize=1200,seed=20260929;
let state=seed>>>0;
function random(){state=(state+0x6D2B79F5)>>>0;let t=state;t=Math.imul(t^(t>>>15),t|1);t^=t+Math.imul(t^(t>>>7),t|61);return ((t^(t>>>14))>>>0)/4294967296}
const sample=[];let population=0,negativePopulation=0;
for(let month=1;month<=12;month++){
  const file=path.join(root,'site','dist','recortes',`2025-${String(month).padStart(2,'0')}.csv`);
  const lines=fs.readFileSync(file,'utf8').trimEnd().split(/\r?\n/);
  if(lines.shift()!=='id;documento;funcao;orgao;valor_pago;arquivo')throw new Error(`Cabeçalho inválido: ${file}`);
  for(const line of lines){
    const fields=line.split(';');
    const negative=Number(fields[4])<0?1:0;
    population++;negativePopulation+=negative;
    if(sample.length<sampleSize)sample.push(negative);
    else {const j=Math.floor(random()*population);if(j<sampleSize)sample[j]=negative;}
  }
}
const successes=sample.reduce((a,b)=>a+b,0),n=sample.length,z=1.959963984540054,p=successes/n,z2=z*z;
const center=(p+z2/(2*n))/(1+z2/n),margin=z*Math.sqrt(p*(1-p)/n+z2/(4*n*n))/(1+z2/n);
const result={purpose:'Intervalo didático; o valor censitário da base publicada é conhecido',population,negativePopulation,censusProportion:negativePopulation/population,sampling:'reservoir sampling uniforme sem reposição; PRNG determinístico mulberry32',seed,n,successes,estimate:p,confidence:0.95,method:'Wilson binomial',lower:center-margin,upper:center+margin};
fs.writeFileSync(path.join(root,'site','dist','ci_2025.json'),JSON.stringify(result,null,2)+'\n');
console.log(result);
