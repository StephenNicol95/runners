 package runners;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import brickbreaker.BrickBreaker;
import leaderboard.LeaderboardClient;
import leaderboard.LeaderboardServer;
import pong.PongPanel;
import snake.Window;
import spaceinvaders.GameCanvas;

public class Main{
	public static final boolean DEBUGGINGMODE = false;
	public static final String RUNNINGLOCATION = System.getProperty("user.dir");
	public static final boolean USINGECLIPSE = false;
	public static final Dimension PONGDIMENSIONS = new Dimension(500,500);
	public static final Dimension SPACEINVADERSDIMENSIONS = new Dimension(600,600);
	public static final Dimension SNAKEDIMENSIONS = new Dimension(300,300);
	public static final Dimension BRICKBREAKERDIMENSIONS = new Dimension(350,450);
	public static boolean OPTIONSELECTED = false;
	private static JFrame frame;
	private static final File[] AUDIOFILES = new File(RUNNINGLOCATION + (USINGECLIPSE ? "/songs" : "\\songs")).listFiles();
	private static boolean musicPlaying = false;
	//Buttons have to stay in this order, otherwise !0.equals("Play Snake"), etc
	private static final String[] BUTTONS = { "Play Snake",
											  "Play Pong",
											  "Play Brick Breaker",
											  "Play Space Invaders",
											  "Show Leaderboards",
											  "Show Controls"};
	
	public static void main(String[] args) {
		//If specified to start the server, then start the server
		if(args.length > 0) {
			if(args[0].toLowerCase().contains("server")) {
				int port = Integer.parseInt(args.length > 1 && !args[1].equals("") ? args[1] : "20090");
				LeaderboardServer leaderboardServer = new LeaderboardServer(port);
				leaderboardServer.startServer();
			}
		} else {
			//or else start the games menu, then the games!
			frame = new JFrame("Portable Arcade");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			
			//Load menu, store option, reopen window for the game
			loadMenu(frame);
		}
	}
    
