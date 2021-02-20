
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

