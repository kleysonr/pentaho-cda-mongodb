import java.util.*;
import org.bson.Document;

import com.example.pentaho.cda.MongoDatasource;

public class Test {

	public static void main(String[] args) throws Exception {
		
    	// Set the databaseName and collectionName
		String DATABASE_NAME = "ssp";
		String COLLECTION_NAME = "customer";
		String JNDI_NAME = "java:/comp/env/mongodb/MongoClient";
		
		Map columns = new HashMap();
		
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

		MongoDatasource mongoDS = new MongoDatasource(JNDI_NAME, DATABASE_NAME, COLLECTION_NAME, columns, true);

		// MongoDB Pipeline
		Object[] query = new Object[] {
				new Document().append("$project",
					new Document()
						.append("_id", 1.0)
						.append("custkey", 1.0)
						.append("name", 1.0)
						.append("address", 1.0)
						.append("city", 1.0)
						.append("nation_name", 1.0)
						.append("region_name", 1.0)
						.append("phone", 1.0)
						.append("mktsegment", 1.0)
				),
				new Document().append("$project",
					new Document()
						.append("_id", 1.0)
						.append("custkey", 1.0)
						.append("name", 1.0)
						.append("address", 1.0)
						.append("city", 1.0)
						.append("nation_name", 1.0)
						.append("region_name", 1.0)
						.append("phone", 1.0)
						.append("mktsegment", 1.0)
				)
			};
		
		mongoDS.run(query);
	}

}
