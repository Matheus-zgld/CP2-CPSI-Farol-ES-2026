# Farol ES — transparência inteligente | CP2 CPSI 2026

Projeto da turma **2ESA**. A POC investiga os pagamentos oficiais do Espírito Santo em 2024 e 2025 com comparação temporal, transparência sobre lacunas de qualidade e trilha do indicador aos registros. Os oito ZIPs foram processados: **1.092.158 registros**, sem linha malformada ou ID duplicado. Pagamento líquido: **R$ 10,34 bi (2024)** e **R$ 11,28 bi (2025)**, crescimento de **9,1%**. Valores negativos (estornos/correções) são mantidos.

## Entrega principal

- [POC interativa publicada](https://farol-es-cp2-2026.zetzgld11.chatgpt.site) — pública, responsiva, com filtro por ano/mês/função e exportação dos IDs e valores de cada recorte.
- [Proposta técnica em PDF, 4 páginas](https://drive.google.com/file/d/1_mn2HXBFTC2qIUZZv5rgfu2ZyMfmjGh_/view) — dentro do limite de oito páginas.
- [Fonte editável da proposta no Google Docs](https://docs.google.com/document/d/19bpmqYhIHRyRZHQmcLumg4TLw07diCUzG3kkiuHZGlI/edit).
- [Apresentação editável no Google Slides, 8 slides](https://docs.google.com/presentation/d/1kj8Hq2QGz1p5_X9S8k5zFIqhhy9U0C-qIiNzxV9QUhQ/edit).
- [Relatório técnico aprofundado no Google Docs](https://docs.google.com/document/d/1y1N7kaVT7HRIMs6-H5rFhLOKW_cZpYFF2gtQw9Jza6o/edit).
- [Roteiro de 12 minutos](entregaveis/GUIA_APRESENTACAO_12_MIN.md) e [matriz de conformidade](entregaveis/MATRIZ_CONFORMIDADE.md).

Os links de Drive foram configurados para **qualquer pessoa com o link visualizar**. Os arquivos Google Docs e Slides são nativamente editáveis pelo grupo; o acesso público é somente de leitura. A entrega oficial no canal da disciplina precisa ser feita pelo grupo até **03/10/2026, 23h59**; apresentação em **05/10/2026**.

**Não submeter** `entregaveis/Proposta_Tecnica_Farol_ES.docx` nem `entregaveis/Apresentacao_Executiva_Farol_ES.pptx`: são versões legadas, anteriores ao limite atualizado de oito páginas e oito slides. Use os links acima.

## O que funciona

O site oferece visão geral, duas questões analíticas com evidências e decisão de produto, painel de qualidade, intervalo de confiança demonstrativo, rastreio e download de recortes completos. O filtro ano/mês/função consulta os índices mensais de todos os registros, concilia contagem e soma com os agregados e mostra os 25 primeiros IDs; o download contém todo o recorte. A busca individual por documento é um atalho ilustrativo limitado aos maiores pagamentos, identificado como tal. Não há login, API dinâmica ou conclusão automatizada de fraude.

## Estrutura e reprodução

- `pipeline/AnalyzeExpenses.java`: leitura em streaming dos oito ZIPs oficiais, normalização, qualidade, agregados e índices mensais minimizados em `site/dist/recortes/`.
- `pipeline/confidence_interval.js`: amostra reproduzível de 1.200 registros de 2025 e IC de Wilson 95% para proporção de `ValorPago` negativo. A proporção censitária é exibida separadamente.
- `site/dist/`: site estático publicável, `analysis.json`, `ci_2025.json` e 24 índices mensais CSV.
- `tests/validate.js`: verificações automatizadas.
- `entregaveis/`: relatório, finanças, cronograma, responsabilidades e roteiro.

Pré-requisitos para reprocessar: Java 21 e Node.js 20+. Coloque os oito ZIPs oficiais disponibilizados na atividade na raiz do projeto, mantendo os nomes originais. Na raiz, execute:

```text
javac -encoding UTF-8 pipeline/AnalyzeExpenses.java
java -Xmx768m -cp pipeline AnalyzeExpenses .
node pipeline/confidence_interval.js
node tests/validate.js
```

Depois copie o `analysis.json` gerado para `site/dist/analysis.json` e sirva `site/dist/` por HTTP estático (por exemplo, `npx serve site/dist`). Não abra diretamente por `file://`, pois o navegador bloqueia o carregamento de JSON/CSV. O site publicado já contém os derivados necessários para a demonstração; os ZIPs brutos não são publicados.

## Premissas e limites

Nomes de órgão não vieram preenchidos na fonte; nomes de função faltam em 2025 e são recuperados por código estável de 2024, com indicação na interface. O histórico está ausente em parte dos registros. Os índices de recorte excluem CPF/CNPJ e outros campos pessoais, mas incluem IDs/documentos públicos necessários à rastreabilidade. Um sinal analítico não prova irregularidade. O orçamento de **R$ 168.480** e a operação estimada em **R$ 8.900/mês** são proposta simulada, não despesa efetiva. Os papéis individuais são uma distribuição proposta, a confirmar pelo grupo.

## Grupo — ordem alfabética

- Dennis Nieto Generoso — RM 563671
- Francisco Nogueira De Queiroz — RM 566309
- Mateus Nunes Araújo — RM 562008
- Matheus Henrique Ferreira Camargo da Silva — RM 566232
- RHARIEL MARCELO DE OLIVEIRA PERMANHANI — RM 566310
- Sara Marangon de Macedo — RM 563807

IA foi usada como apoio à análise, redação, revisão e implementação. O grupo deve revisar dados, código, falas e submissão. O site usa JavaScript, HTML e CSS sem dependências de produção; nunca versionar segredos. Fonte dos dados: Portal da Transparência do ES; material FIAP utilizado no contexto acadêmico.
