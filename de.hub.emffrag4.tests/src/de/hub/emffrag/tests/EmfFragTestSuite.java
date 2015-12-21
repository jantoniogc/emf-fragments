package de.hub.emffrag.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	FStoreObjectTests.class,
	EStoreTests.class,
	SerializationTests.class,
	FragmentationTests.class
})
public class EmfFragTestSuite {

}
