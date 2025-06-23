# Sistema de Processamento de Pedidos com Apache Kafka

Este projeto demonstra uma arquitetura de microsserviços simples para processamento de pedidos em uma plataforma de comércio eletrônico, utilizando Apache Kafka como backbone de mensageria para comunicação assíncrona entre os serviços.

## Integrantes do Grupo

* **Aline Lima** - **202201681**
* **Stephany Milhomem** - **202201714**

## 1. Visão Geral do Projeto

O sistema é composto por três serviços principais (simulados) e uma infraestrutura de suporte:

* **Order-Service (Produtor):**
    * Serviço Spring Boot responsável por receber novos pedidos via uma API REST (`POST /orders`).
    * Gera um UUID para o pedido, registra um timestamp e persiste a lista de itens no banco de dados (PostgreSQL).
    * Publica o evento do pedido completo em um tópico Kafka chamado `orders`.
* **Inventory-Service (Consumidor + Produtor):**
    * Serviço Spring Boot que consome mensagens do tópico `orders`.
    * Simula a reserva de estoque para os produtos do pedido (verificando a disponibilidade, mas sem persistir a alteração de estoque neste exemplo).
    * Publica o resultado da reserva (sucesso ou falha por falta de estoque) em um novo tópico Kafka chamado `inventory-events`.
* **Notification-Service (Consumidor):**
    * Serviço Spring Boot que consome mensagens do tópico `inventory-events`.
    * Registra no console uma notificação simulada (e-mails/SMS) ao cliente com base no status do pedido (confirmado ou falha no estoque).
    * Expõe um endpoint REST (`GET /notifications/latest`) para o frontend buscar a última notificação processada.
* **Frontend (Aplicação React com Ant Design):**
    * Uma interface de usuário simples para permitir a criação e envio de novos pedidos ao Order-Service.
    * Exibe notificações em tempo real sobre o status do pedido (reserva de estoque) buscando informações do Notification-Service.
* **Infraestrutura de Mensageria e Banco de Dados (Docker Compose):**
    * **Apache Kafka:** O broker de mensagens distribuído, essencial para a comunicação assíncrona.
    * **Apache ZooKeeper:** Gerencia o estado e a coordenação do cluster Kafka.
    * **PostgreSQL:** Banco de dados relacional para persistir informações de pedidos e produtos.
    * **pgAdmin:** Ferramenta de administração gráfica para PostgreSQL, útil para visualizar o banco de dados.
    * **Kafka UI:** Interface web para monitorar e gerenciar tópicos, produtores e consumidores Kafka.

## 2. Requisitos Funcionais (RFs)

* **RF-1 (Tópicos Kafka):** Os tópicos `orders` e `inventory-events` são criados no broker Kafka via linha de comando (`kafka-topics.sh`).
* **RF-2 (Order-Service API):** O Order-Service expõe uma REST API (`POST /orders`) que gera um UUID para o pedido, um timestamp de criação, persiste no banco de dados e publica o pedido completo no tópico `orders` do Kafka.
* **RF-3 (Inventory-Service):** O Inventory-Service processa mensagens do tópico `orders`, simula a reserva de estoque (verifica a quantidade disponível dos produtos no banco de dados) e publica o resultado (sucesso ou falha por estoque insuficiente) no tópico `inventory-events`.
* **RF-4 (Notification-Service):** O Notification-Service lê os eventos do tópico `inventory-events` e registra no console uma notificação simulada (e-mail/SMS) para o cliente. Ele também disponibiliza a última notificação via API REST para o frontend.

## 3. Como Clonar e Executar a Solução

Siga os passos abaixo para colocar o sistema em funcionamento.

### Pré-requisitos:

* **Git:** Para clonar o repositório.
* **Docker & Docker Compose:** Para rodar a infraestrutura (Kafka, ZooKeeper, PostgreSQL, pgAdmin, Kafka UI).
* **Java Development Kit (JDK) 17+:** Para compilar e rodar os backends Spring Boot.
* **Maven:** Ferramenta de automação de build para os projetos Java.
* **Node.js & npm:** Para rodar o frontend React (versão 18.x ou superior recomendada).

### Passos para Configuração e Execução:

1.  **Clone o Repositório:**
    Abra seu terminal e execute:
    ```bash
    git clone [https://github.com/aaaaline/kafka-order-processing/](https://github.com/aaaaline/kafka-order-processing/)
    cd kafka-order-processing/
    ```

