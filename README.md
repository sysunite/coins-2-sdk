# COINS 2 SDK
This is the official COINS 2 software development kit for Java and .NET.

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
PumpImpl pump = new PumpImpl(store, defaultPerson);

// Create value
WattsImpl value = new WattsImpl(store, defaultPerson);

// Create property
PowerConsumptionImpl property = new PowerConsumptionImpl(store, defaultPerson);
property.addObjectValueImpl(value);
pump.addHasObjectProperty_PowerConsumptionImpl(property);

// Find the saved elements
Iterator<PumpImpl> searchResult = store.findCoinsObjects(PumpImpl.class, PumpImpl.CLASS_URI);
while(searchResult.hasNext()) {
  log.info(searchResult.next());
}
```

