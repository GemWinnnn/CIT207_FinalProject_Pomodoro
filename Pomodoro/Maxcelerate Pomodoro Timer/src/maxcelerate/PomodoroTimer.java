package maxcelerate;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.UIManager.*;
import javax.swing.border.Border;

import net.miginfocom.swing.*; // For MigLayout

@SuppressWarnings("serial")
class PomodoroTimer extends JFrame
{
	// Instance variables
	private final JPanel topPanel;

	// Custom Components
	private Color skyBlue = new Color(0, 153, 255); // RGB Codes for skyBlue.. Used as Background.
	private Font timerStyle = new Font("MonoAlphabet", Font.BOLD, 140); // Used for displaying the timer values.
	private Color cardinalRed = new Color(189, 32, 49); // RGB codes for cardinalRed.. Used as Background for buttons.
	private Font formBTStyles = new Font("Existence", Font.BOLD, 16); // Used in the buttons in the Pomodoro Timer section.
	private Font delayLabelStyles = new Font("Existence", Font.ITALIC, 20); // Used in the buttons in the Pomodoro Timer section.
	private Color indiaGreen = new Color(19, 136, 8); // RGB code for indiaGreen.. Used as background when the timer is paused.

	private static final int ORIGINAL_COUNTDOWN_MINUTES = 25;
	private static final int ORIGINAL_COUNTDOWN_SECONDS = 0;
	private static final int ORIGINAL_SHORTBREAK_MINUTES = 5;
	private static final int ORIGINAL_SHORTBREAK_SECONDS = 0;
	private static final int ORIGINAL_LONGBREAK_MINUTES = 15;
	private static final int ORIGINAL_LONGBREAK_SECONDS = 0;
	private static final int TOTAL_DELAY_TIME = 30;
	private static final int INTERVAL = 1000; // Iteration interval for the Timers.
	private static final int ONE_POMODORO_CYCLE = 8; // No of rounds in a single Pomodoro cycle.

	private JPanel timerPane;
	private JLabel minuteLabel;
	private JLabel separator;
	private JLabel secondLabel;
	private JLabel delayRemainingLabel;
	private JButton startPauseBT;
	private JButton stopBT;
	private JButton continueBT;
	private JButton startTimerBT;
	private boolean isTimerRunning = false;
	private Icon startIcon;
	private Icon pauseIcon;
	private Icon stopIcon;
	private Icon skipIcon;
	private Timer countDown;
	private Timer shortTimer;
	private Timer longTimer;
	private Timer delayTimer;
	private int secondsRemaining;
	private int minutesRemaining;
	private int delayRemaining;
	private int roundsCompleted; // No. of Pomodoro rounds completed.

	private JPanel welcomePanel;
	private JTextField userNameField;
	private JButton continueButton;

	private String userName; // To store the user's name

	class RoundedBorder implements Border {
		private int radius;
		private Color borderColor;
	
		public RoundedBorder(int radius, Color borderColor) {
			this.radius = radius;
			this.borderColor = borderColor;
		}
	
		@Override
		public Insets getBorderInsets(Component c) {
			int borderWidth = 2; // Adjust the border width as needed
			return new Insets(borderWidth, borderWidth, borderWidth, borderWidth);
		}
	
