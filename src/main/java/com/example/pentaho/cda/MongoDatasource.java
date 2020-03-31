package com.example.pentaho.cda;

import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import java.util.*;
import javax.naming.InitialContext;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDatasource {

	String jndi_name;
	String database_name;
	String collection_name;
	Map columns;
	MongoClient ds;
	TypedTableModel model;
	private boolean debug;
	
	public MongoDatasource(String jndi_name, String database_name, String collection_name, Map columns, boolean debug) throws Exception {

		this.jndi_name = jndi_name;
		this.database_name = database_name;
		this.collection_name = collection_name;
		this.columns = columns;
		
		this.ds = getJndiConnection();
		this.model = createCdaModel();
		
		this.debug = debug;
	
	}

	// Create CDA table model
	private TypedTableModel createCdaModel() {
		
		TypedTableModel model = new TypedTableModel();
		
		for (Object key : this.columns.keySet()) {
			
			String columnName = (String) key;
			Class columnType = (Class) this.columns.get(key);
			
			model.addColumn(columnName, columnType);
			
			if (this.debug) {
				System.out.println("[DEBUG] MongoDatasource: Adding to model: " + columnName + "/" + columnType.getName());
			}
			
		}
		
		return model;
		
	}

	@SuppressWarnings("unused")
	private MongoClient getJndiConnection() throws Exception {
		
		InitialContext cxt = new InitialContext();
		
		if ( cxt == null ) {
		   throw new Exception("Uh oh -- no context!");
		}

		ds = (MongoClient) cxt.lookup(jndi_name);

		if ( ds == null ) {
		   throw new Exception("Data source not found!");
		}
		
		return ds;
		
	}

	public TypedTableModel run(Object[] query) {

		List pipeline = Arrays.asList(query);
		
		// Execute MongoDB Query
		MongoDatabase database = ds.getDatabase(this.database_name);
		MongoCollection<Document> collection = database.getCollection(this.collection_name);

		MongoCursor<Document> results = collection.aggregate(pipeline).allowDiskUse(false).iterator();

		List<String> row = new ArrayList<String>();

		// Loop each returned document
		while (results.hasNext()) {

			Document document = (Document) results.next();
			
			for (Object key : this.columns.keySet()) {
				
				String columnName = (String) key;
				
				String rValue = (document.get(columnName) == null) ? "" : document.get(columnName).toString();

				if (this.debug) {
					System.out.println("[DEBUG] MongoDatasource: Result... " + columnName + ":" + rValue);
				}
				
				row.add(rValue);
			}

			model.addRow(row);

		}
		
		return model;		
		
	}

}
