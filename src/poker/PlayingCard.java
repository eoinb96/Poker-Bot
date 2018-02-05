/* Darragh O'Keeffe
 * 14702321
 * COMP 30050 Assignment 1
 * 31/01/2017
 */

package poker;

/* Class to represent a playing card from a standard deck of 52 cards (jokers excluded).
 * Stores the type of the card, its suit, its face value and its value for the given game.
 */
public class PlayingCard {
	
	// Constants for suit variable of PlayingCard 
	static public final char CLUBS = 'C';
	static public final char DIAMONDS = 'D';
	static public final char HEARTS = 'H';
	static public final char SPADES = 'S';
	
	static public final int ACE_VALUE = 1;
	static public final int JACK_VALUE = 11;
	static public final int QUEEN_VALUE = 12;
	static public final int KING_VALUE = 13;
	
	// PlayingCard instance variables
	private String type;
	private char suit;			
	private int faceValue;		
	private int gameValue;

	// Returns a string containing the type of the card
	public String getType(){
		return type;
	}
	
	// Returns a char containing the suit of the card
	public char getSuit(){
		return suit;
	}
	
	// Returns an int containing the faceValue of the card
	public int getFaceValue(){
		return faceValue;
	}
	
	// Returns an int containing the gameValue of the card
	public int getGameValue(){
		return gameValue;
	}
	
	// Returns a string representation of the card containing its type and suit
	// Eg. Ace of Hearts would have representation "AH"
	public String toString(){
		return type+suit;
	}
	
	// Public constructor for PlayingCard
	public PlayingCard(String type, char suit, int faceValue, int gameValue){
		this.type = type;
		this.suit = suit;
		this.faceValue = faceValue;
		this.gameValue = gameValue;
	}
	
	
	/* Main method tests PlayingCard by creating 52 different representations
	 * of playing cards and printing their toString() representations
	 */
	public static void main(String[] args){
		int deckSize = 52;
		PlayingCard[] cardArray = new PlayingCard[deckSize];
		int noOfCardTypes = 13;
		// Loop creates the 52 separate cards and adds them to cardArray
		for (int i=0;i<noOfCardTypes;i++){
			int faceValue = i+1;
			int gameValue = i+1; 
			String type;
			
			// switch statement sets the value of type to the correct value
			switch(i+1){
				case PlayingCard.ACE_VALUE:
					type = "A";
					gameValue = 14;
					break;
				case PlayingCard.JACK_VALUE:
					type = "J";
					break;
				case PlayingCard.QUEEN_VALUE:
					type = "Q";
					break;
				case PlayingCard.KING_VALUE:
					type = "K";
					break;
				default:
					type = Integer.toString(i+1);
			}
			
			// On each iteration of the loop, all 4 cards of the same type are made and added to the cardArray
			cardArray[i] = new PlayingCard(type, PlayingCard.CLUBS, faceValue, gameValue);
			cardArray[i+13] = new PlayingCard(type, PlayingCard.DIAMONDS, faceValue, gameValue);
			cardArray[i+26] = new PlayingCard(type, PlayingCard.HEARTS, faceValue, gameValue);
			cardArray[i+39] = new PlayingCard(type, PlayingCard.SPADES, faceValue, gameValue);
		}
		
		// Prints the string representation of all 52 cards
		for (int j=0;j<deckSize;j++){
			System.out.println(cardArray[j].toString());
		}
		System.exit(0);
	}
}
