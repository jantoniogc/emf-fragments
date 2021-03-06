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
package de.hub.emffrag.model.emffrag;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Statistics</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.hub.emffrag.model.emffrag.Statistics#getAccessed <em>Accessed</em>}</li>
 *   <li>{@link de.hub.emffrag.model.emffrag.Statistics#getLoaded <em>Loaded</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.hub.emffrag.model.emffrag.EmfFragPackage#getStatistics()
 * @model
 * @generated
 */
public interface Statistics extends Extension {
	/**
	 * Returns the value of the '<em><b>Accessed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Accessed</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Accessed</em>' attribute.
	 * @see #setAccessed(long)
	 * @see de.hub.emffrag.model.emffrag.EmfFragPackage#getStatistics_Accessed()
	 * @model transient="true" volatile="true"
	 * @generated
	 */
	long getAccessed();

	/**
	 * Sets the value of the '{@link de.hub.emffrag.model.emffrag.Statistics#getAccessed <em>Accessed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Accessed</em>' attribute.
	 * @see #getAccessed()
	 * @generated
	 */
	void setAccessed(long value);

	/**
	 * Returns the value of the '<em><b>Loaded</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Loaded</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Loaded</em>' attribute.
	 * @see #setLoaded(long)
	 * @see de.hub.emffrag.model.emffrag.EmfFragPackage#getStatistics_Loaded()
	 * @model transient="true" volatile="true"
	 * @generated
	 */
	long getLoaded();

	/**
	 * Sets the value of the '{@link de.hub.emffrag.model.emffrag.Statistics#getLoaded <em>Loaded</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Loaded</em>' attribute.
	 * @see #getLoaded()
	 * @generated
	 */
	void setLoaded(long value);

} // Statistics
