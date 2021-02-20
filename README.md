This project will help to thest the performance of a functions exposed from the java library (SDK) and deployed in a Restful Server.

## Technologies
* Java 1.8+
* Gradle 6.1.1
* Springboot 2.2.4


Setup
-----

1) Using Intellij IDE
* Open the project in intellij
* Right click on "java-sdk-benchmarking -> src -> main -> java -> com.horizon.testserver -> Application"
* Run 'Application.main()'
* It will listen to port 8080

2) Using terminal
* $ ./gradlew clean build
* $ java -jar -Dspring.profiles.active=test build/libs/java-sdk-benchmarking-0.0.1-SNAPSHOT.jar
* It will listen to port 8090

How to test?
------------
Use any of the tools of your choice for the client (postman / curl / http client plugin in Intellij / ...)

1) Start benchmarking for SampleTask
```
POST http://localhost:8080/benchmarking/start
{
    "threadPoolSize": 4,
    "warmUpThreads": 1,
    "threads": 3,
    "iterationParam":[
        {
            "name": "SampleTask",
            "iteration":20,
            "warmUpIteration": 1,
            "data": "test data"
        }
    ]
}
```
2) Get the benchmarking result of the previous run
```
GET http://localhost:8080/benchmarking/result
```

Result for the above requestwill be similar to below:
```
{
    "threads": 3,
    "warmUpThreads": 1,
    "iterationData": [
        {
            "name": "com.horizon.testserver.concurrency.tasks.SampleTask",
            "iteration": 20,
            "warmUpIteration": 1
        }
    ],
    "passCnt": 60,
    "failCnt": 0,
    "executionTime": 58032.0,
    "elapsedTime": 54984.426,
    "tp": 0.0010912181,
    "mean": 916.40704,
    "sd": 601.1808,
    "min": 6.473503,
    "p25": 498.39926,
    "p50": 822.28674,
    "p75": 1446.2377,
    "p90": 1927.5947,
    "p95": 1972.276,
    "p99": 1994.074,
    "p99_9": 1994.074,
    "p99_99": 1994.074,
    "max": 1994.074
}
```
