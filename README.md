# OnTop
Back-end service for OnTop's job interview Business case

## Stack 🛠️
- maven
- java 11
- spring-boot 2.7.10
- spring jdbc
- spring web
- H2 database
- spring test
- JUnit 5

## Build 🔧
To compile, build and install the build result of the project, run this command:
```bash
$ mvn clean install
```

## Run
To run the project:
```bash
$ mvn spring-boot:run
```

Local environment runs upon:
<http://localhost:8080>

The endpoint to make a withdraw is:
<http://localhost:8080/transfers/api/v1/withdraws>
with a json body like this:
```json
{
  "destination_bank_account_id": 123,
  "user_id": 1000,
  "amount": 2300.00
}
```

To check H2 database instanced in memory use:
<http://localhost:8080/h2-console>

## Unit Tests
To run Unit test's suites:
```bash
$ mvn clean test
```