	private static void loadMenu(JFrame frame) {
		int width = 300;
		int height = 40 + (BUTTONS.length * 100);
		frame.setSize(width, height);
		
		int x = ((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2) - (int)(width/2));
		int y = ((int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2) - (int)(height/2));
		frame.setLocation(x, y);
		
		Dimension buttonSize = new Dimension(280,40);
		JButton[] buttons = new JButton[BUTTONS.length];
		
		//Initialize all of the JButtons & set their text
		for(int i = 0; i < buttons.length; i++) {
			buttons[i] = new JButton();
			buttons[i].setText(BUTTONS[i]);
		}
		
		//Add each button's action Listener (what will each button do if we click it?)
		buttons[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.repaint();
				int x = (int)frame.getLocation().getX();
				int y = (int)frame.getLocation().getY();
				loadSnake(x,y);
			}
		});
		buttons[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.repaint();
				int x = (int)frame.getLocation().getX();
				int y = (int)frame.getLocation().getY();
				loadPong(x,y);
			}
		});
		buttons[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.repaint();
				int x = (int)frame.getLocation().getX();
				int y = (int)frame.getLocation().getY();
				loadBrickBreaker(x,y,new JFrame());
			}
		});
		buttons[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.repaint();
				int x = (int)frame.getLocation().getX();
				int y = (int)frame.getLocation().getY();
				loadSpaceInvaders(x,y,new JFrame());
			}
		});
		buttons[4].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.repaint();
				loadLeaderboards((int)frame.getLocation().getX(), (int)frame.getLocation().getY());
			}
		});
		buttons[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.repaint();
				loadControls((int)frame.getLocation().getX(),(int)frame.getLocation().getY());
			}
		});
		
		//Create JPanels for each button
		JPanel[] panels = new JPanel[BUTTONS.length];
		
		//If running windows, the frame needs to start loading now
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			frame.setVisible(true);
		}
		
		//Initialize panels; for each panel, set its size and add the button functionality
		for(int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel();
			panels[i].setSize(buttonSize);
			panels[i].add(buttons[i]);
		}
		
		//Define the button's width (roughly 10 pixels less than the full width
		int buttonWidth = (frame.getWidth()/2)-(buttonSize.width/2)-10;
		
		//Add each panel to the frame
		for(int i = 0; i < panels.length; i++) {
			frame.add(panels[i]);
		}
		
		//Set the location of all of the buttons, excluding spaceinvaders (as expected)
		for(int i = 0; i < panels.length; i++) {
			panels[i].setLocation(buttonWidth,(frame.getHeight() - 140) - (100*i));
		}
		//spaceInvadersPanel.setLocation(buttonWidth,2);
		//This actually doesn't do anything since spaceInvaders refuses to adjust 
		//		position on the JFrame for whatever reason..
		
		if(!System.getProperty("os.name").toLowerCase().contains("windows")) {
			frame.setVisible(true);
		}
	}

	private static void loadSnake(int x, int y) {
		//Snake:
        Window f1 = new Window();
        f1.setTitle("Snake");
        f1.setSize(SNAKEDIMENSIONS);
        f1.setLocation(x,y);
        f1.setVisible(true);
        f1.setJMenuBar(menubarsetup());
        f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	f1.addWindowListener(newMain.INSTANCE);
    }
    
    private static void loadPong(int x, int y) {
		JFrame frame = new JFrame();
    	//Pong:
        PongPanel pongPanel = new PongPanel();
        
        frame.add(pongPanel, BorderLayout.CENTER);
        frame.setSize(PONGDIMENSIONS);
        frame.setLocation(x, y);
        frame.setVisible(true);
        frame.setJMenuBar(menubarsetup());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.addWindowListener(newMain.INSTANCE);
    }
    
    private static void loadBrickBreaker(int x, int y, JFrame frame) {
    	BrickBreaker game = new BrickBreaker();
    	JButton button = new JButton ("Restart");
    	frame.setSize(BRICKBREAKERDIMENSIONS);
    	frame.setLocation(x,y);
    	
    	frame.add(game);
    	frame.add(button, BorderLayout.SOUTH);
    	frame.setJMenuBar(menubarsetup());
    	frame.setLocationRelativeTo(null);
    	frame.setResizable(false);
    	frame.setVisible(true);
    	button.addActionListener(game);
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.addWindowListener(newMain.INSTANCE);
    	
    	game.addKeyListener(game);
    	game.setFocusable(true);
    	
    	//Start the game
    	Thread ballController = new Thread(game);
    	ballController.start();
    }
    
    private static void loadSpaceInvaders(int x, int y, JFrame frame) {
    	//Load JFrame contents
    	frame.setSize(600,600);
    	frame.setLocation(x,y);
    	frame.setResizable(false);
    	frame.setVisible(true);
    	try {
    		Image pic = ImageIO.read(new File(RUNNINGLOCATION + (Main.USINGECLIPSE ? "/src/spaceinvaders/resources/Enemy.jpg" : "\\spaceinvaders\\resources\\Enemy.jpg")));
    		frame.setIconImage(pic);
    	} catch(Exception e) {
    	}
    	
    	//Deal with actually launching the game
    	Container c = frame.getContentPane();
    	c.add(GameCanvas.getGameCanvas(false));
    	GameCanvas.getGameCanvas(false).grabFocus();
    	frame.setContentPane(c);
    	frame.repaint();
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.addWindowListener(newMain.INSTANCE);
    }
    
    @SuppressWarnings("serial")
	public static void loadLeaderboards(int x, int y) {
    	//Get leaderboard data:
    	String[] leaderboards = new String[12];
    	if(!DEBUGGINGMODE) {
    		String[] temp = LeaderboardClient.getInstance().requestLeaderboardData();
    		for(int i = 0; i < leaderboards.length; i++) {
    			leaderboards[i] = temp[i];
    		}
    	} else {
    		for(int i = 0; i < leaderboards.length; i++) {
    			leaderboards[i] = "AAA 000";
    		}
    	}
    	
    	//Setup UI:
    	JFrame frame = new JFrame();
    	frame.setSize(300,430);
    	frame.setLocation(x,y);
    	frame.setResizable(false);
    	frame.getContentPane().add(new JPanel() {
    		public void paint(Graphics g) {
    			g.drawString("Space Invaders:", 50, 30);
    			g.drawString(leaderboards[0], 55, 45);
    			g.drawString(leaderboards[1], 50, 60);
    			g.drawString(leaderboards[2], 50, 75);
    			g.drawString("Snake:", 50, 130);
    			g.drawString(leaderboards[3], 50, 145);
    			g.drawString(leaderboards[4], 50, 160);
    			g.drawString(leaderboards[5], 50, 175);
    			g.drawString("Pong:", 50, 230);
    			g.drawString(leaderboards[6], 50, 245);
    			g.drawString(leaderboards[7], 50, 260);
    			g.drawString(leaderboards[8], 50, 275);
    			g.drawString("Brick Breaker:", 50, 330);
    			g.drawString(leaderboards[9], 50, 345);
    			g.drawString(leaderboards[10], 50, 360);
    			g.drawString(leaderboards[11], 50, 375);
    		}
    	});
    	frame.setVisible(true);
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.addWindowListener(newMain.INSTANCE);
    }
    
    @SuppressWarnings("serial")
	private static void loadControls(int x, int y) {
		JFrame frame = new JFrame();
		frame.setSize(430, 430);
		frame.setLocation(x,y);
		frame.getContentPane().add(new JPanel() {
			public void paint(Graphics g) {
				g.drawString("Snake:", 50, 30);
				g.drawString("To move snake left: [Left arrow key]", 50, 45);
				g.drawString("To move snake right: [Right arrow key]", 50, 60);
				g.drawString("To move snake up: [Up arrow key]", 50, 75);
				g.drawString("To move snake down: [Down arrow key]", 50, 90);
				g.drawString("Pong:", 50, 130);
				g.drawString("To move your paddle up: [Up arrow key], [W]", 50, 145);
				g.drawString("To move your paddle down: [Down arrow key], [S]", 50, 160);
				g.drawString("Brick Breaker:", 50, 230);
				g.drawString("To move your paddle left: [Left arrow key]", 50, 245);
				g.drawString("To move your paddle right: [Right arrow key]", 50, 260);
				g.drawString("Space Invaders:", 50, 330);
				g.drawString("To fire from your ship: [Space bar]", 50, 345);
				g.drawString("To move your ship left: [Left arrow key]", 50, 360);
				g.drawString("To move your ship right: [Right arrow key]", 50, 375);
			}
		});
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(newMain.INSTANCE);
	}
    
	public static JMenuBar menubarsetup() {
		JMenuBar jmenubar = new JMenuBar();
		JMenu jmenunew = new JMenu("New");
		JMenuItem equation = new JMenuItem("game");
		
		//Add features to JMenu
		equation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("New Game");
				String[] test = new String[0];
				
				main(test);
			}
		});
		jmenunew.add(equation);
		jmenubar.add(jmenunew);
		return jmenubar;
	}
	
	public static void startPlayingMusic(boolean repeat) {
		//This works with mp3 and wav files ONLY
		musicPlaying = true;
		Thread musicThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AudioInputStream audioInputStream;
					Clip clip;
					for(int i = 0; i < AUDIOFILES.length; i++) {
						//Safe-guard against files that aren't .mp3 or .wav in case someone tries to add
						//	.m4 or something weird that AudioInputStream doesn't like.
						if(AUDIOFILES[i].getAbsolutePath().contains(".mp3") || AUDIOFILES[i].getAbsolutePath().contains(".wav")) {
							if(repeat) {
								while(musicPlaying) {
									audioInputStream = AudioSystem.getAudioInputStream(AUDIOFILES[i]);
									clip = AudioSystem.getClip();
									clip.open(audioInputStream);
									clip.start();
									while(musicPlaying || !clip.isRunning()) {
									}
									if(clip.isRunning()) {
										clip.stop();
									}
								}
							} else {
								audioInputStream = AudioSystem.getAudioInputStream(AUDIOFILES[i]);
								clip = AudioSystem.getClip();
								clip.open(audioInputStream);
								clip.start();
								while(musicPlaying || !clip.isRunning()) {
								}
								if(clip.isRunning()) {
									clip.stop();
								}
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		musicThread.start();
	}
	
	public static void stopPlayingMusic() {
		musicPlaying = false;
	}
	
	private static class newMain implements WindowListener {
		public static newMain INSTANCE = new newMain();
		
		@Override
		public void windowOpened(WindowEvent e) {}
		@Override
		public void windowClosing(WindowEvent e) {}
		@Override
		public void windowClosed(WindowEvent e) {
			loadMenu(frame);
		}
		@Override
		public void windowIconified(WindowEvent e) {}
		@Override
		public void windowDeiconified(WindowEvent e) {}
		@Override
		public void windowActivated(WindowEvent e) {}
		@Override
		public void windowDeactivated(WindowEvent e) {}
	}
}