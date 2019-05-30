# Camel Shared File Locker

This project shows the usage of custom Shared File Lock bean to start/stop routes based on  shared lock file.

Use case: Run single route with multiple nodes using same NFS.

## Getting Started

### Prerequisites

```
Java 8
```

## Running sample application

Run Main class to load and run Camel context. 

Located in:
```
src/main/java/eu/mpelikan/camel/spring/Main.java
```

### Application outputs
You can see application instantiating two SharedFileLock beans targeting same file in project folder.
One of them will catch the log on bean startup. At first, routes are stopped but after after 5 seconds one of the routes 
is started up by controlbus start command.

```
[ #2 - scheduler://myscheduler2] StartStopProcessor             INFO  first route is active: false
[ #2 - scheduler://myscheduler2] StartStopProcessor             INFO  second route is active: false
...
[d #1 - scheduler://myscheduler] ControlBusProducer             INFO  ControlBus task done [start route first-route] with result -> void
...
[ #2 - scheduler://myscheduler2] StartStopProcessor             INFO  first route is active: true
[ #2 - scheduler://myscheduler2] StartStopProcessor             INFO  second route is active: false
```

