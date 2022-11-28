## Casper Event Store

This project streams CSPR events emitted from active nodes, fires them into a Kafka cluster, then processes/transforms them down stream into various data stores.

#### Technical Stack

- Java 17
- Kafka
- Kubernetes
- Postgres
- Mongo

#### Sub Projects

The project is split into numerous sub projects, each controlled via a gradle build file.

Some projects are shared classes, some are deployed jars.

The deployed projects are:

- **Audit API** - Provides REST endpoints to replay events that have already been streamed from the nodes
- **Audit Consumer** - Processes events in Kafka topics into a Mongo datastore, allows replaying and DR
- **Producer** - Connects to CSPR node(s) via the Java SDK, wraps the events with metadata and sends to a Kafka topic
- **Store API** - Provides REST endpoints to monitor processed events
- **Store Consumer** - Processes Kafka topics into a Postgres relational db ready for any downstream use

#### GitFlow

The project uses GitHub Actions to deploy changes from main to the Kubernetes cluster.

Each deployable sub project has it's own workflow file in 

.github/workflows

And each deployable sub project also has it's own /deploy folder which contains specific YAML files

#### Documentation

The project is extensively documented using the blog site, Medium.

- [Part 1](https://medium.com/casperblockchain/casper-event-store-pt-1-ae4bc87aecd9) - Highlights the problem space and proposes a technical solution
- [Part 2](https://medium.com/casperblockchain/casper-kafka-event-store-pt-2-d6f1ed37d964) - Discusses how we set up the Kubernetes cluster with the Kafka ensemble

More blog parts are being added as the project matures.

#### Linked Repositories

- [Java SDK](https://github.com/casper-network/casper-java-sdk) - Used to connect to cspr nodes, deserialise events and provide a steam
- [Kubernetes Cluster](https://github.com/stormeye2000/cspr-kafka-cluster) - Collection of YAML files with detailed documentation to build the cluster











