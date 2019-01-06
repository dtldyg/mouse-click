package com.liyiyue;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import javax.swing.JTextField;
import java.awt.Color;

public class MouseClick extends JFrame {
	private static Executor executor = Executors.newSingleThreadExecutor();
	private Robot robot;
	private boolean starting;
	private JTextField inputField;
	private static MouseClick window;
	private JLabel state;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MouseClick();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MouseClick() throws Exception {
		robot = new Robot();
		starting = false;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			FontUtil.setGlobalFonts(new Font("微软雅黑", Font.PLAIN, 12));
		} catch (Exception e) {
			e.printStackTrace();
		}

		getContentPane().setLayout(null);
		setTitle("疯狂小8-左键连点");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(600, 300, 284, 116);
		ToolTipManager.sharedInstance().setInitialDelay(5);
		setAlwaysOnTop(true);
		setResizable(false);

		JLabel label = new JLabel("点击间隔：");
		label.setBounds(15, 19, 68, 15);
		getContentPane().add(label);

		JLabel label_1 = new JLabel("毫秒");
		label_1.setBounds(235, 19, 33, 15);
		getContentPane().add(label_1);

		JLabel lblaltl = new JLabel("按Alt+L开始和结束");
		lblaltl.setBounds(15, 53, 112, 15);
		getContentPane().add(lblaltl);

		inputField = new JTextField();
		inputField.setHorizontalAlignment(SwingConstants.RIGHT);
		inputField.setText("10");
		inputField.setToolTipText("最小10，否则会爆炸");
		inputField.setBounds(86, 16, 145, 21);
		inputField.setColumns(10);
		getContentPane().add(inputField);

		state = new JLabel("停止");
		state.setBackground(Color.RED);
		state.setOpaque(true);
		state.setHorizontalAlignment(SwingConstants.CENTER);
		state.setBounds(137, 50, 118, 22);
		getContentPane().add(state);

		// 注册开始快捷键
		JIntellitype.getInstance().registerHotKey(0, JIntellitype.MOD_ALT, 'L');
		JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
			@Override
			public void onHotKey(int keyCode) {
				switch (keyCode) {
				case 0:
					if (!starting) {
						executor.execute(new Runnable() {
							@Override
							public void run() {
								start();
							}
						});
					} else {
						stop();
					}
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 开始疯狂点击
	 */
	private void start() {
		int sleep = 0;
		try {
			sleep = Integer.parseInt(inputField.getText());
			if (sleep < 10) {
				JOptionPane.showMessageDialog(window, "不能小于10，否则会爆炸");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(window, "数字格式不对");
			return;
		}

		starting = true;
		state.setText("疯狂点击中");
		state.setBackground(Color.GREEN);
		long lastTime = 0;
		while (starting) {
			long curTime = System.currentTimeMillis();
			if ((curTime - lastTime) > sleep) {
				lastTime = curTime;
				robot.mousePress(KeyEvent.BUTTON1_MASK);
				robot.mouseRelease(KeyEvent.BUTTON1_MASK);
			}
			try {
				Thread.sleep(1);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 停止
	 */
	private void stop() {
		starting = false;
		state.setText("停止");
		state.setBackground(Color.RED);
	}
}
