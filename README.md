# COINS-2.0-SDK
This is the official COINS 2.0 software development kit from the Dutch RWS.

###Main components
![Main components](/doc/img/summary.jpg?raw=true "Main components")

###Code example
```java
// Init the coins elements
CoinsParty defaultPerson = new CoinsParty();
Configuration conf = new InMemDefault(defaultPerson);

CoinsContainer container = new CoinsContainer(conf);
CoinsModel store = container.getModel();

// Create object
NCS0025091Impl object = new NCS0025091Impl(store, defaultPerson);

// Create value
NCS0026889Impl value = new NCS0026889Impl(store, defaultPerson);

// Create property
NCR0149407Impl property = new NCR0149407Impl(store, defaultPerson);
property.addObjectValueImpl(value);
object.addHasObjectProperty_NCR0149407Impl(property);

// Find the saved elements
System.out.println("Search results:");
Iterator<NCS0025091Impl> searchResult = store.findCoinsObjects(NCS0025091Impl.class, NCS0025091Impl.CLASS_URI);
while(searchResult.hasNext()) {
  System.out.println(searchResult.next());
}
```

