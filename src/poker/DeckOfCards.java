/* Darragh O'Keeffe
 * 14702321
 * COMP 30050 Assignment 2
 * 07/02/2017
 */

package poker;

import java.util.Random;
import java.util.Vector;

public class DeckOfCards {
	//Constant to hold possible number of cards
	private static final int DECK_SIZE = 52;
	
	//DeckOfCards instance variables
	private Vector<PlayingCard> deck = new Vector<PlayingCard>(DECK_SIZE);
	private Vector<PlayingCard> returnedCards = new Vector<PlayingCard>(DECK_SIZE);
	
	// Reorganises the deck in a new random order by swapping cards 52^2 times
	public void shuffle(){
		int firstCard, secondCard;
		PlayingCard temp;
		Random randomGenerator = new Random();
		for (int i=0;i<deck.size()*deck.size();i++){
			firstCard = randomGenerator.nextInt(deck.size());
			secondCard = randomGenerator.nextInt(deck.size());
			temp = deck.elementAt(firstCard);
			deck.setElementAt(deck.elementAt(secondCard), firstCard);
			deck.setElementAt(temp, secondCard);
		}
	}
	
	public void returnCard(PlayingCard c){
		synchronized(returnedCards){
			returnedCards.add(c);
		}
		return;
	}
	
	// Returns the card at deck[cardsUsed] if the deck is non-empty
	// Returns a special Null card otherwise
	public PlayingCard deal(){
		synchronized(deck){
			if (deck.isEmpty()){
				if (returnedCards.isEmpty()){
					//If no cards have been returned to the deck and all cards have been dealt, return a null card
					PlayingCard nullCard = new PlayingCard("null", ' ', 0, 0);
					return nullCard;
				} else{
					//add returned cards to the deck and shuffle
					deck.addAll(returnedCards);
					returnedCards.clear();
					shuffle();
				}
			}
			return deck.remove(0);
		}
	}
	
	// method for creating an array of 52 PlayingCard objects
	//  (used to avoid code duplication between reset() and constructor)
	private void initialiseDeck(){
		int noOfCardTypes = 13;
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
			deck.add(new PlayingCard(type, PlayingCard.CLUBS, faceValue, gameValue));
			deck.add(new PlayingCard(type, PlayingCard.DIAMONDS, faceValue, gameValue));
			deck.add(new PlayingCard(type, PlayingCard.HEARTS, faceValue, gameValue));
			deck.add(new PlayingCard(type, PlayingCard.SPADES, faceValue, gameValue));
		}
		shuffle();
	}
	
	// resets and shuffles the deck to begin a new round 
	public void reset(){
		deck.clear();
		returnedCards.clear();
		initialiseDeck();
	}
	
	// Returns the number of cards in the deck that can be dealt
	public int cardsLeft(){
		return deck.size()+returnedCards.size();
	}
	
	// Returns a string containing the toString representation of all cards
	//  that have not yet been dealt from the deck
	public String toString(){
		deck.addAll(returnedCards);
		returnedCards.clear();
		String output = "";
		for (int i=0;i<deck.size();i++){
			output += deck.elementAt(i).toString()+" ";
		}
		return output;
	}
	
	// Empty Public Constructor initialises and shuffles the deck and sets cardsUsed=0
	public DeckOfCards(){
		initialiseDeck();
		shuffle();
	}
	
	//tests DeckOfCards Class
	public static void main(String[] args){
		DeckOfCards deck = new DeckOfCards();
		
		//Tests for dealing cards and for dealing from an empty deck
		System.out.println("Deal 52 cards and print toString representation");
		PlayingCard toBeReturned = deck.deal();
		System.out.println(toBeReturned.toString());
		for (int i=0;i<51;i++){
			System.out.println(deck.deal().toString());
		}
		System.out.println("\nAttempt to deal a 53rd card and print toString representation");
		System.out.println(deck.deal().toString());
		
		//return the first card dealt from the first deck and print deck
		deck.returnCard(toBeReturned);
		System.out.println("Print deck with card returned:");
		System.out.println(deck.toString());
		
		//test for shuffle and deck toString method
		deck.reset();
		System.out.println("\nTest shuffle and DeckOfCards.toString() method");
		System.out.println(deck.toString());
	}
}