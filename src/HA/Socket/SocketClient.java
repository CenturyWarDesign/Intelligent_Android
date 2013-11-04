package HA.Socket;

import java.io.*;
import java.net.*;

import android.os.Handler;
import android.os.Message;

 public class SocketClient {
    private static final String HOST = "192.168.1.110";  
    private static final int PORT = 8080;  
	private PrintWriter pw;
	Socket socket;
	public boolean initSocket=false;
	public String sendTem="";
	public SocketClient() {
		new Thread() {
			@Override
			public void run() {
				try {
					socket = new Socket(HOST, PORT);
					handler.sendEmptyMessage(0);
					OutputStream socketOut = socket.getOutputStream();
					pw = new PrintWriter(socketOut, true);
					pw.println("7a941492a0dc743544ebc71c89370a64");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public boolean status() {
		if (socket == null) {
			return false;
		}
		return socket.isConnected();
	}

	public final void sendMessageSocket(String message) {
		if (!pw.checkError()&& socket.isConnected()&&!socket.isClosed()
				&& socket != null) {
			pw.println(message);
		} else {
			sendTem = message;
			System.out.println("pw is not ready..");
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		// ������Ϣ���ͳ�����ʱ���ִ��Handler���������
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// ����UI
			initSocket=true;
			System.out.println("connecting..");
			if (sendTem.length() > 0) {
				sendMessageSocket(sendTem);
				sendTem = "";
			}
		}
	};

	


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

	public static void main(String[] args) throws Exception {

	}

}