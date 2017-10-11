import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

	public static void main(String[] args) {
		login();
	}

	private static void login() {
		JFrame frame = new JFrame("Connect");
		frame.setBounds(400, 200, 200, 170);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JTextField name = new JTextField("Your name", 10);
		Dimension namePreferredSizeize = name.getPreferredSize();
		name.setBounds(10, 10, namePreferredSizeize.width, namePreferredSizeize.height);

		JTextField adres = new JTextField("localhost", 15);
		Dimension adresPreferredSize = adres.getPreferredSize();
		adres.setBounds(10, 40, adresPreferredSize.width, adresPreferredSize.height);

		JTextField port = new JTextField("6666", 4);
		Dimension portPreferredSize = port.getPreferredSize();
		port.setBounds(10, 70, portPreferredSize.width, portPreferredSize.height);

		JButton ok = new JButton("Ok");
		Dimension okpreferredSize = ok.getPreferredSize();
		ok.setBounds(10, 100, okpreferredSize.width, okpreferredSize.height);

		ok.addActionListener(e -> {
			frame.dispose();
			workSpace(name.getText(), adres.getText(), port.getText());
		});

		frame.setLayout(null);
		frame.add(name);
		frame.add(adres);
		frame.add(port);
		frame.add(ok);
		frame.setVisible(true);
	}

	private static void workSpace(String name, String adres, String port) {
		JFrame.setDefaultLookAndFeelDecorated(false);
		JFrame frame = new JFrame("Chated");

		frame.setBounds(400, 200, 400, 300);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JLabel label = new JLabel();
		JScrollPane srcpane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		label.setText("<html>");
		JTextField massage = new JTextField(50);

		try {
			InetAddress ipAdress = InetAddress.getByName(adres);
			Socket socket = new Socket(ipAdress, Integer.parseInt(port));
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			SpeackWithServer server = new SpeackWithServer(in, out, label);
			server.start();
			server.sendToServer(name);

			massage.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {/*Do nothing*/}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (massage.getText() != "") {

							server.sendToServer(massage.getText());
							massage.setText("");
						}
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {/*Do nothing*/}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		frame.add(label, BorderLayout.NORTH);
		frame.add(massage, BorderLayout.SOUTH);
		frame.setVisible(true);

	}


	static class SpeackWithServer extends Thread {
		BufferedReader reader;
		PrintWriter writer;
		JLabel label;

		SpeackWithServer(BufferedReader reader, PrintWriter writer, JLabel label) {
			this.reader = reader;
			this.writer = writer;
			this.label = label;
		}

		void sendToServer(String s) {
			writer.println(s);
		}


		private void takeFromServer() {
			try {
				while (true) {
					String s = reader.readLine() + "<br>";
					//s.replaceAll("\n","<br>");
					label.setText(label.getText() + s);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			while (true) {
				takeFromServer();
			}
		}
	}
}