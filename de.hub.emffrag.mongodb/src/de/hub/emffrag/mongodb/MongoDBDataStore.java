package de.hub.emffrag.mongodb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import de.hub.emffrag.datastore.DataStore;

public class MongoDBDataStore extends DataStore {

	private static final String VALUE = "value";
	private static final String KEY = "key";
	private DBCollection collection;
	private DB db;
	
	public MongoDBDataStore(String host, String dataStoreId) {
		this(host, dataStoreId, false);
	}

	public MongoDBDataStore(String host, String dataStoreId, boolean dropFirst) {
		super("mongodb", host, dataStoreId);
		String hostName = null;
		int hostPort = -1;
		
		String[] hostParts = host.split(":");
		if (hostParts.length == 1) {
			hostName = hostParts[0];
		} else if (hostParts.length == 2) {
			hostName = hostParts[0];
			try {
				hostPort = Integer.parseInt(hostParts[1]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid host format: " + host);
			}
		} else {
			throw new IllegalArgumentException("Invalid host format: " + host);			
		}

		db = EmfFragMongoDBActivator.instance.getDataBase(hostName, hostPort);
		collection = db.getCollection(dataStoreId);		
		if (dropFirst) {
			collection.dropIndexes();
			collection.drop();
			collection = db.getCollection(dataStoreId);	
		}
		collection.ensureIndex(KEY);				
	}

	@Override
	synchronized public byte[] ceiling(byte[] key) {
		String keyString = new String(key);
		keyString.getBytes();
		DBObject result = collection.findOne(new BasicDBObject(KEY, new BasicDBObject("$gte", keyString)), new BasicDBObject(KEY, ""), new BasicDBObject(KEY, 1));
		if (result != null) {
			return ((String)result.get(KEY)).getBytes();
		} else {
			return null;
		}
	}

	@Override
	synchronized public byte[] floor(byte[] key) {
		DBObject result = collection.findOne(new BasicDBObject(KEY, new BasicDBObject("$lte", new String(key))), new BasicDBObject(KEY, ""), new BasicDBObject(KEY, -1));
		if (result != null) {
			return ((String)result.get(KEY)).getBytes();
		} else {
			return null;
		}		
	}

	@Override
	synchronized public InputStream openInputStream(byte[] key) {
		DBObject result = collection.findOne(new BasicDBObject(KEY, new String(key)));
		if (result != null) {
			return new ByteArrayInputStream((byte[])result.get(VALUE));
		} else {
			return null;
		}		
	}

	@Override
	synchronized public OutputStream openOutputStream(final byte[] key) {
		return new ByteArrayOutputStream(256) {
			@Override
			public void close() throws IOException {
				super.close();
				String keyString = new String(key);
				collection.update(new BasicDBObject(KEY, keyString), new BasicDBObject(KEY, keyString).append(VALUE, toByteArray()), true, false);
			}
		};
	}

	@Override
	synchronized public boolean check(byte[] key) {
		DBCursor cursor = collection.find(new BasicDBObject(KEY, new String(key)));
		try {
			return !cursor.hasNext();
		} finally {
			cursor.close();
		}		
	}

	@Override
	synchronized public boolean ckeckAndCreate(byte[] key) {
		String keyString = new String(key);
		DBObject result = collection.findAndModify(
				new BasicDBObject(KEY, keyString),								// query 
				null, 															// field
				null, 															// sort
				false, 															// remove
				new BasicDBObject(KEY, keyString).append(VALUE, new byte[]{}), 	// update
				false, 															// return new
				true);															// upsert
		return result == null;				
	}

	@Override
	synchronized public void delete(byte[] key) {
		collection.findAndRemove(new BasicDBObject(KEY, new String(key)));
	}

	@Override
	synchronized public void drop() {
		collection.drop();
	}

	
}
