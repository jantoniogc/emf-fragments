package de.hub.emffrag.mongodb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.hub.emffrag.fragmentation.FragmentsBaseTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
		// fragmentation tests based on XMI-fragments
		MongodbFragmentationTests.class, MongodbCrossReferenceTests.class, MongodbCacheTests.class,
		// fragmentation tests based on binary fragments
		MongodbBinaryFragmentBasicFragmentationTests.class, MongodbBinaryFragmentCrossReferenceTests.class, 
		MongodbBinaryFragmentCacheTests.class })
public class MongodbFragmentsBaseTestSuite extends FragmentsBaseTestSuite {
	// nothing
}
