---

## TODO: Implementar downsampling / agregação de dados

Reduzir a quantidade de pontos transmitidos agrupando registros em janelas de
tempo, enviando valores consolidados em vez de cada leitura individual.

**Tags:** `performance` `backend` `data`

### Checklist

- [ ] Definir granularidades suportadas (por minuto, por cinco minutos, etc.)
- [ ] Implementar agrupamento por janela de tempo na query
- [ ] Expor métricas de média, mínimo, máximo e mediana por intervalo
- [ ] Validar redução de volume no tráfego e memória
- [ ] Testar visualização no frontend com dados agregados

---

## TODO: Implementar streaming em batch / chunking interno

Enviar os dados em blocos menores e contínuos, permitindo que o frontend
consuma e processe os registros à medida que chegam, sem aguardar a
conclusão completa da consulta.

**Tags:** `performance` `backend` `streaming`

### Checklist

- [ ] Definir tamanho do lote (ex: 5.000 registros por vez)
- [ ] Implementar leitura paginada do banco com scroll ou offset
- [ ] Transmitir cada lote via resposta HTTP progressiva
- [ ] Garantir que o frontend processe os lotes incrementalmente
- [ ] Validar comportamento em consultas de longo período

---

## TODO: Migrar repositório para JdbcTemplate + Protobuf

Remover overhead do JPA nas queries de alta frequência. Usar `JdbcTemplate`
com mapeamento direto para o builder do Protobuf, eliminando DTOs intermediários.

**Tags:** `performance` `refactor` `jdbc` `protobuf`

### Checklist

- [ ] Adicionar dependência `spring-boot-starter-jdbc`
- [ ] Criar classe `*ProtoRepository` com `JdbcTemplate`
- [ ] Mapear `ResultSet` diretamente no builder Protobuf
- [ ] Remover entidades JPA das queries de alta frequência
- [ ] Benchmark antes e depois com JMH ou similar

---

## TODO: Criar rota de listagem de sensores por usuário

Expor endpoint que retorna apenas os sensores associados ao usuário autenticado,
respeitando o isolamento de dados por conta.

**Tags:** `feature` `api` `sensor`

### Checklist

- [ ] Criar endpoint `GET /api/sensors`
- [ ] Filtrar sensores pelo usuário autenticado no `SecurityContext`
- [ ] Mapear resposta para DTO ou Protobuf adequado
- [ ] Cobrir com testes de integração

---

## TODO: Revisar restrição de entidades do usuário autenticado

Garantir que nenhum endpoint permita acesso a entidades de outros usuários,
validando o vínculo entre o recurso solicitado e o usuário do token.

**Tags:** `security` `authorization`

### Checklist

- [ ] Auditar endpoints existentes quanto ao isolamento por usuário
- [ ] Adicionar verificação de ownership nas queries críticas
- [ ] Retornar `403 Forbidden` em tentativas de acesso indevido
- [ ] Cobrir cenários com testes de segurança

---

## TODO: Revisar associação de usuário com device

Validar e corrigir o mapeamento entre usuários e devices, garantindo que a
relação esteja corretamente modelada e persistida.

**Tags:** `refactor` `data-model` `device`

### Checklist

- [ ] Revisar a entidade `Device` e seu relacionamento com `User`
- [ ] Verificar se a FK está sendo populada corretamente no cadastro
- [ ] Garantir que queries de device filtrem pelo usuário dono
- [ ] Validar comportamento ao deletar usuário (cascade ou bloqueio)