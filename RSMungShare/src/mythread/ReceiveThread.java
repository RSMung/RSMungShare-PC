package mythread;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

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
				System.out.println("****************>>>>");
				MsgBean msg = (MsgBean) streamFromNetwork.readObject();
				String type = msg.getType();
				if(type.equals("text")) {
					streamFromNetwork.close();
				}else if(type.equals("image")) {
					bytes2Image(msg.getTvContent(), msg.getImage_data());
				}
				MainActivity m = weakReference.get();
				m.displayMsg(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void bytes2Image(String name, byte[] data) {
        //将字节写入文件
        try {
        	File parent_file = new File("./MobileImage");
        	if(!parent_file.exists()) {
        		if(!parent_file.mkdirs()) {
        			System.out.println("存放图片的父文件夹创建失败");
        			return;
        		}
        	}
        	File file = new File("./MobileImage/"+name);
//        	//获得后缀名IMG_20210117_172340.jpg--->jpg
//        	String[] strs = name.split("\\."); //以.分割要写成\\.
//        	String formatName = strs[strs.length - 1];
        	FileImageOutputStream fous = new FileImageOutputStream(file);
        	fous.write(data);
        	fous.flush();
        	fous.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
