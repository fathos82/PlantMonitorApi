
---

## 1. Downsampling / Agregação

### Definição
Consiste em reduzir a quantidade de pontos transmitidos, preservando apenas informações representativas do intervalo analisado.

### Funcionamento
O backend agrupa os registros em janelas de tempo, como por minuto ou por cinco minutos, e envia valores consolidados em vez de cada leitura individual. As métricas mais utilizadas são média, mínimo, máximo e mediana.

### Benefícios
- Reduz significativamente o volume de dados trafegado.
- Diminui o uso de memória no backend e no frontend.
- Mantém uma visão adequada para análise histórica e visualização em gráficos.
- Melhora o tempo de resposta em consultas de longo período.

### Exemplo conceitual
| Intervalo | Valor agregado |
|----------|----------------|
| 12:00:00 | 27,1           |
| 12:01:00 | 26,8           |
| 12:02:00 | 27,3           |

---

## 2. Streaming em batch / Chunking interno

### Definição
Consiste em enviar os dados em blocos menores e contínuos, em vez de carregar todo o conjunto de uma única vez.

### Funcionamento
O backend lê os registros do banco em lotes, processa cada lote e os envia progressivamente pela resposta HTTP. Dessa forma, o frontend pode consumir e processar os dados à medida que eles chegam, sem aguardar o término completo da consulta.

### Benefícios
- Evita consumo excessivo de memória.
- Reduz o risco de timeout em consultas volumosas.
- Permite processamento incremental no frontend.
- Mantém o conjunto completo de dados, sem fragmentar a consulta em múltiplas requisições manuais.

### Exemplo conceitual
- O backend busca 5.000 registros por vez.
- Cada lote é convertido e enviado imediatamente.
- O frontend recebe os lotes em sequência e atualiza a visualização progressivamente.

---

## Consideração final

A escolha entre as duas abordagens depende do objetivo da aplicação:

- Use **downsampling/agregação** quando o objetivo for reduzir volume e manter apenas a informação essencial.
- Use **streaming em batch** quando for necessário preservar o conjunto completo de dados, mas com maior controle sobre memória e latência.