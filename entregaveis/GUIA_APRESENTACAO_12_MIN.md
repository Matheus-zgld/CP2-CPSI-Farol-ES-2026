# Guia de apresentação — Farol ES | 2ESA | 12 minutos

O [Google Slides oficial](https://docs.google.com/presentation/d/1kj8Hq2QGz1p5_X9S8k5zFIqhhy9U0C-qIiNzxV9QUhQ/edit) tem **oito slides**. O modo Apresentação do site oferece uma síntese alternativa; para a banca, seguir os tempos do Google Slides abaixo. Os papéis de fala são uma **proposta**, a confirmar e ensaiar pelo grupo. Abrir site e slides antes da banca. O cronômetro do site começa em 12:00 e pode ser pausado.

| Tempo | Slide | Voz principal | Mensagem verificável |
|---|---:|---|---|
| 0:00–0:15 | 1 | Matheus | Capa e tese: transparência útil é responder perguntas com trilha para a fonte. |
| 0:15–1:00 | 2 | Matheus | Público: jornalistas de dados. Oito ZIPs oficiais, 1.092.158 registros. |
| 1:00–2:30 | 3 | Mateus | Q1: pagamento líquido de R$ 10,34 bi para R$ 11,28 bi, +9,1%; Saúde +R$ 397,54 mi e Transporte +R$ 249,99 mi. |
| 2:30–3:45 | 4 | Dennis | Q2: nomes de órgão ausentes; nomes de função ausentes em 2025. Código preservado; rótulo recuperado é marcado. |
| 3:45–6:15 | 5 | Francisco | Abrir a POC; filtrar 2025, mês e função; ler soma/contagem; mostrar IDs e exportar CSV integral do recorte. |
| 6:15–8:15 | 5 | Sara | Mostrar estado vazio/erro, painel de qualidade e IC didático versus proporção censitária. |
| 8:15–9:15 | 6 | RHARIEL | Pipeline em streaming, índices minimizados, segurança, 17 testes e atribuições propostas. |
| 9:15–10:30 | 7 | Matheus | 16 semanas; R$ 168.480 de investimento; R$ 8.900/mês de operação estimada. Valores simulados. |
| 10:30–12:00 | 8 | Sara e Dennis | Limites, riscos e próximo passo. Sinal não é prova de irregularidade. |

## Roteiro exato da demonstração — 4 min 30 s

1. **3:45–4:15** — Visão Geral: alternar 2024/2025, apontar o aumento nominal de 9,1% sem confundir com crescimento real.
2. **4:15–4:45** — Evidências: conectar Q1 ao comparador e Q2 ao painel de lacunas de qualidade.
3. **4:45–6:15** — Rastrear: selecionar ano, mês e função, clicar Consultar. Mostrar contagem, total líquido e registros negativos. Abrir as primeiras linhas e clicar em Baixar recorte completo. Os 25 exibidos são prévia; o CSV tem todas as linhas do recorte. O índice mensal é conciliado com o agregado.
4. **6:15–6:55** — Mostrar um estado vazio ou erro apenas se houver tempo. A busca individual por documento é atalho entre maiores pagamentos; declarar esse limite.
5. **6:55–7:35** — Qualidade: mostrar ausência de rótulos, histórico e IDs únicos. Não chamar qualquer variação de fraude.
6. **7:35–8:15** — Intervalo de confiança: amostra 1.200, 30 valores negativos, estimativa 2,50%, IC 95% Wilson 1,76%–3,55%; censo 2,17%. O IC mede variação amostral, não incerteza do total processado.

## Reserva operacional

Ensaiar com cronômetro duas vezes. Deixar 15–20 segundos de margem para a troca de apresentador. No início, compartilhar a aba do site e manter os slides abertos em segunda aba. Se a internet falhar, servir `site/dist/` localmente por HTTP; a POC não depende de API externa. Se a demo falhar, usar slides 3–6 e o PDF, dizendo explicitamente que a falha ocorreu. Não prometer funcionalidades futuras como se já estivessem implementadas.

## Perguntas prováveis

**Por que pagamento líquido?** A fonte contém valores negativos de estorno/correção. Excluí-los inflaria o total pago; o valor líquido preserva a semântica publicada.

**O filtro alcança toda a base?** Sim, pelo índice de 24 arquivos mensais que cobrem 1.092.158 linhas. A visualização limita a prévia a 25 linhas por ergonomia; o download traz todos os registros do recorte. A busca rápida por documento, em área separada, cobre exemplos dos maiores pagamentos apenas.

**O nome recuperado de 2025 é oficial?** O código é publicado em 2025; o texto é recuperado de 2024 pelo código correspondente e identificado como derivado. Uma produção exigiria dimensão oficial versionada.

**O IC prova incerteza nos R$ 11,28 bi?** Não. Esse total é calculado na base integral publicada. O IC é um exercício de inferência sobre uma amostra da proporção de valores negativos.

**Isso acusa fraude?** Não. Variação e concentração são sinais para exploração. Conclusão de irregularidade requer documentação, contexto administrativo e análise competente.

**Como a privacidade foi tratada?** Os índices públicos de recorte carregam só ID, documento, códigos de função/órgão, valor e arquivo de origem. Dados pessoais e bancários não são publicados nesses índices.

**Qual é o aceite do produto futuro?** Consultar, comparar e exportar recortes com totais conciliados, manter proveniência e limites visíveis, medir desempenho/acessibilidade e validar com usuários reais antes de expansão.
