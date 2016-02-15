import java.net.URL;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.event.*;

/**
 * This class provides a GUI for solitaire games related to Elevens.
 */
public class CardGameGUI extends JFrame implements ActionListener
{
	/** Height of the game frame. */
	private static final int DEFAULT_HEIGHT = 575;
	/** Width of the game frame. */
	private static final int DEFAULT_WIDTH = 750;
	/** Width of a card. */
	private static final int CARD_WIDTH = 73;
	/** Height of a card. */
	private static final int CARD_HEIGHT = 97;
	/** Row (y coord) of the upper left corner of the first card. */
	private static final int LAYOUT_TOP = 30;
	/** Column (x coord) of the upper left corner of the first card. */
	private static final int LAYOUT_LEFT = 30;
	/** Distance between the upper left x coords of
	 *  two horizonally adjacent cards. */
	private static final int LAYOUT_WIDTH_INC = 100;
	/** Distance between the upper left y coords of
	 *  two vertically adjacent cards. */
	private static final int LAYOUT_HEIGHT_INC = 125;
	/** y coord of the "Replace" button. */
	private static final int BUTTON_TOP = 30;
	/** x coord of the "Replace" button. */
	private static final int BUTTON_LEFT = 470;
	/** Distance between the tops of the "Replace" and "Restart" buttons. */
	private static final int BUTTON_HEIGHT_INC = 50;
	/** y coord of the "n undealt cards remain" label. */
	private static final int LABEL_TOP = 160;
	/** x coord of the "n undealt cards remain" label. */
	private static final int LABEL_LEFT = 440;
	/** Distance between the tops of the "n undealt cards" and
	 *  the "You lose/win" labels. */
	private static final int LABEL_HEIGHT_INC = 35;
	
	private static final int MSG_FACTOR = 130;

	/** The board (Board subclass). */
	private Board board;

	/** The main panel containing the game components. */
	private JPanel panel;
	/** The Replace button. */
	private JButton dealButton;
	/** The Restart button. */
	private JButton restartButton;
	
	private JButton standButton;
	
	/** The "number of undealt cards remain" message. */
	private JLabel statusMsg;
	private JLabel pointMsg;
	private JLabel dealerPointMsg;

	/** The "you've won n out of m games" message. */
	private JLabel totalsMsg;
	/** The card displays. */
	private JLabel[] displayCards;
	private JLabel[] displayDealerCards;
	/** The win message. */
	private JLabel winMsg;
	/** The even message. */
	private JLabel evenMsg;
	/** The loss message. */
	private JLabel lossMsg;
	/** The coordinates of the card displays. */
	private Point[] cardCoords;
	private Point[] dealerCardCoords;

	/** kth element is true iff the user has selected card #k. */
	private boolean[] selections;
	/** The number of games won. */
	private int totalWins;
	/** The number of games played. */
	private int totalGames;
	
	private JTextField yourPoints;

	/**
	 * Initialize the GUI.
	 * @param gameBoard is a <code>Board</code> subclass.
	 */
	public CardGameGUI(Board gameBoard) 
	{
		board = gameBoard;
		totalWins = 0;
		totalGames = 0;

		// Initialize cardCoords using 5 cards per row
		cardCoords = new Point[board.size()];
		dealerCardCoords = new Point[board.size()];
		int x = LAYOUT_LEFT;
		int y = LAYOUT_TOP;
		y = y + 2*LAYOUT_HEIGHT_INC;
		for (int i = 0; i < cardCoords.length; i++) 
		{
			cardCoords[i] = new Point(x, y);
			if (i % 5 == 3) 
			{
				x = LAYOUT_LEFT;
				y += LAYOUT_HEIGHT_INC;
			}
			else 
			{
				x += LAYOUT_WIDTH_INC;
			}
		}
		x = LAYOUT_LEFT;
		y = LAYOUT_TOP;
		for (int i = 0; i < dealerCardCoords.length; i++) 
		{
			dealerCardCoords[i] = new Point(x, y);
			if (i % 5 == 3) 
			{
				x = LAYOUT_LEFT;
				y += LAYOUT_HEIGHT_INC;
			}
			else 
			{
				x += LAYOUT_WIDTH_INC;
			}
		}

		selections = new boolean[board.size()];
		initDisplay();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		repaint();
	}

