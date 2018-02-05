package poker;

import java.util.Random;
import java.util.Scanner;

public class AutomatedPokerPlayer extends PokerPlayer {
	
	static public final boolean HUMAN = false;
	
	protected double riskAversion; //1.0 means will only bet with good cards, 0.0 means is likely to bet even with bad cards
	protected double bluffLevel; //1.0 means will bluff and possibly raise, even with bad cards
	protected Random rand;

	public AutomatedPokerPlayer(DeckOfCards d, String playerName, double risk_Aversion, double bluff_Level) {
		super(d, playerName);
		riskAversion = risk_Aversion;
		bluffLevel = bluff_Level;
		Random rand = new Random();
	}
	
	public boolean isHuman(){
		return false;
	}
	
	public void changeRisk(double newRisk){
		riskAversion = newRisk;
	}
	
	//gets the discard probabilities of the cards in the hand, generates 5 random
		// numbers from zero to 99 and discards cards whose discard probabilities are
		// lower than the corresponding discard probabilities
		public int discard(){
			int numOfCardsDiscarded=0;
			Random rand = new Random();
			int discardProbabilities[] = new int[HandOfCards.HAND_SIZE];
			//get discard probabilities
			for (int j=0;j<HandOfCards.HAND_SIZE;j++){
				discardProbabilities[j] = hand.getDiscardProbability(j);
			}
			//compare discard probabilities to random numbers and discard cards
			for (int i=0;i<HandOfCards.HAND_SIZE;i++){
				int randomNumber = rand.nextInt(100);
				if (randomNumber<discardProbabilities[i]){
					hand.discardAndReplace(i, deck.deal());
					numOfCardsDiscarded++;
				}
			}
			//returns the number of cards discarded
			return numOfCardsDiscarded;
		}
	
	public int getBet(int currentHighBet, boolean open){
		//bet will store -1 for fold, 0 for call, 1 for raise
		int bet = -1;
		int percentageOfChips = 0;
		
		if(currentHighBet<numberOfChips){
			if(currentHighBet>0){
				percentageOfChips = (int) (((float)currentHighBet/numberOfChips)*100.0);	
			}
		}
		else if(currentHighBet>=numberOfChips){
			percentageOfChips = 100;
		}
		
		if(hand.getGameValue()<1500000 && percentageOfChips<20){
			
			float possibleBluff = new Random().nextFloat();
			
			if(possibleBluff<bluffLevel/3){
				setNumberOfChips(-currentHighBet-1);
				return 1;
			}
		}
		
		
		//decision to bet or raise is a function of the quality of the hand + the risk-aversion of the agent
		double confidence = hand.getGameValue()/1000000.0 / (riskAversion*10);

			if (isBetween(percentageOfChips, 0, 15)) {
			  confidence = confidence * 1;
			}
			else if (isBetween(percentageOfChips, 15, 40)) {
				confidence = confidence * 0.8;
			}
			else if (isBetween(percentageOfChips, 40, 70)) {
				confidence = confidence * 0.7;
			}
			
			if(confidence>10){
				bet = 1;
			}
			else if(confidence>0.15){
				bet = 0;
			}
			else if(confidence<0.15){
				bet = -1;
			}
			
			if (percentageOfChips > 70) {
				if(confidence>0.15){
					bet = 0;
				}
				else{
					bet = -1;
				}
			}
			
			if(open==false){
				
				if(confidence>0.2){
					bet=1;
				}
				else{
					bet=-1;
				}
				
			}
			
			
			if(bet==0){
				setNumberOfChips(-currentHighBet);
				amountToCall = 0;
			}
			
			if(bet==1){
				setNumberOfChips(-currentHighBet-1);
				amountToCall = 0;
			}
			
		return bet;
		
	}
	
	private static boolean isBetween(int x, int lower, int upper) {
		  return lower <= x && x <= upper;
	}
	
	public static void main(String[] args){
		DeckOfCards deck = new DeckOfCards();
		AutomatedPokerPlayer player = new AutomatedPokerPlayer(deck, "Robo", 0.3, 0.3);
		player.discard();
		System.out.println(player.toString() + "\n");
		System.out.print("HIGH RISK TAKER:\t");
		System.out.println("getBet result: " + player.getBet(3,true));
		player.changeRisk(0.6);
		System.out.print("MEDIUM RISK TAKER:\t");
		System.out.println("getBet result: " + player.getBet(3,true));
		player.changeRisk(0.85);
		System.out.print("LOW RISK TAKER:\t\t");
		System.out.println("getBet result: " + player.getBet(3,true));
	}

}
