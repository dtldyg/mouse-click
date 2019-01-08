package com.liyiyue;

import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;

public class MouseClick extends JFrame {
	private static Executor executor = Executors.newSingleThreadExecutor();
	private Robot robot;
	private boolean starting;
	private JTextField inputField;
	private static MouseClick window;
	private JTextField leftTimeField;
	private JTextField stayTimeField;
	private JButton startBtn;
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		getContentPane().setLayout(null);
		setTitle("疯狂小8-左键连点");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ToolTipManager.sharedInstance().setInitialDelay(5);
		setAlwaysOnTop(true);
		setResizable(false);

		if (IsWindows()) {
			setBounds(600, 300, 284, 116);

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
		} else {
			setBounds(600, 300, 284, 146);

			JLabel label = new JLabel("点击间隔：");
			label.setBounds(15, 19, 68, 15);
			getContentPane().add(label);

			JLabel label_1 = new JLabel("毫秒");
			label_1.setBounds(235, 19, 33, 15);
			getContentPane().add(label_1);

			JLabel lblaltl = new JLabel("倒计时");
			lblaltl.setBounds(15, 54, 49, 15);
			getContentPane().add(lblaltl);

			inputField = new JTextField();
			inputField.setHorizontalAlignment(SwingConstants.RIGHT);
			inputField.setText("10");
			inputField.setToolTipText("最小10，否则会爆炸");
			inputField.setBounds(86, 16, 145, 21);
			inputField.setColumns(10);
			getContentPane().add(inputField);

			JLabel label_2 = new JLabel("持续");
			label_2.setBounds(147, 54, 27, 15);
			getContentPane().add(label_2);

			leftTimeField = new JTextField();
			leftTimeField.setHorizontalAlignment(SwingConstants.RIGHT);
			leftTimeField.setText("5");
			leftTimeField.setBounds(57, 51, 61, 21);
			leftTimeField.setColumns(10);
			getContentPane().add(leftTimeField);

			JLabel label_3 = new JLabel("秒");
			label_3.setBounds(121, 54, 20, 15);
			getContentPane().add(label_3);

			stayTimeField = new JTextField();
			stayTimeField.setHorizontalAlignment(SwingConstants.RIGHT);
			stayTimeField.setText("10");
			stayTimeField.setBounds(180, 50, 61, 21);
			stayTimeField.setColumns(10);
			getContentPane().add(stayTimeField);

			JLabel label_4 = new JLabel("秒");
			label_4.setBounds(244, 53, 20, 15);
			getContentPane().add(label_4);

			startBtn = new JButton("开始");
			startBtn.setBounds(15, 84, 253, 23);
			startBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!starting) {
						executor.execute(new Runnable() {
							@Override
							public void run() {
								start();
							}
						});
					}
				}
			});
			getContentPane().add(startBtn);
		}
	}

	/**
	 * 开始疯狂点击
	 */
	private void start() {
		int sleep = 0;
		int backTime = 0;
		int stayTime = 0;
		try {
			sleep = Integer.parseInt(inputField.getText());
			if (sleep < 10) {
				JOptionPane.showMessageDialog(window, "间隔时间不能小于10，否则会爆炸");
				return;
			}
			if (!IsWindows()) {
				backTime = Integer.parseInt(leftTimeField.getText());
				if (backTime <= 0) {
					JOptionPane.showMessageDialog(window, "倒计时不能小于1");
					return;
				}
				stayTime = Integer.parseInt(stayTimeField.getText());
				if (stayTime <= 0) {
					JOptionPane.showMessageDialog(window, "持续时间不能小于1");
					return;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(window, "数字格式不对");
			return;
		}

		starting = true;
		if (IsWindows()) {
			state.setText("疯狂点击中");
			state.setBackground(Color.GREEN);
		} else {
			startBtn.setText("倒计时（" + backTime + "）");
			while (backTime > 0) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				backTime--;
				startBtn.setText("倒计时（" + backTime + "）");
			}
		}
		long lastTime = 0;
		long startTime = System.currentTimeMillis();
		int staySec = stayTime;
		long lastStayTime = System.currentTimeMillis();
		if (!IsWindows()) {
			startBtn.setText("疯狂点击中（" + staySec + "）");
		}
		while (starting) {
			long curTime = System.currentTimeMillis();
			if (!IsWindows()) {
				if (curTime - startTime > stayTime * 1000) {
					stop();
					break;
				}
				if (curTime - lastStayTime > 1000) {
					lastStayTime = curTime;
					staySec--;
					startBtn.setText("疯狂点击中（" + staySec + "）");
				}
			}
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
		if (IsWindows()) {
			state.setText("停止");
			state.setBackground(Color.RED);
		} else {
			startBtn.setText("开始");
		}
	}

	private static boolean IsWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;
	}
}
