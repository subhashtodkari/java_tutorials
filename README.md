# java_tutorials
My Java tutorials

1. Producer - Consumer problem - com.subhash.concurrency.ProducerConsumer

2. AWS S3 tutorial - com.subhash.aws.S3
    - project is converted to gradle (5.x)
    - gradle dependencies added for AWS SDK (2.x)
    - aws account created
    - user created in aws account
    - config files created locally as below
        - ~/.aws/credentials - file containing default aws_access_key_id and aws_secret_access_key
        - ~/.aws/config - file containing default region
    - APIs added to S3 bucket create, list and delete operations

3. Kafka Tutorial - https://kafka.apache.org/quickstart
    All commands are for windows OS
    start cmd.exe @cmd /k "command" is used to execute command in new command prompt
    1. Download and extract binaries
    2. cd to kafka home directory
    3. start zookeeper
        start cmd.exe @cmd /k "bin\windows\zookeeper-server-start.bat config\zookeeper.properties"
    4. start kafka cluster with single node or server
        start cmd.exe @cmd /k "bin\windows\kafka-server-start.bat config\server.properties"
        for multiple nodes, say {N} nodes
        replicate config\server.properties {N} times config\server-{N}.properties
        edit below properties in each file
            broker.id={N}
            listeners=PLAINTEXT://:{port}           e.g 9094
            log.dirs=/tmp/kafka-logs-{N}
         then execute below {N} commands
         start cmd.exe @cmd /k "bin\windows\kafka-server-start.bat config\server-{N}.properties"
    5. Topic commands
        create topic        - bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 partitions 1 --topic test
                            - replication-factor = 1 will fail if node localhost:9092 goes down
        create topic        - bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9094 --replication-factor 3 partitions 3 --topic my-repl-topic-1
                            - replication-factor = 3 will work even if node localhost:9092 goes down, other nodes will take care of message delivery
        list topics         - bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
        describe topics     - bin\windows\kafka-topics.bat --describe --bootstrap-server localhost:9094
        describe topic      - bin\windows\kafka-topics.bat --describe --bootstrap-server localhost:9094 --topic my-repl-topic-1
    6. Client commands
        Producer        - start cmd @cmd /k "bin\windows\kafka-console-producer.bat --bootstrap-server localhost:9093 --topic test"
        Consumer        - start cmd @cmd /k "bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9094 --topic test --from-beginning"
        Producer        - start cmd @cmd /k "bin\windows\kafka-console-producer.bat --bootstrap-server localhost:9093 --topic test"
        Consumer        - start cmd @cmd /k "bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9094 --topic test --from-beginning"
    7. File Source and Sink
        start cmd @cmd /k "bin\windows\connect-standalone.bat config\connect-standalone.properties config\connect-file-source.properties config\connect-file-sink.properties"
        No need to create topic connect-test
        We can consume messages from connect-test in parallel
        start cmd @cmd /k "bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9094 --topic connect-test --from-beginning"

