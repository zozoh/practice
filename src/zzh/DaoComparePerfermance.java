package zzh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Stopwatch;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

public class DaoComparePerfermance {

	private static Dao dao;

	private static final int MAX = 5000;

	public static void main(String[] args) throws SQLException {

		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://127.0.01:3306/abc");
		ds.setUsername("root");
		ds.setPassword("123456");

		dao = new NutDao(ds);

		dao.create(Abc.class, true);

		System.out.println("Ready: " + dao.count(Abc.class));

		Stopwatch sw = Stopwatch.begin();
		 Trans.exec(new ByPojo());
		//dao.run(new PreparedBatchdRun());
		sw.stop();
		ds.close();

		System.out.println(sw.toString());

	}

	public static class PreparedBatchdRun implements ConnCallback {
		public void invoke(Connection conn) throws Exception {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("INSERT INTO t_abc (name) VALUES(?)");
			for (int i = 0; i < MAX; i++) {
				ps.setString(1, "abc_" + i);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		}
	}

	public static class BatchdRun implements ConnCallback {
		public void invoke(Connection conn) throws Exception {
			conn.setAutoCommit(false);
			Statement st = conn.createStatement();
			for (int i = 0; i < MAX; i++) {
				st.addBatch("INSERT INTO t_abc (name) VALUES('abc_" + i + "')");
			}
			st.executeBatch();
			conn.commit();
		}
	}

	public static class PreparedRun implements ConnCallback {
		public void invoke(Connection conn) throws Exception {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("INSERT INTO t_abc (name) VALUES(?)");
			for (int i = 0; i < MAX; i++) {
				ps.setString(1, "abc_" + i);
				ps.execute();
			}
			conn.commit();
		}
	}

	public static class ByMultipleSql implements Atom {
		public void run() {
			Sql[] sqls = new Sql[MAX];
			for (int i = 0; i < MAX; i++) {
				sqls[i] = Sqls.create("INSERT INTO t_abc (name) VALUES(@name)");
				sqls[i].params().set("name", "abc_" + i);
			}
			dao.execute(sqls);
		}
	}

	public static class ByOneSql implements Atom {
		public void run() {
			Sql sql = Sqls.create("INSERT INTO t_abc (name) VALUES(@name)");
			for (int i = 0; i < MAX; i++) {
				sql.params().set("name", "abc_" + i);
				dao.execute(sql);
			}
		}
	}

	public static class ByPojo implements Atom {

		public void run() {
			for (int i = 0; i < MAX; i++) {
				Abc abc = new Abc();
				abc.setName("abc_" + i);
				dao.fastInsert(abc);
			}
		}
	}
}