2.  **Inicie a Infraestrutura de Suporte (Docker Compose):**
    Na raiz do projeto (`kafka-order-processing/`), onde está o `docker-compose.yml`, execute:
    ```bash
    sudo docker-compose up -d
    ```
    Isso iniciará o ZooKeeper, Kafka, PostgreSQL, pgAdmin e Kafka UI em segundo plano. Aguarde alguns segundos para que todos os serviços estejam completamente inicializados.

3.  **Crie e Popule o Banco de Dados (PostgreSQL):**

    * **Acesse o pgAdmin:** Abra seu navegador e vá para `http://localhost:5050`. Faça login com o Email: `admin@admin.com` e Senha: `admin`.
    * **Conecte-se ao Servidor PostgreSQL:**
        * Na interface do pgAdmin, clique em "Add New Server" ou "Register" -> "Server".
        * Na aba **General**, defina um nome como `My PostgreSQL Local`.
        * Na aba **Connection**, preencha:
            * `Host name/address`: `localhost`
            * `Port`: `5432`
            * `Maintenance database`: `ecommerce_db`
            * `Username`: `postgres`
            * `Password`: `postgres`
        * Clique em "Save".
    * **Execute o Script de Schema e População de Dados:**
        * No pgAdmin, navegue até o servidor recém-conectado -> "Databases" -> `ecommerce_db`.
        * Abra uma "Query Tool" (ícone de folha com triângulo verde).
        * Copie e cole o conteúdo do arquivo `orderskafka/src/main/resources/data.sql` (ou o conteúdo abaixo) na Query Tool e execute. Este script contém tanto a criação das tabelas quanto a população inicial dos produtos.
        ```sql
        -- orderskafka/src/main/resources/data.sql

        -- Remove tabelas existentes para garantir um ambiente limpo
        DROP TABLE IF EXISTS order_items;
        DROP TABLE IF EXISTS orders;
        DROP TABLE IF EXISTS products;

        -- Criação da tabela de produtos
        CREATE TABLE products (
                                  id UUID PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,
                                  price NUMERIC(10, 2) NOT NULL,
                                  quantity INT NOT NULL
        );

        -- Criação da tabela de pedidos
        CREATE TABLE orders (
                                id UUID PRIMARY KEY,
                                customer_name VARCHAR(255) NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE NOT NULL
        );

        -- Criação da tabela de itens do pedido
        CREATE TABLE order_items (
                                     id BIGSERIAL PRIMARY KEY,
                                     order_id UUID NOT NULL REFERENCES orders(id),
                                     product_id UUID NOT NULL,
                                     product_name VARCHAR(255) NOT NULL,
                                     quantity INT NOT NULL,
                                     unit_price NUMERIC(10, 2) NOT NULL
        );

        -- Inserir alguns produtos de exemplo
        INSERT INTO products (id, name, price, quantity) VALUES
                                                             ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Laptop Gamer Ultra', 7500.00, 10),
                                                             ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Mouse Sem Fio Fast', 150.50, 50),
                                                             ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Teclado Mecânico RGB', 450.00, 30),
                                                             ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Monitor 4K 27"', 2200.00, 15);
        ```

4.  **Crie os Tópicos Kafka:**
    Entre no contêiner Kafka e crie os tópicos necessários.
    ```bash
    sudo docker exec -it kafka bash
    kafka-topics --bootstrap-server kafka:29092 --create --topic orders --partitions 1 --replication-factor 1
    kafka-topics --bootstrap-server kafka:29092 --create --topic inventory-events --partitions 1 --replication-factor 1
    kafka-topics --bootstrap-server kafka:29092 --list # Para verificar se foram criados
    exit
    ```

5.  **Rode o Backend do Order-Service:**
    Na raiz do projeto (`kafka-order-processing/`), navegue até a pasta `orderskafka/` e execute:
    ```bash
    cd orderskafka/
    mvn clean install
    mvn spring-boot:run
    ```
    O serviço estará rodando na porta `8080`. Aguarde a mensagem `Started OrderskafkaApplication...` nos logs.

6.  **Rode o Backend do Inventory-Service:**
    Abra um **novo terminal**. Na raiz do projeto (`kafka-order-processing/`), navegue até a pasta `inventory-service/` e execute:
    ```bash
    cd inventory-service/
    mvn clean install
    mvn spring-boot:run
    ```
    O serviço estará rodando na porta `8081`. Aguarde a mensagem `Started InventoryServiceApplication...` nos logs.

7.  **Rode o Backend do Notification-Service:**
    Abra um **novo terminal**. Na raiz do projeto (`kafka-order-processing/`), navegue até a pasta `notification-service/` e execute:
    ```bash
    cd notification-service/
    mvn clean install
    mvn spring-boot:run
    ```
    O serviço estará rodando na porta `8083`. Aguarde a mensagem `Started NotificationServiceApplication...` nos logs.

