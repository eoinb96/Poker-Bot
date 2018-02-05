/* Darragh O'Keeffe
 * 14702321
 * COMP 30050 Assignment 3/4/5
 * 02/03/2017
 */

package poker;

import java.util.Vector;

public class HandOfCards {
	//constant for number of cards in hand
	public static final int HAND_SIZE = 5;
	
	//base values for each type of hand, increments of 1 million
	// chosen so that no type of hand can outscore a higher ranked hand
	public static final int ROYAL_FLUSH_DEFAULT_VALUE = 9000000;
	public static final int STRAIGHT_FLUSH_DEFAULT_VALUE = 8000000;
	public static final int FOUR_OF_A_KIND_DEFAULT_VALUE = 7000000;
	public static final int FULL_HOUSE_DEFAULT_VALUE = 6000000;
	public static final int FLUSH_DEFAULT_VALUE = 5000000;
	public static final int STRAIGHT_DEFAULT_VALUE = 4000000;
	public static final int THREE_OF_A_KIND_DEFAULT_VALUE = 3000000;
	public static final int TWO_PAIR_DEFAULT_VALUE = 2000000;
	public static final int ONE_PAIR_DEFAULT_VALUE = 1000000;
	public static final int HIGH_HAND_DEFAULT_VALUE = 0;
	
	/* Values used to weight cards in a hand. Chosen as powers of 14 because highest gameValue
	 *  of a card is 14 and any gameValue multiplied by a weight will be higher than the sum
	 *  of all lower weights multiplied by the highest possible gameValues
	 */ 
	private static final int FIRST_CARD_WEIGHT = (int) Math.pow(14, 4);
	private static final int SECOND_CARD_WEIGHT = (int) Math.pow(14, 3);
	private static final int THIRD_CARD_WEIGHT = (int) Math.pow(14, 2);
	private static final int FOURTH_CARD_WEIGHT = (int) Math.pow(14, 1);
	private static final int FIFTH_CARD_WEIGHT = (int) Math.pow(14, 0);
	
	//Assume that no cards have been dealt other than this hand and that discarded cards cannot be redrawn
	private static final double CARDS_REMAINING = 47;
	
	//Probability of drawing one specific card from the cards remaining in the deck
	private static final double ONE_CARD_DRAW = 1.0/(CARDS_REMAINING);
	
	//HandOfCards instance variables
	private PlayingCard[] cards = new PlayingCard[HAND_SIZE];
	private DeckOfCards deck;
	private int gameValue = 0;
	private boolean isSorted = false;

	//constructor draws HAND_SIZE number of cards from the deck 
	public HandOfCards(DeckOfCards deck){
		this.deck = deck;		
		for (int i=0;i<HAND_SIZE;i++){
			cards[i]=deck.deal();
		}
		sort();
	}
	
	public void returnCards(){
		for (int i=0;i<HAND_SIZE;i++){
			deck.returnCard(cards[i]);
		}
	}
	
	// uses insertion sort algorithm to sort cards by GameValue from lowest to highest
	//  with lowest value card being in cards[0]
	private void sort(){
		int j;
		PlayingCard temp;
		for (int i=1;i<HAND_SIZE;i++){
			j = i;
		    while (j>0 && cards[j-1].getGameValue()>cards[j].getGameValue()){
		        temp = cards[j];
		        cards[j]=cards[j-1];
		        cards[j-1]=temp;
		        j = j-1;
		    }
		}
		isSorted = true;
	}
	
	/* Method for replacing the card in cardPosition with a new card. Sets the boolean value
	 *  isSorted to false. A check on isSorted has been added to the start of all other public
	 *  methods so that the hand can be sorted again before any more tasks are carried out 
	 */
	public void discardAndReplace(int cardPosition, PlayingCard card){
		deck.returnCard(cards[cardPosition]);
		cards[cardPosition] = card;
		isSorted = false;
	}
	
	private boolean isRoyalFlush(){
		//check if cards are A, 10, J, Q, K by GameValues
		if (cards[0].getGameValue()==10 && cards[1].getGameValue()==11 && cards[2].getGameValue()==12
				&& cards[3].getGameValue()==13 && cards[4].getGameValue()==14){
			
			//check if all cards have same suit
			if(cards[0].getSuit()==cards[1].getSuit() && cards[0].getSuit()==cards[2].getSuit() &&
					cards[0].getSuit()==cards[3].getSuit() && cards[0].getSuit()==cards[4].getSuit()){
				
				return true;
			}
		}
		return false;
	}
	