	/**
	 * Run the game.
	 */
	public void displayGame() 
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				setVisible(true);
			}
		});
	}

	/**
	 * Draw the display (cards and messages).
	 */
	public void repaint() 
	{
		for (int k = 0; k < board.cardListSize(); k++) 
		{
			String cardImageFileName = imageFileName(board.cardAt(k), selections[k]);
			URL imageURL = getClass().getResource(cardImageFileName);
			if (imageURL != null) 
			{
				ImageIcon icon = new ImageIcon(imageURL);
				displayCards[k].setIcon(icon);
				displayCards[k].setVisible(true);
			} 
			else 
			{
				throw new RuntimeException("Card image not found: \"" + cardImageFileName + "\"");
			}
		}
		for (int k = 0; k < board.dealerCardListSize(); k++) 
		{
			if (k == 0)
			{
				String cardImageFileName = imageFileName(null, selections[k]);
				URL imageURL = getClass().getResource(cardImageFileName);
				if (imageURL != null) 
				{
					ImageIcon icon = new ImageIcon(imageURL);
					displayDealerCards[k].setIcon(icon);
					displayDealerCards[k].setVisible(true);
				} 
				else 
				{
				throw new RuntimeException("Card image not found: \"" + cardImageFileName + "\"");
				}
			}
			else
			{
				String cardImageFileName = imageFileName(board.dealerCardAt(k), selections[k]);
				URL imageURL = getClass().getResource(cardImageFileName);
				if (imageURL != null) 
				{
					ImageIcon icon = new ImageIcon(imageURL);
					displayDealerCards[k].setIcon(icon);
					displayDealerCards[k].setVisible(true);
				} 
				else 
				{
					throw new RuntimeException("Card image not found: \"" + cardImageFileName + "\"");
				}
			}
		}
		statusMsg.setText(board.deckSize() + " undealt cards remain."); 
		statusMsg.setVisible(true);
		pointMsg.setText("" + board.getTotalPoints());
		pointMsg.setVisible(true);
		
		totalsMsg.setText("You've won " + totalWins + " out of " + totalGames + " games.");
		totalsMsg.setVisible(true);
		pack();
		panel.setBackground(new Color (40,182,59));
		panel.repaint();
	}
	
	public void revealRepaint() 
	{
		for (int k = 0; k < board.cardListSize(); k++) 
		{
			String cardImageFileName =
				imageFileName(board.cardAt(k), selections[k]);
			URL imageURL = getClass().getResource(cardImageFileName);
			if (imageURL != null) 
			{
				ImageIcon icon = new ImageIcon(imageURL);
				displayCards[k].setIcon(icon);
				displayCards[k].setVisible(true);
			} 
			else 
			{
				throw new RuntimeException("Card image not found: \"" + cardImageFileName + "\"");
			}
		}
		for (int k = 0; k < board.dealerCardListSize(); k++) 
		{
			String cardImageFileName = imageFileName(board.dealerCardAt(k), selections[k]);
			URL imageURL = getClass().getResource(cardImageFileName);
			if (imageURL != null) 
			{
				ImageIcon icon = new ImageIcon(imageURL);
				displayDealerCards[k].setIcon(icon);
				displayDealerCards[k].setVisible(true);
			} 
			else 
			{
				throw new RuntimeException(
					"Card image not found: \"" + cardImageFileName + "\"");
			}
		}
		statusMsg.setText(board.deckSize() + " undealt cards remain."); 
		statusMsg.setVisible(true);
		pointMsg.setText("" + board.getTotalPoints());
		pointMsg.setVisible(true);
		dealerPointMsg.setText("" + board.getDealerTotalPoints());
		dealerPointMsg.setVisible(true);
		totalsMsg.setText("You've won " + totalWins + " out of " + totalGames + " games.");
		totalsMsg.setVisible(true);

		pack();
		panel.setBackground(new Color (40,182,59));
		panel.repaint();
	}
	
	

	/**
	 * Initialize the display.
	 */
	private void initDisplay()	
	{
		panel = new JPanel()
		{
			public void paintComponent(Graphics g) 
			{
				super.paintComponent(g);
			}
		};

		// If board object's class name follows the standard format
		// of ...Board or ...board, use the prefix for the JFrame title
		String className = board.getClass().getSimpleName();
		int classNameLen = className.length();
		int boardLen = "Board".length();
		String boardStr = className.substring(classNameLen - boardLen);
		if (boardStr.equals("Board") || boardStr.equals("board")) {
			int titleLength = classNameLen - boardLen;
			setTitle(className.substring(0, titleLength));
		}

		// Calculate number of rows of cards (5 cards per row)
		// and adjust JFrame height if necessary
		int numCardRows = (board.size() + 4) / 5;
		int height = DEFAULT_HEIGHT;
		if (numCardRows > 2)
		{
			height += (numCardRows - 2) * LAYOUT_HEIGHT_INC;
		}

		this.setSize(new Dimension(DEFAULT_WIDTH, height));
		panel.setLayout(null);
		panel.setPreferredSize(
			new Dimension(DEFAULT_WIDTH - 20, height - 20));
		displayCards = new JLabel[board.size()];
		displayDealerCards = new JLabel[board.size()];
		for (int k = 0; k < board.size(); k++) 
		{
			displayCards[k] = new JLabel();
			panel.add(displayCards[k]);
			displayCards[k].setBounds(cardCoords[k].x, cardCoords[k].y,
										CARD_WIDTH, CARD_HEIGHT);
			displayCards[k].addMouseListener(new MyMouseListener());
			selections[k] = false;
		}
		for (int k = 0; k < board.size(); k++) 
		{
			displayDealerCards[k] = new JLabel();
			panel.add(displayDealerCards[k]);
			displayDealerCards[k].setBounds(dealerCardCoords[k].x, dealerCardCoords[k].y,
										CARD_WIDTH, CARD_HEIGHT);
			displayDealerCards[k].addMouseListener(new MyMouseListener());
		}
		dealButton = new JButton();
		dealButton.setText("Deal");
		panel.add(dealButton);
		dealButton.setBounds(BUTTON_LEFT + 50, BUTTON_TOP*12 - 50, 100, 30);
		dealButton.addActionListener(this);

		restartButton = new JButton();
		restartButton.setText("Restart");
		panel.add(restartButton);
		restartButton.setBounds(BUTTON_LEFT + 50, BUTTON_TOP*12 + BUTTON_HEIGHT_INC*2 - 50, 100, 30);
		restartButton.addActionListener(this);
		
		standButton = new JButton();
		standButton.setText("Stand");
		panel.add(standButton);
		standButton.setBounds(BUTTON_LEFT + 50, BUTTON_TOP*12 + BUTTON_HEIGHT_INC - 50, 100, 30);
		standButton.addActionListener(this);
		
		statusMsg = new JLabel(
		board.deckSize() + " undealt cards remain.");
		panel.add(statusMsg);
		statusMsg.setBounds(LABEL_LEFT + 50, LABEL_TOP*2 + BUTTON_HEIGHT_INC*4 - 15, 250, 30);
		
		pointMsg = new JLabel("");
		panel.add(pointMsg);
		pointMsg.setBounds(LABEL_LEFT + 90, 120, 150, 150);
		pointMsg.setFont(new Font("Arial", Font.BOLD, 50));
		//pointMsg.setBorder
		
		dealerPointMsg = new JLabel("");
		panel.add(dealerPointMsg);
		dealerPointMsg.setBounds(LABEL_LEFT + 90, 10, 150, 150);
		dealerPointMsg.setFont(new Font("Arial", Font.BOLD, 50));

		winMsg = new JLabel();
		winMsg.setBounds(LABEL_LEFT + 70, LABEL_TOP + LABEL_HEIGHT_INC + MSG_FACTOR - 100, 200, 30);
		winMsg.setFont(new Font("SansSerif", Font.BOLD, 25));
		winMsg.setForeground(Color.GREEN);
		winMsg.setText("You win!");
		panel.add(winMsg);
		winMsg.setVisible(false);

		lossMsg = new JLabel();
		lossMsg.setBounds(LABEL_LEFT + 38, LABEL_TOP + LABEL_HEIGHT_INC + MSG_FACTOR - 100, 200, 30);
		lossMsg.setFont(new Font("SanSerif", Font.BOLD, 25));
		lossMsg.setForeground(Color.RED);
		lossMsg.setText("Sorry, you lose.");
		panel.add(lossMsg);
		lossMsg.setVisible(false);
		
		evenMsg = new JLabel();
		evenMsg.setBounds(LABEL_LEFT + 70, LABEL_TOP + LABEL_HEIGHT_INC + MSG_FACTOR - 100, 200, 30);
		evenMsg.setFont(new Font("SanSerif", Font.BOLD, 25));
		evenMsg.setForeground(Color.ORANGE);
		evenMsg.setText("You tied!");
		panel.add(evenMsg);
		evenMsg.setVisible(false);

		totalsMsg = new JLabel("You've won " + totalWins + " out of " + totalGames + " games.");
		totalsMsg.setBounds(LABEL_LEFT + 50, LABEL_TOP + 9 * LABEL_HEIGHT_INC - 15, 250, 30);
		panel.add(totalsMsg);
		
		JTextArea scoreboard = new JTextArea(60,60);
		scoreboard.setBounds(LABEL_LEFT + 50, LABEL_TOP - 40, 150, 40);
		scoreboard.setFont(new Font("Helvetica", Font.BOLD, 25));
		scoreboard.setForeground(Color.BLACK);
		scoreboard.setBackground(new Color (225,225,225));
		scoreboard.setText("Your Points");
		panel.add(scoreboard);
		scoreboard.setEditable(false);
		scoreboard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		
		JTextArea dealerScoreboard = new JTextArea(60,60);
		dealerScoreboard.setBounds(LABEL_LEFT + 39, LABEL_TOP - 150, 170, 40);
		dealerScoreboard.setFont(new Font("Helvetica", Font.BOLD, 25));
		dealerScoreboard.setForeground(Color.BLACK);
		dealerScoreboard.setBackground(new Color (225,225,225));
		dealerScoreboard.setText("Dealer Points");
		panel.add(dealerScoreboard);
		dealerScoreboard.setEditable(false);
		dealerScoreboard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		
		/*JTextField yourPoints = new JTextField(50);
		yourPoints.setBounds(LABEL_LEFT, LABEL_TOP, 200,30);
		yourPoints.setFont(new Font("Helvetica", Font.BOLD, 25));
		yourPoints.setForeground(Color.BLUE);
		yourPoints.setBackground(new Color (225,225,225));
		yourPoints.setText("You have" + board.getTotalPoints() + " points.");
		panel.add(yourPoints);
		//yourPoints.setEditable(false);*/
		
		pack();
		getContentPane().add(panel);
		getRootPane().setDefaultButton(dealButton);
		panel.setVisible(true);
	}

	/**
	 * Deal with the user clicking on something other than a button or a card.
	 */
	private void signalError()
	{
		Toolkit t = panel.getToolkit();
		t.beep();
	}

	/**
	 * Returns the image that corresponds to the input card.
	 * Image names have the format "[Rank][Suit].GIF" or "[Rank][Suit]S.GIF",
	 * for example "aceclubs.GIF" or "8heartsS.GIF". The "S" indicates that
	 * the card is selected.
	 *
	 * @param c Card to get the image for
	 * @param isSelected flag that indicates if the card is selected
	 * @return String representation of the image
	 */
	private String imageFileName(Card c, boolean isSelected) 
	{
		String str = "cards/";
		if (c == null)
	    {
			return "cards/back1.GIF";
		}
		str += c.rank() + c.suit();
		if (isSelected)
		{
			str += "S";
		}
		str += ".GIF";
		return str;
	}

	/**
	 * Respond to a button click (on either the "Replace" button
	 * or the "Restart" button).
	 * @param e the button click action event
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource().equals(dealButton))
		{	
			board.dealACard();
			if (board.getTotalPoints() > 21)
			{
				board.dealerDeal();
				signalLoss();
				revealRepaint();
			}
			else
			{
				repaint();
			}
		}
		else if (e.getSource().equals(restartButton)) 			
		{
			standButton.setVisible(true);
			dealButton.setVisible(true);
			board.newGame();
			getRootPane().setDefaultButton(dealButton);
			winMsg.setVisible(false);
			lossMsg.setVisible(false);
			evenMsg.setVisible(false);
			dealerPointMsg.setVisible(false);

			for (int k = 0; k < displayCards.length; k++)
			{
				displayCards[k].setVisible(false);
				displayDealerCards[k].setVisible(false);
			}
			repaint();
		} 
		else if (e.getSource().equals(standButton))
		{
			board.dealerDeal();
			int winOrLose = board.youWin();	

			if (winOrLose < 0)
			{
				signalLoss();
			}
			else if (winOrLose == 0)
			{
				signalEven();
			}
			else
			{
				signalWin();
			}
			revealRepaint();
		}
		else 
		{
			signalError();
			return;
		}
	}
	/**
	 * Display a win.
	 */
	private void signalWin() 
	{
		getRootPane().setDefaultButton(restartButton);
		lossMsg.setVisible(false);
		evenMsg.setVisible(false);
		winMsg.setVisible(true);
		totalWins++;
		totalGames++;
		dealButton.setVisible(false);
		standButton.setVisible(false);
	}
	
	private void signalEven() 
	{
		getRootPane().setDefaultButton(restartButton);
		lossMsg.setVisible(false);
		winMsg.setVisible(false);
		evenMsg.setVisible(true);
		totalGames++;
		dealButton.setVisible(false);
		standButton.setVisible(false);
	}

	/**
	 * Display a loss.
	 */
	private void signalLoss() 
	{
		getRootPane().setDefaultButton(restartButton);
		winMsg.setVisible(false);
		evenMsg.setVisible(false);
		lossMsg.setVisible(true);
		totalGames++;
		dealButton.setVisible(false);
		standButton.setVisible(false);
	}

	/**
	 * Receives and handles mouse clicks.  Other mouse events are ignored.
	 */
	private class MyMouseListener implements MouseListener 
	{

		/**
		 * Handle a mouse click on a card by toggling its "selected" property.
		 * Each card is represented as a label.
		 * @param e the mouse event.
		 */
		public void mouseClicked(MouseEvent e) 
		{
			/*for (int k = 0; k < board.size(); k++) {
				if (e.getSource().equals(displayCards[k])
						&& board.cardAt(k) != null) {
					selections[k] = !selections[k];
					repaint();
					return;
				}
			}
			signalError();*/
		}

		/**
		 * Ignore a mouse exited event.
		 * @param e the mouse event.
		 */
		public void mouseExited(MouseEvent e) 
		{
		}

		/**
		 * Ignore a mouse released event.
		 * @param e the mouse event.
		 */
		public void mouseReleased(MouseEvent e)
	    {
		}

		/**
		 * Ignore a mouse entered event.
		 * @param e the mouse event.
		 */
		public void mouseEntered(MouseEvent e) 
		{
		}

		/**
		 * Ignore a mouse pressed event.
		 * @param e the mouse event.
		 */
		public void mousePressed(MouseEvent e) 
		{
		}
	}
}