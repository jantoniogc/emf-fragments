package de.hub.emffrag.mongodb;

import de.hub.emffrag.datastore.IBaseDataStore;
import de.hub.emffrag.mongodb.MongoDBDataStore;
import de.hub.emffrag.tests.FragmentationCacheTests;

@SuppressWarnings("all")
public class MongodbFragmentationCacheTests extends FragmentationCacheTests {
  @Override
  public IBaseDataStore createDataStore() {
    return new MongoDBDataStore("localhost", "testmodel", true);
  }
}
