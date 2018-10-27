import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Executors;

public class Main {

	public static void main(String[] args) throws IllegalArgumentException, Exception, JIException, AlreadyConnectedException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;

//		OPC
		ConnectionInformation ci = new ConnectionInformation();
		ci.setHost("x.x.x.x");
		ci.setDomain("");
		ci.setUser("xxxx");
		ci.setPassword("pwd");
		ci.setProgId("ProgId");
		ci.setClsid("tClsid");
//		new a group
		Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
		server.connect();
//		add items into the new group
		Group group1 = server.addGroup("group1");
		try {
//		    database prepare
			conn = DBManager.getConnection();
			String sql = "select name from test";
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
//			flag for count
			int flag = 0;
			while (rs.next() && flag < 4000) {
				try {
					Item item = group1.addItem(rs.getString("name") + ".PV");
					flag++;
					System.out.println("NO." + flag++ + "  ItemName:" + item.getId() + ",value:" + item.read(false).getValue());
				} catch (AddFailedException e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.closeAll(conn, st, rs);
		}
		server.dispose();

	}
}
