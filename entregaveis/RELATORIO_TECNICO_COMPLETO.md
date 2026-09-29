# Proposta Técnica — Farol ES: Transparência Inteligente

**CPSI Simulado — CP2 · Engenharia de Software · FIAP · Turma 2ESA · 2026**  
**Empresa fictícia:** Lumina Dados Cívicos  
**Plano simulado:** 16 semanas · **Investimento proposto:** R$ 168.480,00 · **Operação mensal:** R$ 8.900,00

## 1. Resumo executivo

O Farol ES é uma POC web que transforma dados públicos de despesas do Espírito Santo em informação compreensível e verificável para jornalistas de dados, seu público prioritário. O produto resolve três atritos: volume excessivo, linguagem orçamentária técnica e baixa visibilidade da qualidade dos dados. A jornada principal permite escolher ano, mês e função, calcular o pagamento líquido, reconciliar o indicador com o índice integral do mês e identificar ou exportar os registros do recorte.

A análise processou integralmente oito arquivos ZIP, totalizando **1.092.158 registros** e aproximadamente 1 GB de CSVs descompactados. Foram encontradas **505.950 linhas em 2024** e **586.208 em 2025**, sem linhas estruturalmente malformadas e sem IDs duplicados. Os pagamentos líquidos passaram de **R$ 10,337 bilhões** para **R$ 11,275 bilhões**, crescimento de **R$ 938,109 milhões (9,1%)**.

## 2. Problema, público e proposta de valor

Publicar arquivos não garante entendimento. Para responder “onde, como e quanto o Estado pagou?”, o usuário precisa conhecer códigos, distinguir empenho, liquidação e pagamento, lidar com estornos e descobrir se campos vazios alteram a leitura. Portais tradicionais tendem a priorizar consulta cadastral, não explicação.

O público prioritário é o **jornalista local que prepara uma pauta baseada em gastos públicos**. O público secundário é o cidadão interessado. A jornada foi delimitada: (1) formular uma pergunta comparativa; (2) selecionar ano, mês e função; (3) interpretar valor pago líquido, contagem e lacunas; (4) conferir IDs e documentos da base integral no recorte; (5) exportar os registros minimizados para apuração.

**Proposta de valor:** reduzir o caminho entre uma pergunta pública e uma evidência verificável, sem transformar correlação, concentração ou valor atípico em acusação.

## 3. Dados, ingestão e tratamento

Fonte: Portal da Transparência do Governo do Estado do Espírito Santo, arquivos completos de despesas de 2024 e 2025 e respectivo dicionário. Cada ano possui quatro ZIPs. O pipeline Java lê diretamente os ZIPs em streaming, interpreta CSV UTF-8 com BOM delimitado por ponto e vírgula, respeita campos entre aspas e valida a estrutura de 71 colunas.

Tratamentos realizados: conversão de moeda pt-BR; normalização de espaços; preservação de negativos; contagem de ausentes; verificação de unicidade do ID; agregação por mês, função, órgão, unidade, elemento, licitação e favorecido; mascaramento de pessoa física; e recuperação controlada de rótulos por código. O resultado analítico é serializado em JSON sem identificadores fiscais.

Reprodutibilidade: a rotina, os comandos, as bases de entrada, os totais esperados e testes estão documentados no repositório. A execução gera `analysis.json`, 24 arquivos de recorte mensal com campos minimizados e uma demonstração estatística reproduzível. O tempo de execução depende da máquina; não há promessa de desempenho sem ambiente de referência.

## 4. Qualidade e limitações dos dados

Não foram observadas linhas com quantidade incorreta de colunas ou IDs repetidos. Entretanto, completude semântica é um problema: `Orgao` está vazio em 100% dos dois anos; `Funcao` está completa em 2024, mas vazia em 100% de 2025; `HistoricoDocumento` está vazio em 18,8% de 2024 e 20,8% de 2025. Os códigos de órgão e função permanecem disponíveis.

A solução não preenche lacunas silenciosamente. O rótulo de função de 2025 é recuperado pelo mesmo código observado em 2024 e recebe selo de proveniência. Para órgão, quando não existe dicionário confiável no conjunto fornecido, exibe-se o código. Históricos ausentes aparecem como “não informado na fonte”.

Limitações: os dados representam registros publicados; negativos podem ser estornos; uma concentração elevada pode ser esperada em fundos e transferências; o valor de um documento não prova irregularidade; e o recorte não inclui contexto contratual externo. A POC apoia investigação, não substitui auditoria.

## 5. Questões investigadas e evidências

### Q1 — Como o perfil dos pagamentos mudou entre 2024 e 2025?

O total líquido passou de **R$ 10.337.098.858,62** em 2024 para **R$ 11.275.208.229,53** em 2025: diferença nominal de **R$ 938.109.370,91 (9,1%)**. **Saúde** (código 10) passou de R$ 1.835.070.145,72 para R$ 2.232.605.995,91: +R$ 397.535.850,19. **Transporte** (código 26) passou de R$ 680.790.708,77 para R$ 930.776.841,32: +R$ 249.986.132,55. **Previdência Social** recuou R$ 162.207.439,50. A comparação não foi corrigida pela inflação e não autoriza inferência causal.

