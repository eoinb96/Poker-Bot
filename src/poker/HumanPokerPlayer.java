package poker;

import java.util.Scanner;

public class HumanPokerPlayer extends PokerPlayer{
	
	
	/* The scanners are left open because closing them causes System.in to close
	 *  and this cannot be reopened without causing an exception
	 */
	
	public HumanPokerPlayer(DeckOfCards d, String playerName) {
		super(d, playerName);
	}
	
	public boolean isHuman(){
		return true;
	}

	/* Asks the user which cards they would like to discard. If the user enters
	 *  more than 3 cards only the first 3 will be discarded.
	 * 
	 */
	public boolean discard(String input){
		
		input = input.toLowerCase();
		input = input.substring(14);
		int commas = 0;
		int spaces = 0;
		boolean validInput = false;
		if (input.contains("n")){
			validInput=true;
			return validInput;
		}
		char nextChar;
		int cardsDiscarded=0;
		for (int i=0;i<input.length();i++){
			nextChar = input.charAt(i);
			if (nextChar>='0' && nextChar<='4'){
				int cardPosition = nextChar - '0';
				PlayingCard card = deck.deal();
				hand.discardAndReplace(cardPosition, card);
				cardsDiscarded++;
				validInput = true;
			}
			if(nextChar==','){ //To facilitate either format 0,1 or 01
				commas++;
			}
			if(nextChar==' '){ //To facilitate either format with spacing
				spaces++;
			}
			if (cardsDiscarded==3){
				break;
			}
		}
		if(input.length()-commas-spaces>3){
			return false;
		}
		return validInput;
	}
	
	
	/* Returns 1 if player raises, 0 if player sees, -1 if player folds.
	 *  If currentHighBet is 0, the betting has not been opened yet, and
	 *  players can only raise or fold.
	 */
	public int getBet(String input, int currentHighBet, boolean open){
		int bet = 0;
		boolean validInput = false;
		input = input.toLowerCase();
		if (input.contains("raise")){
			bet = 1;		
			validInput = true;
				
			if(numberOfChips-currentHighBet<=0){
				validInput=false;
			}
				
			if(validInput==true){
				setNumberOfChips(-currentHighBet-1);
			}
				
				
			amountToCall = 0;
		
		}else if (input.contains("see") && open){
			bet = 0;
			setNumberOfChips(-currentHighBet);
			amountToCall = 0;
			validInput = true;
		}else if (input.contains("fold")){
			bet = -1;
			validInput = true;
		}
		
		if (!validInput){
				bet = -2;
		}

		return bet;
	}

}
