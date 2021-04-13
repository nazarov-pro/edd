# Kafka event processing (publishing and consuming) #

Approaches for handling events:

![Approaches to handle events](./data/asserts/images/kafka-publishment.png)

## Approach v1 ##

This approach is the simplest way to implement kafka, one of big disadvantage of this approach is that,
1.1 and 1.2 steps is not isolated.

## Approach v2 ##

This approach requires another table(s) for saving failed events to send again to kafka.

## Approach v3 ##

This approach requires another table for events, after the change event will be saved in one transaction and this 
transaction will be sended via Kafka connect or custom app in both cases it requires another batch with interval.

## Approach v4 ##

This approach stands with CDC (Change Data Capture) principal (for implementing such flow database(RDBMS/NoSql) 
should support streaming changes (it can be enabled or can be done via plugin), and CDC tool (Debezium is the most 
popular open source tool)). When handling an event user has to save data and also event which has to be sent 
through the kafka. Debezium will automatically get that change and publish them to specified topic.

You can use the following commands below for testing (debezium/kafka/portgres) combination.

```shell
#sets up docker network
make set-up
#building app, making db migrations, starting apps and attaching logs
make krm-up
#sends request to debezium to connect database and stream data
make krm-add-connector-request
#it sends a request and save event database
make krm-send-email-request
#for stopping all apps gracefully
make krm-down
```

## Approach v5 ##

This approach will take more development effort, all event tales should have lock guaranteed column which identifies 
this event is locked and will be sent, and status column *SENT*, *PENDING*. After saving all data with event in the 
table (you have to set event locking guarantee column 10 seconds after now, and status is PENDING) and call 
asynchronous kafka template send method, if it is successful then you can remove the event otherwise dont do anything(note that 
in this case kafka producer also have time out less than 10 seconds).

There is also a bach which sends failed events again, it will edit and fetch events from database which is in 
PENDING status and lock guarantee time is lower than now, it will update them to now + 10 seconds and try to send so 
after some retry if it is unsuccessful it will mark that event as *ERROR* status.

Credits:

- [Reliable Messaging Without Distributed Transactions](https://vimeo.com/111998645)
- [Exactly Once Processing in Kafka with Java](https://www.baeldung.com/kafka-exactly-once)
- [Spring boot kafka with transactions](https://medium.com/@milo.felipe/spring-boot-kafka-transactions-97a2f653b60a)
- [Kafka transactional integration](https://medium.com/dev-genius/transactional-integration-kafka-with-database-7eb5fc270bdc)
