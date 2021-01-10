package mythread;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import bean.MsgBean;

public class SendThread extends Thread{
	private MsgBean msg;//消息内容
	private final int port = 1999;//端口号
	public SendThread(MsgBean msg) {
		this.msg = msg;
	}
	@Override
	public void run() {
		super.run();
		try {
			//把序列化后的消息存储到本地
//			String filePath = msg.getSeq()+"-"+msg.getType()+".rs";//1-pdf.rs
//			FileOutputStream fStream = new FileOutputStream(filePath);
//			ObjectOutputStream streamToFile = new ObjectOutputStream(fStream);
//			streamToFile.writeObject(msg);
//			streamToFile.close();
//			fStream.close();
			//通过网络传输消息到目标主机
			Socket socket = new Socket(msg.getTarget(),port);
			ObjectOutputStream streamToNetwork = new ObjectOutputStream(socket.getOutputStream());
			streamToNetwork.writeObject(msg);
			streamToNetwork.flush();
			streamToNetwork.close();
			socket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
