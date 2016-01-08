package de.hub.emffrag.datastore.tests;

import de.hub.emffrag.datastore.IBulkInsertExtension;

public class InMemoryStoreWithScanAndBulkInsertTest extends InMemoryStoreWithScanTest {

	@Override
	protected IBulkInsertExtension createBulkInsertExtension() {
		return inMemoryDataStore;
	}

}