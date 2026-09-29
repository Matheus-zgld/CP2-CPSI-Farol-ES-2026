# Guia de apresentação — Farol ES | 2ESA | 12 minutos

O [Google Slides oficial](https://docs.google.com/presentation/d/1kj8Hq2QGz1p5_X9S8k5zFIqhhy9U0C-qIiNzxV9QUhQ/edit) tem **oito slides**. A mesma sequência está disponível no modo Apresentação do site. Os papéis de fala abaixo são uma **proposta**, a confirmar e ensaiar pelo grupo. Abrir site e slides antes da banca. O cronômetro do site começa em 12:00 e pode ser pausado.

| Tempo | Slide | Voz principal | Mensagem verificável |
|---|---:|---|---|
| 0:00–1:00 | 1 | Matheus | Transparência útil é responder perguntas com trilha para a fonte. Público: jornalistas de dados. |
| 1:00–2:00 | 2 | Mateus | Oito ZIPs oficiais, 1.092.158 linhas, 71 campos; 0 malformadas e 0 IDs duplicados. |
| 2:00–3:00 | 3 | Mateus | Q1: pagamento líquido de R$ 10,34 bi para R$ 11,28 bi, +9,1%; Saúde +R$ 397,54 mi e Transporte +R$ 249,99 mi. |
| 3:00–4:00 | 4 | Dennis | Q2: nomes de órgão ausentes; nomes de função ausentes em 2025. Código preservado; rótulo recuperado é marcado. |
| 4:00–6:30 | 5 | Francisco | Abrir a POC; filtrar 2025, mês e função; ler soma/contagem; mostrar IDs e exportar CSV integral do recorte. |
| 6:30–8:30 | 5 | Sara | Demonstrar caminho de volta, estado vazio/erro, painel de qualidade e IC didático versus proporção censitária. |
| 8:30–9:30 | 6 | RHARIEL | Pipeline em streaming, índices minimizados, segurança, testes e atribuições propostas. |
| 9:30–10:45 | 7 | Matheus | 16 semanas; R$ 168.480 de investimento; R$ 8.900/mês de operação estimada. Valores simulados, com critérios de aceite. |
| 10:45–12:00 | 8 | Sara e Dennis | Limites, riscos, o que está funcional e próximo passo. Sinal não é prova de irregularidade. |

## Roteiro exato da demonstração — 4 min 30 s

1. **4:00–4:30** — Visão Geral: alternar 2024/2025, apontar o aumento de 9,1% sem confundir preço corrente com crescimento real.
2. **4:30–5:00** — Evidências: conectar Q1 ao comparador e Q2 ao painel de lacunas de qualidade.
3. **5:00–6:30** — Rastrear: selecionar ano, mês e função, clicar Consultar. Mostrar contagem, total líquido e registros negativos. Abrir as primeiras linhas e clicar em Baixar recorte completo. Explicar que os 25 exibidos são prévia, enquanto o CSV baixado tem todas as linhas do recorte. O índice mensal é conciliado com o agregado.
4. **6:30–7:10** — Selecionar um recorte sem linhas ou simular falha de rede apenas se houver tempo. A interface deve informar o estado sem fabricar dados. A busca individual por documento é apenas um atalho entre maiores pagamentos; declarar esse limite.
5. **7:10–7:50** — Qualidade: mostrar ausência de rótulos, histórico e IDs únicos. Não chamar qualquer variação de fraude.
6. **7:50–8:30** — Intervalo de confiança: amostra 1.200, 30 valores negativos, estimativa 2,50%, IC 95% Wilson 1,76%–3,55%; censo 2,17%. Explicar que o IC mede variação amostral e não incerteza do total processado.

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
