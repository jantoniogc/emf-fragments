package de.hub.emffrag.fragmentation;

import junit.framework.Assert;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;

import de.hub.emffrag.testmodels.frag.testmodel.TestModelPackage;

/**
 * EMF-Fragments needs a customized version of the used client meta-models as
 * meta-models for internal reflective object. These meta-models are generated
 * from client meta-models via {@link ReflectiveMetaModelRegistry}. This class
 * contains tests for the generated meta-models. Since EMF-Fragments heavily
 * depends on EMF internals, we think these tests are necessary to detect
 * possible slight changes in EMF semantics.
 */
public class ReflectiveMetaModelTests extends AbstractReflectiveModelTests {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testContainmentReferences() {
		EList contents = (EList) object1.eGet(metaModel.getTestObject_FragmentedContents());
		contents.add(object2);

		Assert.assertEquals(1, contents.size());
		Assert.assertEquals(object2, contents.get(0));
	}

	@Test
	public void testIsSet() {
		object1.eIsSet(metaModel.getTestObject_RegularContents());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testNonContainmentReferences() {
		EReference testObject_crossReferences = metaModel.getTestObject_CrossReferences();
		EList crossReferences = (EList) object1.eGet(testObject_crossReferences);
		crossReferences.add(object2);

		Assert.assertEquals(1, crossReferences.size());
		Assert.assertEquals(object2, crossReferences.get(0));
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testContainmentProxyResolution() {
		EReference fragmentedContents = metaModel.getTestObject_FragmentedContents();
		EList contents = (EList) object1.eGet(fragmentedContents);
		contents.add(object2);
		object2.eSet(metaModel.getTestObject_Name(), "testValue");

		Resource[] resources = createResourceSet(dataStore, metaModel, 2, false);
		resources[0].getContents().add(object1);
		resources[1].getContents().add(object2);
		saveResources(resources);

		resources = createResourceSet(dataStore, metaModel, 1, true);
		object1 = (FInternalObjectImpl) resources[0].getContents().get(0);
		contents = (EList) object1.eGet(fragmentedContents);

		Assert.assertEquals(1, contents.size());
		object2 = (FInternalObjectImpl) contents.get(0);
		Assert.assertEquals(object2, contents.get(0));
		Assert.assertTrue(resources[0] != object2.eResource());
		Assert.assertTrue(resources[0] == object1.eResource());
		Assert.assertFalse(object2.eIsProxy());
		Assert.assertEquals(object2.eGet(metaModel.getTestObject_Name()), "testValue");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testProxyResolution() {
		EReference testObject_crossReferences = metaModel.getTestObject_CrossReferences();
		EList crossReferences = (EList) object1.eGet(testObject_crossReferences);
		crossReferences.add(object2);
		object2.eSet(metaModel.getTestObject_Name(), "testValue");

		Resource[] resources = createResourceSet(dataStore, metaModel, 2, false);
		resources[0].getContents().add(object1);
		resources[1].getContents().add(object2);
		saveResources(resources);

		resources = createResourceSet(dataStore, metaModel, 1, true);
		object1 = (FInternalObjectImpl) resources[0].getContents().get(0);
		crossReferences = (EList) object1.eGet(testObject_crossReferences);
		object2 = (FInternalObjectImpl) crossReferences.get(0);

		Assert.assertEquals(1, crossReferences.size());
		Assert.assertEquals(object2, crossReferences.get(0));
		Assert.assertTrue(resources[0] != object2.eResource());
		Assert.assertTrue(resources[0] == object1.eResource());
		Assert.assertFalse(object2.eIsProxy());
		Assert.assertEquals("testValue", object2.eGet(metaModel.getTestObject_Name()));
	}

	@Test
	public void testReflectiveMetaModelRegistryTest() {
		TestModelPackage one = ReflectiveMetaModelRegistry.instance.registerRegularMetaModel(TestModelPackage.eINSTANCE);
		TestModelPackage two = ReflectiveMetaModelRegistry.instance.registerRegularMetaModel(TestModelPackage.eINSTANCE);

		Assert.assertEquals(one, two);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMoveContents() {
		((EList) object1.eGet(metaModel.getTestObject_FragmentedContents())).add(object2);
		Resource[] resources = createResourceSet(dataStore, metaModel, 2, false);
		resources[0].getContents().add(object1);
		resources[1].getContents().add(object2);
		saveResources(resources);

		((EList) object1.eGet(metaModel.getTestObject_RegularContents())).add(object2);
		saveResources(resources);
	}

	/**
	 * This test case checks if URIs for regulary contained objects are formed
	 * correctly when the reflective meta-model is used. It some point this did
	 * not work correctly within fragmented models.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRegularContainmentURIs() {
		Resource[] resources = createResourceSet(dataStore, metaModel, 1, false);

		resources[0].getContents().add(object1);
		EList eList = (EList) object1.eGet(metaModel.getTestObject_RegularContents());
		eList.add(object2);
		Assert.assertTrue(resources[0].getContents().contains(object1));
		Assert.assertTrue(eList.contains(object2));
		boolean isContainedInResource = false;
		TreeIterator<EObject> allContents = resources[0].getAllContents();
		while (allContents.hasNext()) {
			isContainedInResource |= allContents.next().equals(object2);
		}
		Assert.assertTrue(isContainedInResource);
		Assert.assertEquals("//@regularContents.0", resources[0].getURIFragment(object2));
	}
}
