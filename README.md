# NTTDATA Bootcamp Tech Girls Power - Project III

## Project description

This project focuses on developing a **microservices system** to manage the **transaction history** in the banking context, using **Spring Boot**, **MongoDB**, **Spring Webflux**, and **Lombok**. The system is designed to record and manage **Deposit**, **Withdrawal**, and **Transfer** transactions, allowing the querying of transaction history.

## System features

The system handles the following functionalities:

1. **Record Transactions**:
    - **Deposits**: POST `/transactions/deposit`
    - **Withdrawals**: POST `/transactions/withdrawal`
    - **Transfers**: POST `/transactions/transferTo`

2. **Query Transaction History**:
    - **GET** `/transactions/history`: Retrieve transaction history of customers.

## System requirements

- **Spring Boot** to create microservices.
- **MongoDB** as a non-relational database to store transaction history.
- **Spring Webflux** for reactive programming.
- **Lombok** to reduce boilerplate code.
- **Use of Stream APIs** for efficient collection handling.

## Business rules

1. **Transaction Validations**:
    - Deposits and withdrawals are applied to specific accounts.
    - Transfers require both the destination account and amount to be specified.
    - Withdrawals or transfers cannot occur if the available balance is insufficient.

## System architecture

The system is based on **microservices** and follows a reactive, decoupled architecture where each microservice is responsible for a specific part of the banking process.

**Component Diagram**:  
The system consists of the following microservices:
- **Transaction Service**: Manages banking transactions.
- **Account Service**: Manages customer bank accounts.

![Component Diagram](https://raw.githubusercontent.com/avsoto/NTTDATA-Transaction/refs/heads/main/diagram/componentDiagram.jpg)

**Sequence Diagram**:  
Each microservice interacts reactively and asynchronously to process banking transactions.

![Sequence Diagram](https://raw.githubusercontent.com/avsoto/NTTDATA-Transaction/refs/heads/main/diagram/transferSecuence.jpg)

## Implementation and Connection with other microservices

The **Transaction Management** microservice is integrated with other microservices:

- **Account Service**: Connected via **RestTemplate** for balance validation in deposits, withdrawals, and transfers.  
  Repository: [NTTDATA-AccountMS](https://github.com/avsoto/NTTDATA-AccountMS)

- **Customer Service**: Connected via **WebClient** to access customer and account information.  
  Repository: [NTTDATA-CustomerMS](https://github.com/avsoto/NTTDATA-CustomerMS)

## Technical requirements

1. **MongoDB**: Used to store transactions. Each transaction is represented as a document in MongoDB.
2. **Spring Webflux**: Used to handle reactive programming logic, making the system asynchronous and non-blocking.
3. **Lombok**: Used to reduce boilerplate code, making the implementation cleaner and more maintainable.

## Usage and functionality verification

The system does not have a graphical user interface. The functionalities can be verified using **Postman** by sending HTTP requests to the following endpoints:

- **POST** `/transactions/deposit`: To register a deposit.
- **POST** `/transactions/withdrawal`: To register a withdrawal.
- **POST** `/transactions/transferTo`: To register a transfer.
- **GET** `/transactions/history`: To retrieve the transaction history.

## Deliverables

- **Source Code**: All the source code for the project is available in this repository.
- **OpenAPI Documentation**: The API documentation is provided using the **Contract First** approach with OpenAPI.
- **Diagrams**: Sequence and component diagrams are included, detailing the architecture and communication flow between microservices.

## Execution instructions

1. **Clone the repository**:

   ```bash
   git clone https://github.com/your-user/NTTDATA-TransactionMS.git
   cd NTTDATA-TransactionMS
   ```

2. **Install the dependencies**:

   If using Maven:

   ```bash
   mvn install
   ```

3. **Run the project**:

   To run the microservice, simply execute:

   ```bash
   mvn spring-boot:run
   ```

4. **Testing**:
    - Use Postman to test the provided endpoints.

## Swagger/OpenAPI

The Swagger/OpenAPI documentation offers a comprehensive overview of each endpoint, detailing the parameters, request bodies, and response formats.

1. To access this documentation, ensure that the service is running locally: http://localhost:8082.
2. Once confirmed, open a web browser and navigate to the following URL: http://localhost:8082/docs/swagger-ui.html.
