# Coding Assignment for Adevinta

This document follows [RFC2119](https://www.ietf.org/rfc/rfc2119.txt) notation.

## Instructions

You are the only developer of a small startup that manages messages.
Your application receives messages via a REST API, stores them in memory and then make them available through a REST API.

You already wrote a maven/spring based application with two basic endpoints:

* [POST] `/messages` which adds a new message to the list of messages
* [GET]  `/messages` which returns the content of the list of messages

The JSON structure used for posting messages **MUST NOT** change.

To run locally the application you have to build it:

```shell
mvn package
```
and then execute the jar:
```shell
java -jar target/messages-assignment-1.0-SNAPSHOT.jar
```

You can run the tests with:
```shell
mvn test
```



### Task #1

You noticed that sometimes the `GET /messages` endpoint doesn't return all the messages that were previously added: you **SHOULD** verify that this can happen and you **MUST** fix it.




### Task #2
The evil marketer of the startup needs some statistics information about the messages to fulfill some obscure goal; you **MUST** add to this application a new endpoint called `/stats` returning some statistics about the last posted messages; in detail, the marketer needs:
  * the number of messages posted in the last minute

  * the average (mean) length of unique words in all the messages received in last minute

  * the number of occurrences of each word contained in the messages posted in the last minute; the word separator is any not alphanumeric character. For example, if in the last minute the application received the two messages "test message1" and "test message2", you **MUST** count:

    | word     | count |
    | -------- | ----- |
    | test     | 2     |
    | message1 | 1     |
    | message2 | 1     |



The JSON payload returned by the endpoint **MUST** be like this:

```json
{
  "posted_messages": 2,
  "average_length": 6.67,
  "occurrences": [
    {"word": "test", "count": 2},
    {"word": "message1", "count": 1},
    {"word": "message2", "count": 1}
  ]
}
```

You **MAY** optimize the procedure for time complexity or you **MAY** prefer code readibility.



### Task #3

This application has to connect to a Machine Learning external HTTP service that will crunch the messages to obtain some interesting data. You **MUST** add to the application a process that every day at 23:00:00Z connects to the external HTTP service passing a JSON containing all the messages received until that moment; all the messages received during the transmission of data **MUST** be sent the day after, with the new execution of the process; after having sent the messages to the external service, the process **MUST** delete all the sent messages. Take into account that the external service can be down, can crash while receiving, and can fail for whatever reason; you **MUST** assume either an `at least once` or an `exactly once` logic from the external service.

During the execution of this task and for the next minute the results of the statistics endpoint (the one in Task #2) **MAY** not return accurate result. The GET `/messages` endpoint **MUST** return all the messages until the execution of the process starts and from that moment on it **MUST** return only the newly arrived messages.

We have implemented for you a mock external service listening at http://localhost:8181/mock_external_service which just returns a 200 HTTP status code. If you already have something binding that port on your local machine, you **MAY** change the port number in `MessagesApplication.java:16`.

You can create the JSON structure you prefer for passing the data to the external process.


## Application constraints / notes:
* you can safely assume that your users will send no more than 100.000 messages every day (it's still a startup!)

* the application **SHOULD NOT** store messages in a persistent way, you can just leave them in memory

* you **MAY** use any external library you want

* you **SHOULD** write a production ready, tested, and well-modularised code

* you **MAY** choose one of these JVM languages in your solution: java, scala or kotlin


## Candidate notes / assumptions / comments
// You **MAY** write your assumptions and comments here
Assumption:
* Simplicity is more important than other things. Tried to have a small code. I didn't add any exceptions and @ControllerAdvice.
* I **COULD** change the already written class, Except the JSON input of adding message(POST).
* I **COULD NOT** change the version of Spring Boot. 
* There is one instance of the application running at the moment.
* We can use CompletableFuture to send  some pages at the same time. Due to keeping the code simple, I did not use it.
* I thought the team was familiar with Streams. I use them in the code for task #2.
* For task #1 after adding synchronized the problem solve, it does not appear in the test.
* Because we don't have more than one task at the same time. There was no need to use ThreadPoolTaskScheduler for Task#.
* There is no need for Swagger for documenting API
* There is no need for Jacoco test report and code coverage issue
* Occurrences **SHOULD** sort reverse order. Max is at top of our list
* The max message we have are Integer.MAX_VALUE page.
* Can use Caffeine as a local cache for saving partitioned messages in the future.
* Used **HTTPie** for command line request instead of curl
* The test converage are: 100% Class, 92% Method ,and 80% Line.
* For prod profile you can run it from Maven directly using the Spring Boot Maven plugin or without profile, it will use with default
```bash 
mvn spring-boot:run -Dspring-boot.run.profiles=prod 
```

### Task #1
Found a concurrency problem for this task. Added as synchronized to setter and getter of MessagesContainer (mutable data object).
There is another option that use CopyOnWriteArrayList(). Because CopyOnWriteArrayList is thread safe as well. 
I preferred to use the synchronized on my method because in messaging systems there are more write operations than reading operations in ArrayList.
Because in our project, we will read them when wants to send them to the external server, or when we call /stats endpoint. It means we have more write rather than read.
The CopyOnWriteArrayList should be used when there are more read operations than write operations in ArrayList.
I defined a Singleton for saving the MessagesContainer object. Used enum for creating singleton class. 
* Note: 1- when multiple threads share mutable data, each thread that reads or writes the data must perform synchronization. Effective Java Item 78
      2- Enforce non instantiability with a private constructor or enum. Effective Java Item 3
* For checking multi thread issue,
    1- Uncomment this line from MessageControllerTest class.
       .andExpect(content().string(not(totalRequestNo+"")));
    2- Comment this line
       .andExpect(content().string(equalTo(totalRequestNo+"")));
    3- Remove **synchronized** from MessageContainer class.

To obtain statistics, please send the following **HTTPie** request:  
```bash
http POST localhost:8080/messages <<<'{ "message": "test message1"}'
http POST localhost:8080/messages <<<'{ "message": "test message2"}'
```

### Task #2
//You MAY optimize the procedure for time complexity or you MAY prefer code readibility.
It depends on 2 different things:: 
    1- How much message I have and how much process/RAM is important for project.
    2- Used Streams because are small and code readability. But it depends on the team. If the team knows Streams, it is a good code.
       If the team doesn't know about it, I **SHOULD** not use Streams to calculate them.

* [GET]  `/stats` which returns the statistics of the list of messages base on the condition mentioned.

To obtain statistics, please send the following **HTTPie** request: 
```bash
http GET localhost:8080/stats
```
The JSON payload returned by the endpoint is:

```json
{
  "average_length": 6.67,
  "occurrences": [
    {
      "count": 2,
      "word": "test"
    },
    {
      "count": 1,
      "word": "message2"
    },
    {
      "count": 1,
      "word": "message1"
    }
  ],
  "posted_messages": 2
}
```

### Task #3
I partitioned data and sent them in different parts.
Partitioned data by page and send the data page by page to the third-party(Machine Learning external HTTP service) using pagination.
If the external server goes down, we can send that part of the data that was not sent to the external server again.
Defined WeakHashMap to save unsent data between 2 scheduled method run. (Caffeine also could be use)
When the scheduler starts, first of all, it added copies of new data to WeakHashMap in order to meet the following needs:
  - after having sent the messages to the external service, the process MUST delete all the sent messages.
  - all the messages received during the transmission of data MUST be sent the day after;
  - transmission of data takes time base on network our status of our third party if some pages left after retrying to send, 
    we can send them in next scheduler round.
  - Added all the messages received until 23:00:00Z to WeakHashMap not after this moment;

Used a WeakHashMap as a temporary repository for storing partitioned data before send to external server. 
Used WeakHashMap because object will be garbage collected in the next GC cycle. There are some options like Caffeine as local cache.

A simple retry pattern was implemented to attempt to send data to the external server if it failed to get them.
It tries to send them again after a while until our maxRetry counter reaches zero.
If some messages are still left, in the next scheduler round, with new messages we will try to send all of them.

## Questions?

If you have any questions please ask by sending an email to `gp.gt.shield@adevinta.com`

## Finished?
Then, please push your code to the repository and send an email to `gp.gt.shield@adevinta.com` to let us know you're done.

Have fun!
