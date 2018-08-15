# ticket-service
A simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance seatTracker.

### Assumptions
1. The project is written from an exercise perspective and may not have covered all the testing scenario's or lack the test coverage. Also, the libraries/frameworks used in live applications are out of scope of this exercise.
2. In case of error or exception or validation failure the program will either return null or print error message on the console or do nothing, as the spec for the service signatures does n't specify such requirements.
3. Seat ID is unique for a seat and can be leveraged for multiple floors or special seats for future requirements. 
4. The seats availability and other operations may not reflect the current state as the expiration thread scheduler is scheduled for 10 ms delays. If the availability is critical, seat availability check should be a service and should expire the seats before holding the seats. The seat hold expiration thread scheduler is configurable and has been defaulted to run with 10 milliseconds fixed time delays.
5. To minimize the complexity, the best seat selection criteria sort the seats by the row number and column number in ascending order. All the seats for a customer may not be reserved together or in a row.
6. The application using the ticketing service would need to provide the seat tracker or the number of rows/columns, hold expiration time etc. Also, the service assumes the each row would have same number of columns as depicted in the spec. The maximum number of seats supported in the seat tracker are 2,147,483,647 (inclusive).
7. The seat hold ID uniqueness will not preserved after a program restart. Using the UUID to maintain the uniqueness is out of scope for now.
8. Multiple customers can reserve the seats with the same email ID and email address validation is out of scope. The combination of seat hold id and email address or just the seat hold ID will be unique.
9. At last, the service is expected to be thread safe however the multithreading testing scenarios are not covered due to limited time spent on the exercise. The performance testing is not observed and service has scope for improving the performance.

#### Setup

Java and Git are prerequisite before installing:
```
git clone https://github.com/ysanjeev84/ticket-service
cd ticket-service
```

#### Testing

mvn clean install
