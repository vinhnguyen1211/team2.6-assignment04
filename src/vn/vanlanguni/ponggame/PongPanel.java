/*
 * PONG GAME REQUIREMENTS
 * This simple "tennis like" game features two paddles and a ball, 
 * the goal is to defeat your opponent by being the first one to gain 3 point,
 *  a player gets a point once the opponent misses a ball. 
 *  The game can be played with two human players, one on the left and one on 
 *  the right. They use keyboard to start/restart game and control the paddles. 
 *  The ball and two paddles should be red and separating lines should be green. 
 *  Players score should be blue and background should be black.
 *  Keyboard requirements:
 *  + P key: start
 *  + Space key: restart
 *  + W/S key: move paddle up/down
 *  + Up/Down key: move paddle up/down
 *  
 *  Version: 0.5
 */
package vn.vanlanguni.ponggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;


/**
 * 
 * @author Invisible Man
 *
 */
public class PongPanel extends JPanel implements ActionListener, KeyListener, MouseMotionListener, MouseListener {
	private static final long serialVersionUID = -1097341635155021546L;

	private boolean showTitleScreen = true;
	private boolean playing;
	private boolean gameOver;

	/** Background. */
	private Color backgroundColor = Color.BLACK;

	/** State on the control keys. */
	private boolean upPressed;
	private boolean downPressed;
	private boolean wPressed;
	private boolean sPressed;

	/** The ball: position, diameter */
	private int ballX = 200;
	private int ballY = 200;
	private int diameter = 20;
	private int ballDeltaX = -1;
	private int ballDeltaY = 3;
	
	/** Player 1's paddle: position and size */
	private int playerOneX = 0;
	private int playerOneY = 250;
	private int playerOneWidth = 10;
	private int playerOneHeight = 60;

	/** Player 2's paddle: position and size */
	private int playerTwoX = 465;
	private int playerTwoY = 250;
	private int playerTwoWidth = 10;
	private int playerTwoHeight = 60;

	/** Speed of the paddle - How fast the paddle move. */
	private int paddleSpeed = 5;

	/** Player score, show on upper left and right. */
	private int playerOneScore;
	private int playerTwoScore;

	
	//
	Rectangle rect;
	int xRect =  130, yRect = 265, wRect = 200, hRect = 55;
	Rectangle rectSettings;
	int xSettings = 428, ySettings = yRect, wSettings = hRect, hSettings = hRect;
	//ImageIcon imagePlayGame, imageSettings;
	BufferedImage imagePlayGame, imageSettings;
	
