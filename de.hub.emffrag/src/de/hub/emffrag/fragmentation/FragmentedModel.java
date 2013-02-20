package de.hub.emffrag.fragmentation;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;

import com.google.common.base.Throwables;

import de.hub.emffrag.datastore.DataIndex;
import de.hub.emffrag.datastore.DataStore;
import de.hub.emffrag.datastore.DataStoreURIHandler;
import de.hub.emffrag.datastore.LongKeyType;
import de.hub.emffrag.fragmentation.UserObjectsCache.UserObjectsCacheListener;
import de.hub.emffrag.model.emffrag.EmfFragPackage;

public class FragmentedModel {

	private final static XMLParserPoolImpl xmlParserPool = new XMLParserPoolImpl(true);
	private final static Map<Object, Object> options = new HashMap<Object, Object>();
	static {
		options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.FALSE);
		options.put(XMLResource.OPTION_ENCODING, "UTF-8");
		options.put(XMLResource.OPTION_USE_PARSER_POOL, xmlParserPool);
		HashMap<String, Boolean> parserFeatures = new HashMap<String, Boolean>();
		parserFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		options.put(XMLResource.OPTION_PARSER_FEATURES, parserFeatures);
	}

	private final ResourceSet resourceSet;
	private Fragment rootFragment;
	private final FragmentCache fragmentCache;
	private final DataStore dataStore;
	private final DataIndex<Long> fragmentIndex;
	private final DataIndex<Long> indexesIndex;
	private final DataIndex<Long> crossReferenceIndex;
	private final URI rootFragmentKeyURI;
	private final Statistics statistics = new Statistics();

	public class Statistics {
		private int creates = 0;
		private int loads = 0;
		private int unloads = 0;

		public int getCreates() {
			return creates;
		}

		public int getLoads() {
			return loads;
		}

		public int getUnloads() {
			return unloads;
		}
	}

	class FragmentCache {

		class CacheState implements UserObjectsCacheListener {
			private final Fragment fragment;
			private boolean cached = false;
			private int useKey;

			public CacheState(Fragment fragment) {
				super();
				this.fragment = fragment;
			}

			@Override
			public void handleUsed() {
				useKey = currentUseKey++;
				purgeUnreferencedFragments();
			}

			@Override
			public void handleReferenced() {
				if (cached) {
					remove(this);
				}
			}

			@Override
			public void handleUnReferenced() {
				if (!cached) {
					add(this);
				}
				cached = true;
				markDirty();
			}
		}

		private final int cacheSize;
		private int currentUseKey = 0;
		private boolean isDirty = false;
		private final TreeMap<CacheState, CacheState> cacheContents = new TreeMap<CacheState, CacheState>(
				new Comparator<CacheState>() {
					@Override
					public int compare(CacheState o1, CacheState o2) {
						return Integer.valueOf(o2.useKey).compareTo(o1.useKey);
					}
				});

		public FragmentCache(int cacheSize) {
			super();
			this.cacheSize = cacheSize;
		}

		synchronized void remove(CacheState state) {
			cacheContents.remove(state);
		}

		synchronized void add(CacheState state) {
			cacheContents.put(state, state);
		}

		synchronized void registerFragment(Fragment fragment) {
			fragment.getUserObjectsCache().setListener(new CacheState(fragment));
		}

		void markDirty() {
			isDirty = true;
		}

		synchronized void purgeUnreferencedFragments() {
			if (isDirty) {
				int size = cacheContents.size();
				if (size > 1.5f * cacheSize) {
					int numberOfFragmentsToRemove = Math.max(0, size - cacheSize);
					for (int i = 0; i < numberOfFragmentsToRemove; i++) {
						CacheState cacheStateToRemove = cacheContents.pollFirstEntry().getValue();
						unloadFragment(cacheStateToRemove.fragment);
					}
				}
			}
		}
	}

	private void unloadFragment(Fragment fragment) {
		if (!fragment.isLoaded()) {
			return;
		}

		statistics.unloads++;
		try {
			fragment.save(options);
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		fragment.unload();
		resourceSet.getResources().remove(fragment);
	}

	/**
	 * The {@link ResourceSet}s used to store a {@link FragmentedModel} need to
	 * match specific characteristics. These are: It has to create
	 * {@link Fragment}s and {@link FInternalObjectImpl}s on loading resources;
	 * it has to use a {@link DataStoreURIHandler}; it needs to refer to the
	 * reflective version of meta models; it needs specific performance
	 * configuration loading and saving models. This methods creates a
	 * {@link ResourceSet} with the listed characteristics.
	 */
	private ResourceSet createAndConfigureAResourceSet(DataStore dataStore, EPackage... metaModels) {
		ResourceSet resourceSet = new ResourceSetImpl() {
			@Override
			public EObject getEObject(URI uri, boolean loadOnDemand) {
				Resource resource = getResource(uri.trimFragment(), loadOnDemand);
				if (resource != null) {
					if (uri.fragment() == null) {
						// The URI must reference a cross reference entry
						String objectURIString = crossReferenceIndex.get(crossReferenceIndex.getKeyFromURI(uri));
						EObject result = super.getEObject(URI.createURI(objectURIString), true);
						return result;
					} else {
						return resource.getEObject(uri.fragment());
					}
				} else {
					return null;
				}
			}

			@Override
			protected void demandLoad(Resource resource) throws IOException {
				super.demandLoad(resource);
				statistics.loads++;
			}

		};
		resourceSet.getPackageRegistry().put(EmfFragPackage.eINSTANCE.getNsURI(),
				ReflectiveMetaModelRegistry.instance.registerRegularMetaModel(EmfFragPackage.eINSTANCE));
		for (EPackage metaModel : metaModels) {
			EPackage reflectiveMetaModel = ReflectiveMetaModelRegistry.instance.registerRegularMetaModel(metaModel);
			resourceSet.getPackageRegistry().put(metaModel.getNsURI(), reflectiveMetaModel);
		}
		resourceSet.getURIConverter().getURIHandlers().add(0, new DataStoreURIHandler(dataStore));
		resourceSet.getLoadOptions().putAll(options);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put(dataStore.getProtocol(), new XMIResourceFactoryImpl() {
					@Override
					public Resource createResource(URI uri) {
						Fragment fragment = createFragment(uri, FragmentedModel.this);
						fragmentCache.registerFragment(fragment);
						return fragment;
					}
				});

		return resourceSet;
	}

	protected Fragment createFragment(URI uri, FragmentedModel model) {
		return new XMIFragmentImpl(uri, model);
	}

	public FragmentedModel(DataStore dataStore, URI rootFragmentKeyURI, EPackage... metaModel) {
		this(dataStore, rootFragmentKeyURI, -1, metaModel);
	}

	public FragmentedModel(DataStore dataStore, URI rootFragmentKeyURI, int cacheSize, EPackage... metaModel) {
		this.dataStore = dataStore;
		if (cacheSize == -1) {
			cacheSize = 100;
		}
		if (cacheSize < 1) {
			throw new IllegalArgumentException("A zero fragment cache is not allowed. Try a larger cache size.");
		}
		fragmentCache = new FragmentCache(cacheSize);

		this.fragmentIndex = new DataIndex<Long>(dataStore, "f", LongKeyType.instance);
		this.indexesIndex = new DataIndex<Long>(dataStore, "i", LongKeyType.instance);
		this.crossReferenceIndex = new DataIndex<Long>(dataStore, "c", LongKeyType.instance);

		resourceSet = createAndConfigureAResourceSet(dataStore, metaModel);

		if (rootFragmentKeyURI == null) {
			rootFragment = addFragment(null, null, null, null);
			this.rootFragmentKeyURI = rootFragment.getURI();
		} else {
			this.rootFragmentKeyURI = rootFragmentKeyURI;
			rootFragment = (Fragment) resourceSet.getResource(this.rootFragmentKeyURI, true);
		}
	}

	public String createNextIndexPrefix() {
		return "i_" + indexesIndex.add();
	}

	public EList<EObject> getRootContents() {
		EList<EObject> contents = rootFragment.getContents();
		EList<EObject> result = new BasicEList<EObject>(contents.size());
		for (EObject internalObject : contents) {
			result.add(rootFragment.getUserObjectsCache().getUserObject((FInternalObjectImpl) internalObject));
		}
		return result;
	}

	public void addContent(EObject eObject) {
		FInternalObjectImpl internalObject = ((FObjectImpl) eObject).internalObject();
		if (internalObject.eResource() == null) {
			UserObjectsCache.newUserObjectsCache.removeCachedUserObject(internalObject);
			rootFragment.getContents().add(internalObject);
			rootFragment.getUserObjectsCache().addUserObjectToCache(internalObject, (FObjectImpl) eObject);
		} else {
			// TODO (multi fragmentation models)
			throw new UnsupportedOperationException();
		}
	}

	protected ResourceSet getResourceSet() {
		return resourceSet;
	}

	/**
	 * Creates a new fragment and adds it to the model. That means it creates a
	 * resource (fragment), adds it to the resource set, and creates an
	 * appropriate entry in the persistence.
	 * 
	 * @param fragmentRoot
	 *            The object that is going to be the root of the new fragment.
	 *            It can be a part of an existing fragment or something from the
	 *            realm of new objects.
	 * @param fragmentRootUserObject
	 *            The user object of the fragment root. Can be null.
	 * @param containingObject
	 *            The object that contains the root. It can be null, if this
	 *            method is called for the root fragment.
	 * @param containmentFeature
	 *            Can be null (see containingObject)
	 */
	public Fragment addFragment(FInternalObjectImpl fragmentRoot, FObjectImpl fragmentRootUserObject,
			InternalEObject containingObject, EStructuralFeature containmentFeature) {
		Fragment newFragment = (Fragment) resourceSet.createResource(createNewFragmentURI());

		if (fragmentRoot != null) {
			Resource oldResource = fragmentRoot.eResource();
			Fragment oldFragment = null;
			if (oldResource != null && oldResource instanceof Fragment) {
				oldFragment = (Fragment) oldResource;
			}

			newFragment.getContents().add(fragmentRoot);
			if (fragmentRootUserObject != null) {
				newFragment.getUserObjectsCache().addUserObjectToCache(fragmentRoot, fragmentRootUserObject);
			}
			if (oldFragment != null) {
				oldFragment.getUserObjectsCache().removeCachedUserObject(fragmentRoot);
			}
		}

		statistics.creates++;
		return newFragment;
	}

	/**
	 * Creates a new fragment in persistence.
	 * 
	 * @return The URI for a new fragment.
	 */
	private URI createNewFragmentURI() {
		return fragmentIndex.getURI(fragmentIndex.add());
	}

	/**
	 * Removes the fragment of the given root. Removes the resource and the
	 * corresponding entry in persistence. This must not remove the fragment
	 * immediately, since the fragmentRoot is still attached to it.
	 * 
	 * @param fObjectImpl
	 *            The root of the fragment to delete.
	 */
	protected void removeFragment(FInternalObjectImpl fragmentRoot) {
		Fragment oldFragment = fragmentRoot.getFragment();
		try {
			oldFragment.delete(options);
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	public void save() {
		for (Resource resource : resourceSet.getResources()) {
			try {
				resource.save(options);
			} catch (IOException e) {
				Throwables.propagate(e);
			}
		}
	}

	public URI getRootFragmentURI() {
		return rootFragmentKeyURI;
	}

	/**
	 * Changes (or add) the entry for a cross referenced object.
	 * 
	 * @param crossReferenceURI
	 *            An extrinsic that identifies an object within a resource, as
	 *            long as it is already given (null if the object is cross
	 *            referenced for the first time).
	 * @param object
	 *            The object that is cross references.
	 * @return The extrinsic ID that the cross referenced object should be
	 *         identified with from now on.
	 */
	public String updateCrossReference(String extrinsicID, FInternalObjectImpl object) {
		if (extrinsicID == null) {
			extrinsicID = Long.toString(crossReferenceIndex.add());
		}

		Resource resource = object.eResource();
		URI objectURI = resource.getURI().appendFragment(resource.getURIFragment(object));
		crossReferenceIndex.set(Long.parseLong(extrinsicID), objectURI.toString());
		return extrinsicID;
	}

	/**
	 * Creates and returns a URI that identified the database entry that
	 * contains an URI that identified the object with the given extrinsicID.
	 * 
	 * @param extrinsicID
	 *            The extrinsicID that identified the object.
	 * @return The created URI that points to the DB entry that contains the URI
	 *         that points to the object.
	 */
	public URI getURIForExtrinsicCrossReferencedObjectID(String extrinsicID) {
		return crossReferenceIndex.getURI(Long.parseLong(extrinsicID));
	}

	/**
	 * Resolves the given URI and returns the object identified by the given
	 * URI. The URI has to be a cross reference URI. This means it points
	 * towards a DB entry that contains the actual object identifying URI. These
	 * URIs are usually created via
	 * {@link #getURIForExtrinsicCrossReferencedObjectID(String)}.
	 * 
	 * @param uri
	 *            The cross reference URI.
	 * @return The resolved object.
	 */
	public EObject resolveCrossReferenceURI(URI uri) {
		String objectURIString = crossReferenceIndex.get(crossReferenceIndex.getKeyFromURI(uri));
		EObject result = resourceSet.getEObject(URI.createURI(objectURIString), true);
		return result;
	}

	/**
	 * Extracts the extrinsic ID from the given URI. The given URI should be a
	 * cross reference URI. This means a URI that depicts a entry in a DB that
	 * contains actual URIs.
	 * 
	 * @param extrinsicURI The URI that contains the extrinsic ID as last segment.
	 * @return The extrinsic ID.
	 */
	public String getExtrinsicID(URI extrinsicURI) {
		return Long.toString(crossReferenceIndex.getKeyFromURI(extrinsicURI));
	}

	/**
	 * Tries to remove unnecessary fragments from main memory.
	 */
	void purgeCache() {
		fragmentCache.purgeUnreferencedFragments();
	}

	int numberOfLoadedFragments() {
		return getResourceSet().getResources().size();
	}

	Fragment getFragment(URI fragmentURI) {
		return (Fragment) getResourceSet().getResource(fragmentURI, false);
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public DataStore getDataStore() {
		return dataStore;
	}

}
