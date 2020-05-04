import java.util.*;
import org.bson.Document;

import com.example.pentaho.cda.MongoDatasource;

public class Test {

	public static void main(String[] args) throws Exception {
		
    	// Set the databaseName and collectionName
		String DATABASE_NAME = "ssp";
		String COLLECTION_NAME = "customer";
		String JNDI_NAME = "java:/comp/env/mongodb/MongoClient";
		
		Map columns = new LinkedHashMap();
		
		// Set the output fields from the query - use only String.class
		columns.put("_id", String.class);
		columns.put("custkey", String.class);
		columns.put("name", String.class);
		columns.put("address", String.class);
		columns.put("city", String.class);
		columns.put("nation_name", String.class);
		columns.put("region_name", String.class);
		columns.put("phone", String.class);
		columns.put("mktsegment", String.class);

		MongoDatasource mongoDS = new MongoDatasource(JNDI_NAME, DATABASE_NAME, true);

		// MongoDB Pipeline
		String query = "[ {'$project': { '_id': 1, 'custkey': 1, 'name': 1, 'address': 1, 'city': 1, 'nation_name': 1, 'region_name': 1, 'phone': 1, 'mktsegment': 1 } } ]"; 
		
		mongoDS.runPipeline(columns, COLLECTION_NAME, query);
	}

}