	/** Construct a PongPanel. */
	public PongPanel() {
		setBackground(backgroundColor);

		// listen to key presses
		setFocusable(true);
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		
		//new rect
		rect = new Rectangle(xRect,yRect,wRect,hRect);
		rectSettings = new Rectangle(xSettings,ySettings,wSettings,hSettings);

		try {
			imagePlayGame = ImageIO.read(new File("images/btn_playgame.png"));
			imageSettings = ImageIO.read(new File("images/setting.png"));
			//Ball Image
			imgSoccerBall = ImageIO.read(new File(imgURL[0]));
			imgKABall = ImageIO.read(new File(imgURL[1]));
			imgMasterBall = ImageIO.read(new File(imgURL[2]));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		// call step() 60 fps
		Timer timer = new Timer(1000 / 60, this);
		timer.start();
		
		//create plus or minus
		initPlusOrMinus();
	}

	/** Implement actionPerformed */
	public void actionPerformed(ActionEvent e) {
		step();
	}

	/** Repeated task */
	public void step() {

		if (playing) {

			/* Playing mode */

			// move player 1
			// Move up if after moving, paddle is not outside the screen
			if (upPressed && playerOneY - paddleSpeed > 0) {
				playerOneY -= paddleSpeed;
			}
			// Move down if after moving paddle is not outside the screen
			if (downPressed && playerOneY + playerOneHeight + paddleSpeed < getHeight()) {
				playerOneY += paddleSpeed;
			}

			// move player 2
			// Move up if after moving paddle is not outside the screen
			if (wPressed && playerTwoY - paddleSpeed > 0) {
				playerTwoY -= paddleSpeed;
			}
			// Move down if after moving paddle is not outside the screen
			if (sPressed && playerTwoY + playerTwoHeight + paddleSpeed < getHeight()) {
				playerTwoY += paddleSpeed;
			}

			/*
			 * where will the ball be after it moves? calculate 4 corners: Left,
			 * Right, Top, Bottom of the ball used to determine whether the ball
			 * was out yet
			 */
			int nextBallLeft = ballX + ballDeltaX;
			int nextBallRight = ballX + diameter + ballDeltaX;
			// FIXME Something not quite right here
			int nextBallTop = ballY;
			int nextBallBottom = ballY + diameter;

			// Player 1's paddle position
			int playerOneRight = playerOneX + playerOneWidth;
			int playerOneTop = playerOneY;
			int playerOneBottom = playerOneY + playerOneHeight;

			// Player 2's paddle position
			float playerTwoLeft = playerTwoX;
			float playerTwoTop = playerTwoY;
			float playerTwoBottom = playerTwoY + playerTwoHeight;

			// ball bounces off top and bottom of screen
			if (nextBallTop < 0 || nextBallBottom > getHeight()) {
				ballDeltaY *= -1;
			}

			// will the ball go off the left side?
			if (nextBallLeft < playerOneRight) {
				// is it going to miss the paddle?
				if (nextBallTop > playerOneBottom || nextBallBottom < playerOneTop) {

					playerTwoScore++;

					// Player 2 Win, restart the game
					if (playerTwoScore == 3) {
						playing = false;
						gameOver = true;
					}
					ballX = 200;
					ballY = 200;
				} else {
					// If the ball hitting the paddle, it will bounce back
					
					ballDeltaX *= -1;
				}
			}

			// will the ball go off the right side?
			if (nextBallRight > playerTwoLeft) {
				// is it going to miss the paddle?
				if (nextBallTop > playerTwoBottom || nextBallBottom < playerTwoTop) {

					playerOneScore++;

					// Player 1 Win, restart the game
					if (playerOneScore == 3) {
						playing = false;
						gameOver = true;
					}
					ballX = 200;
					ballY = 200;
				} else {
					// If the ball hitting the paddle, it will bounce back
					
					ballDeltaX *= -1;
				}
			}

			// move the ball
			ballX += ballDeltaX;
			ballY += ballDeltaY;
		}

		// stuff has moved, tell this JPanel to repaint itself
		repaint();
	}

	/** Paint the game screen. */
	boolean hoverPlayGame;
	boolean hoverSettings;
	
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (showTitleScreen) {

			/* Show welcome screen */

			// Draw game title and start message
			g.setColor(Color.GREEN);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
			g.drawString("Pong Game", 130, 100);

			
			if(hoverPlayGame){
				g.setColor(Color.WHITE);
				g.fillRect(xRect, yRect, wRect, hRect);
				g.drawImage(imagePlayGame, xRect, yRect, xRect + wRect, yRect + hRect, 0, 0, 522, 186, null);
			}
			else{
				g.drawImage(imagePlayGame, xRect, yRect, xRect + wRect, yRect + hRect, 0, 0, 522, 186, null);
			}
			if(hoverSettings){
				g.drawImage(imageSettings, xSettings, ySettings, xSettings + wSettings, ySettings + hSettings, 0, 0, 300, 300, null);
			}
			else{
				g.drawImage(imageSettings, xSettings, ySettings, xSettings + wSettings, ySettings + hSettings, 0, 0, 300, 300, null);
			}
			
		} else if (playing) {

			/* Game is playing */

			// set the coordinate limit
			int playerOneRight = playerOneX + playerOneWidth;
			int playerTwoLeft = playerTwoX;

			// draw dashed line down center
			for (int lineY = 0; lineY < getHeight(); lineY += 50) {
				g.drawLine(250, lineY, 250, lineY + 25);
			}

			// draw "goal lines" on each side
			g.drawLine(playerOneRight, 0, playerOneRight, getHeight());
			g.drawLine(playerTwoLeft, 0, playerTwoLeft, getHeight());

			// draw the scores
			g.setColor(Color.BLUE);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
			g.drawString(String.valueOf(playerOneScore), 100, 100); // Player 1
																	// score
			g.drawString(String.valueOf(playerTwoScore), 400, 100); // Player 2
																	// score

			// draw the ball
			if(nImageBallIndex == 0){
				g.setColor(ballColor);
				g.fillOval(ballX, ballY, diameter, diameter);
			}
			else if(nImageBallIndex == 1){
				g.drawImage(imgSoccerBall, ballX, ballY, ballX + diameter, ballY + diameter, 0, 0, 256, 256, null);
			}
			else if (nImageBallIndex == 2){
				g.drawImage(imgKABall, ballX, ballY, ballX + diameter, ballY + diameter, 0, 0, 333, 328, null);
			}
			else if (nImageBallIndex == 3){
				g.drawImage(imgMasterBall, ballX, ballY, ballX + diameter, ballY + diameter, 0, 0, 400, 400, null);
			}
			// draw the paddles
			g.setColor(Color.RED);
			g.fillRect(playerOneX, playerOneY, playerOneWidth, playerOneHeight);
			g.fillRect(playerTwoX, playerTwoY, playerTwoWidth, playerTwoHeight);
			
		} else if (gameOver) {

			/* Show End game screen with winner name and score */

			// Draw scores
			g.setColor(Color.BLUE);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
			g.drawString(String.valueOf(playerOneScore), 100, 100);
			g.drawString(String.valueOf(playerTwoScore), 400, 100);

			// Draw the winner name
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
			if (playerOneScore > playerTwoScore) {
				g.drawString("Player 1 Wins!", 165, 200);
			} else {
				g.drawString("Player 2 Wins!", 165, 200);
			}

			// Draw Restart message
			g.setColor(Color.YELLOW);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
			g.drawString("Press 'Space' to restart the game", 150, 250);
			
			//reset score each player
			playerOneScore = playerTwoScore = 0;
		}
	}

