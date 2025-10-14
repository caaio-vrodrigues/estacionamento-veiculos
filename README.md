# carpark - Backend API

Esta documentação fornece uma visão da API backend `carpark`, focando em seus recursos, endpoints e configurações.

## Sumário
1. [Visão Geral](#1-visão-geral)
2. [Tecnologias Principais](#2-tecnologias-principais)
3. [Entidades Principais](#3-entidades-principais)
    - [3.1. `Owner`](#31-owner)
    - [3.2. `Vehicle`](#32-vehicle)
    - [3.3. `ParkingSpace`](#33-parkingspace)
    - [3.4. `VehicleOwnership`](#34-vehicleownership)
    - [3.5. `ParkingSpaceRental`](#35-parkingspacerental)
4. [Enums Utilizados](#4-enums-utilizados)
    - [4.1. `VehicleBrand`](#41-vehiclebrand)
    - [4.2. `ParkingSpacePrice`](#42-parkingspaceprice)
5. [Exceções Personalizadas](#5-exceções-personalizadas)
6. [Tratamento Global de Exceções](#6-tratamento-global-de-exceções)
7. [Endpoints da API](#7-endpoints-da-api)
    - [7.1. Endpoints de Proprietários (`/owner`)](#71-endpoints-de-proprietários-owner)
    - [7.2. Endpoints de Veículos (`/vehicle`)](#72-endpoints-de-veículos-vehicle)
    - [7.3. Endpoints de Propriedade de Veículos (`/vehicle-ownership`)](#73-endpoints-de-propriedade-de-veículos-vehicle-ownership)
    - [7.4. Endpoints de Vagas de Estacionamento (`/parking-space`)](#74-endpoints-de-vagas-de-estacionamento-parking-space)
    - [7.5. Endpoints de Aluguéis de Vagas (`/parkingspace-rental`)](#75-endpoints-de-aluguéis-de-vagas-parkingspace-rental)
8. [Configurações Essenciais (`application.properties`)](#8-configurações-essenciais-applicationproperties)
9. [Executando Localmente](#9-executando-localmente)

---

### 1. Visão Geral

A aplicação `carpark` é um serviço backend desenvolvido em Spring Boot, com o objetivo de gerenciar um sistema de estacionamento de veículos. Ela permite o cadastro de proprietários, veículos, o registro da propriedade de veículos por proprietários e o gerenciamento de vagas de estacionamento, incluindo o aluguel dessas vagas.

### 2. Tecnologias Principais

*   **Framework**: Spring Boot 3.x (versão `3.5.6`)
*   **Linguagem**: Java 21
*   **Persistência**: Spring Data JPA
*   **Banco de Dados**: H2 Database (em memória para desenvolvimento, configurável para arquivo)
*   **Auxiliares**: Lombok (para reduzir código boilerplate)
*   **Validação**: Spring Boot Starter Validation
*   **Build Tool**: Maven

### 3. Entidades Principais

As principais entidades que compõem o sistema são:

#### 3.1. `Owner`

Representa o proprietário de um veículo.

*   **id**: `Long` (Gerado automaticamente)
*   **fullName**: `String` (Nome completo do proprietário, obrigatório, não pode ser vazio)
*   **driversLicense**: `String` (Número da carteira de motorista, único, obrigatório, não pode ser vazio)

#### 3.2. `Vehicle`

Representa um veículo no estacionamento.

*   **id**: `Long` (Gerado automaticamente)
*   **model**: `String` (Modelo do veículo, obrigatório, não pode ser vazio)
*   **brand**: `VehicleBrand` (Enum que representa a marca do veículo, obrigatório)
*   **country**: `String` (País de origem da marca do veículo, preenchido automaticamente com base na `brand`)
*   **plaque**: `String` (Placa do veículo, única, obrigatório, não pode ser vazio)
*   **type**: `ParkingSpacePrice` (Enum que define o tipo de veículo e o preço padrão da vaga, obrigatório)

#### 3.3. `ParkingSpace`

Representa uma vaga de estacionamento.

*   **id**: `Integer` (Gerado automaticamente)
*   **type**: `ParkingSpacePrice` (Enum que define o tipo de vaga (Carro/Moto), obrigatório)
*   **price**: `BigDecimal` (Preço da vaga, preenchido automaticamente com base no `type`)
*   **occupied**: `Boolean` (Indica se a vaga está ocupada ou não, padrão `false`)

#### 3.4. `VehicleOwnership`

Representa a relação de propriedade entre um `Owner` e um `Vehicle`.

*   **id**: `Long` (Gerado automaticamente)
*   **vehicle**: `Vehicle` (Associação Many-to-One com o veículo, obrigatório)
*   **owner**: `Owner` (Associação Many-to-One com o proprietário, obrigatório)

#### 3.5. `ParkingSpaceRental`

Representa um registro de aluguel (ocupação) de uma vaga de estacionamento por um veículo.

*   **id**: `Long` (Gerado automaticamente)
*   **parkingSpace**: `ParkingSpace` (Associação Many-to-One com a vaga de estacionamento, obrigatório)
*   **vehicleOwnership**: `VehicleOwnership` (Associação Many-to-One com a propriedade do veículo, obrigatório)
*   **startRenting**: `LocalDateTime` (Timestamp de início do aluguel, preenchido automaticamente na criação)
*   **endRenting**: `LocalDateTime` (Timestamp de fim do aluguel, nulo se ainda em aluguel)
*   **totalRent**: `BigDecimal` (Valor total do aluguel, calculado ao finalizar o aluguel)

### 4. Enums Utilizados

#### 4.1. `VehicleBrand`

Enumeração para marcas de veículos, incluindo nome e país de origem.

*   `FORD` ("Ford", "Estados Unidos")
*   `PORSHE` ("Porshe", "Alemanha")
*   `FERRARI` ("Ferrari", "Itália")
*   `MERCEDES_BENZ` ("Mercedes-benz", "Alemanha")
*   `FIAT` ("Fiat", "Itália")
*   `PEUGEOT` ("Peugeot", "França")
*   `SUZUKI` ("Suzuki", "Japão")

#### 4.2. `ParkingSpacePrice`

Enumeração para tipos de vagas de estacionamento e seus preços associados.

*   `CAR` ("Car", R\$ 10.0)
*   `MOTORCYCLE` ("Motorcycle", R\$ 5.0)

### 5. Exceções Personalizadas

O sistema utiliza exceções personalizadas para lidar com cenários específicos de negócios:

*   **`IncompatibleParkingSpaceException`**: Lançada quando há uma incompatibilidade entre o tipo de veículo e o tipo de vaga de estacionamento. (HTTP Status: `422 UNPROCESSABLE_ENTITY`)
*   **`MissingRequiredFieldException`**: Lançada quando um ou mais campos obrigatórios estão ausentes ou incompletos na requisição. (HTTP Status: `400 BAD_REQUEST`)
*   **`OccupiedParkingSpaceException`**: Lançada quando se tenta estacionar um veículo em uma vaga já ocupada. (HTTP Status: `409 CONFLICT`)
*   **`ResourceAlreadyExistsException`**: Lançada quando se tenta criar um recurso que já existe (por exemplo, placa de veículo ou carteira de motorista duplicada, ou um veículo já está alugando uma vaga). (HTTP Status: `409 CONFLICT`)
*   **`ResourceNotFoundException`**: Lançada quando um recurso não é encontrado pelo ID fornecido. (HTTP Status: `404 NOT_FOUND`)

### 6. Tratamento Global de Exceções

A classe `ExceptionHandlerController` é responsável por interceptar e tratar as exceções lançadas pela aplicação, retornando respostas padronizadas e informativas aos clientes da API.

*   **`MethodArgumentNotValidException`**: Captura erros de validação de campos em requisições (`@Valid`), retornando `400 BAD_REQUEST` com detalhes dos campos inválidos.
*   **`ConstraintViolationException`**: Captura erros de validação em parâmetros de métodos (ex: `@PathVariable @Min(1)`), retornando `400 BAD_REQUEST`.
*   **`HttpMessageNotReadableException`**: Captura erros de parsing do corpo da requisição (ex: JSON malformado), retornando `400 BAD_REQUEST`.
*   As **exceções personalizadas** (mencionadas na seção anterior) são mapeadas para os respectivos códigos de status HTTP e mensagens de erro.

O corpo da resposta de erro segue um padrão JSON contendo: `timestamp`, `status`, `error`, `message`, `path` e `details` (para erros de validação).

### 7. Endpoints da API

A API é organizada por recursos relacionados a proprietários, veículos, propriedade de veículos, vagas de estacionamento e aluguéis de vagas.

#### 7.1. Endpoints de Proprietários (`/owner`)

Base: `/owner`

*   **`POST /owner`**
    *   **Descrição**: Cria um novo proprietário.
    *   **Método**: `POST`
    *   **Corpo da Requisição**: `Owner` (JSON)
        ```json
        {
            "fullName": "Caio Vinicius Rodrigues",
            "driversLicense": "12345678900"
        }
        ```
    *   **Resposta**: `200 OK` com o `Owner` criado.

*   **`GET /owner`**
    *   **Descrição**: Lista todos os proprietários registrados.
    *   **Método**: `GET`
    *   **Resposta**: `200 OK` com uma lista de `Owner`.

*   **`GET /owner/{id}`**
    *   **Descrição**: Busca um proprietário pelo ID.
    *   **Método**: `GET`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com o `Owner` encontrado.

*   **`PUT /owner/{id}`**
    *   **Descrição**: Atualiza um proprietário existente.
    *   **Método**: `PUT`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Corpo da Requisição**: `Owner` (JSON - apenas os campos a serem atualizados)
        ```json
        {
            "fullName": "Caio Vinicius Rodrigues",
            "driversLicense": "12345678901"
        }
        ```
    *   **Resposta**: `200 OK` com o `Owner` atualizado.

*   **`DELETE /owner/{id}`**
    *   **Descrição**: Exclui um proprietário pelo ID.
    *   **Método**: `DELETE`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com `true` se a exclusão for bem-sucedida.

#### 7.2. Endpoints de Veículos (`/vehicle`)

Base: `/vehicle`

*   **`POST /vehicle`**
    *   **Descrição**: Cria um novo veículo. O campo `country` é preenchido automaticamente com base na marca (`brand`).
    *   **Método**: `POST`
    *   **Corpo da Requisição**: `Vehicle` (JSON)
        ```json
        {
            "model": "HB20",
            "brand": "FIAT",
            "plaque": "ABC-1234",
            "type": "CAR"
        }
        ```
    *   **Resposta**: `200 OK` com o `Vehicle` criado.

*   **`GET /vehicle`**
    *   **Descrição**: Lista todos os veículos registrados.
    *   **Método**: `GET`
    *   **Resposta**: `200 OK` com uma lista de `Vehicle`.

*   **`GET /vehicle/{id}`**
    *   **Descrição**: Busca um veículo pelo ID.
    *   **Método**: `GET`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com o `Vehicle` encontrado.

*   **`PUT /vehicle/{id}`**
    *   **Descrição**: Atualiza um veículo existente. O campo `country` é atualizado se a `brand` for alterada.
    *   **Método**: `PUT`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Corpo da Requisição**: `Vehicle` (JSON - apenas os campos a serem atualizados)
        ```json
        {
            "model": "HB20s",
            "plaque": "XYZ-5678"
        }
        ```
    *   **Resposta**: `200 OK` com o `Vehicle` atualizado.

*   **`DELETE /vehicle/{id}`**
    *   **Descrição**: Exclui um veículo pelo ID.
    *   **Método**: `DELETE`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com `true` se a exclusão for bem-sucedida.

#### 7.3. Endpoints de Propriedade de Veículos (`/vehicle-ownership`)

Base: `/vehicle-ownership`

*   **`POST /vehicle-ownership`**
    *   **Descrição**: Associa um veículo a um proprietário. Requer que `Owner` e `Vehicle` já existam.
    *   **Método**: `POST`
    *   **Corpo da Requisição**: `VehicleOwnership` (JSON)
        ```json
        {
            "owner": {
                "id": 1
            },
            "vehicle": {
                "id": 1
            }
        }
        ```
    *   **Resposta**: `200 OK` com o `VehicleOwnership` criado.

*   **`GET /vehicle-ownership`**
    *   **Descrição**: Lista todas as associações de propriedade de veículos.
    *   **Método**: `GET`
    *   **Resposta**: `200 OK` com uma lista de `VehicleOwnership`.

*   **`GET /vehicle-ownership/{id}`**
    *   **Descrição**: Busca uma associação de propriedade pelo ID.
    *   **Método**: `GET`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com o `VehicleOwnership` encontrado.

*   **`PUT /vehicle-ownership/{id}`**
    *   **Descrição**: Atualiza uma associação de propriedade existente.
    *   **Método**: `PUT`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Corpo da Requisição**: `VehicleOwnership` (JSON - apenas os IDs a serem atualizados)
        ```json
        {
            "owner": {
                "id": 2
            },
            "vehicle": {
                "id": 3
            }
        }
        ```
    *   **Resposta**: `200 OK` com o `VehicleOwnership` atualizado.

*   **`DELETE /vehicle-ownership/{id}`**
    *   **Descrição**: Exclui uma associação de propriedade pelo ID.
    *   **Método**: `DELETE`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com `true` se a exclusão for bem-sucedida.

#### 7.4. Endpoints de Vagas de Estacionamento (`/parking-space`)

Base: `/parking-space`

*   **`POST /parking-space`**
    *   **Descrição**: Cria uma nova vaga de estacionamento. O `price` e `occupied` são preenchidos automaticamente.
    *   **Método**: `POST`
    *   **Corpo da Requisição**: `ParkingSpace` (JSON)
        ```json
        {
            "type": "CAR"
        }
        ```
    *   **Resposta**: `200 OK` com o `ParkingSpace` criado.

*   **`GET /parking-space`**
    *   **Descrição**: Lista todas as vagas de estacionamento.
    *   **Método**: `GET`
    *   **Resposta**: `200 OK` com uma lista de `ParkingSpace`.

*   **`GET /parking-space/{id}`**
    *   **Descrição**: Busca uma vaga de estacionamento pelo ID.
    *   **Método**: `GET`
    *   **Parâmetros de Path**: `id` (Integer)
    *   **Resposta**: `200 OK` com o `ParkingSpace` encontrado.

*   **`PUT /parking-space/{id}`**
    *   **Descrição**: Atualiza uma vaga de estacionamento existente. O `price` é atualizado se o `type` for alterado.
    *   **Método**: `PUT`
    *   **Parâmetros de Path**: `id` (Integer)
    *   **Corpo da Requisição**: `ParkingSpace` (JSON - apenas os campos a serem atualizados)
        ```json
        {
            "type": "MOTORCYCLE",
            "occupied": true
        }
        ```
    *   **Resposta**: `200 OK` com o `ParkingSpace` atualizado.

*   **`DELETE /parking-space/{id}`**
    *   **Descrição**: Exclui uma vaga de estacionamento pelo ID.
    *   **Método**: `DELETE`
    *   **Parâmetros de Path**: `id` (Integer)
    *   **Resposta**: `200 OK` com `true` se a exclusão for bem-sucedida.

#### 7.5. Endpoints de Aluguéis de Vagas (`/parkingspace-rental`)

Base: `/parkingspace-rental`

*   **`POST /parkingspace-rental`**
    *   **Descrição**: Registra um novo aluguel de vaga. O `startRenting` é preenchido automaticamente. Verifica se a vaga está livre, se a vaga e o veículo são compatíveis, e se o veículo já não está em uma locação.
    *   **Método**: `POST`
    *   **Corpo da Requisição**: `ParkingSpaceRental` (JSON)
        ```json
        {
            "parkingSpace": {
                "id": 1
            },
            "vehicleOwnership": {
                "id": 1
            }
        }
        ```
    *   **Resposta**: `200 OK` com o `ParkingSpaceRental` registrado.

*   **`GET /parkingspace-rental`**
    *   **Descrição**: Lista todos os registros de aluguéis de vagas.
    *   **Método**: `GET`
    *   **Resposta**: `200 OK` com uma lista de `ParkingSpaceRental`.

*   **`GET /parkingspace-rental/{id}`**
    *   **Descrição**: Busca um registro de aluguel pelo ID.
    *   **Método**: `GET`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com o `ParkingSpaceRental` encontrado.

*   **`PUT /parkingspace-rental/{id}`**
    *   **Descrição**: Atualiza um registro de aluguel existente (sem finalizar o aluguel). Permite alterar a vaga ou a propriedade do veículo associada, verificando a compatibilidade.
    *   **Método**: `PUT`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Corpo da Requisição**: `ParkingSpaceRental` (JSON - apenas os campos a serem atualizados, ex: `parkingSpace.id`, `vehicleOwnership.id`)
        ```json
        {
            "parkingSpace": {
                "id": 2
            },
            "vehicleOwnership": {
                "id": 1
            }
        }
        ```
    *   **Resposta**: `200 OK` com o `ParkingSpaceRental` atualizado.

*   **`PUT /parkingspace-rental/end-rental/{id}`**
    *   **Descrição**: Finaliza um aluguel de vaga. O `endRenting` é preenchido com a data e hora atuais, a vaga é desocupada e o `totalRent` é calculado com base na duração e no preço da vaga.
    *   **Método**: `PUT`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com o `ParkingSpaceRental` atualizado.

*   **`DELETE /parkingspace-rental/{id}`**
    *   **Descrição**: Exclui um registro de aluguel pelo ID. Libera a vaga associada se ela estiver ocupada por este aluguel.
    *   **Método**: `DELETE`
    *   **Parâmetros de Path**: `id` (Long)
    *   **Resposta**: `200 OK` com `true` se a exclusão for bem-sucedida.

### 8. Configurações Essenciais (`application.properties`)

*   **Nome da Aplicação**:
    *   `spring.application.name=carpark`
*   **Console H2 Database**:
    *   `spring.h2.console.enabled=true`
    *   `spring.h2.console.path=/h2-console`
*   **Configurações de Banco de Dados H2**:
    *   `spring.datasource.driver-class-name=org.h2.Driver`
    *   `spring.datasource.url=jdbc:h2:file:/data/db/carparkdb` (Banco de dados persistido em arquivo)
    *   `spring.datasource.username=sa`
    *   `spring.datasource.password=` (sem senha)
*   **JPA (atualização do banco de dados)**:
    *   `spring.jpa.hibernate.ddl-auto=update`

### 9. Executando Localmente

A aplicação utiliza Maven para construção e pode ser executada diretamente como uma aplicação Spring Boot.

*   **Pré-requisitos**:
    *   Java 21 instalado
    *   Maven instalado
*   **Passos para Execução**:
    *   Navegue até a raiz do projeto (onde está o arquivo `pom.xml`).
    *   Execute a aplicação usando o comando Maven: `mvn spring-boot:run`
    *   A aplicação estará disponível em `http://localhost:8080` (porta padrão do Spring Boot).
    *   O console do H2 Database estará acessível em `http://localhost:8080/h2-console`.