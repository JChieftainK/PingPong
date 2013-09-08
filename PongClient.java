import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class PongClient extends JApplet implements ActionListener{
	private JButton playButton;
	private JComboBox leftPaddleAI, rightPaddleAI;
	
	public void paint(Graphics g){
		super.paint(g);
	}
	
	public void init(){
		playButton = new JButton("Start");
		playButton.addActionListener(this);
		String[] temp = {"Default", "Justin", "Nathan"};
		leftPaddleAI = new JComboBox(temp);
		leftPaddleAI.setSelectedIndex(0);
		leftPaddleAI.addActionListener(this);
		rightPaddleAI = new JComboBox(temp);
		rightPaddleAI.setSelectedIndex(0);
		rightPaddleAI.addActionListener(this);
		JPanel myPanel = new JPanel();
		myPanel.setLayout(new GridLayout(1,3));
		myPanel.add(leftPaddleAI);
		myPanel.add(playButton);
		myPanel.add(rightPaddleAI);
		
		getContentPane().add(myPanel, BorderLayout.NORTH);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == playButton){
			System.out.println("PlayButton");
			playButton.setText("Stop");
		}
	}

}
