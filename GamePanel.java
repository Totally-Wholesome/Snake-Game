import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
public class GamePanel extends JPanel implements ActionListener {
	
	static final int SCREEN_WIDTH = 600; // static variables to store the size of the window, size of the game grid, and how fast the game runs
	static final int SCREEN_HEIGHT = 600;
	static final int SQUARE_SIZE = 50;
	static final int GRID_SQUARES = (SCREEN_WIDTH * SCREEN_HEIGHT) / SQUARE_SIZE;
	static final int DELAY = 75;
	
	final int[] x = new int[GRID_SQUARES]; // array to store the location of the body parts of the snake and score
	final int[] y = new int[GRID_SQUARES];
	int segments = 4;
	int score;
	
	int foodXLocation; // where the food is on the game board
	int foodYLocation;
	char direction = 'E'; // north, south, east, west
	boolean running = false;
	boolean gameWon = false;
	Timer timer = new Timer(DELAY, this);
	Random random = new Random();
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new SnakeKeyAdapter());
		startGame();
	}
	
	public void startGame() {
		drawFood();
		running = true;
		timer.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void drawFood() {
       foodXLocation = random.nextInt(SCREEN_WIDTH / SQUARE_SIZE) * SQUARE_SIZE;
       foodYLocation = random.nextInt(SCREEN_HEIGHT / SQUARE_SIZE) * SQUARE_SIZE;
      
       for (int i = 0; i < segments; i++) {
           if (x[i] == foodXLocation && y[i] == foodYLocation) {
               drawFood();
               break;
           }
       }
   }
	
	public void draw(Graphics g) {
		if (running) {	
			g.setColor(Color.red);
			g.fillOval(foodXLocation, foodYLocation, SQUARE_SIZE, SQUARE_SIZE);
			
			for (int i = 0; i < segments; i++) {
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], SQUARE_SIZE, SQUARE_SIZE);
				} else {
					g.setColor(new Color(45, 200, 0));
					g.fillRect(x[i], y[i], SQUARE_SIZE, SQUARE_SIZE);
				}
			}
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + score, (SCREEN_WIDTH - metrics.stringWidth("Score: " + score)) / 2, g.getFont().getSize());
			
			if (score == 10) {
				timer.stop();
				gameWon = true;
				gameWin(g);
			}
		} else {
			gameOver(g);
		}
	}
	
	public void move() {
		for (int i = segments; i > 0 ; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		switch (direction) {
		case 'N':
			y[0] = y[0] - SQUARE_SIZE;
			break;
		case 'S':
			y[0] = y[0] + SQUARE_SIZE;
			break;
		case 'E':
			x[0] = x[0] + SQUARE_SIZE;
			break;
		case 'W':
			x[0] = x[0] - SQUARE_SIZE;
			break;
		}
	}
	
	public void checkCollisions() {
		for (int i = 1; i < segments; i++) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
			
			if (x[0] < 0) {
				running = false;
			}
			
			if (x[0] >= SCREEN_WIDTH) {
				running = false;
			}
			
			if (y[0] < 0) {
				running = false;
			}
			
			if (y[0] >= SCREEN_HEIGHT) {
				running = false;
			}
			
			if (!running) {
				timer.stop();
			}
		}
	}
	
	public void checkFood() {
		if ((x[0] == foodXLocation) && (y[0] == foodYLocation)) {
			segments++;
			score++;
			drawFood();
		}
	}
	
	public void gameOver(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: " + score, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + score)) / 2, g.getFont().getSize());
		g.drawString("Press R to restart", (SCREEN_WIDTH - metrics1.stringWidth("Press R to restart")) / 2, (SCREEN_HEIGHT / 3) * 2);
		
		g.setFont(new Font("Arial", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over!", (SCREEN_WIDTH - metrics2.stringWidth("Game Over!")) / 2, SCREEN_HEIGHT / 2);
	}
	
	public void gameWin(Graphics g) {
		g.setColor(new Color(255, 215, 0));
		g.setFont(new Font("Arial", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("You Win!", (SCREEN_WIDTH - metrics.stringWidth("You Win!")) / 2, SCREEN_HEIGHT / 2);
	}
	
	public void restartGame() {
	    segments = 4;
	    score = 0;
	    direction = 'E';
	   
	    for (int i = 0; i < segments; i++) {
	        x[i] = 100 - (i * SQUARE_SIZE);
	        y[i] = 100;
	    }
	    drawFood();
	    running = true;
	    timer.start();
	    repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			move();
			checkFood();
			checkCollisions();
		}
		repaint();
	}
	
	public class SnakeKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'E') {
					direction = 'W';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'W') {
					direction = 'E';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction != 'S') {
					direction = 'N';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'N') {
					direction = 'S';
				}
				break;
			case KeyEvent.VK_R: // Restart the game
               if (!running || gameWon) {
                   restartGame();
               }
               break;
			}
		}
	}
}
