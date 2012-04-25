package socket;

import java.util.Calendar;

import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketContext;
import org.nutz.lang.socket.Sockets;

public class SocketListener {

	public static void main(String[] args) {

		Sockets.localListenOne(5678, "$:.*", new SocketAction() {
			public void run(SocketContext context) {
				System.out.println(context.getCurrentLine());
				context.writeLine(Calendar.getInstance().toString());
				if ("stop".equals(context.getCurrentLine()))
					Sockets.close();
			}
		});

	}

}