8.  **Rode o Frontend:**
    Abra um **novo terminal**. Na raiz do projeto (`kafka-order-processing/`), navegue até a pasta `frontend-app/`.
    ```bash
    cd frontend-app/
    npm install
    npm run dev
    ```
    O frontend estará acessível (geralmente em `http://localhost:5173`, verifique o output do `npm run dev`).

### 4. Teste a Solução Completa:

* Abra o frontend no seu navegador (`http://localhost:5173`).
* Preencha o campo "Nome do Cliente".
* Adicione produtos ao pedido usando o "Search Select".
* Clique em "Registrar Pedido".
* **Observe:**
    * A notificação de sucesso/erro no frontend (pop-up do Ant Design e no card "Status das Notificações").
    * Os logs do seu `orderskafka` (Order-Service) para a mensagem "📦 Pedido enviado ao Kafka".
    * Os logs do seu `inventoryservice` (Inventory-Service) para "📦 Evento de Pedido Recebido no Inventory-Service".
    * Os logs do seu `notificationservice` (Notification-Service) para "📬 Notificação Recebida".
    * No Kafka UI (`http://localhost:8180`), vá em "Topics", clique em "orders" e depois em "Messages" para ver os pedidos publicados. Faça o mesmo para "inventory-events".

---

## 5. Requisitos Não-Funcionais (RNFs)

### 1. Escalabilidade

**Conceito:** A escalabilidade é a capacidade de um sistema lidar com um aumento na carga de trabalho ou demanda, adicionando recursos (hardware, software) sem comprometer o desempenho ou a funcionalidade.

**Como o Apache Kafka Consegue Escalabilidade:**

* **Partições (Partitions):** Os tópicos no Kafka são divididos em partições. Cada partição é uma sequência de mensagens ordenada e imutável. As partições permitem que os dados sejam distribuídos entre vários brokers (servidores Kafka) e que os consumidores de um grupo de consumidores leiam dados em paralelo. Adicionar mais partições a um tópico permite um maior paralelismo de leitura e escrita.
* **Consumidores Paralelos (Consumer Groups):** Um grupo de consumidores pode ler de um tópico, onde cada consumidor no grupo lê de um subconjunto de partições. Adicionar mais instâncias de consumidores a um grupo aumenta a taxa de processamento, pois eles processam partições em paralelo, cada um responsável por um conjunto exclusivo de partições dentro do grupo.
* **Adição Horizontal de Brokers:** É fácil adicionar novos brokers a um cluster Kafka em execução. Quando novos brokers são adicionados, as partições existentes podem ser rebalanceadas para esses novos brokers, distribuindo a carga de armazenamento e processamento entre mais máquinas. Isso permite aumentar a capacidade de throughput e armazenamento do cluster.
* **Armazenamento Distribuído:** As mensagens são persistidas em disco nos brokers de forma distribuída, e o Kafka é projetado para lidar com grandes volumes de dados de forma eficiente. Isso permite que a taxa de dados de entrada exceda a taxa de dados de saída momentaneamente (armazenamento em buffer) sem perda de dados, mantendo a robustez do sistema sob picos de carga.

### 2. Tolerância à Falha

**Conceito:** Tolerância à falha é a propriedade de um sistema continuar operando corretamente e sem interrupções significativas mesmo na ocorrência de falhas em um ou mais de seus componentes.

**Como o Apache Kafka Trata a Falha:**

* **Replicação de Dados (Replication Factor):** No Kafka, cada partição pode ter múltiplas réplicas (`replication-factor`). Uma dessas réplicas é designada como "líder" (leader) e as outras são "seguidoras" (followers). Todos os produtores escrevem para o líder, e todos os consumidores leem do líder. As réplicas seguidoras replicam continuamente os dados do líder.
* **Eleição Automática de Líder:** Se o broker que hospeda a partição líder falhar, o Kafka (historicamente com a ajuda do ZooKeeper, ou mais recentemente, o próprio Kafka com o processo de `KRaft` que substitui o ZooKeeper para gestão de metadata) automaticamente elege uma das réplicas seguidoras que estão em sincronia com o líder falho como o novo líder. Isso acontece de forma transparente para produtores e consumidores (após um breve período de recuperação).
* **Persistência em Disco:** As mensagens são duravelmente persistidas em disco nos brokers e são replicadas, minimizando a perda de dados mesmo em caso de falha de hardware ou rede.
* **Rebalanceamento Automático de Consumidores:** Se um consumidor em um grupo de consumidores falhar, as partições que ele estava consumindo são automaticamente reatribuídas a outros consumidores ativos no mesmo grupo. Isso garante que o processamento continue e que todas as partições continuem sendo lidas.

