package de.hub.emffrag;

import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;

import de.hub.emffrag.internal.FStoreObject;
import de.hub.emffrag.internal.FStoreObjectImpl;


public class FObjectImpl extends EStoreEObjectImpl implements FObject {

	private FStoreObject fStoreObject = null;

	protected FObjectImpl() {
		eSetStore(FStore.fINSTANCE);
	}

	@Override
	public FStoreObject fStoreObject() {
		if (fStoreObject == null) {
			if (eClass() == null) {
				throw new IllegalStateException();
			}
			fStoreObject = new FStoreObjectImpl();
			fStoreObject.fSetClass(eClass());
			FStore.fINSTANCE.onNewObject(this);
		}
		return fStoreObject;
	}

	protected void fSetStoreObject(FStoreObject fStoreObject) {
		this.fStoreObject = fStoreObject;
	}

	protected boolean eIsCaching() {
		return false;
	}
}
