# EchoVita — Monitoramento Acústico Preventivo

Aplicação Java de console para o Challenge FIAP. Monitora ambientes rurais e de saúde por meio de sensores acústicos, detecta anomalias sonoras, gera alertas por criticidade e notifica perfis operacionais conforme limiar configurado.

Sem framework e sem gerenciador de dependências — apenas Java puro.

## Arquitetura em camadas

```
br.com.echovita
├── Main.java                 → ponto de entrada
└── domain/                   → modelo de domínio
    ├── alerta/
    │   ├── Alerta            → alerta preventivo com criticidade
    │   └── AnomaliaAcustica  → resultado da análise de sinal
    ├── enums/
    │   ├── TipoAnomalia      → tosse, estresse, ruído anômalo, etc.
    │   └── NivelCriticidade  → baixo, médio, alto
    ├── interfaces/
    │   ├── GeradorRelatorio  → relatórios com filtro e limite
    │   ├── MonitorAmbiente   → monitoramento e recebimento de alertas
    │   └── NotificadorAlerta → notificação e limiar de alerta
    ├── local/
    │   ├── LocalMonitorado   → classe abstrata (herança)
    │   ├── Fazenda           → ambiente rural / saúde animal
    │   └── UnidadeSaude      → clínica ou unidade pública
    ├── sensor/
    │   ├── Sensor            → classe abstrata (herança)
    │   ├── SensorRespiratorio
    │   └── SensorComportamental
    └── usuario/
        ├── Usuario           → classe abstrata (herança)
        ├── Veterinario
        ├── Medico
        ├── ProprietarioRural
        └── GestorOperacional
```

### Responsabilidades

| Pacote | Responsabilidade |
|--------|------------------|
| `domain.alerta` | Representação de anomalias detectadas e alertas gerados |
| `domain.enums` | Tipos de anomalia acústica e níveis de criticidade |
| `domain.interfaces` | Contratos de monitoramento, relatório e notificação |
| `domain.local` | Ambientes monitorados, sensores associados e relatórios |
| `domain.sensor` | Captação e análise polimórfica de sinais sonoros |
| `domain.usuario` | Perfis que recebem alertas conforme limiar configurado |

## Diagrama de classes

```mermaid
classDiagram
    direction LR

    namespace interfaces {
        class MonitorAmbiente {
            <<interface>>
            +monitorar() void
            +getStatus() String
            +receberAlerta(Alerta) void
        }
        class NotificadorAlerta {
            <<interface>>
            +notificar(Alerta) void
            +configurarLimiarAlerta(int) void
        }
        class GeradorRelatorio {
            <<interface>>
            +gerarRelatorio() String
            +gerarRelatorio(String) String
            +gerarRelatorio(String, int) String
        }
    }

    namespace local {
        class LocalMonitorado {
            <<abstract>>
            #nome String
            #tipo String
            +getDescricao()* String
            +adicionarSensor(Sensor) void
        }
        class Fazenda {
            -proprietario String
            -numeroCabecas int
            +gerarRelatorio() String
        }
        class UnidadeSaude {
            -responsavel String
            -capacidadeAtendimento int
            +gerarRelatorio() String
        }
    }

    namespace sensor {
        class Sensor {
            <<abstract>>
            #id String
            #localizacao String
            #ativo boolean
            +analisarSinal(String)* AnomaliaAcustica
            +getTipoSensor()* String
        }
        class SensorRespiratorio {
            -sensibilidade int
        }
        class SensorComportamental {
            -raioCobertura double
        }
    }

    namespace usuario {
        class Usuario {
            <<abstract>>
            #nome String
            #rm String
            #limiarAlerta NivelCriticidade
            +exibirPerfil()* String
            +getCargo()* String
        }
        class Veterinario {
            -crmv String
            -especialidade String
        }
        class Medico {
            -crm String
            -especialidade String
        }
        class ProprietarioRural {
            -nomeFazenda String
            -regiao String
        }
        class GestorOperacional {
            -setor String
        }
    }

    namespace enums {
        class TipoAnomalia {
            <<enumeration>>
            TOSSE
            ESPIRRO
            CHORO
            ESTRESSE
            AGITACAO
            RUIDO_ANOMALO
        }
        class NivelCriticidade {
            <<enumeration>>
            BAIXO
            MEDIO
            ALTO
        }
    }

    class Alerta {
        -id Long
        -sensorId String
        -localizacao String
        -dataHora LocalDateTime
        -tipoAnomalia TipoAnomalia
        -nivelCriticidade NivelCriticidade
    }

    class AnomaliaAcustica {
        -tipo TipoAnomalia
        -intensidade int
        -frequencia int
    }

    LocalMonitorado ..|> MonitorAmbiente
    Usuario ..|> NotificadorAlerta
    Fazenda --|> LocalMonitorado
    UnidadeSaude --|> LocalMonitorado
    Fazenda ..|> GeradorRelatorio
    UnidadeSaude ..|> GeradorRelatorio
    SensorRespiratorio --|> Sensor
    SensorComportamental --|> Sensor
    Veterinario --|> Usuario
    Medico --|> Usuario
    ProprietarioRural --|> Usuario
    GestorOperacional --|> Usuario
    LocalMonitorado o-- "0..*" Sensor : sensores
    LocalMonitorado o-- "0..*" Alerta : alertas
    Sensor ..> AnomaliaAcustica : produz
    Alerta --> TipoAnomalia
    Alerta --> NivelCriticidade
    AnomaliaAcustica --> TipoAnomalia
    Usuario --> NivelCriticidade : limiar
```

