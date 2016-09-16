import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Serverside extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8111260195373973572L;
	static ServerSocket ss;
	static Socket s;
	static DataInputStream din;
	static ObjectInputStream oin;
	static DataOutputStream dout;
	static ObjectOutputStream oout;
	public int Npnt, oldx, oldy, nwx, nwy;
	public ArrayList<Coordinates> cords;
	long starttime;
	private Graphics2D g2;
	public Coordinates c, c2;
	static Serverside gui;
	private java.awt.Image image;
	JButton clrButton, RedBtn, BlueBtn, GreenBtn, BlackBtn, HighlightBtn;
	JTextField txtField;
	private JButton btnConnect;
	private JPasswordField pwdPassword;
	String accessCode;
	static Boolean isConnected = false;
	BufferedReader reader;
	PrintWriter writer;
	public Coordinates tempc;
	int pnt[][][];
	ActionListener actionListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == clrButton) {
				gui.clear();
			} else if (e.getSource() == RedBtn) {
				gui.redBtn();

			} else if (e.getSource() == BlueBtn) {
				gui.blueBtn();
			} else if (e.getSource() == GreenBtn) {
				gui.greenBtn();
			} else if (e.getSource() == BlackBtn) {
				gui.blackBtn();
			} else if (e.getSource() == HighlightBtn) {
				gui.highlightBtn();
			} else if (e.getSource() == btnConnect) {
				char[] pwd = pwdPassword.getPassword();
				String p = new String(pwd);
				connectBtn(p);
				if (isConnected = true) {
					pwdPassword.setEditable(false);
				}
			}
		}

	};

	public Serverside() {
		cords = new ArrayList<Coordinates>();
		setDoubleBuffered(false);

		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e2) {
				oldx = e2.getX();
				oldy = e2.getY();
				c2 = new Coordinates(oldx, oldy);
				c2.setColor(g2.getColor());
				if (cords.isEmpty()){
					starttime=System.currentTimeMillis();
				}
				cords.add(c2);
			}

			public void mouseReleased(MouseEvent e3) {

				sendData();

			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e1) {
				nwx = e1.getX();
				nwy = e1.getY();

				if (g2 != null) {

					g2.drawLine(oldx, oldy, nwx, nwy);
					repaint();
					oldx = nwx;
					oldy = nwy;

				}
				c2 = new Coordinates(e1.getX(), e1.getY());
				c2.setColor(g2.getColor());
				Npnt = Npnt + 1;
				cords.add(c2);
			}

		});

	}

	protected void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) image.getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			clear();
		}
		g.drawImage(image, 0, 0, null);
	}

	public void clear() {
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setPaint(Color.black);
		repaint();
		cords.clear();
	}

	public int getNpnt() {
		Npnt = cords.size();

		return Npnt;
	}

	public static void main(String[] args) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Serverside().show();

			}
		});
		String msgin = "";
		while (true) {
			try {
				// ss = new ServerSocket(1210);
				ss = new ServerSocket();
				ss.bind(new InetSocketAddress("0.0.0.0", 1210));
				// System.out.println("Remote IP:"
				// +s.getInetAddress()+"\nRemotePort: "+s.getPort());
				s = ss.accept();
				din = new DataInputStream(s.getInputStream());
				dout = new DataOutputStream(s.getOutputStream());
				msgin = din.readUTF();
				System.out.println("Server:Recieved data!");
				int flag = 0, ctr = 0, flag2 = 0;
				int xs = 0, ys = 0, oldx = 0, oldy = 0;
				Color clr = null;
				for (String s : msgin.split(",")) {
					if (flag == 0) {
						xs = Integer.valueOf(s);
						flag = 1;
					} else if (flag == 1) {
						ys = Integer.valueOf(s);
						flag = 2;
						ctr = ctr + 1;
						if (flag2 == 0) {
							oldx = xs;
							oldy = ys;
							flag2 = 1;
						}
					} else {
						if (Objects.equals(s, "black")) {
							clr = Color.black;
						} else if (Objects.equals(s, "blue")) {
							clr = Color.blue;
						} else if (Objects.equals(s, "red")) {
							clr = Color.red;
						} else if (Objects.equals(s, "green")) {
							clr = Color.green;
						} else if (Objects.equals(s, "highlight")) {
							float alpha = 0.1f;
							Color color = new Color(1, 0.5f, 0, alpha);
							clr = color;
						}
						flag = 0;
						ctr = ctr + 1;
					}
					if (ctr == 3) {
						gui.drawnow(oldx, oldy, xs, ys, clr);
						oldx = xs;
						oldy = ys;
						ctr = 0;
					}

				}
				ss.close();

			} catch (Exception e) {
				System.out.println("Not able to recieve data!-server");
			}
		}
	}

	public void show() {
		JFrame frame = new JFrame("Collaborative Sketching-Server");
		java.awt.Container content = frame.getContentPane();
		content.setLayout(new BorderLayout());
		gui = new Serverside();
		content.add(gui, BorderLayout.CENTER);
		JPanel options = new JPanel();
		clrButton = new JButton("Clear");
		clrButton.addActionListener(actionListener);
		RedBtn = new JButton("Red");
		RedBtn.addActionListener(actionListener);
		BlackBtn = new JButton("Black");
		BlackBtn.addActionListener(actionListener);
		GreenBtn = new JButton("Green");
		GreenBtn.addActionListener(actionListener);
		BlueBtn = new JButton("Blue");
		BlueBtn.addActionListener(actionListener);
		HighlightBtn = new JButton("Highlight");
		HighlightBtn.addActionListener(actionListener);
		options.add(clrButton);
		options.add(RedBtn);
		options.add(GreenBtn);
		options.add(BlackBtn);
		options.add(BlueBtn);
		options.add(HighlightBtn);
		content.add(options, BorderLayout.SOUTH);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(actionListener);
		options.add(btnConnect);

		pwdPassword = new JPasswordField();
		pwdPassword.setBackground(Color.WHITE);
		pwdPassword.setColumns(8);
		options.add(pwdPassword);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public void redBtn() {
		g2.setStroke(new BasicStroke(2.0f));
		g2.setPaint(Color.red);

	}

	public void blueBtn() {
		g2.setStroke(new BasicStroke(2.0f));
		g2.setPaint(Color.blue);

	}

	public void greenBtn() {
		g2.setStroke(new BasicStroke(2.0f));
		g2.setPaint(Color.green);

	}

	public void blackBtn() {
		g2.setStroke(new BasicStroke(2.0f));
		g2.setPaint(Color.black);

	}

	public void highlightBtn() {
		float alpha = 0.1f;
		Color color = new Color(1, 0.5f, 0, alpha);
		g2.setPaint(color);
		g2.setStroke(new BasicStroke(10.0f));

	}

	public void drawnow(int x1, int y1, int x2, int y2, Color color) {
		float alpha = 0.1f;
		Color c = new Color(1, 0.5f, 0, alpha);
		if (Objects.equals(color, c)) {
			g2.setStroke(new BasicStroke(10.0f));
		} else {
			g2.setStroke(new BasicStroke(2.0f));
		}
		g2.setPaint(color);
		g2.drawLine(x1, y1, x2, y2);
		repaint();
		g2.setPaint(Color.black);
	}

	private void connectBtn(String string) {
		if (isConnected == false) {
			accessCode = string;
			if (Objects.equals(accessCode, "amit")) {
				isConnected = true;
				JOptionPane.showMessageDialog(null, "You are now connected! ");

			} else {
				JOptionPane.showMessageDialog(null,
						"Wrong access code. Please try again!");

			}
		} else if (isConnected == true) {
			JOptionPane.showMessageDialog(null, "You are already Connected.");
		}

	}

	private void sendData() {
		if (isConnected == true) {
			String Msgout = ",";
			try {
				if (din.available() <= 0) {
					int flag = 0;
					Color clr;
					if (gui.cords.size() != 0) {
						for (Coordinates c : gui.cords) {
							String tempx = Integer.toString(c.getX());
							String tempy = Integer.toString(c.getY());
							clr = c.getColor();
							String tempc = "";
							if (clr == Color.black) {
								tempc = "black";
							} else if (clr == Color.blue) {
								tempc = "blue";
							} else if (clr == Color.red) {
								tempc = "red";
							} else if (clr == Color.green) {
								tempc = "green";
							} else {
								tempc = "highlight";
							}
							if (flag == 0) {
								Msgout = tempx + "," + tempy + "," + tempc;
								flag = 1;
							} else {
								Msgout = Msgout + "," + tempx + "," + tempy
										+ "," + tempc;
							}

						}
						dout.writeUTF(Msgout);
						System.out.println("Server:Data sent!");
						dout.flush();
						ss.close();
						gui.cords.clear();
					}
				}
			} catch (Exception e) {
				System.out.println("Server: Was not able to send!");
				//e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Please wait for the client to finish drawing.");
			}
		} else {
			gui.cords.clear();
		}
	}

}
