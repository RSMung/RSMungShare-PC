package mythread;

import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;

import bean.MsgBean;
import tool.MyObjectInputStream;
import ui.MainActivity;

public class ReceiveThread extends Thread{
	private final int port = 1998;//端口号
	private WeakReference<MainActivity> weakReference;
	//构造函数
	public ReceiveThread(MainActivity mainActivity) {
		this.weakReference = new WeakReference<>(mainActivity);
	}

	@Override
	public void run() {
		super.run();
		ServerSocket serverSocket = null;
		Socket socket = null;
		MyObjectInputStream streamFromNetwork = null;
		try {
			while(true) {
				if(serverSocket == null) {
					serverSocket = new ServerSocket(port);
				}
				socket = serverSocket.accept();//等待客户端的连接---阻塞
				streamFromNetwork = new MyObjectInputStream(socket.getInputStream());
				MsgBean msg = (MsgBean) streamFromNetwork.readObject();
				MainActivity m = weakReference.get();
				m.displayMsg(msg);
//				m.displayMsg(
//						"Time:"+msg.getTime()+
//						"   Source:"+msg.getSource()+
//						"   Target:"+msg.getTarget()+
//						"   Content:"+msg.getTvContent()+"\n");
				streamFromNetwork.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