**Exemplo de Situação de Falha e Tratamento:**

* **Situação de Falha:** Um dos servidores (brokers) do seu cluster Kafka desliga inesperadamente (ex: falha de hardware, queda de energia na máquina do broker).
* **Como o Kafka Trata:**
    1.  As partições líderes que estavam sendo hospedadas nesse broker falho se tornam indisponíveis temporariamente.
    2.  O Kafka, através do seu mecanismo de coordenação, detecta a falha do broker.
    3.  Para cada partição que perdeu seu líder, uma nova eleição de líder é iniciada. Uma das réplicas seguidoras restantes (que já possui uma cópia idêntica e em sincronia dos dados) é promovida a novo líder.
    4.  Produtores e consumidores que estavam interagindo com o líder falho são automaticamente redirecionados para o novo líder da partição.
    5.  O fluxo de dados continua com uma interrupção mínima e controlada, e nenhum dado é perdido, pois ele já estava replicado e disponível em outras réplicas.

### 3. Idempotência

**Conceito:** A idempotência, em sistemas distribuídos, significa que uma operação pode ser executada múltiplas vezes, mas produzirá o mesmo resultado (ou efeito final) como se tivesse sido executada apenas uma única vez. Ou seja, repetir uma operação idempotente não causa efeitos colaterais adicionais ou inconsistências no estado do sistema.

**Como Garantir Idempotência no Kafka:**

O Kafka oferece garantias de idempotência para produtores e, em conjunto com outras estratégias, permite o processamento "exactly-once" (processar cada mensagem exatamente uma vez) para consumidores.

* **Idempotência do Produtor (`enable.idempotence=true`):**
    * No Kafka (a partir da versão 0.11), os produtores podem ser configurados para serem **idempotentes** ao definir a propriedade `enable.idempotence=true`.
    * Quando ativada, o broker Kafka atribui um ID de produtor (Producer ID) e um número de sequência (Sequence Number) a cada lote de mensagens enviado. Se uma mensagem for enviada novamente (por exemplo, devido a uma falha de rede temporária e uma retransmissão automática do produtor), o broker usa esses IDs para detectar e descartar duplicatas, garantindo que a mensagem seja escrita no log da partição apenas uma vez.
    * **Implementação:** No seu `application.properties` do serviço produtor (Order-Service, Inventory-Service):
        ```properties
        spring.kafka.producer.properties.enable.idempotence=true
        ```

* **Idempotência do Consumidor (Processamento "Exactly-Once" com Transações ou Lógica de Aplicação):**
    * Garantir que os *consumidores* processem uma mensagem exatamente uma vez é mais complexo, pois envolve o consumo, o processamento da lógica de negócio e a atualização de um estado (como um banco de dados ou outra gravação em Kafka).
    * **Transações Atômicas no Kafka:** O Kafka oferece suporte a transações atômicas de ponta a ponta. Isso permite que um consumidor/produtor (também conhecido como "stream processor") consuma mensagens de tópicos de entrada, execute processamento e produza mensagens para tópicos de saída (ou atualize um banco de dados) como uma única operação atômica. Se a transação falhar, todas as alterações são revertidas (rollback), evitando duplicatas ou estados inconsistentes. Isso é ativado usando `spring.kafka.producer.transaction-id-prefix` e `KafkaTransactionManager` no Spring Kafka.
    * **Idempotência no Nível da Aplicação:** Para operações que atualizam um banco de dados ou outros sistemas externos, a lógica de negócio do consumidor deve ser projetada para ser **idempotente**. Isso significa que a operação de atualização deve produzir o mesmo resultado mesmo se executada várias vezes. Exemplos incluem:
        * **UPSERT:** Utilizar operações que inserem um registro se ele não existe ou atualizam-no se já existe.
        * **Verificação de Duplicatas:** Antes de executar uma operação, verificar se ela já foi realizada usando um ID de idempotência (por exemplo, o `orderId` da mensagem Kafka).
        * **IDs de Idempotência:** Incluir um ID único de idempotência na mensagem ou na requisição, e o sistema receptor usa esse ID para garantir que a operação seja processada apenas uma vez.

Ao combinar a idempotência do produtor do Kafka com a idempotência no nível da aplicação (para as operações de consumidor que modificam estado) e, quando necessário, as transações transacionais do Kafka, é possível alcançar um processamento robusto e sem duplicatas.