	public void keyTyped(KeyEvent e) {
	
	}

	
	public void keyPressed(KeyEvent e) {
		if (showTitleScreen) {
			if (e.getKeyChar() == 'p') {
				showTitleScreen = false;
				playing = true;
			}
		} else if (playing) {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = true;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				downPressed = true;
			} else if (e.getKeyCode() == KeyEvent.VK_W) {
				wPressed = true;
			} else if (e.getKeyCode() == KeyEvent.VK_S) {
				sPressed = true;
			}
		} else if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
			gameOver = false;
			showTitleScreen = true;
			playerOneY = 250;
			playerTwoY = 250;
			ballX = 250;
			ballY = 250;
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			upPressed = false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			downPressed = false;
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			wPressed = false;
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			sPressed = false;
		}
		
	}
	
	public void mouseMoved(MouseEvent arg0) {
		//System.out.println(String.format("%d %d",arg0.getX(), arg0.getY()));
		if(showTitleScreen){
			//rectangle playgame
			if(rect.contains(arg0.getX(), arg0.getY())){
				hoverPlayGame = true;
			}
			else{
				hoverPlayGame = false;
			}
			
			//rectangle settings
			if(rectSettings.contains(arg0.getX(),arg0.getY())){
				hoverSettings = true;
			}
			else{
				hoverSettings = false;
			}
		}
	}
	
	JDialogSettings settings = new JDialogSettings();
	JDialogSettings ballSettings;
	
	public void mouseClicked(MouseEvent arg0) {
		
		if(showTitleScreen){
			if(hoverPlayGame){
				showTitleScreen = false;
				playing = true;
			}
			if(hoverSettings){
				
				settings.setModal(true);
				settings.setVisible(true);
				
				ballSettings = settings.getSettingsBall();
				if(ballSettings.getnIndexImageBall() == 0 ){
					nImageBallIndex = 0;
					setBallColor(ballSettings.getBallColor());
				}
				else{
					nImageBallIndex = ballSettings.getnIndexImageBall();
				}
				settings.dispose();
			}
		}
		
	}

	private int nImageBallIndex = 0;
	private Color ballColor = Color.RED;
	
	BufferedImage imgBallPlaying,imgSoccerBall, imgKABall, imgMasterBall;
	String[] imgURL = {
			"images/SoccerBall.png",
			"images/KA_Ball.png",
			"images/MasterBall.png"
	};
	
	private void setBallColor(Color ballColor) {
		this.ballColor = ballColor;
	}
	
	
	//
	private boolean ShowingMinus = false;
	int seconds;
	
	private void initPlusOrMinus(){
		
		if(!ShowingMinus){
			seconds = new Random().nextInt(15)+1;
			//int seconds = 5;
			System.out.println(seconds);
			Timer timerInitPlus = new Timer(seconds*1000, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Date date = new Date();
					System.out.println(date);
					
				}
			});
			timerInitPlus.start();
			timerInitPlus.setRepeats(false);
			seconds = new Random().nextInt(15)+1;
			timerInitPlus.setDelay(seconds*1000);
			timerInitPlus.setRepeats(true);
			
		}
		
		
	}
	
	
	
	public void mouseEntered(MouseEvent arg0) { }

	public void mouseExited(MouseEvent arg0) { }

	public void mousePressed(MouseEvent arg0) { }

	public void mouseReleased(MouseEvent arg0) { }

	public void mouseDragged(MouseEvent arg0) { }

	
}
