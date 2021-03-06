/**
 * Copyright 2012 Markus Scheidgen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hub.emffrag.testmodels.testmodel.frag.meta;

import de.hub.emffrag.testmodels.testmodel.TestContainmentIndex;
import de.hub.emffrag.testmodels.testmodel.TestObject;
import de.hub.emffrag.testmodels.testmodel.TestObjectIndex;
import de.hub.emffrag.testmodels.testmodel.TestObjectWithIndexes;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see de.hub.emffrag.testmodels.testmodel.frag.meta.TestModelPackage
 * @generated
 */
public interface TestModelFactory extends de.hub.emffrag.testmodels.testmodel.TestModelFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TestModelFactory eINSTANCE = de.hub.emffrag.testmodels.testmodel.frag.impl.TestModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Test Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Test Object</em>'.
	 * @generated
	 */
	TestObject createTestObject();

	/**
	 * Returns a new object of class '<em>Test Object Index</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Test Object Index</em>'.
	 * @generated
	 */
	TestObjectIndex createTestObjectIndex();

	/**
	 * Returns a new object of class '<em>Test Containment Index</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Test Containment Index</em>'.
	 * @generated
	 */
	TestContainmentIndex createTestContainmentIndex();

	/**
	 * Returns a new object of class '<em>Test Object With Indexes</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Test Object With Indexes</em>'.
	 * @generated
	 */
	TestObjectWithIndexes createTestObjectWithIndexes();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	TestModelPackage getTestModelPackage();

} //TestModelFactory
