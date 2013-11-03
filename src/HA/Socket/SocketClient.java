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
	public boolean status(){
		return socket.isConnected();
	}
	public void sendMessage(String message) {
		pw.println(message);
	}
	
	private Handler handler = new Handler() {
		@Override
		// 当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 处理UI
			System.out.println("connecting..");
		}
	};

	public String sendMsg(String msg) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(msg);
			out.flush();
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

	}

}