		@Override
		public boolean isBorderOpaque() {
			return true;
		}
	
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(borderColor);
			g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
		}
	}

	private void initializeWelcomePanel() {
		welcomePanel = new JPanel(new MigLayout("align center, center", "[grow]", "[center]20[center]20[center]"));
		welcomePanel.setBackground(skyBlue);
	
		JLabel welcomeLabel = new JLabel("Enter your name:");
		welcomeLabel.setForeground(Color.white);
		welcomePanel.add(welcomeLabel, "align center, center, wrap");
	
		userNameField = new JTextField(20);
	
		// Create a custom rounded border with thicker lines and more rounded corners
		Border roundedBorder = new RoundedBorder(20, Color.BLACK); // Adjust the radius as needed
		userNameField.setBorder(BorderFactory.createCompoundBorder(roundedBorder, BorderFactory.createEmptyBorder(5, 10, 5, 10)));
	
		// Initially set a reasonable number of columns
		welcomePanel.add(userNameField, "align center, center, growx, width 200!, wrap");
	
		continueButton = new JButton("Continue");
		continueButton.setActionCommand("Continue");
		welcomePanel.add(continueButton, "align center, center, wrap");
	
		continueButton.addActionListener((ActionEvent event) -> {
			if (event.getActionCommand().equals("Continue")) {
				this.userName = userNameField.getText(); // Store the user name
				topPanel.remove(welcomePanel);
				topPanel.add(addMainTimer());
				topPanel.revalidate();
				topPanel.repaint();
			}
		});
	}
	
	public PomodoroTimer()
	{
		setTitle("FocusFlow");

		// Setting up the Nimbus Look and Feel of the GUI application
		try
		{
			for(LookAndFeelInfo info: UIManager.getInstalledLookAndFeels())
			{
				if("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}

			// To disable 'Space' button to act as a click for buttons if it has focus.
			//InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
			//im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
			//im.put(KeyStroke.getKeyStroke("released SPACE"), "none");
		}
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			// If Nimbus is not available, you can set the GUI to default look and feel.
		}

		initializeCountDownTimer();

		topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(900, 594));
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel, BorderLayout.CENTER);
		roundsCompleted = 0;

		topPanel.add(addMainTimer());
		initializeWelcomePanel();
		topPanel.add(welcomePanel);
	}

	/**
	 * @return
	 */
	private JPanel addMainTimer()
	{
		timerPane = new JPanel(new MigLayout("align center, center", "[grow]", "[][]0[]"));
		timerPane.setBackground(skyBlue);

		JLabel welcomeMessage = new JLabel("Welcome, " + this.userName);
		welcomeMessage.setForeground(Color.white);
		welcomeMessage.setFont(new Font("Arial", Font.BOLD, 24));
		timerPane.add(welcomeMessage, "cell 0 0, center, wrap"); // Position the label at the top center

		minuteLabel = new JLabel(String.format("%02d", ORIGINAL_COUNTDOWN_MINUTES));
		minuteLabel.setForeground(Color.white);
		minuteLabel.setFont(timerStyle);
		timerPane.add(minuteLabel, "split 3, gapright 20, gaptop 20, pushx, spanx, alignx center, height 145!");

		separator = new JLabel(":");
		separator.setForeground(Color.white);
		separator.setFont(timerStyle);
		timerPane.add(separator, "alignx center, gapright 20, height 145!");

		secondLabel = new JLabel(String.format("%02d", ORIGINAL_COUNTDOWN_SECONDS));
		secondLabel.setForeground(Color.white);
		secondLabel.setFont(timerStyle);
		timerPane.add(secondLabel, "alignx center, height 145!, wrap");

		startTimerBT = new JButton("Start");
		startTimerBT.setBackground(cardinalRed);
		startTimerBT.setContentAreaFilled(false);
		startTimerBT.setForeground(Color.white);
		startTimerBT.setFont(formBTStyles);
		timerPane.add(startTimerBT, "alignx center, gaptop 10, wrap");

		startIcon = new ImageIcon("E:/RarFiles/Files/School Works/2nd Year/CIT 207/Final Project/Pomodoro/Maxcelerate Pomodoro Timer/src/Play.png");
		pauseIcon = new ImageIcon("E:/RarFiles/Files/School Works/2nd Year/CIT 207/Final Project/Pomodoro/Maxcelerate Pomodoro Timer/src/Pause.png");
		startPauseBT = new JButton(startIcon);
		startPauseBT.setContentAreaFilled(false);
		startPauseBT.setBackground(indiaGreen);
		startPauseBT.setActionCommand("Start"); // Set initial action command to "Start"
		startPauseBT.setForeground(Color.white);
		startPauseBT.setFont(formBTStyles);
		timerPane.add(startPauseBT, "gaptop 10, alignx center, split 3, spanx, pushx");


		skipIcon = new ImageIcon("E:/RarFiles/Files/School Works/2nd Year/CIT 207/Final Project/Pomodoro/Maxcelerate Pomodoro Timer/src/Skip.png");
		continueBT = new JButton(skipIcon);
		continueBT.setBackground(cardinalRed);
		continueBT.setContentAreaFilled(false);
		continueBT.setForeground(Color.white);
		continueBT.setFont(formBTStyles);
		continueBT.setVisible(true);
		timerPane.add(continueBT, "alignx center, hidemode 0, gapleft 30, gapright 30");

		stopIcon = new ImageIcon("E:/RarFiles/Files/School Works/2nd Year/CIT 207/Final Project/Pomodoro/Maxcelerate Pomodoro Timer/src/Stop.png");
		stopBT = new JButton(stopIcon);
		stopBT.setContentAreaFilled(false);
		stopBT.setBackground(cardinalRed);
		stopBT.setForeground(Color.white);
		stopBT.setFont(formBTStyles);
		timerPane.add(stopBT, "alignx center, wrap");

		delayRemainingLabel = new JLabel("Resumes in " + TOTAL_DELAY_TIME + " seconds");
		delayRemainingLabel.setForeground(Color.white);
		delayRemainingLabel.setVisible(false); 
		delayRemainingLabel.setFont(delayLabelStyles);
		timerPane.add(delayRemainingLabel, "alignx center");

		startPauseBT.addActionListener((ActionEvent event) -> {
			switch (event.getActionCommand()) {
				case "Pause":
					if (countDown != null && isTimerRunning) {
						countDown.stop();
						isTimerRunning = false;
						startPauseBT.setIcon(startIcon);
						startPauseBT.setActionCommand("Start");
					}
					break;
				case "Start":
					if (countDown != null && !isTimerRunning) {
						countDown.start();
						isTimerRunning = true;
						startPauseBT.setIcon(pauseIcon);
						startPauseBT.setActionCommand("Pause");
					}
					break;
				default:
					break;
			}
		});

		startTimerBT.addActionListener((ActionEvent event) -> {
			// Start the timer only if it's not running
			if (!isTimerRunning) {
				countDown.start();
				isTimerRunning = true;
				startPauseBT.setIcon(pauseIcon);
				startPauseBT.setActionCommand("Pause");
			}
		});

		continueBT.addActionListener((ActionEvent event) -> {
			if (event.getActionCommand().equals("SkipShortTimer") && shortTimer != null) {
				shortTimer.stop();
				roundsCompleted++;
			} else if (event.getActionCommand().equals("SkipLongTimer") && longTimer != null) {
				longTimer.stop();
				roundsCompleted = 0; // Timer is Reset.
			}
		
			if (delayTimer != null && delayTimer.isRunning()) {
				delayTimer.stop();
				delayRemainingLabel.setVisible(false);
			}
		
			startPauseBT.setVisible(true);
			continueBT.setVisible(false);
			stopBT.setVisible(true);
		});

		stopBT.addActionListener((ActionEvent event) -> {
			// Handle the stop button click event here
			if (countDown != null && countDown.isRunning()) {
				countDown.stop();
			}
			if (shortTimer != null && shortTimer.isRunning()) {
				shortTimer.stop();
			}
			if (longTimer != null && longTimer.isRunning()) {
				longTimer.stop();
			}
			
			// Reset the timer values
			minutesRemaining = ORIGINAL_COUNTDOWN_MINUTES;
			secondsRemaining = ORIGINAL_COUNTDOWN_SECONDS;
			
			// Update the UI
			minuteLabel.setText(String.format("%02d", minutesRemaining));
			secondLabel.setText(String.format("%02d", secondsRemaining));
			isTimerRunning = false;
			startPauseBT.setIcon(startIcon);
			startPauseBT.setActionCommand("Start");
			
			// Make the start button visible
			startPauseBT.setVisible(true);
			
			// Show the continue button only if it was previously visible
			if (continueBT.isVisible()) {
				continueBT.setVisible(true);
			}
		});
		

		return timerPane;
	}

	private void runMainTimer()
	{
		//System.out.println("Start Main " + String.format("%d", roundsCompleted));
		minutesRemaining = ORIGINAL_COUNTDOWN_MINUTES;
		secondsRemaining = ORIGINAL_COUNTDOWN_SECONDS;

		minuteLabel.setText(String.format("%02d", ORIGINAL_COUNTDOWN_MINUTES));
		secondLabel.setText(String.format("%02d", ORIGINAL_COUNTDOWN_SECONDS));

		if(roundsCompleted == ONE_POMODORO_CYCLE)
		{
			//System.out.println("Stop Long " + roundsCompleted);
			longTimer.stop();
			roundsCompleted = 0; // Timer is Reset.
		}

		else if(roundsCompleted > 0 && roundsCompleted % 2 == 0)
		{
			//System.out.println("Stop Short " + roundsCompleted);
			shortTimer.stop();
		}
		/*else
		{
			System.out.println("Don't Stop Main " + roundsCompleted);
		}*/

		countDown = new Timer(INTERVAL, (ActionEvent event) -> {
                    if(secondsRemaining == ORIGINAL_COUNTDOWN_SECONDS)
                    {
                        if(minutesRemaining == 0)
                        {
                            //startPauseBT.setText("Begin");
                            //startPauseBT.setActionCommand("Start");
                            //mainCompleted = false;
                            roundsCompleted++;
                            
                            // Selection of which break timer to run.
                            if(roundsCompleted == ONE_POMODORO_CYCLE)
                            {
                                runLongTimer();
                            }
                            else if(roundsCompleted > 0 && roundsCompleted % 2 == 0)
                            {
                                runShortTimer();
                            }
                        }
                        else
                        {
                            minutesRemaining -= 1;
                            secondsRemaining = 59;
                            minuteLabel.setText(String.format("%02d", minutesRemaining));
                            secondLabel.setText(String.format("%02d", secondsRemaining));
                        }
                    }
                    else
                    {
                        if(secondsRemaining > ORIGINAL_COUNTDOWN_SECONDS)
                        {
                            secondsRemaining -= 1;
                            secondLabel.setText(String.format("%02d", secondsRemaining));
                        }
                    }
                });
		
		countDown.start();
	}

	private void runShortTimer()
	{
		//System.out.println("Start Short " + String.format("%d", roundsCompleted));
		minutesRemaining = ORIGINAL_SHORTBREAK_MINUTES;
		secondsRemaining = ORIGINAL_SHORTBREAK_SECONDS;
		stopBT.setVisible(false);
		startPauseBT.setVisible(false);
		continueBT.setVisible(true);
		continueBT.setActionCommand("SkipShortTimer");
		countDown.stop();

		minuteLabel.setText(String.format("%02d", ORIGINAL_SHORTBREAK_MINUTES));
		secondLabel.setText(String.format("%02d", ORIGINAL_SHORTBREAK_SECONDS));

		shortTimer = new Timer(INTERVAL, (ActionEvent event) -> {
                    if(secondsRemaining == ORIGINAL_SHORTBREAK_SECONDS)
                    {
                        if(minutesRemaining == 0)
                        {
                            continueBT.setVisible(false);
                            stopBT.setVisible(true);
                            startPauseBT.setVisible(true);
                            //roundsCompleted++;
                            runMainTimer();
                        }
                        else
                        {
                            minutesRemaining -= 1;
                            secondsRemaining = 59;
                            minuteLabel.setText(String.format("%02d", minutesRemaining));
                            secondLabel.setText(String.format("%02d", secondsRemaining));
                        }
                    }
                    else
                    {
                        secondsRemaining -= 1;
                        secondLabel.setText(String.format("%02d", secondsRemaining));
                    }
                });

		shortTimer.start();
	}

	private void runLongTimer()
	{
		//System.out.println("Start Long " + String.format("%d", roundsCompleted));
		minutesRemaining = ORIGINAL_LONGBREAK_MINUTES;
		secondsRemaining = ORIGINAL_LONGBREAK_SECONDS;
		stopBT.setVisible(false);
		startPauseBT.setVisible(false);
		continueBT.setVisible(true);
		continueBT.setActionCommand("SkipLongTimer");
		countDown.stop();

		minuteLabel.setText(String.format("%02d", ORIGINAL_LONGBREAK_MINUTES));
		secondLabel.setText(String.format("%02d", ORIGINAL_LONGBREAK_SECONDS));

		longTimer = new Timer(INTERVAL, (ActionEvent event) -> {
                    if(secondsRemaining == 0)
                    {
                        if(minutesRemaining == 0)
                        {
                            continueBT.setVisible(false);
                            stopBT.setVisible(true);
                            startPauseBT.setVisible(true);
                            //roundsCompleted++;
                            runMainTimer();
                        }
                        else
                        {
                            minutesRemaining -= 1;
                            secondsRemaining = 59;
                            minuteLabel.setText(String.format("%02d", minutesRemaining));
                            secondLabel.setText(String.format("%02d", secondsRemaining));
                        }
                    }
                    else
                    {
                        secondsRemaining -= 1;
                        secondLabel.setText(String.format("%02d", secondsRemaining));
                    }
                });

		longTimer.start();
	}

	private void countDownPaused() {
		delayRemaining = TOTAL_DELAY_TIME;
		delayRemainingLabel.setVisible(true);
		startPauseBT.setVisible(false);
		stopBT.setVisible(false);
		countDown.stop();
	
		if (delayTimer != null) {
			delayTimer.stop();
		}
	
		delayTimer = new Timer(INTERVAL, (ActionEvent event) -> {
			if (delayRemaining > 0) {
				delayRemainingLabel.setText("Resumes in " + delayRemaining + " seconds");
				delayRemaining--;
			} else {
				delayTimer.stop();
				delayRemainingLabel.setVisible(false);
				startPauseBT.setIcon(pauseIcon);
				delayRemainingLabel.setText("Resumes in " + TOTAL_DELAY_TIME + " seconds");
				countDown.start();
				stopBT.setEnabled(true);
				startPauseBT.setVisible(true);
				startPauseBT.requestFocusInWindow();
			}
		});
	
		delayTimer.start();
	}

	// Add a method to initialize the countDown timer
	private void initializeCountDownTimer() {
		countDown = new Timer(INTERVAL, (ActionEvent event) -> {
			if (secondsRemaining == ORIGINAL_COUNTDOWN_SECONDS) {
				if (minutesRemaining == 0) {
					// Handle timer completion by resetting the clock
					minutesRemaining = ORIGINAL_COUNTDOWN_MINUTES;
					secondsRemaining = ORIGINAL_COUNTDOWN_SECONDS;
					minuteLabel.setText(String.format("%02d", minutesRemaining));
					secondLabel.setText(String.format("%02d", secondsRemaining));
	
					// Stop the timer and update the UI
					countDown.stop();
					isTimerRunning = false;
					startPauseBT.setIcon(startIcon);
					startPauseBT.setActionCommand("Start");
				} else {
					minutesRemaining--;
					secondsRemaining = 59;
					minuteLabel.setText(String.format("%02d", minutesRemaining));
					secondLabel.setText(String.format("%02d", secondsRemaining));
				}
			} else {
				secondsRemaining--;
				secondLabel.setText(String.format("%02d", secondsRemaining));
			}
		});
	}
	

    private static void runGUI()
    {
    	PomodoroTimer mainFrame = new PomodoroTimer();
    	mainFrame.pack();
    	mainFrame.setLocationRelativeTo(null);
    	mainFrame.setVisible(true);
    	mainFrame.setResizable(false);
    	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String args[])
	{
		SwingUtilities.invokeLater(() -> {
                    runGUI();
                });
	}
}
