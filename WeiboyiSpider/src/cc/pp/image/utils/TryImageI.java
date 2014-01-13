package cc.pp.image.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TryImageI extends JFrame {

	private static final long serialVersionUID = 1L;

	public JPanel jp = new JPanel() {

		private static final long serialVersionUID = 1L;

		Image img = Toolkit.getDefaultToolkit().getImage("image/2.jpg");
		Image img1 = img.getScaledInstance(141, 102, Image.SCALE_SMOOTH);
		Image img2 = img.getScaledInstance(70, 53, Image.SCALE_SMOOTH);

		@Override
		public void paint(Graphics g) {
			g.drawImage(img, 10, 10, this);
			g.drawImage(img1, 300, 10, this);
			g.drawImage(img2, 460, 10, this);
		}
	};

	public TryImageI() {
		this.add(jp);
		this.setTitle("图像缩放演示");
		this.setBounds(100, 100, 550, 260);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public static void main(String[] args) {
		new TryImageI();
	}

}

