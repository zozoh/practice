package mongo;

import java.net.UnknownHostException;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class TryMongo {

	/**
	 * @param args
	 * @throws MongoException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException, MongoException {
		Mongo m = new Mongo("localhost", 27017);
		DB db = m.getDB("mydb");
		DBCollection co = db.getCollection("things");
		DBCursor c = co.find();
		while (c.hasNext()) {
			DBObject dbo = c.next();
			Object ID = dbo.get("_id");
			System.out.println(ID);
			System.out.println(Json.toJson(dbo.toMap(), JsonFormat.compact()));
		}
	}

}
