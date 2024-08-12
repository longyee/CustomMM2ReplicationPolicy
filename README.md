# Custom Kafka MirrorMaker 2.0 (MM2) Replication Policy for Renaming Topics

Currently, there is no configurable option available in connect-mirror-maker.properties to support renaming a topic during replication using MirrorMake 2 in Apache Kafka. This code adds the "replication.policy.topics.rename" option to connect-mirror-maker.properties and allow renaming topics during replication.  It Implements the custom [**IdentityReplicationPolicy**](https://github.com/apache/kafka/blob/3.8.0/connect/mirror-client/src/main/java/org/apache/kafka/connect/mirror/IdentityReplicationPolicy.java) and provides a custom replication policy JAR that overrides the topicSource method to change how MirrorMake 2 names topics in the replicated cluster.

Prerequisites
=====

Make sure JDK and Maven are installed:
```
$ java --version
openjdk 17.0.12 2024-07-16
OpenJDK Runtime Environment (build 17.0.12+7-Debian-2deb12u1)
OpenJDK 64-Bit Server VM (build 17.0.12+7-Debian-2deb12u1, mixed mode, sharing)

$ mvn --version
Apache Maven 3.8.7
Maven home: /usr/share/maven
Java version: 17.0.12, vendor: Debian, runtime: /usr/lib/jvm/java-17-openjdk-amd64
```

Build and setup
=====
Build the project with Maven:
```
$ git clone https://github.com/longyee/CustomMM2ReplicationPolicy.git
$ cd CustomMM2ReplicationPolicy
$ mvn clean install
```

Go to parent directory, download kafka, unzip it and copy the .jar to the Kafka libs directory:
```
$ cd ..
$ wget https://dlcdn.apache.org/kafka/3.8.0/kafka_2.13-3.8.0.tgz
$ tar zxvf kafka_2.13-3.8.0.tgz

$ cp CustomMM2ReplicationPolicy/target/CustomMM2ReplicationPolicy-1.0-SNAPSHOT.jar kafka_2.13-3.8.0/libs
```

Usage
=====

Open/edit connect-mirror-maker.properties and add replication.policy.class and replication.policy.topics.rename two parameters:

```
$ vi kafka_2.13-3.8.0/config/connect-mirror-maker.properties
...
# customize as needed
# replication.policy.separator = _

#replication.policy.separator =
#source.cluster.alias =
#target.cluster.alias =

# sync.topic.acls.enabled = false
# emit.heartbeats.interval.seconds = 5

replication.policy.class=com.deimos.kafka.CustomMM2ReplicationPolicy
replication.policy.topics.rename=event01,deimos-event01;event03,deimos-event03
...
```
For example, for Cluster A -> Cluster B replication, event01 is the topic running in Cluster A, will be renamed to new-event01 in Cluster B during replication.

Specify only those topics you want to rename in replication.policy.topics.rename, for the rest of topics the default replication policy applies.


