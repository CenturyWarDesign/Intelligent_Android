package Socket;

import java.util.TimerTask;

import com.centurywar.intelligent.BaseActivity;

public class SocketHeart extends TimerTask {
	public void run() {
		try {
//			boolean status = BaseActivity.socketClient.status();
//			if (!status) {
//				BaseActivity.socketClient = new SocketClient();
//				System.out.println("断线了，重新连了一次");
//			}
//			System.out.println("发送心跳包");
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}
}
