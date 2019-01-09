package com.liyiyue;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.liyiyue.MouseClick.PosManager.PosNode;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JButton;

public class MouseClick extends JFrame {
	private static Executor executor = Executors.newSingleThreadExecutor();
	private static MouseClick window;

	// const
	private static int Win_H = 100;
	private static int Max_Pos_Num = 6;
	private static int One_Line_Pos_Num = 2;
	private static int Pos_Begin_X = 25;
	private static int Pos_Begin_Y = 69;
	private static int Pos_W = 178;
	private static int Pos_H = 90;
	private static int Pos_Img_W = 140;
	private static int Pos_Img_H = 70;
	private static int W_Off = Pos_Img_W / 2;
	private static int H_Off = Pos_Img_H / 2;

	// var

	// component
	private Robot robot;
	private boolean starting;
	private JTextField intervalInput;
	private JLabel runningState;
	private PosManager posManager;

	// private JLabel posImg;
	// private JLabel posDel;

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
		posManager = new PosManager();
		posManager.init();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			FontUtil.setGlobalFonts(new Font("微软雅黑", Font.PLAIN, 12));
		} catch (Exception e) {
			e.printStackTrace();
		}
		getContentPane().setLayout(null);
		setTitle("狂怒小8");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ToolTipManager.sharedInstance().setInitialDelay(5);
		setAlwaysOnTop(true);
		setResizable(false);
		setBounds(600, 300, 382, Win_H);

		JLabel label01 = new JLabel("点击间隔：");
		label01.setBounds(25, 16, 68, 15);
		getContentPane().add(label01);

		JLabel label02 = new JLabel("毫秒");
		label02.setBounds(150, 16, 33, 15);
		getContentPane().add(label02);

		JLabel label04 = new JLabel("【Alt+K】添加点击位置");
		label04.setHorizontalAlignment(SwingConstants.CENTER);
		label04.setBounds(75, 44, 150, 15);
		getContentPane().add(label04);

		intervalInput = new JTextField();
		intervalInput.setHorizontalAlignment(SwingConstants.RIGHT);
		intervalInput.setText("100");
		intervalInput.setToolTipText("最小10，否则会爆炸");
		intervalInput.setBounds(86, 13, 60, 21);
		intervalInput.setColumns(10);
		getContentPane().add(intervalInput);

		runningState = new JLabel("【Alt+L】开始/结束");
		runningState.setBackground(Color.RED);
		runningState.setOpaque(true);
		runningState.setHorizontalAlignment(SwingConstants.CENTER);
		runningState.setBounds(193, 12, 150, 22);
		getContentPane().add(runningState);

		JButton help = new JButton("说明");
		help.setBounds(224, 40, 93, 23);
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showMsg("模式1：狂点鼠标指向的位置（未添加点击位置）\n" + "模式2：狂点指定点击位置（快捷键添加，支持最多" + Max_Pos_Num + "个）\n\n" + "快捷键开始和结束");
			}
		});
		getContentPane().add(help);

		// 注册全局快捷键
		try {
			JIntellitype.getInstance().registerHotKey(0, JIntellitype.MOD_ALT, 'L');
			JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_ALT, 'K');
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
					case 1:
						if (!starting) {
							catchAMousePos();
						}
					default:
						break;
					}
				}
			});
		} catch (Exception e) {
			showMsg(e.toString());
		}
	}

	class PosManager {
		List<PosNode> nodes;

		public void init() {
			nodes = new ArrayList<PosNode>(Max_Pos_Num);
			for (int i = 0; i < Max_Pos_Num; i++) {
				nodes.add(makePosNode());
			}
		}

		public boolean hasPos() {
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).show) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 刷新UI，并重排序
		 */
		public void refresh() {
			int index = 0;
			List<PosNode> newNodes = new ArrayList<PosNode>(Max_Pos_Num);
			for (int i = 0; i < nodes.size(); i++) {
				PosNode node = nodes.get(i);
				if (node.show) {
					int x = Pos_Begin_X + (index % One_Line_Pos_Num) * Pos_W;
					int y = Pos_Begin_Y + (index / One_Line_Pos_Num) * Pos_H;
					node.posDel.setBounds(x, y, Pos_Img_W, Pos_Img_H);
					node.posImg.setBounds(x, y, Pos_Img_W, Pos_Img_H);
					node.posDel.setVisible(false);
					node.posImg.setVisible(true);
					newNodes.add(index, node);
					index++;
				} else {
					node.posDel.setVisible(false);
					node.posImg.setVisible(false);
					newNodes.add(node);
				}
			}
			nodes = newNodes;
			setBounds(getBounds().x, getBounds().y, 382, Win_H + (index + 1) / 2 * Pos_H);
		}

		/**
		 * 添加截图
		 */
		public void add(Icon icon, int x, int y) {
			for (int i = 0; i < nodes.size(); i++) {
				PosNode node = nodes.get(i);
				if (!node.show) {
					node.x = x;
					node.y = y;
					node.posImg.setIcon(icon);
					node.show = true;
					posManager.refresh();
					break;
				}
			}
		}

		class PosNode {
			boolean show;
			int x;
			int y;
			JLabel posDel;
			JLabel posImg;
		}

		public PosNode makePosNode() {
			PosNode node = new PosNode();
			JLabel posDel = new JLabel("D E L");
			JLabel posImg = new JLabel("");
			node.show = false;
			node.x = 0;
			node.y = 0;
			node.posDel = posDel;
			node.posImg = posImg;

			posDel.setForeground(Color.WHITE);
			posDel.setFont(new Font("Consolas", Font.BOLD, 24));
			posDel.setHorizontalAlignment(SwingConstants.CENTER);
			posDel.setOpaque(true);
			posDel.setBounds(25, 69, Pos_Img_W, Pos_Img_H);
			posDel.setBackground(new Color(0, 0, 0, 150));
			posDel.setVisible(false);
			posDel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					node.show = false;
					posManager.refresh();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					posDel.setVisible(false);
				}

			});
			getContentPane().add(posDel);

			posImg.setBorder(new LineBorder(new Color(0, 0, 0)));
			posImg.setBounds(25, 69, Pos_Img_W, Pos_Img_H);
			posImg.setVisible(false);
			posImg.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					posDel.setVisible(true);
				}
			});
			getContentPane().add(posImg);

			return node;
		}
	}

	/**
	 * 获取一个鼠标位置
	 */
	private void catchAMousePos() {
		int x = MouseInfo.getPointerInfo().getLocation().x;
		int y = MouseInfo.getPointerInfo().getLocation().y;
		BufferedImage bufImg = robot.createScreenCapture(new Rectangle(x - W_Off, y - H_Off, Pos_Img_W, Pos_Img_H));
		ImageUtil.DrawCrossDotLine(bufImg);
		posManager.add(new ImageIcon(bufImg), x, y);
	}

	/**
	 * 开始疯狂点击
	 */
	private void start() {
		int sleep = 0;
		try {
			sleep = Integer.parseInt(intervalInput.getText());
			if (sleep < 10) {
				showMsg("间隔时间不能小于10，否则会爆炸");
				return;
			}
		} catch (Exception e) {
			showMsg("数字格式不对");
			return;
		}

		starting = true;
		runningState.setText("疯狂点击中");
		runningState.setBackground(Color.GREEN);
		long lastTime = 0;
		boolean hasPos = posManager.hasPos();
		while (starting) {
			long curTime = System.currentTimeMillis();
			if ((curTime - lastTime) > sleep) {
				lastTime = curTime;
				if (hasPos) {
					Point originP = MouseInfo.getPointerInfo().getLocation();
					for (int i = 0; i < posManager.nodes.size(); i++) {
						PosNode node = posManager.nodes.get(i);
						if (node.show) {
							robot.mouseMove(node.x, node.y);
							robot.mousePress(KeyEvent.BUTTON1_MASK);
							robot.mouseRelease(KeyEvent.BUTTON1_MASK);
						}
					}
					robot.mouseMove(originP.x, originP.y);
				} else {
					robot.mousePress(KeyEvent.BUTTON1_MASK);
					robot.mouseRelease(KeyEvent.BUTTON1_MASK);
				}
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
		runningState.setText("【Alt+L】开始/结束");
		runningState.setBackground(Color.RED);
	}

	private static void showMsg(String s) {
		JOptionPane.showMessageDialog(window, s);
	}
}
