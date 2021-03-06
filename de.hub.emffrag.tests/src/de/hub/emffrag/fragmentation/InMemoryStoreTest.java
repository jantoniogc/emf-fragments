package de.hub.emffrag.fragmentation;

import org.eclipse.emf.common.util.URI;

import de.hub.emffrag.datastore.IBaseDataStore;
import de.hub.emffrag.datastore.InMemoryDataStore;

public class InMemoryStoreTest extends AbstractDataStoreTest {
	
	protected InMemoryDataStore inMemoryDataStore;

	@Override
	protected IBaseDataStore createBaseDataStore() {
		inMemoryDataStore = new InMemoryDataStore(false);
		return inMemoryDataStore;
	}

	@Override
	protected URI createURI() {
		return URI.createURI("memory://localhost/test");
	}
}
