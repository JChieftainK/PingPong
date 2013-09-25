import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Formatter;

/* This program was made to demonstrate a timer that can be paused.
 * There are a lot of lines of comments, but some of it is explaining other ways of doing things.
 * Also, I split a lot of them to multiple lines so that they're not too wide.
 * 
 * I hope the comments explain everything well enough, but if not feel free to ask me about it the next time you see me and I'll try to explain it better.
 * 
 * SuppressWarnings here is unnecessary to make the program run; it's just here to make Eclipse happy.
 */
@SuppressWarnings("serial")
public class SimpleTimer extends JFrame implements ActionListener {
	//Basic GUI stuff...
	private Container contents;
	private JPanel timerPanel, buttonPanel;
	
	/* There are only 2 buttons because the methods were made to be toggles.
	 * So startButton will both start and stop the timer, and pauseButton will both pause and resume it.  
	 */
	private JButton startButton, pauseButton;
	
	// GameTimer is the private class that implements Runnable.
	private GameTimer gameTime;
	
	public SimpleTimer() {
		// This is just making the basic GUI.
		super("Timer");
		contents = getContentPane();
		contents.setLayout(new GridLayout(2, 1));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		timerPanel = new JPanel();
		timerPanel.setLayout(new FlowLayout());
		
		startButton = new JButton("Start/Stop");
		startButton.addActionListener(this);
		buttonPanel.add(startButton);
		
		pauseButton = new JButton("Pause/Resume");
		pauseButton.addActionListener(this);
		buttonPanel.add(pauseButton);
		
		gameTime = new GameTimer();
		timerPanel.add(gameTime);
		
		contents.add(buttonPanel);
		contents.add(timerPanel);
		
		setSize(300, 200);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) {
		/* Again, it's just the 2 methods that toggle the state of the timer.
		 * startButton will both start and stop it,
		 * and pauseButton will both pause and resume it.
		 */
		if (ae.getSource() == startButton) {
			gameTime.start();
		} else if (ae.getSource() == pauseButton) {
			gameTime.pause();
		}
	}
	
