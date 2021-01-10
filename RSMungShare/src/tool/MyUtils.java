package tool;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class MyUtils {// 一些和组件相关的工具类
	public static void middle(JFrame frame) {
		Dimension screenSize1 = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize1 = frame.getSize();
		if (frameSize1.height > screenSize1.height) {
			frameSize1.height = screenSize1.height;
		}
		if (frameSize1.width > screenSize1.width) {
			frameSize1.width = screenSize1.width;
		}
		frame.setLocation((screenSize1.width - frameSize1.width) / 2, (screenSize1.height - frameSize1.height) / 2);
		frame.setVisible(true);
	}

	public static void uniformFont(JComponent c) {
		c.setFont(new Font("Dialog", Font.BOLD, 20));
	}

}