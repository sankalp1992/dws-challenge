# Money Transfer Service

This is a Spring Boot-based REST service that provides basic functionality for managing accounts and transferring money between them. It ensures thread-safe transfers, validation of inputs, and proper error handling.

## Features

- **Create Account**: Add new accounts with an initial balance.
- **Transfer Money**: Transfer funds between accounts ensuring:
    - Only positive amounts can be transferred.
    - No overdrafts allowed; accounts cannot have negative balances.
    - Notifications are sent to both the sender and the receiver.
- **Thread Safety**: The transfer functionality is thread-safe and supports concurrent transfers.
- **Error Handling**: Custom exceptions and error handling with meaningful responses.

## Prerequisites

- **Java 11** or higher
- **Gradle** or **Maven** (whichever you are using)
- **Spring Boot 2.6.6**
- **Postman** or **cURL** for API testing (optional)

## Project Setup

1. Clone the repository:

    ```bash
    git clone https://github.com/sankalp1992/dws-challenge.git
    cd dws-challenge
    ```

2. **Build the project**:

   If using Gradle:

    ```bash
    ./gradlew clean build
    ```

3. **Run the application**:

    ```bash
    ./gradlew bootRun
    ```


The application should now be running on `http://localhost:18080`.

## API Endpoints

### 1. Create Account

**URL**: `/v1/accounts`  
**Method**: `POST`  
**Request Body**:

```json
{
  "accountId": "Id-123",
  "balance": 1000
}
```
### 2. Transfer Money Between Accounts

**URL**: `/v1/accounts/transfer`  
**Method**: `POST`  
**Request Body**:

```json
{
  "accountFromId": "Id-123",
  "accountToId": "Id-456",
  "amount": 200
}
```

### 3. Get Account Information

**URL**: `/v1/accounts/{accountId}`  
**Method**: `GET`  
**Response Body**:
```json
{
  "accountId": "Id-123",
  "balance": 1000
}
```