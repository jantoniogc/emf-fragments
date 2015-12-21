package de.hub.emffrag;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EReference;

public class FURI extends FAbstractStoreObject {

	private static final int SEGMENT = 1 << 0;
	private static final int FRAGMENT = 1 << 1;
	private static final int DB = 1 << 2;
	private static final int HOST = 1 << 3;
	private static final int SCHEME = 1 << 4;

	private static final int FIELD_MASK = SEGMENT | FRAGMENT | DB | HOST | SCHEME;
	
	public FURI() {
		setField(SEGMENT, new ArrayList<Integer>());
	}
	
	@Override
	protected int firstField() {
		return SEGMENT;
	}

	@Override
	protected int lastField() {
		return SCHEME;
	}

	@Override
	protected int fieldMask() {
		return FIELD_MASK;
	}
	
	public int fragment() {
		return (int) getField(FRAGMENT);
	}
	
	public void setFragment(int id) {
		setField(FRAGMENT, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> segment() {
		return (List<Integer>) getField(SEGMENT);
	}
	
	@SuppressWarnings("unchecked")
	public void addFeatureToSegment(int featureID, int slotIndex) {
		List<Integer> segment = (List<Integer>) getField(SEGMENT);
 		
		segment.add(featureID);
		segment.add(slotIndex);
	}
	
	public void addFeatureToSegment(EReference feature, int slotIndex) {
		addFeatureToSegment(feature.getFeatureID(), slotIndex);
	}
}