## Java (SDKMAN)

O arquivo [`.sdkmanrc`](.sdkmanrc) fixa o JDK deste projeto. Com [SDKMAN](https://sdkman.io/) instalado, na raiz do repositório:

```bash
sdk env
java -version
```

Se essa distribuição ainda não estiver instalada:

```bash
sdk install java 21.0.2-open
sdk env
```

Requisito: **Java 21.0.2-open**

## Como executar

```bash
sdk env
mkdir -p out
find src -name "*.java" -print0 | xargs -0 javac --release 21 -d out -encoding UTF-8
java -cp out br.com.echovita.Main
```

Ou abra o projeto na IDE com **source root** em `src` e execute `br.com.echovita.Main`.

## Estado atual

O `Main` valida a estrutura base do projeto. O menu interativo de console e a orquestração completa dos fluxos estão previstos para as próximas entregas.

## Recursos orientados a objetos

- **Entidades de domínio**: `Alerta`, `AnomaliaAcustica`, `LocalMonitorado` (abstrata), `Sensor` (abstrata) e `Usuario` (abstrata)
- **Herança e polimorfismo**: `Fazenda` e `UnidadeSaude` estendem `LocalMonitorado`; `SensorRespiratorio` e `SensorComportamental` estendem `Sensor` com `@Override` em `analisarSinal()` e `getTipoSensor()`
- **Perfis de usuário**: `Veterinario`, `Medico`, `ProprietarioRural` e `GestorOperacional` especializam `Usuario` com `exibirPerfil()` e `getCargo()`
- **Interfaces**: `MonitorAmbiente` ← `LocalMonitorado`; `GeradorRelatorio` ← `Fazenda` / `UnidadeSaude`; `NotificadorAlerta` ← `Usuario`
- **Enums**: `TipoAnomalia` e `NivelCriticidade` para classificação de eventos e priorização
- **Composição**: `LocalMonitorado` agrega listas de `Sensor` e `Alerta`; sensores produzem `AnomaliaAcustica` que alimentam a criação de `Alerta`

## Funcionalidades de negócio

- **Análise acústica**: sensores interpretam sinais sonoros e classificam o tipo de anomalia (ex.: tosse, estresse, agitação)
- **Alertas preventivos**: `Alerta` registra sensor, local, tipo de anomalia, criticidade e data/hora
- **Monitoramento de ambiente**: locais recebem alertas e expõem status conforme sensores cadastrados
- **Notificação por limiar**: usuários configuram criticidade mínima (`configurarLimiarAlerta`) antes de receber alertas
- **Relatórios**: `Fazenda` e `UnidadeSaude` geram relatórios completos, filtrados por texto ou limitados por quantidade

## Exemplo de fluxo

1. Instancie um ambiente monitorado (`Fazenda` ou `UnidadeSaude`)
2. Cadastre sensores (`SensorRespiratorio`, `SensorComportamental`) no local via `adicionarSensor`
3. Analise um sinal com `analisarSinal("tosse repetida")` ou `analisarSinal("estresse no curral")`
4. Crie um `Alerta` a partir da anomalia detectada e registre no local com `receberAlerta`
5. Notifique um perfil (`Veterinario`, `Medico`, etc.) com `notificar(alerta)` respeitando o limiar
6. Gere relatório do ambiente com `gerarRelatorio()`, `gerarRelatorio("tosse")` ou `gerarRelatorio("alto", 5)`
