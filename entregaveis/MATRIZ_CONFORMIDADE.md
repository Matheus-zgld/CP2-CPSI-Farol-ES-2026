# Matriz de conformidade — CP2 CPSI 2026 | 2ESA

Esta matriz conecta cada obrigação do enunciado atualizado a uma evidência verificável. O grupo deve fazer a submissão oficial no canal da disciplina; publicar artefatos não equivale a submetê-los.

| Exigência | Evidência | Estado / ressalva |
|---|---|---|
| Proposta em PDF com até 8 páginas | [PDF da proposta](https://drive.google.com/file/d/1_mn2HXBFTC2qIUZZv5rgfu2ZyMfmjGh_/view) e [Google Docs fonte](https://docs.google.com/document/d/19bpmqYhIHRyRZHQmcLumg4TLw07diCUzG3kkiuHZGlI/edit) | 4 páginas; acesso com link para visualização. |
| Apresentação de até 8 slides, 12 min | [Google Slides](https://docs.google.com/presentation/d/1kj8Hq2QGz1p5_X9S8k5zFIqhhy9U0C-qIiNzxV9QUhQ/edit), [roteiro](GUIA_APRESENTACAO_12_MIN.md) e modo Apresentação no site | 8 slides e fala distribuída em 12 min; ensaio presencial ainda necessário. |
| POC executável e interativa | [Site público](https://farol-es-cp2-2026.zetzgld11.chatgpt.site), `site/dist/` | Comparação, filtros, estados, exportação e apresentação integrados. |
| Duas questões de dados e ligação ao produto | Proposta seções 2–3; site seção Evidências; relatório seções analíticas | Q1: mudança 2024/25; Q2: lacunas de qualidade. |
| Uso de inferência e intervalo de confiança | `pipeline/confidence_interval.js`, `site/dist/ci_2025.json`, site Evidências | IC Wilson 95% sobre amostra; censo separado; ressalvas explicitadas. |
| Rastreio do agregado à fonte | `site/dist/recortes/YYYY-MM.csv`, site Rastrear, `pipeline/AnalyzeExpenses.java` | Todos os 1.092.158 IDs indexados; prévia 25, download integral do recorte; conciliação de soma e contagem. |
| Cronograma de até 16 semanas | `entregaveis/cronograma_16_semanas.csv`, proposta, relatório | 16 semanas, marcos e entregas. |
| Orçamento até R$ 180 mil e operação mensal | `entregaveis/proposta_financeira.csv`, proposta, relatório | R$ 168.480 de implantação e R$ 8.900/mês de operação simulada. |
| Equipe, RMs e papéis | `entregaveis/matriz_responsabilidades.csv`, proposta, relatório, site Projeto | Seis nomes em ordem alfabética; papéis propostos, a validar entre integrantes. |
| Riscos, limites e privacidade | Proposta, relatório e site Qualidade/Projeto | Sem alegação de fraude; índice minimizado; ausências de rótulos visíveis. |
| Reprodução e testes | `README.md`, `tests/validate.js`, pipeline | Testes automatizados e instruções de reprocessamento; ZIPs oficiais exigidos para refazer o pipeline. |
| Repositório acessível | [GitHub Farol ES](https://github.com/Matheus-zgld/CP2-CPSI-Farol-ES-2026) | Público; contém código, testes, documentação e índices derivados, mas não os ZIPs brutos. |
| Acesso da banca | Links desta matriz | Site público; Docs/Slides/PDF para qualquer pessoa com o link, visualização. |

## Checklist final do grupo

1. Conferir nomes, RMs e turma 2ESA em todos os artefatos.
2. Confirmar internamente os papéis propostos e ajustar se necessário.
3. Abrir todos os links em janela anônima e testar download de recorte.
4. Ensaiar a apresentação em até 12 minutos.
5. Submeter os links e o PDF no canal exigido pelo professor até **03/10/2026, 23h59**; comparecer à apresentação de **05/10/2026**.