	public static void main(String[] args) {
		SimpleTimer st = new SimpleTimer();
		st.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private class GameTimer extends JLabel implements Runnable {
		/* This is the special String used to format the Timer.
		 * %d tells it to look for a decimal number,
		 * %2d tells it to pad the number out to 2 decimal places,
		 * and %02d tells it to use '0' to pad out the number.
		 */
		private String format = new String("%d:%02d:%02d.%03d");
		
		//The Formatter is needed to actually use the format String. 
		private Formatter f;
		private long hours, minutes, seconds, thousandths;
		private boolean paused;
		
		/* I actually had to learn to do this differently than I had it in my Sudoku program...
		 * This time, the Thread is a field of the timer.
		 * Either way, it's done so that the thread can be directly referred to.
		 * I think it works better here, though, because now it never has to be null, so you don't have to check for that in other places.
		 */
		private Thread timerThread;
		
		public GameTimer() {
			super("0:00:00.000");
			hours = 0;
			minutes = 0;
			seconds = 0;
			thousandths = 0;
			paused = false;
			//Instantiating the Thread, setting it to manage this Runnable object.
			timerThread = new Thread(this);
		}
		
		public void start() {
			/* I did this differently, too.
			 * Now I used one master method that starts and stops the timer.
			 * If you'd rather split them up,
			 * remove the else block from this method and just use the reset method to stop it.
			 */
			if (!timerThread.isAlive()) {
				//If the thread is not already running, start it.
				timerThread.start();
			} else {
				//Otherwise, stop it and reset the timer.
				reset();
				/* Alternatively, within the else block have only this statement:
				 * 
				 * timerThread.interrupt();
				 * 
				 * and no reset method in the class.
				 * I explain this more in comments in the catch block of the run method,
				 * but the disadvantage is that your only method of starting or stopping the timer is to toggle it; you can't have a button that exclusively does one or the other.
				 */
			}
		}
		
		public synchronized void pause() {
			/* A single method to toggle pause and resume.
			 * 
			 * If you want to have them separate instead,
			 * a method that only pauses should just set paused to true,
			 * and a resume method should set it to false and run the synchronized block.
			 * 
			 * I *think* the run method itself has to be coded to respond to
			 * a boolean like this to actually be paused, but I'm not sure...
			 * 
			 * Like I mention later, I can't remember if I was just using wait, or timerThread.wait,
			 * so that could've been the difference...
			 */
			paused = !paused;
			if (!paused) {
				/* I'm not 100% sure I understand what synchronized does...
				 * I *think* what it does is ensure that every thread can see
				 * the changes made within the synchronized block.
				 * 
				 * In this case, it would make sure that the main thread can see
				 * that timerThread restarted.
				 */
				synchronized(timerThread) {
					timerThread.notify();
				}
			}
		}
		
		public void reset() {
			/* This if statement is unnecessary with the program as-is.
			 * It's only called in the start method,
			 * and it already checks whether or not the thread is running.
			 * 
			 * It's safer this way, though,
			 * so that you can call it in other places without adding that check.
			 */
			if (timerThread.isAlive()) {
				timerThread.interrupt();
			}
			
			/* In my Sudoku game I set the thread to null,
			 * but again this is better because now timerThread is never null.
			 */
			timerThread = new Thread(this);
			hours = 0;
			minutes = 0;
			seconds = 0;
			thousandths = 0;
			paused = false;
			
			//This is just to reset the text along with the numbers.
			f = new Formatter();
			f.format(format, hours, minutes, seconds, thousandths);
			this.setText(f.toString());
		}
		
		public void run() {
			try {
				while (true) {
					/* This... was annoying to work out. :P
					 * I think I know why it has to be this way, though.
					 * The thread is only running this method, so to pause the timer you have to use this method.
					 * The way I tried it before, it would freeze the whole program.
					 * 
					 * I can't remember if I was using timerThread.wait, which is what is needed,
					 * or just wait, which would freeze whatever thread called the method
					 * (which was the main thread... *facepalm*).
					 * 
					 * You're supposed to put the wait method in an infinite loop,
					 * because apparently threads in Java can resume for no reason...
					 * 
					 * Again, I *think* synchronized ensures that any other threads can see
					 * what changes are made within its block...
					 */
					if (paused) {
						synchronized(timerThread) {
							while (paused) {
								timerThread.wait();
							}
						}
					}
					
					/* On to the main part of the timer, and it looks confusing
					 * It's not really, though, I just wrote it to use fewer lines.
					 * The shortcut operator means that the number will be updated before the check is run.
					 * 
					 * The nested statements are to increase performance
					 * (you don't need to check minutes if seconds haven't changed, etc).
					 * 
					 * I'm not sure what would happen to this timer if the computer is under heavy load while it's running...
					 * 
					 * I would *think* that it'd make the timer lag,
					 * but in for instance a game it wouldn't matter because the whole game would be lagging.
					 * 
					 * Just something to note (and perhaps test :P ), though.
					 */
					if (++thousandths == 1000) {
						thousandths %= 1000;
						if (++seconds == 60) {
							seconds %= 60;
							if (++minutes == 60) {
								minutes %= 60;
								++hours;
							}
						}
					}
					
					/* And then this part is just to update the text on the label,
					 * with a delay so that this method runs once every millisecond.
					 */
					f = new Formatter();
					f.format(format, hours, minutes, seconds, thousandths);
					this.setText(f.toString());
					Thread.sleep(1);
				}
			} catch (InterruptedException ie) {
				/* And... do nothing here.
				 * For a timer, there's not really anything special you need to do if it's interrupted.
				 * You can rearrange the code, though.
				 * You could make the timer reset itself here,
				 * basically just fill it with everything from the reset method except for the interrupt statement.
				 * 
				 * So put all this:
				 * 
				 * timerThread = new Thread(this);
				 * hours = 0;
				 * minutes = 0;
				 * seconds = 0;
				 * thousandths = 0;
				 * paused = false;
				 * f = new Formatter();
				 * f.format(format, hours, minutes, seconds, thousandths);
				 * this.setText(f.toString());
				 * 
				 * here, *instead* of in the reset method, and have timerThread.interrupt be in
				 * the start method in the else statement's block instead of a call to the reset method.
				 * 
				 * I tested it and it does work that way,
				 * it's just that then anything that you want to stop the timer can also start it.
				 */
			}
			
		}
		
	}
	
}
