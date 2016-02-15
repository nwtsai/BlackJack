public class BlackjackBoard extends Board 
{
	private static final int BOARD_SIZE = 8;

	private static final String[] RANKS =
		{"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

	private static final String[] SUITS =
		{"spades", "hearts", "diamonds", "clubs"};

	private static int[] POINT_VALUES =
		{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
	
	 public BlackjackBoard() 
	 {
	 	super(BOARD_SIZE, RANKS, SUITS, POINT_VALUES);
	 }	
}