Decisão derivada: comparador anual por função, série mensal e consulta integral de recortes ano–mês–função com valor, contagem, IDs e arquivo de origem. O usuário vê a mudança antes de investigar seus registros, evitando confundir posição com tendência.

### Q2 — É possível explicar os totais sem esconder lacunas?

Sim, desde que a qualidade seja parte da interface. A ausência total dos nomes de órgão e dos nomes de função em 2025 torna uma simples exibição de rótulos enganosa. Os códigos, porém, são consistentes. Também existem 9.886 registros com pagamento negativo em 2024 e 12.747 em 2025, somando R$ 381,8 mi e R$ 801,0 mi em valor absoluto, respectivamente.

Decisão derivada: painel de qualidade, selo “rótulo recuperado”, soma líquida explícita, preservação de estornos e estado de erro quando um arquivo está indisponível ou o índice não concilia com o agregado.

### Intervalo de confiança sem confundir amostra e censo

Para demonstrar inferência, `pipeline/confidence_interval.js` extrai por reservoir sampling uma amostra aleatória simples sem reposição de 1.200 registros de 2025, com semente 20260929. O estimando didático é a proporção de registros com `ValorPago` negativo. Foram observados 30 casos na amostra: estimativa **2,50%**, intervalo de confiança aproximado de 95% por Wilson **1,76%–3,55%**. Como toda a base publicada também foi processada, a proporção censitária é conhecida: **12.747/586.208 = 2,17%**. O intervalo mede variabilidade da amostra, não incerteza do total censitário, erro da publicação ou fraude. A amostra sistemática didática do enunciado não foi usada como amostra aleatória.

### Leitura de concentração

O HHI por favorecido caiu de 404 para 292 pontos (escala 0–10.000), sugerindo menor concentração relativa em 2025. O indicador não é apresentado como risco de fraude; ele ajuda a decidir onde explorar. Fundos, secretarias e transferências internas aparecem entre os maiores favorecidos, exigindo interpretação institucional.

## 6. Solução e jornada funcional

O site possui cinco áreas: Visão Geral, Evidências, Rastrear, Qualidade e Projeto. A tela inicial apresenta seletor de ano, KPIs, série mensal, sinais e ranking por função. Em Evidências, cada questão é ligada à decisão de produto e a demonstração de intervalo de confiança distingue amostra e censo. Em Rastrear, o usuário filtra ano, mês e função, obtém soma e contagem, examina 25 registros iniciais e baixa todos os registros do recorte; a busca individual por maiores pagamentos é apenas um atalho separado. Em Qualidade, ausências e limitações ficam visíveis. Projeto cobre arquitetura, cronograma, equipe, orçamento, riscos e aceite.

Tratamento de erro demonstrável: um recorte vazio recebe explicação; arquivo mensal indisponível ou divergente bloqueia o resultado e informa o problema. Funcional hoje: comparação, gráficos, navegação, consulta de recorte completo por filtros definidos, exportação minimizada, busca individual demonstrativa, qualidade, intervalo didático e apresentação de oito telas. Futuro: busca livre em todas as colunas, alertas contínuos, autenticação e atualização automática.

## 7. Arquitetura, tecnologias e evolução

Arquitetura em quatro camadas: (1) fontes oficiais; (2) pipeline de dados em Java; (3) camada semântica agregada em JSON e 24 índices CSV mensais; (4) aplicação web estática em HTML, CSS e JavaScript. Os índices contêm apenas ID, documento, códigos de função e órgão, valor pago e ZIP de origem. A soma e a contagem de cada mês são reconciliadas com o agregado antes da exibição. A separação permite substituir o índice estático por API sem reescrever as regras analíticas.

O pipeline usa processamento sequencial em streaming e logs por arquivo. O frontend é responsivo, usa HTML semântico, foco visível, link de salto, contraste elevado, teclado no modo apresentação e preferência de redução de movimento. A execução local por HTTP é a contingência; acesso público da hospedagem deve ser verificado antes do envio.

Evolução: armazenamento colunar; API paginada; atualização incremental; catálogo de metadados; autenticação; alertas configuráveis; observabilidade; cache; testes de carga; e integração com dados de contratos. Configurações sensíveis serão fornecidas por variáveis de ambiente e segredos gerenciados.

## 8. Segurança, privacidade, ética e acessibilidade

Princípios: minimização; nenhum CPF/CNPJ, NIS ou dado bancário no índice de recortes; pessoas físicas anonimizadas na busca demonstrativa; nenhum segredo versionado; sanitização de entrada; cabeçalhos de segurança previstos para produção; logs sem dados pessoais; dependências mínimas; backups e revisão de acesso. Como os dados são públicos, ainda assim se evita reidentificação desnecessária.

