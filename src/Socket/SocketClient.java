package Socket;

import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;


public class SocketClient {
	// private static final String HOST = "192.168.1.107";
	// private static final String HOST = "192.168.1.111";
	// private static final String HOST = "42.121.123.185";
	private static final String HOST = "192.168.1.31";
	private static final int PORT = 8080;
	// private static final int PORT = 8686;
	private PrintWriter pw;
	public static Socket socket;
	public boolean initSocket = false;
	private ExecutorService executorService;
	private final int POOL_SIZE = 10;
	public String sendTem = "";


	public SocketClient() {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * POOL_SIZE);
		new Thread() {
			@Override
			public void run() {
				try {
					socket = new Socket(HOST, PORT);
					OutputStream socketOut = socket.getOutputStream();
					pw = new PrintWriter(socketOut, true);
					pw.println("android");
					InputStream socketIn = socket.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(
							socketIn));
					String sec = br.readLine();
					SocketClient.socketRead(sec);
					// pw.println("7a941492a0dc743544ebc71c89370a64");
					executorService.execute(new Handler(socket));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	public boolean status() {
		if (!pw.checkError() && socket.isConnected() && !socket.isClosed()
				&& socket != null) {
			return true;
		}
		return false;
	}

	public final void sendMessageSocket(String message) {
		if (!pw.checkError() && socket.isConnected() && !socket.isClosed()
				&& socket != null) {
			pw.println(message);
			System.out.println("[send to server]"+message);
		} else {
			sendTem = message;
			System.out.println("pw is not ready..");
		}
	}


	public void closeSocket() {
		try {
			if (initSocket) {
				socket.close();
			}
			System.out.println("socket close...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * socket 写入
	 * @param content
	 * @return
	 */
	public static boolean socketWrite(String content) {
		try {
			OutputStream socketOut = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(socketOut, true);
			pw.println(content);
			return true;
		} catch (Exception e) {
			// 记录失败的程序
			e.printStackTrace();
			// 把socket给移除
		}
		return false;
	}

	/**
	 * 取得命令行，可以是手机，也可以是板子
	 * 
	 * @param gameuid
	 * @param content
	 * @return
	 * @throws JSONException 
	 */
	public static boolean socketRead(String content) throws Exception {
		System.out.println("[get from server]" + content);
		JSONObject jsonobj = new JSONObject(content);
		SocketHandleMap.sendToActivity(jsonobj);
		return true;
	}
}

class Handler implements Runnable {
	private Socket socket;

	public Handler(Socket socket) {
		this.socket = socket;
	}

	private BufferedReader getReader(Socket socket) throws IOException {
		InputStream socketIn = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));
	}

	public String echo(String msg) {
		return "echo:" + msg;
	}

	public void run() {
		try {
			BufferedReader br = getReader(socket);
			String msg = null;
			while ((msg = br.readLine()) != null) {
				try {
					SocketClient.socketRead(msg);
				} catch (Exception e) {

				}
			}
		} catch (IOException e) {
			System.out.println("断开连接了");
			e.printStackTrace();
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}