	private boolean isStraightFlush(){
		if (!isRoyalFlush()){
			
			if(cards[0].getSuit()==cards[1].getSuit() && cards[0].getSuit()==cards[2].getSuit() &&
					cards[0].getSuit()==cards[3].getSuit() && cards[0].getSuit()==cards[4].getSuit()){
				
				/* Check if cards are a straight, if each card has gameValue one greater
				 *  than the gameValue of the card before it 
				 */
				if(cards[0].getGameValue()+1==cards[1].getGameValue() && 
						cards[1].getGameValue()+1==cards[2].getGameValue() &&
						cards[2].getGameValue()+1==cards[3].getGameValue() &&
						cards[3].getGameValue()+1==cards[4].getGameValue()){
					
					return true;
				}
				
				// check for special case of ace-low straight
				if(cards[0].getGameValue()==2 && cards[1].getGameValue()==3 && cards[2].getGameValue()==4
					&& cards[3].getGameValue()==5 && cards[4].getGameValue()==14){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isFourOfAKind(){
		//check if first four cards are the same and last is different
		// or if last 4 cards are the same and first is different
		if ((cards[0].getGameValue()==cards[3].getGameValue() &&
				cards[0].getGameValue()!=cards[4].getGameValue()) 
				|| 
				(cards[0].getGameValue()!=cards[1].getGameValue() &&
				cards[1].getGameValue()==cards[4].getGameValue())){
			
			return true;
		}
		return false;
	}
	
	private boolean isFullHouse(){
		//two possibilities for full house, there are three of the lower value card and two of the higher
		// or two of the lower and three of the higher
		
		
		//	if first then cards at positions 0, 1 and 2 will have the same value, and cards at 3 and 4
		if ((cards[0].getGameValue()==cards[2].getGameValue() &&
				cards[3].getGameValue()==cards[4].getGameValue())
				//if second then cards at positions 0 and 1 will have the same value, and cards at 2, 3 and 4	
				|| (cards[0].getGameValue()==cards[1].getGameValue() &&
				cards[2].getGameValue()==cards[4].getGameValue())){
		
			return true;
		}
		return false;	
	}
	
	private boolean isFlush(){
		if (!isRoyalFlush() && !isStraightFlush()){
			
			//check if all cards have same suit
			if(cards[0].getSuit()==cards[1].getSuit() && cards[0].getSuit()==cards[2].getSuit() &&
					cards[0].getSuit()==cards[3].getSuit() && cards[0].getSuit()==cards[4].getSuit()){
				
				return true;
			}
		}
		return false;
	}
	
	private boolean isStraight(){
		if (!isRoyalFlush() && !isStraightFlush()){
			
			/* Check if cards are a straight, if each card has gameValue one greater
			 *  than the gameValue of the card before it 
			 */
			if(cards[0].getGameValue()+1==cards[1].getGameValue() && 
					cards[1].getGameValue()+1==cards[2].getGameValue() &&
					cards[2].getGameValue()+1==cards[3].getGameValue() &&
					cards[3].getGameValue()+1==cards[4].getGameValue()){
				
				return true;
			}
			
			// check for Ace-Low straight, excluded from first search because it is done by GameValue
			if (cards[0].getGameValue()==2 && cards[1].getGameValue()==3 && cards[2].getGameValue()==4
					&& cards[3].getGameValue()==5 && cards[4].getGameValue()==14){
				return true;
			}
			
		}
		return false;		
	}
	
	private boolean isThreeOfAKind(){
		//three possibilities for three of a kind, first 3 cards are the same, last 3 cards
		//  are the same or three middle cards are the same.
		
		if (!isFourOfAKind() && !isFullHouse()){
			if (cards[0].getGameValue()==cards[2].getGameValue() ||
				cards[1].getGameValue()==cards[3].getGameValue() ||
				cards[2].getGameValue()==cards[4].getGameValue()){
				
				return true;
			}
		}
		return false;
	}
	
	private boolean isTwoPair(){
		// cases where both pairs are identical or odd card is not odd are caught in
		//  isFourOfAKind() and isFullHouse()
		if (!isFourOfAKind() && !isFullHouse()){

			//three possibilities, the odd card is at position 0, 2 or 4
					//pairs at 0 and 1, 2 and 3, odd card at 4
			if ((cards[0].getGameValue()==cards[1].getGameValue() && 
					cards[2].getGameValue()==cards[3].getGameValue()) 
				
					//pairs at 0 and 1, 3 and 4, odd card at 2
				|| (cards[0].getGameValue()==cards[1].getGameValue() && 
					cards[3].getGameValue()==cards[4].getGameValue()) 
					
					//pairs at 1 and 2, 3 and 4, odd card at 0
				|| (cards[1].getGameValue()==cards[2].getGameValue() && 
					cards[3].getGameValue()==cards[4].getGameValue())){
				
				return true;
			}
		}
		return false;
	}
	
	private boolean isOnePair(){
		// four possibilities for one pair, cards at positions 0 and 1, 1 and 2,
		//  2 and 3, or 3 and 4 are the same
		if (!isFourOfAKind() && !isFullHouse()  &&
				!isFlush() && !isStraight() && !isThreeOfAKind() && !isTwoPair()){
			
			if (cards[0].getGameValue()==cards[1].getGameValue() ||
					cards[1].getGameValue()==cards[2].getGameValue() ||
					cards[2].getGameValue()==cards[3].getGameValue() ||
					cards[3].getGameValue()==cards[4].getGameValue()){
				
				return true;
			}
			 
		}
		return false;
	}
	
	
	private boolean isHighHand(){
		//returns true if not a straight or a flush and no 2 cards are equal
		if (!isStraight() && !isFlush() && !isStraightFlush() && !isRoyalFlush()){
			if (cards[0].getGameValue()!=cards[1].getGameValue() &&
					cards[1].getGameValue()!=cards[2].getGameValue() &&
					cards[2].getGameValue()!=cards[3].getGameValue() &&
					cards[3].getGameValue()!=cards[4].getGameValue()){
						
				return true;
			}
		}
		return false;
	}
	
	/* Returns true if the hand is a busted flush (ie has 4 cards of the same suit
	 *  and one of a different suit), false otherwise.
	 * A count is taken of how many of each suit there are in the hand, if there are
	 *  four of any suit, return true.
	 */
	private int isBustedFlush(){
		int numOfClubs=0, numOfDiamonds=0, numOfHearts=0, numOfSpades=0;
		for (int i=0;i<HAND_SIZE;i++){
			switch (cards[i].getSuit()){
			case 'C':
				 numOfClubs++;
				 break;
		 	case 'D':
			 	numOfDiamonds++;
			 	break;
	 		case 'H':
		 		numOfHearts++;
		 		break;
			case 'S':
	 			numOfSpades++;
	 			break;
			}
		}
		if(numOfClubs==4 || numOfDiamonds==4 || numOfHearts==4 || numOfSpades==4){
			if (cards[0].getSuit()!=cards[1].getSuit() && cards[0].getSuit()!=cards[2].getSuit()){
				return 0;
			}
			if (cards[1].getSuit()!=cards[0].getSuit() && cards[1].getSuit()!=cards[2].getSuit()){
				return 1;
			}
			if (cards[2].getSuit()!=cards[1].getSuit() && cards[2].getSuit()!=cards[3].getSuit()){
				return 2;
			}
			if (cards[3].getSuit()!=cards[2].getSuit() && cards[3].getSuit()!=cards[4].getSuit()){
				return 3;
			}
			if (cards[4].getSuit()!=cards[3].getSuit() && cards[4].getSuit()!=cards[2].getSuit()){
				return 4;
			}
		}
		return -1;
	}
		
	/* If a hand is a broken straight, isBrokenStraight returns an integer representing
	 *  the position of the card which is breaking the straight. I have implemented this
	 *  in this way to save duplicating code by having one method to return a boolean
	 *  value and then code somewhere else which carries out the same operations to figure
	 *  out which card it should discard.
	 * If the hand is not a broken straight, -1 is returned.
	 */
	private int isBrokenStraight(){
		if (isStraight() || isStraightFlush() || isRoyalFlush()){
			return -1;
		}
		/* To check for a broken straight, the value in i in the for loop is
		 *  assumed to be the index of the card which is breaking the straight
		 *  and then the four variables a, b, c and d represent the other 4 indices.
		 * The four cards which form the incomplete straight can be arranged in 4
		 *  possible ways (when i is ignored), where the missing card can be at either
		 *  end, between the first and second cards, between the second and third
		 *  cards or between the third and fourth cards. Each if statement checks for
		 *  one of these possibilities. 
		 * On any given iteration of the for loop, the values 0, 1, 2, 3 and 4 will
		 *  be held in i, a, b, c and d, with the lowest value not equal to i in a
		 *  and the highest in d.
		 * The special case of the ace-low broken straight is taken care of by a separate method
		 */
		int a=1, b=2, c=3, d=4;
		for (int i=0;i<HAND_SIZE;i++){
			if (i==1){ a--; }
			if (i==2){ b--; }
			if (i==3){ c--; }
			if (i==4){ d--; }

			if (cards[a].getGameValue()+1==cards[b].getGameValue() &&
					cards[a].getGameValue()+2==cards[c].getGameValue() &&
					cards[a].getGameValue()+3==cards[d].getGameValue()){
				return i;
			}
			if (cards[a].getGameValue()+2==cards[b].getGameValue() &&
					cards[a].getGameValue()+3==cards[c].getGameValue() &&
					cards[a].getGameValue()+4==cards[d].getGameValue()){
				return i;
			}
			if (cards[a].getGameValue()+1==cards[b].getGameValue() &&
					cards[a].getGameValue()+3==cards[c].getGameValue() &&
					cards[a].getGameValue()+4==cards[d].getGameValue()){
				return i;
			}
			if (cards[a].getGameValue()+1==cards[b].getGameValue() &&
					cards[a].getGameValue()+2==cards[c].getGameValue() &&
					cards[a].getGameValue()+4==cards[d].getGameValue()){
				return i;
			}
		}
		return isAceLowBrokenStraight();
	}
	
	/* This works exactly the same way as the algorithm above but on a smaller
	 *  scale. The ace must be in position 4, therefore we are looking for a 
	 *  three-card broken straight among the first 4 cards of the hand.
	 * 
	 */
	private int isAceLowBrokenStraight(){
		int a=1, b=2, c=3;
		if ((cards[0].getGameValue()==2 || cards[0].getGameValue()==3) && cards[2].getGameValue()<=5 && cards[4].getGameValue()==14){
			for (int i=0;i<HAND_SIZE-1;i++){
				if (i==1){ a--; }
				if (i==2){ b--; }
				if (i==3){ c--; }
				
				if (cards[a].getGameValue()+1==cards[b].getGameValue() &&
						cards[a].getGameValue()+2==cards[c].getGameValue()){
					return i;
				}
				if (cards[a].getGameValue()+2==cards[b].getGameValue() &&
						cards[a].getGameValue()+3==cards[c].getGameValue()){
					return i;
				}
				if (cards[a].getGameValue()+1==cards[b].getGameValue() &&
						cards[a].getGameValue()+3==cards[c].getGameValue()){
					return i;
				}
			}
		}
		return -1;
	}
		
	//returns string with string representation of all 5 cards in the hand
	public String toString(){
		if (!isSorted){
			sort();
			setGameValue();
		}
		String output = "";
		for (int i=0;i<HAND_SIZE;i++){
			output += cards[i].toString()+" ";
		}
		return output;
	}
	
	//returns the instance of deck used to initialise the hand
	public DeckOfCards getDeck(){
		return deck;
	}
	
	/* returns an int representing the gameValue of the hand
	 * Official poker rules state that the suit of cards be irrelevant when
	 *  deciding the value of a hand so identical hands will result in a split pot.
	 * We were also advised in class to take this approach to valuing hands
	 * When testing my code I found that I was calling this method often to sort
	 *  hands which was making it very inefficient. To improve this I added a private
	 *  variable gameValue and a public method getGameValue which returns this variable.
	 *  This setGameValue method is now private and is called by getGameValue if gameValue
	 *  equals 0 (as it does after construction).
	 * In future assignments this gameValue variable can be updated whenever a new card is
	 *  dealt to the hand 
	 */ 
	private void setGameValue(){
		if (!isSorted){
			sort();
		}
		
		//each card multiplied by a weight according to its gameValue with highest
		// gameValue receiving the highest weight
		if (isHighHand()){
			gameValue = cards[4].getGameValue()*FIRST_CARD_WEIGHT 
					+ cards[3].getGameValue()*SECOND_CARD_WEIGHT
					+ cards[2].getGameValue()*THIRD_CARD_WEIGHT 
					+ cards[1].getGameValue()*FOURTH_CARD_WEIGHT
					+ cards[0].getGameValue()*FIFTH_CARD_WEIGHT
					+ HIGH_HAND_DEFAULT_VALUE;
			return;
		}
		
		//the cards in the pair will receive the highest weight with each single card
		// after receiving the next highest weight
		if (isOnePair()){
			//four possibilities for onePair organisation, the pair at
			// 0 and 1, 1 and 2, 2 and 3, or 3 and 4
			if (cards[0].getGameValue()==cards[1].getGameValue()){ //Pair at 0 and 1
				gameValue = cards[0].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[4].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[3].getGameValue()*THIRD_CARD_WEIGHT 
						+ cards[2].getGameValue()*FOURTH_CARD_WEIGHT
						+ ONE_PAIR_DEFAULT_VALUE;
			}
			if (cards[1].getGameValue()==cards[2].getGameValue()){ //Pair at 1 and 2
				gameValue = cards[1].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[4].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[3].getGameValue()*THIRD_CARD_WEIGHT 
						+ cards[0].getGameValue()*FOURTH_CARD_WEIGHT
						+ ONE_PAIR_DEFAULT_VALUE;
			}
			if (cards[2].getGameValue()==cards[3].getGameValue()){ //Pair at 2 and 3
				gameValue = cards[2].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[4].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[1].getGameValue()*THIRD_CARD_WEIGHT 
						+ cards[0].getGameValue()*FOURTH_CARD_WEIGHT
						+ ONE_PAIR_DEFAULT_VALUE;
			}
			if (cards[3].getGameValue()==cards[4].getGameValue()){ //Pair at 3 and 4
				gameValue = cards[3].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[2].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[1].getGameValue()*THIRD_CARD_WEIGHT 
						+ cards[0].getGameValue()*FOURTH_CARD_WEIGHT
						+ ONE_PAIR_DEFAULT_VALUE;
			}
			return;
		}
		
		//the cards in the pair with higher gameValue will receive the highest weight,
		// followed by the other pair and then the single card
		if (isTwoPair()){
			
			//Three possibilities for twoPair organisation, with the odd card at position
			// 0, 2 or 4 and the pairs around it
					//pairs at 0 and 1, 2 and 3, odd card at 4
			if (cards[0].getGameValue()==cards[1].getGameValue() && 
					cards[2].getGameValue()==cards[3].getGameValue()){
				
				gameValue = cards[3].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[1].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[4].getGameValue()*THIRD_CARD_WEIGHT 
						+ TWO_PAIR_DEFAULT_VALUE;
			}
				
					//pairs at 0 and 1, 3 and 4, odd card at 2
			if (cards[0].getGameValue()==cards[1].getGameValue() && 
					cards[3].getGameValue()==cards[4].getGameValue()){

				gameValue = cards[4].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[1].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[2].getGameValue()*THIRD_CARD_WEIGHT 
						+ TWO_PAIR_DEFAULT_VALUE;
			}
					
					//pairs at 1 and 2, 3 and 4, odd card at 0
			if (cards[1].getGameValue()==cards[2].getGameValue() && 
					cards[3].getGameValue()==cards[4].getGameValue()){

				gameValue = cards[4].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[2].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[0].getGameValue()*THIRD_CARD_WEIGHT 
						+ TWO_PAIR_DEFAULT_VALUE;
			}
			return;
		}
		
		//Cards in the threeOfAKind will receive the highest weight followed by
		// the two remaining cards
		if (isThreeOfAKind()){
			//Three possibilities for threeOfAKind organisation, the matching cards can
			// be at positions 0 1 and 2, 1 2 and 3, or 2 3 and 4 
			if (cards[0].getGameValue()==cards[2].getGameValue()){ //Matching cards at 0 1 and 2
				gameValue = cards[2].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[4].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[3].getGameValue()*THIRD_CARD_WEIGHT 
						+ THREE_OF_A_KIND_DEFAULT_VALUE;
			}
			if (cards[1].getGameValue()==cards[3].getGameValue()){ //Matching cards at 1 2 and 3
				gameValue = cards[3].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[4].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[0].getGameValue()*THIRD_CARD_WEIGHT 
						+ THREE_OF_A_KIND_DEFAULT_VALUE;
			}
			if (cards[2].getGameValue()==cards[4].getGameValue()){ //Matching cards at 2 3 and 4
				gameValue = cards[4].getGameValue()*FIRST_CARD_WEIGHT 
						+ cards[1].getGameValue()*SECOND_CARD_WEIGHT
						+ cards[0].getGameValue()*THIRD_CARD_WEIGHT 
						+ THREE_OF_A_KIND_DEFAULT_VALUE;
			}
			return;
		}
		
		//The value of a straight will be decided by the highest gameValue of the cards in the hand,
		// except in the case of the ace-low straight, which will have a value of 5+straight_default
		if (isStraight()){
			//special case of ace-low straight
			if (cards[4].getGameValue()==14 && cards[3].getGameValue()==5){
				gameValue = cards[3].getGameValue() + STRAIGHT_DEFAULT_VALUE; 
			}
			//all other cases
			else {
				gameValue = cards[4].getGameValue() + STRAIGHT_DEFAULT_VALUE;
			}
			return;
		}
		
		//scores for a flush are decided in the same way as highHand, with most valuable card
		// receiving the highest weight etc.
		if (isFlush()){
			gameValue = cards[4].getGameValue()*FIRST_CARD_WEIGHT 
					+ cards[3].getGameValue()*SECOND_CARD_WEIGHT
					+ cards[2].getGameValue()*THIRD_CARD_WEIGHT 
					+ cards[1].getGameValue()*FOURTH_CARD_WEIGHT
					+ cards[0].getGameValue()*FIFTH_CARD_WEIGHT
					+ FLUSH_DEFAULT_VALUE;
			return;
		}
		
		//cards in the threeOfAkind will receive the highest weight and cards
		// in the pair will receive the secoond highest weight
		if (isFullHouse()){
			//Two possibilities for organisation of a fullHouse, three matching cards
			// at the start of the hand, or at the end of the hand
				//case where first three cards match
			if (cards[0].getGameValue()==cards[2].getGameValue()){
				gameValue = cards[0].getGameValue()*FIRST_CARD_WEIGHT
						+ cards[4].getGameValue()*SECOND_CARD_WEIGHT
						+ FULL_HOUSE_DEFAULT_VALUE;
			}
				//case where last three cards match
			else {
				gameValue = cards[4].getGameValue()*FIRST_CARD_WEIGHT
						+ cards[0].getGameValue()*SECOND_CARD_WEIGHT
						+ FULL_HOUSE_DEFAULT_VALUE;
			}
			return;
		}
		
		//cards in the fourOfAKind will receive the highest weight and the odd card will
		// receive the next highest weight
		if (isFourOfAKind()){
			//Two possibilities for fourOfAKind organisation, odd card at position 0 or position 4
				//case where odd card is at position 4
			if (cards[0].getGameValue()==cards[3].getGameValue()){
				gameValue = cards[0].getGameValue()*FIRST_CARD_WEIGHT
						+ cards[4].getGameValue()*SECOND_CARD_WEIGHT
						+ FOUR_OF_A_KIND_DEFAULT_VALUE;
			}
				//case where odd card is at position 0
			else {
				gameValue = cards[4].getGameValue()*FIRST_CARD_WEIGHT
						+ cards[0].getGameValue()*SECOND_CARD_WEIGHT
						+ FOUR_OF_A_KIND_DEFAULT_VALUE;
			}
			return;
		}
		
		//straightFlush hands are scored in the same way as a straight, with the value being
		// decided by the card with highest gameValue, except in the case of the ace-low straight
		if (isStraightFlush()){
			//check for ace_low straight
			if (cards[4].getGameValue()==14){
				gameValue = cards[3].getGameValue() + STRAIGHT_FLUSH_DEFAULT_VALUE;
			}
			//all other possibilities
			else {
				gameValue = cards[4].getGameValue() + STRAIGHT_FLUSH_DEFAULT_VALUE;
			}
			return;
		}
		
		//as royalFlush hands only differ by suit they all receive the same gameValue which is simply
		// the default value for the hand, to rank them as the most valuable hand
		if (isRoyalFlush()){
			gameValue = ROYAL_FLUSH_DEFAULT_VALUE;
			return;
		}
	}
	
	//returns the value of the current hand, stored in private variable gameValue
	public int getGameValue(){
		if (!isSorted){
			sort();
			setGameValue();
		}
		if (gameValue==0){
			setGameValue();
		}
		return gameValue;
	}
	
	// returns a string containing the type of the hand
	public String getType(){
		if (!isSorted){
			sort();
			setGameValue();
		}
		String output = "";
		if (gameValue==0){ setGameValue(); }
		if (gameValue>=HIGH_HAND_DEFAULT_VALUE){ output = "High Hand";}
		if (gameValue>=ONE_PAIR_DEFAULT_VALUE){ output = "One Pair";}
		if (gameValue>=TWO_PAIR_DEFAULT_VALUE){ output = "Two Pair";}
		if (gameValue>=THREE_OF_A_KIND_DEFAULT_VALUE){ output = "Three Of A Kind";}
		if (gameValue>=STRAIGHT_DEFAULT_VALUE){ output = "Straight";}
		if (gameValue>=FLUSH_DEFAULT_VALUE){ output = "Flush";}
		if (gameValue>=FULL_HOUSE_DEFAULT_VALUE){ output = "Full House";}
		if (gameValue>=FOUR_OF_A_KIND_DEFAULT_VALUE){ output = "Four Of A Kind";}
		if (gameValue>=STRAIGHT_FLUSH_DEFAULT_VALUE){ output = "StraightFlush";}
		if (gameValue>=ROYAL_FLUSH_DEFAULT_VALUE){ output = "Royal Flush";}
		return output;
	}
	
	/* When holding a Straight Flush the most likely strategy to improve the hand
	 *  is to discard the lowest ranked card and hope to draw the card that would
	 *  re-complete the straight at the upper end, thereby improving the value of
	 *  the hand or possibly improving it to a RoyalFlush. Discarding two or three
	 *  cards in the hope of similar results has very low probability of success,
	 *  (0.000925 and 0.000061 respectively) so I have chosen to leave the probability
	 *  of discarding more than one card as 0.
	 */
	
	private int getStraightFlushDiscardProbability(int cardPosition){
		int probability = 0;
		if (cards[4].getGameValue()==14 && cardPosition==4){
			probability = (int) (100 * ONE_CARD_DRAW);
		}
		if (cards[4].getGameValue()!=14 && cardPosition==0){
			probability = (int) (100 * ONE_CARD_DRAW);
		}
		return probability;
	}
	
	
	/* A full house can be upgraded to a FourOfAKind by discarding one card and drawing
	 *  the fourth of the ThreeOfAKind or can be maintained as a full house by drawing
	 *  a different match for the remaining card of the pair. Therefore there are three
	 *  possible cards that can maintain or improve the hand so the probability of drawing
	 *  one of these is 3/52 or 3*ONE_CARD_DRAW
	 * 
	 */
	private int getFullHouseDiscardProbability(int cardPosition){
		int probability = 0;
		//Case where matching three have lower value than pair
		if (cards[0].getGameValue()==cards[2].getGameValue() && cardPosition==4){
			probability = (int) (3 * ONE_CARD_DRAW * 100);
		}
		//case where matching three have higher value than pair
		if (cards[2].getGameValue()==cards[4].getGameValue() && cardPosition==0){
			probability = (int) (3 * ONE_CARD_DRAW * 100);
		}
		return probability;
	}
	
	/* If a flush is also a broken straight, discarding one card and drawing
	 *  the one card to fix the straight and maintain the flush would upgrade
	 *  the hand to a straight flush. Drawing another card of the same suit as
	 *  the rest of the flush would also maintain the flush so there are 13-5=8
	 *  desirable draws from a deck, giving probability of 8/52 or 8*ONE_CARD_DRAW.
	 * If the flush is not a broken straight and is not an Ace-high flush then 
	 *  discard the lowest ranked card only. There are 8 other cards to maintain
	 *  the flush and at least one of these will increase the value of the hand.
	 *  Similar to the straight flush probabilities, the odds of maintaining the
	 *  flush decrease dramatically if discarding 2 or 3 cards. 
	 */
	private int getFlushDiscardProbability(int cardPosition){
		int probability = 0;
		if (isBrokenStraight()==cardPosition){
			probability = (int) (14 * ONE_CARD_DRAW * 100);
		}
		else {
			if (cards[4].getGameValue()!=14 && cardPosition==0){
				probability = (int) (8 * ONE_CARD_DRAW * 100);
			}
		}
		return probability;
	}
	
	/* If a straight is also a busted flush, discard the card busting the flush.
	 *  This can upgrade the hand to a straight flush. There is only one possible
	 *  card to upgrade the hand to a busted flush but there are 8 others which will
	 *  see it become a flush and 2 possibilities for other suits to maintain the
	 *  straight if it is a closed straight after discarding or 5 if it is an open
	 *  straight, giving 11 or 14 desirable draws for closed or open broken straights
	 *  respectively. For the sake of simplicity, assume open and closed straights
	 *  will occur with similar probabilities so the probability of a desirable outcome
	 *  is on average 12.5/52, or 12.5 * ONE_CARD_DRAW 
	 * If the straight is not a busted flush, discard the lowest ranked card. There
	 *  will be four possible cards in the deck which can improve the value of the
	 *  straight by completing the 4-card straight at the upper end and 3 which can
	 *  complete it at the lower end, for probability 7/cards_remaining or 7*ONE_CARD_DRAW
	 */
	private int getStraightDiscardProbability(int cardPosition){
		int probability = 0;
		int oddCardIndex = isBustedFlush();	
		if (oddCardIndex==cardPosition){
			probability = (int) (12.5*ONE_CARD_DRAW * 100);
		}
		else {
			if (cards[4].getGameValue()!=14 && cardPosition==0){
				probability = (int) (7 * ONE_CARD_DRAW * 100);
			}
			else if (cards[4].getGameValue()==14 && cardPosition==4){
				probability = (int) (7 * ONE_CARD_DRAW * 100);
			}
		}
		return probability;
	}
	
	/* With a threeOfAKind hand, discarding the two odd cards and drawing
	 *  either the fourth card with same rank as the threeOfAKind or a any pair
	 *  of cards of the same value will upgrade the hand, with no potential for
	 *  loss. As there is no potential for loss the two odd cards should be discarded
	 *  any time a three of a kind is drawn, and the threeOfAKind cards should never
	 *  be discarded.
	 */
	private int getThreeOfAKindDiscardProbability(int cardPosition){
		int probability = 0;
		if (cards[0].getGameValue()==cards[2].getGameValue() && (cardPosition==3 || cardPosition==4)){
			probability = 100;
		}
		if (cards[1].getGameValue()==cards[3].getGameValue() && (cardPosition==0 || cardPosition==4)){
			probability = 100;
		}
		if (cards[2].getGameValue()==cards[4].getGameValue() && (cardPosition==0 || cardPosition==1)){
			probability = 100;
		}
		return probability;
	}
	
	/* When holding Two Pairs, discarding one card and drawing a third card of
	 *  equal value to either of the two pairs would upgrade the hand to a full
	 *  house with no possibility of loss. Therefore the odd card should always
	 *  be discarded and any card which is part of a pair should never be
	 *  discarded.
	 * 
	 */
	private int getTwoPairDiscardProbability(int cardPosition){
		int probability = 0;
		if (cards[0].getGameValue()==cards[1].getGameValue() && 
				cards[2].getGameValue()==cards[3].getGameValue() && cardPosition==4){
			probability = 100;
		}
		if (cards[0].getGameValue()==cards[1].getGameValue() && 
				cards[3].getGameValue()==cards[4].getGameValue() && cardPosition==2){
			probability = 100;
		}
		if (cards[1].getGameValue()==cards[2].getGameValue() && 
				cards[3].getGameValue()==cards[4].getGameValue() && cardPosition==0){
			probability = 100;
		}
		return probability;
	}
	
	/* When holding one pair the hand can also be a busted flush, a broken straight
	 *  or both. The probabilities for success when holding a busted flush and a
	 *  broken straight are calculated in the getStraightDiscardProbability and
	 *  getFlushDiscardProbability methods respectively so those methods will be
	 *  called again here. In these cases there is also the possibility of drawing
	 *  a card to maintain the pair. There are 11 possible cards to maintain a pair
	 *  (3 of each rank apart from the rank which was discarded, of which there are
	 *  only two which can be drawn) giving probability 11/cards_remaining or
	 *  11*ONE_CARD_DRAW. This will be added to the probabilities for the busted flush
	 *  and broken straight.
	 * If the hand is not a broken straight or busted flush the three cards which are
	 * 	not part of the pair should be discarded 100% at all times, as the hand could
	 *  be upgraded to TwoPair, ThreeOfAKind, FullHouse, or FourOfAKind, with no
	 *  possibility of loss.
	 */
	private int getOnePairDiscardProbability(int cardPosition){
		int probability = 0;
		int maintainPairProbability = (int) (11*ONE_CARD_DRAW*100);
		if (isBustedFlush()==cardPosition && isBrokenStraight()==cardPosition){
			probability = getFlushDiscardProbability(cardPosition) + getStraightDiscardProbability(cardPosition);
		}
		if (isBustedFlush()==cardPosition && isBrokenStraight()<0){
			probability = getStraightDiscardProbability(cardPosition);
		}
		if (isBustedFlush()<0 && isBrokenStraight()==cardPosition){
			if (cardPosition<HAND_SIZE-1){
				if (cards[cardPosition].getGameValue()==cards[cardPosition+1].getGameValue()){
					probability = (int) (4*ONE_CARD_DRAW*100);
				}
			}
		}
		if (probability!=0){
			probability += maintainPairProbability;
		}
		if (isBustedFlush()<0 && isBrokenStraight()<0){
			if (cards[0].getGameValue()==cards[1].getGameValue() && (cardPosition==2 || cardPosition==3 || cardPosition==4)){
				probability = 100;
			}
			if (cards[1].getGameValue()==cards[2].getGameValue() && (cardPosition==0 || cardPosition==3 || cardPosition==4)){
				probability = 100;
			}
			if (cards[2].getGameValue()==cards[3].getGameValue() && (cardPosition==0 || cardPosition==1 || cardPosition==4)){
				probability = 100;
			}
			if (cards[3].getGameValue()==cards[4].getGameValue() && (cardPosition==0 || cardPosition==1 || cardPosition==2)){
				probability = 100;
			}
		}
		return probability;
	}
	
	/* A highHand may also be a broken straight or a busted flush or both, however 
	 *  a highHand cannot be made much worse by not completing the flush or straight so
	 *  the card busting the straight or breaking the straight should always be discarded.
	 * The methods getFlushDiscardProbability and getStraightDiscardProbability will return
	 *  a non-zero value for the card which is busting the flush or breaking the straight
	 *  so these methods will be called to determine which card to discard and the probability
	 *  for that card will be raised to 100.
	 * If the hand is not a broken straight or a busted flush, always discard the three cards
	 *  with lowest gameValues 
	 */
	private int getHighHandDiscardProbability(int cardPosition){
		int probability = 0;
		if (isBustedFlush()==cardPosition && isBrokenStraight()==cardPosition){
			probability = 100;
		}
		if (isBustedFlush()==cardPosition && isBrokenStraight()<0){
			probability = 100;
		}
		if (isBustedFlush()<0 && isBrokenStraight()==cardPosition){
			probability = 100;
		}
		if (isBustedFlush()<0 && isBrokenStraight()<0){
			if (cardPosition<=2){
				probability = 100;
			}
		}
		return probability;
	}
	
	
	/* If a hand has probability of improving which is less than 0.01 or 1% I have chosen to
	 *  simply return 0 for this hand (ie RoyalFlush, FourOfAKind, discarding three cards from
	 *  a straight or a flush). 
	 */
	public int getDiscardProbability(int cardPosition){
		if (!isSorted){
			sort();
			setGameValue();
		}
		if (cardPosition<0 || cardPosition>4){
			return 0;
		}
		//A royal flush cannot be improved on, do not discard any cards
		if (isRoyalFlush()){
			return 0;
		}
		if (isStraightFlush()){
			return getStraightFlushDiscardProbability(cardPosition);
		}
		/* improving a fourOfAKind hand requires discarding three of the
		 *  four matching cards and hoping to create a straight or royal flush
		 *  from the drawn cards. The probability of completing this is 0.000061
 		 * Therefore I have decided to return 0 for all cards of a FourOfAKind
 		 *  hand
		 */
		if (isFourOfAKind()){
			return 0;
		}
		if (isFullHouse()){
			return getFullHouseDiscardProbability(cardPosition);
		}
		if (isFlush()){
			return getFlushDiscardProbability(cardPosition);
		}
		if (isStraight()){
			return getStraightDiscardProbability(cardPosition);
		}
		if (isThreeOfAKind()){
			return getThreeOfAKindDiscardProbability(cardPosition);
		}
		if (isTwoPair()){
			return getTwoPairDiscardProbability(cardPosition);
		}
		if (isOnePair()){
			return getOnePairDiscardProbability(cardPosition);
		}
		if (isHighHand()){
			return getHighHandDiscardProbability(cardPosition);
		}
		return 0;
	}
	
	
	/* For testing hands of cards are generated until there are a set number (testSize) of each type of hand, 
	 *  each in their own vector (so they can be counted). 
	 * Once testSize of each hand have been generated they are all pooled into one vector (results) and
	 *  printed to the console followed by the discard probabilities for each card of the hand
	 * Note: It may take a while for results to appear depending on testSize, as it takes ~4,000,000 hands
	 *  to generate ten of each hand randomly.
	 */

	public static void main(String[] args){
		
		//vectors to hold, count and sort hands
		Vector<HandOfCards> results = new Vector<HandOfCards>();
		Vector<HandOfCards> highHandResults = new Vector<HandOfCards>();
		Vector<HandOfCards> onePairResults = new Vector<HandOfCards>();
		Vector<HandOfCards> twoPairResults = new Vector<HandOfCards>();
		Vector<HandOfCards> threeOfAKindResults = new Vector<HandOfCards>();
		Vector<HandOfCards> straightResults = new Vector<HandOfCards>();
		Vector<HandOfCards> flushResults = new Vector<HandOfCards>();
		Vector<HandOfCards> fullHouseResults = new Vector<HandOfCards>();
		Vector<HandOfCards> fourOfAKindResults = new Vector<HandOfCards>();
		Vector<HandOfCards> straightFlushResults = new Vector<HandOfCards>();
		Vector<HandOfCards> royalFlushResults = new Vector<HandOfCards>();
		
		int testSize = 100; //number of each type of hands required to do a comparison
		
		while (onePairResults.size()<testSize ||
				highHandResults.size()<testSize)
		{	
			DeckOfCards deck = new DeckOfCards();
			HandOfCards hand = new HandOfCards(deck);
			int handValue = hand.getGameValue();
			if (hand.toString().contains("2") && hand.toString().contains("3") &&
					hand.toString().contains("4") && hand.toString().contains("j")){
				
			}
			if (handValue>=ROYAL_FLUSH_DEFAULT_VALUE) {
				if (royalFlushResults.size()<testSize){ royalFlushResults.add(hand); }
			}
			if (handValue>=STRAIGHT_FLUSH_DEFAULT_VALUE && handValue<ROYAL_FLUSH_DEFAULT_VALUE) {
				if (straightFlushResults.size()<testSize){ straightFlushResults.add(hand); }
			}
			if (handValue>=FOUR_OF_A_KIND_DEFAULT_VALUE && handValue<STRAIGHT_FLUSH_DEFAULT_VALUE) {
				if (fourOfAKindResults.size()<testSize){ fourOfAKindResults.add(hand); }
			}
			if (handValue>=FULL_HOUSE_DEFAULT_VALUE && handValue<FOUR_OF_A_KIND_DEFAULT_VALUE) {
				if (fullHouseResults.size()<testSize){ fullHouseResults.add(hand); }
			}
			if (handValue>=FLUSH_DEFAULT_VALUE && handValue<FULL_HOUSE_DEFAULT_VALUE) {
				if (flushResults.size()<testSize){ flushResults.add(hand); }
			}
			if (handValue>=STRAIGHT_DEFAULT_VALUE && handValue<FLUSH_DEFAULT_VALUE) {
				if (straightResults.size()<testSize){ straightResults.add(hand); }
			}
			if (handValue>=THREE_OF_A_KIND_DEFAULT_VALUE && handValue<STRAIGHT_DEFAULT_VALUE) {
				if (threeOfAKindResults.size()<testSize){ threeOfAKindResults.add(hand); }
			}
			if (handValue>=TWO_PAIR_DEFAULT_VALUE && handValue<THREE_OF_A_KIND_DEFAULT_VALUE) {
				if (twoPairResults.size()<testSize){ twoPairResults.add(hand); }
			}
			if (handValue>=ONE_PAIR_DEFAULT_VALUE && handValue<TWO_PAIR_DEFAULT_VALUE) {
				if (onePairResults.size()<testSize){ onePairResults.add(hand); }
			}
			if (handValue>=HIGH_HAND_DEFAULT_VALUE && handValue<ONE_PAIR_DEFAULT_VALUE) {
				if (highHandResults.size()<testSize){ highHandResults.add(hand); }
			}
			if (hand.toString().contains("2") && hand.toString().contains("3") &&
					hand.toString().contains("4") && hand.toString().contains("j")){
				break;
			}
		}
		
		//add all results to the results vector
		results.addAll(royalFlushResults);
		results.addAll(straightFlushResults);
		results.addAll(fourOfAKindResults);
		results.addAll(fullHouseResults);
		results.addAll(flushResults);
		results.addAll(straightResults);
		results.addAll(threeOfAKindResults);
		results.addAll(twoPairResults);
		results.addAll(onePairResults);
		results.addAll(highHandResults);
		
		//print results
		for (HandOfCards h: results){
			System.out.println("Testing Hand: "+h.toString()+"\t"+h.getType());
			System.out.println("Probabilities: "+h.getDiscardProbability(0)+" "+h.getDiscardProbability(1)+" "
			+h.getDiscardProbability(2)+" "+h.getDiscardProbability(3)+" "+h.getDiscardProbability(4)+"\n");
		}
	}
	
}