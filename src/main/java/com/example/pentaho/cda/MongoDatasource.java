package com.example.pentaho.cda;

import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import java.util.*;
import javax.naming.InitialContext;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDatasource {

	String jndi_name;
	String database_name;
	
	MongoClient conn;
	MongoDatabase database;
	
	boolean debug;
	
	public MongoDatasource(String jndi_name, String database_name, boolean debug) throws Exception {

		this.jndi_name = jndi_name;
		this.database_name = database_name;
		
		this.conn = getJndiConnection();
		
		this.database = this.conn.getDatabase(this.database_name);
		
		this.debug = debug;
	
	}

	// Create CDA table model
	private TypedTableModel createCdaModel(Map columns) {
		
		TypedTableModel model = new TypedTableModel();
		
		for (Object key : columns.keySet()) {
			
			String columnName = (String) key;
			Class columnType = (Class) columns.get(key);
			
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

		conn = (MongoClient) cxt.lookup(this.jndi_name);

		if ( conn == null ) {
		   throw new Exception("Data source not found!");
		}
		
		return conn;
		
	}

	public TypedTableModel runPipeline(Map columns, String collection_name, String query) throws JSONException {

		TypedTableModel model = createCdaModel(columns);

		List<Document> pipeline = convertQuery2Pipeline(query);

		// Execute MongoDB Pipeline
		MongoCollection<Document> collection = this.database.getCollection(collection_name);

		@SuppressWarnings("unchecked")
		MongoCursor<Document> results = collection.aggregate(pipeline).allowDiskUse(false).iterator();

		// Loop each returned document
		while (results.hasNext()) {

			List<Object> row = new ArrayList<Object>();

			Document document = (Document) results.next();

			if (this.debug) {
				System.out.println("----------------------");
			}
			
			for (Object key : columns.keySet()) {
				
				String columnName = (String) key;

				String rValue = "";
				if (document.get(columnName) instanceof Document) {
					rValue = new JSONObject(((Document) document.get(columnName)).toJson()).toString();
				} else {
					rValue = document.get(columnName).toString();
				}
				
				Object rClass = columns.get(columnName);

				if (this.debug) {
					System.out.println("[DEBUG] MongoDatasource: Result... " + columnName + ":" + rValue);
				}
				
				row.add(new String(rValue));
			}

			model.addRow(row.toArray());
			
		}
		
		return model;		
		
	}
	
	private List<Document> convertQuery2Pipeline(String query) throws JSONException {
		
		JSONArray array = new JSONArray(query);

		List<Document> pipeline = new ArrayList<>();
		
        for (int i = 0; i < array.length(); i++) {
        	JSONObject item = array.getJSONObject(i);
        	Document document = Document.parse(item.toString());
        	pipeline.add(document);
        }
		
		return pipeline;
	}

	public MongoDatabase getDb() {
		return this.database;
	}

}
