package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import bean.MsgBean;
import bean.MsgCollector;
import mythread.ReceiveThread;
import mythread.SendThread;
import tool.MyUtils;

/*主界面*/
public class MainActivity extends JFrame implements ActionListener{
	private String TAG = "MainActivity";
	private String user = "RSMungShare";
	private int width = 900,height = 650;
	private JTextArea jt_input,jt_record;
	private JTextField target_ip;
	private JButton btn_send;
	private ReceiveThread receiveThread;
	private String source_ip;
	/* 构造函数 */
	private MainActivity() {
		try {
			source_ip = getLocalHostLANAddress().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		initFrame();
		//开启子线程等待其他客户端的连接
		receiveThread = new ReceiveThread(this);
		receiveThread.start();
	}

	/* 初始化界面 */
	private void initFrame() {
		setSize(width, height);//窗口大小
		setDefaultCloseOperation(EXIT_ON_CLOSE);//退出按钮
		//字体
		setFont(new Font("System",Font.PLAIN,20));
		Font f = getFont();
		/*设置标题居中*/
		FontMetrics fm = getFontMetrics(f);
		int width_user = fm.stringWidth(user);
		int width_blank = fm.stringWidth(" ");
		int start = getWidth()/2 - (width_user/2);
		int blank_num = start/width_blank;
		String pad ="";
		pad = String.format("%"+blank_num+"s", pad);
		setTitle(pad+user);
		//设置窗口左上角的图标
		String curDir = System.getProperty("user.dir");
//        System.out.println("当前的工作目录为 :" + curDir);
		ImageIcon icon=new ImageIcon(curDir+"\\icon\\title_icon.png");
		this.setIconImage(icon.getImage());
		
		//传输记录界面
		int h_unit = height/8;
	    jt_record = new JTextArea();
	    jt_record.setLineWrap(true);
	    jt_record.setFont(new Font("System",Font.PLAIN,20));
	    jt_record.setEditable(false);
	    JScrollPane js_record = new JScrollPane(jt_record);
		//输入界面
	    jt_input = new JTextArea();
	    jt_input.setLineWrap(true);
	    jt_input.setFont(new Font("System",Font.PLAIN,20));
	    JScrollPane js_input = new JScrollPane(jt_input);
		//发送按钮
		JPanel send = new JPanel();
		send.setLayout(new BorderLayout());
		send.setBorder(new EmptyBorder(20, 20, 20, 20));
		//文本框显示本机ip
		JLabel localMsg = new JLabel();
		String s_localname = "";
		//目标客户端信息
		JPanel target = new JPanel();
		target.setLayout(new BorderLayout());
		//输入目标IP提示
		JLabel target_hint = new JLabel("消息发送至(ip):");
		target_hint.setFont(new Font("System",Font.PLAIN,20));
		target.add("West",target_hint);
		target.setBorder(new EmptyBorder(0, 30, 0, 10));
		//输入目标IP
		target_ip = new JTextField();
		target_ip.setBorder(new EmptyBorder(0, 10, 0, 10));
		target_ip.setFont(new Font("System",Font.PLAIN,20));
		target.add("Center",target_ip);
        try {
        	InetAddress ia=InetAddress.getLocalHost();
            s_localname=ia.getHostName();
            localMsg.setText("本机信息: "+s_localname+" / "+source_ip);
            localMsg.setFont(new Font("System",Font.PLAIN,20));
        } catch (Exception e) {
            e.printStackTrace();
        }
		btn_send = new JButton("发送");
		btn_send.setBackground(Color.LIGHT_GRAY);
		btn_send.setFont(new Font("System",Font.PLAIN,20));
		btn_send.addActionListener(this);
		send.add("West",localMsg);
		send.add("Center",target);
		send.add("East",btn_send);
		//把三个子控件加入主界面
		setLayout(new BorderLayout());
		JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		main.setTopComponent(js_record);
		main.setBottomComponent(js_input);
		main.setDividerSize(1);
		main.setDividerLocation(h_unit*5);
		add("Center",main);
		add("South",send);
		MyUtils.middle(this);//居中
	}

	/*子线程接收到消息后调用*/
	public void displayMsg(MsgBean msg) {
		String s_msg = null;
		String title = null;
		if(msg.getSource().equals(source_ip)) {
			//是本机发送出去的消息
			title = "PC to APP";
		}else {
			//APP发送给本机的消息
			title = "APP to PC";
		}
		s_msg = title+
				"	Time:"+msg.getTime()+
				"   Type:"+msg.getType()+
				"   Content:"+msg.getTvContent()+"\n";
		jt_record.append(s_msg);
		System.out.println(TAG+",新消息:"+s_msg);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(btn_send != null && 
				target_ip.getText() != null && 
				!target_ip.getText().equals("") &&
				e.getSource() == btn_send) {
			//输入不为空,发送按钮的点击事件
			Date date = new Date();
			SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String time = s.format(date);
			String tvContent = jt_input.getText();
			int size = tvContent.length();
			MsgBean msg = new MsgBean(
					MsgCollector.size(),
					"text",
					time,
					source_ip,
					target_ip.getText(),
					size, 
					tvContent,
					null,
					null);
			MsgCollector.addMsg(msg);
			displayMsg(msg);
			new SendThread(msg).start();
			jt_input.setText("");
		}
	}
	
	// 获取本机IP地址，优先拿site-local地址
    private InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;//候选地址
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                if(!iface.getDisplayName().contains("VirtualBox") && 
                		!iface.getDisplayName().contains("VMware")) {//排除VirtualBox和VMware虚拟机的IP地址
                	// 在所有的接口下再遍历IP
                    for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                        InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                        if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                            if (inetAddr.isSiteLocalAddress()) {
                                // 如果是site-local地址，就是它了
                                return inetAddr;
                            } else if (candidateAddress == null) {
                                // site-local类型的地址未被发现，先记录候选地址
                                candidateAddress = inetAddr;
                            }
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException(
                		"The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

	public static void main(String[] args) {
		new MainActivity();
	}

}
