import java.util.List;
import java.util.ArrayList;

public abstract class Board 
{
	private Card[] cards;
	
	private List<Card> cardList;
	
	private List<Card> dealerCardList;
	
	private int totalPoints = 0;
	
	private int dealerTotalPoints = 0;
	
	// dealerStopsAt represents the standard casino dealer rule,
	// which states that the dealer stands when he has 17 or more.
	private final static int dealerStopsAt = 17;

	private Deck deck;

	private static final boolean I_AM_DEBUGGING = false;

	public Board(int size, String[] ranks, String[] suits, int[] pointValues)
	{
		cards = new Card[size];
		cardList = new ArrayList<Card>();
		dealerCardList = new ArrayList<Card>();
		deck = new Deck(ranks, suits, pointValues);
		if (I_AM_DEBUGGING) 
		{
			System.out.println(deck);
			System.out.println("----------");
		}
		dealMyCards();
	}
	
	public void newGame() 
	{
		deck.shuffle();
		cardList = new ArrayList<Card>();
		dealerCardList = new ArrayList<Card>();
		totalPoints = 0;
		dealerTotalPoints = 0;
		dealMyCards();
	}

	public int size() 
	{
		return cards.length;
	}
	
	public int cardListSize()
	{
		return cardList.size();
	}
	
	public int dealerCardListSize()
	{
		return dealerCardList.size();
	}

	public boolean isEmpty()
	{
		for (int k = 0; k < cards.length; k++) 
		{
			if (cards[k] != null) 
			{
				return false;
			}
		}
		return true;
	}

	public void deal(int k)
	{
		cardList.add(deck.deal());
		dealerCardList.add(deck.deal());
	}
	
	public void deal()
	{
		if(totalPoints < 21)
		{
			Card myCard = deck.deal();
			cardList.add(myCard);
			totalPoints = calculateTotalPoints(cardList);
		}
	}
	
	public void dealerDeal()
	{
		while (dealerTotalPoints < dealerStopsAt)
		{
			Card dealerCard = deck.deal();
			dealerCardList.add(dealerCard);
			dealerTotalPoints = calculateTotalPoints(dealerCardList);
		}
	}
	
	// This algorithm takes into account whether an ace card should
	// have a point value of 1 or 11, depending on the rest of your hand.
	public int calculateTotalPoints(List<Card> cards)
	{
		int total1 = 0;
		int total2 = 0;
		
		for (int x = 0; x < cards.size(); x++)
		{
			if (cards.get(x).rank().equals("ace"))
			{
				total1 += 1;
				total2 += 11;
			}
			else
			{
				total1 += cards.get(x).pointValue();
				total2 += cards.get(x).pointValue();
			}
		}
		if (total1 <= 21 && total2 <= 21)
		{
			return max(total1, total2);
		}
		else 
		{
			return min(total1, total2);
		}
	}
	
	public int max(int a, int b)
	{
		if (a > b)
			return a;
		else
			return b;
	}
	
	public int min(int a, int b)
	{
		if (a < b)
			return a;
		else
			return b;
	}
	
	public int youWin()
	{
		if (totalPoints > 21)
			return -1;
		else if (dealerTotalPoints > 21)
			return 1;
		else if (dealerTotalPoints > totalPoints)
			return -1;
		else if (dealerTotalPoints == totalPoints)
			return 0;
		else
			return 1;		
	}
	
	public int getTotalPoints()
	{
		return totalPoints;
	}
	
	public int getDealerTotalPoints()
	{
		return dealerTotalPoints;
	}

	public int deckSize() 
	{
		return deck.size();
	}

	public Card cardAt(int k)
	{
		return cardList.get(k);
	}

	public Card dealerCardAt(int k)
	{
		return dealerCardList.get(k);
	}
	
	public void dealACard()
	{
		deal();
	}

	public List<Integer> cardIndexes() 
	{
		List<Integer> selected = new ArrayList<Integer>();
		for (int k = 0; k < cards.length; k++) 
		{
			if (cards[k] != null)
		    {
				selected.add(new Integer(k));
			}
		}
		return selected;
	}

	public String toString() 
	{
		String s = "";
		for (int k = 0; k < cards.length; k++) 
		{
			s = s + k + ": " + cards[k] + "\n";
		}
		return s;
	}

	public boolean gameIsWon() 
	{
		if (deck.isEmpty()) 
		{
			for (Card c : cards) 
			{
				if (c != null) 
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	// This is called by the initDisplay method in the GUI class.
	// This method deals two cards to both the dealer and the player.
	private void dealMyCards() 
	{
		for (int k = 0; k < 2; k++) 
		{
			Card myCard = deck.deal();
			cardList.add(myCard);
			Card dealerCard = deck.deal();
			dealerCardList.add(dealerCard);
		}
		
		totalPoints = calculateTotalPoints(cardList);
		dealerTotalPoints = calculateTotalPoints(dealerCardList);
	}
}