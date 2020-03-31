# Configuring MongoDB connection as JNDI in Pentaho

* _tomcat/webapps/pentaho/META-INF/context.xml_

 ```xml
<Resource name="mongodb/MongoClient"
        auth="Container"
        type="com.mongodb.MongoClient"
        closeMethod="close"
        factory="com.mongodb.client.jndi.MongoClientFactory"
        singleton="true"
        connectionString="mongodb://localhost"/>
 ```

* _tomcat/webapps/pentaho/WEB-INF/web.xml_

 ```xml
<resource-ref>
    <res-ref-name>mongodb/MongoClient</res-ref-name>
    <res-auth>Container</res-auth>
    <res-type>com.mongodb.MongoClient</res-type>
</resource-ref>
 ```

 * Copy the `pentaho-cda-mongodb-<version>.jar` file into the `tomcat/webapps/pentaho/WEB-INF/lib` folder.