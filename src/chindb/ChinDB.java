package chindb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.nutz.dao.Chain;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ChinDB {

	private static Log log = Logs.getLog(ChinDB.class);

	private static String rName() {
		return "名" + R.random(1, 10000) + ":" + R.sg(2, 2).next();
	}

	private static String rDesp() {
		return "注释_" + R.random(1, 10000) + ":" + R.sg(6, 8).next();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChinDB cdb = new ChinDB();
		cdb.init();
		//cdb.insertByJDBC();
		 cdb.insertByChain(1);
		// cdb.insertByPojo(1);
		cdb.depose();
	}

	private BasicDataSource ds;
	private Dao dao;

	public void insertByJDBC() {
		String sql = "INSERT INTO t_chin(描述,名字) VALUES( ?, ?)";
		Connection conn = null;
		try {
			try {
				conn = ds.getConnection();
				PreparedStatement stat = conn.prepareStatement(sql);
				stat.setString(1, rName());
				stat.setString(2, rDesp());
				stat.execute();
			}
			finally {
				if (null != conn)
					conn.close();
			}
		}
		catch (SQLException e) {
			throw Lang.wrapThrow(e);
		}

	}

	public void insertByChain(int num) {
		dao.insert("t_chin", Chain.make("名字", rName()).add("描述", rDesp()));

	}

	public void insertByPojo() {
		Chin c = new Chin();
		c.setName(rName());
		c.setDescription(rDesp());
		dao.insert(c);
	}

	public void defineTable() {
		throw Lang.noImplement();
	}

	public void init() {
		if (log.isInfoEnabled())
			log.info("Init ...");
		ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/zzh");
		ds.setUsername("root");
		ds.setPassword("123456");
		dao = new NutDao(ds);
	}

	public void depose() {
		try {
			ds.close();
			if (log.isInfoEnabled())
				log.info("...deposed!");
		}
		catch (SQLException e) {
			log.warn("Error: " + e.getMessage(), e);
		}
	}

}
