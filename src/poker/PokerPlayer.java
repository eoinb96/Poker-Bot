/* Darragh O'Keeffe
 * 14702321
 * COMP 30050 Assignment 6
 * 10/03/2017
 */

package poker;

import java.util.Random;

/* Player class with ability to discard cards from a hand dealt from a deck
 *  which is passed as an argument to the constructor
 */
public class PokerPlayer {
	protected HandOfCards hand;
	protected DeckOfCards deck;
	protected String name = "";
	protected int numberOfChips;
	public int amountToCall=0;
	protected static final int STARTING_NUMBER_OF_CHIPS = 10;
	
	//public constructor takes a deck of cards and creates a new hand of cards
	public PokerPlayer(DeckOfCards d, String playerName){
		name = playerName;
		numberOfChips = STARTING_NUMBER_OF_CHIPS;
		deck = d;
	}
	
	public void newHand(){
		hand = new HandOfCards(deck);
	}
	
	public void returnCards(){
		hand.returnCards();
	}
	
	public boolean isHuman(){
		return false;
	}
	
	public String toString(){
		//returns details of hand held by the player
		return (name + ": " + hand.toString() + " " + hand.getType() + ", Value: " + hand.getGameValue());
	}
	
	//returns the value of the hand held by the player 
	public int getHandValue(){
		return hand.getGameValue();
	}
	
	//returns the players name
	public String getName(){
		return name;
	}
	
	//returns number of chips held by the player
	public int getNumberOfChips(){
		return numberOfChips;
	}
	
	//use negative argument for player losing chips
	public void setNumberOfChips(int chips){
		numberOfChips+=chips;
	}
	
	//a player can open the betting if they have at least a pair
	public boolean canOpenBetting(){
		return hand.getGameValue()>HandOfCards.ONE_PAIR_DEFAULT_VALUE;
	}
	
	//A player is bust and will be removed from the game when they have no chips remaining
	public boolean isBust(){
		return (numberOfChips==0);
	}
	
}
