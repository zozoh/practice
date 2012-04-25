package socket;

import java.net.*;
import java.io.*;

import org.nutz.lang.socket.Sockets;

public class SocketSender {

	public static void main(String[] args) throws Exception {
		Sockets.sendText("localhost", 5678, "stop\r\n");
	}

	static void sagasdf() throws IOException, UnknownHostException {
		Socket server = new Socket(InetAddress.getLocalHost(), 5678);
		BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		PrintWriter out = new PrintWriter(server.getOutputStream());
		BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			String str = wt.readLine();
			out.println(str);
			out.flush();
			if (str.equals("stop")) {
				break;
			}
			System.out.println(in.readLine());
		}
		server.close();
	}
}