Ética: fatos e inferências possuem rótulos distintos; “sinal” não significa fraude; métodos e limites são explicados; totais incluem estornos; rótulos recuperados informam proveniência; e a ferramenta não produz acusação automatizada.

Acessibilidade prevista: WCAG 2.2 AA como alvo, navegação por teclado, contraste, tamanho mínimo de texto, labels, tabela semântica, alternativas textuais para gráficos, layout responsivo e teste com leitor de tela.

## 9. Plano de 16 semanas e critérios de aceite

- **S1–2, Descoberta:** problema, público, pesquisa, perguntas e métricas. Aceite: jornada e duas questões aprovadas.
- **S3, Planejamento:** arquitetura, backlog, riscos e plano de dados. Aceite: decisão arquitetural registrada.
- **S4–6, Engenharia de dados:** ingestão, testes, qualidade e catálogo. Aceite: oito ZIPs reprocessados e totais reconciliados.
- **S7–10, Alfa:** comparação, gráficos e rastreio. Aceite: jornada parcialmente funcional com fonte real.
- **S11–13, Beta:** integração, acessibilidade, desempenho e privacidade. Aceite: testes críticos aprovados.
- **S14–15, Validação e implantação:** testes com usuários, correções, deploy controlado. Aceite: jornada concluída por usuários-alvo.
- **S16, Encerramento:** relatório, apresentação, treinamento e transferência. Aceite: documentação e demonstração final.

Dependências: Descoberta precede backlog; dicionário e base precedem modelo; pipeline precede indicadores; alfa precede testes de jornada; beta precede implantação. Marcos financeiros seguem 15%, 20%, 20%, 25% e 20% do valor total.

## 10. Equipe e responsabilidades

**Distribuição proposta, sujeita à confirmação do grupo.** Em ordem alfabética: **Dennis Nieto Generoso — RM 563671**, Qualidade e Testes, 160 h, plano e evidências de teste; **Francisco Nogueira De Queiroz — RM 566309**, Frontend, 200 h, interface e exportação; **Mateus Nunes Araújo — RM 562008**, Engenharia de Dados, 220 h, pipeline e catálogo; **Matheus Henrique Ferreira Camargo da Silva — RM 566232**, Produto e Gestão, 120 h, proposta, escopo e finanças; **RHARIEL MARCELO DE OLIVEIRA PERMANHANI — RM 566310**, Arquitetura e Backend, 180 h, arquitetura e plano de implantação; **Sara Marangon de Macedo — RM 563807**, UX e Pesquisa, 120 h, jornada, roteiro e validação.

## 11. Proposta financeira

Pessoas: Produto/PM, 120 h × R$ 120 = R$ 14.400; Arquitetura/Backend, 180 h × R$ 150 = R$ 27.000; Engenharia de Dados, 220 h × R$ 140 = R$ 30.800; Frontend, 200 h × R$ 120 = R$ 24.000; QA/Segurança, 160 h × R$ 100 = R$ 16.000; UX/Pesquisa, 120 h × R$ 125 = R$ 15.000. **Subtotal de pessoal: R$ 127.200.**

Infraestrutura e licenças: R$ 11.760. Encargos simulados: R$ 18.000. Contingência: R$ 11.520. **Total: R$ 168.480**, 6,4% abaixo do teto de R$ 180.000. Operação: R$ 8.900/mês; R$ 106.800 por 12 meses. A memória de cálculo está em CSV.

## 12. Riscos, entregáveis e resultado esperado

Riscos: dados incompletos (alta/alto; validação e visibilidade; Mateus); interpretação como fraude (média/alto; linguagem cautelosa e revisão editorial; Matheus); exposição de identificadores (baixa/alto; minimização e testes; Rhariel); desempenho (média/médio; agregação e carga incremental; Francisco); indisponibilidade da fonte (média/médio; cache versionado e reprocessamento; Rhariel); adoção limitada (média/médio; testes de usabilidade; Sara). Dennis acompanha critérios e regressões.

Entregáveis verificáveis: pipeline, POC web, testes, documentação, relatório de qualidade, proposta financeira, cronograma, matriz de responsabilidades, proposta técnica em PDF de até oito páginas com fonte editável em Google Docs, apresentação editável de até oito slides e transferência. Critério final: banca consegue navegar pela jornada, reproduzir totais essenciais, baixar os IDs de um recorte, observar um erro tratado e distinguir claramente o que é funcional, simulado e futuro.

**Quadro-resumo:** solução Farol ES; empresa Lumina Dados Cívicos; público jornalistas e cidadãos; problema: dados disponíveis, porém difíceis de interpretar e verificar; funcionalidade central: comparação explicável com rastreabilidade; tecnologias: Java 21, HTML, CSS, JavaScript e hospedagem estática; duração 16 semanas; total R$ 168.480; operação R$ 8.900/mês; risco principal: interpretação indevida; diferencial: qualidade e proveniência incorporadas à experiência.
