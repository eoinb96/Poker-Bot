package poker;

import java.util.ArrayList;
import java.util.Scanner;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;

public class HandOfPoker {

	protected int lastBet = 0;
	int state = 0;
	protected int pot = 0;
	protected boolean open = false;
	private ArrayList<PokerPlayer> pokerPlayers = new ArrayList<PokerPlayer>();
	private ArrayList<PokerPlayer> playersIn = new ArrayList<PokerPlayer>();
	private boolean cleanRound = false;
	private boolean roundOver = false;
	private int clean = 0;
	private int needToCall = 0;
	private int cantOpen = 0;
	private static long botLastTweetId;
	PokerPlayer winner;
	DeckOfCards deck;
	Scanner scanner;
	
	private static Twitter twit;
	private static Configuration config;
	private static TwitterStream twitterStream;
	private static String tweet;
	public static String userName = "";
	private static Status tweetId;
	private static Status currentStatus;
	public static Status lastUserReplyTweet;


	public HandOfPoker(DeckOfCards d, ArrayList<PokerPlayer> players, Twitter twitter, Configuration configuration, String name, Status id) {
		
		userName = name;
		deck = d;
		pokerPlayers.addAll(players);
		playersIn.addAll(players);
		scanner = new Scanner(System.in);
		twit = twitter;
		config = configuration;
		tweetId = id;
		TwitterStream twitterStream = new TwitterStreamFactory(config).getInstance();
		printPlayerChips();
		
	}
	
	public Status executeHandOfPoker(){

		newHandCycle();
		discardCycle();
		


		
	
		//ready to start the betting cycle
		//betting stops whenever clean round is true
		//becomes true when there has been a full rotation of calling/seeing
		cleanRound = false;
		
		while(cleanRound!=true){
			bettingRound();
			if(roundOver){
				return findReply(botLastTweetId);
			}
		}
		
		showCards();
		returnCards();
		
		if(!playersIn.isEmpty()){
			
			winner = playersIn.get(decideWinner());
			winner.setNumberOfChips(pot);
			System.out.println("\n" + winner.name + " won " + pot + " chips");
			
			tweet+= "\n" + winner.name + " won " + pot + " chips";
			
			tweet+= "\nPlay another round? (Y or N)";
			
			sendReply(lastUserReplyTweet,tweet);
			playersIn.clear();
			playersIn.addAll(pokerPlayers);
		}
		
		printPlayerChips();
		
		return findReply(botLastTweetId);
		
	}
	
	private void returnCards(){
		for (int i=0;i<pokerPlayers.size();i++){
			pokerPlayers.get(i).returnCards();
		}
	}

	public void printPlayerChips() {		
		tweet+=("\nCHIPS\n");
		System.out.println("\n>> CHIP LISTINGS\n");
		
		for (int i = 0; i<playersIn.size(); i++) {
			tweet+=">" + playersIn.get(i).name + ": " + playersIn.get(i).numberOfChips + " chip(s)\n";
			System.out.println("> " + playersIn.get(i).name + " has " + playersIn.get(i).numberOfChips + " chip(s) in the bank");
		}
	}
	
	//dealing all players a new hand
	public void newHandCycle(){
		
		tweet+= "\nDEALING\n";
		
		System.out.println("\n>DEALING\n");
		
		for (int i = 0; i < playersIn.size(); i++) {
			//won't owe anything at start of new round
			playersIn.get(i).amountToCall=0;
			playersIn.get(i).newHand();
			if(playersIn.get(i).isHuman()){
				tweet+=playersIn.get(i).hand.toString();
				System.out.println(playersIn.get(i).hand.toString());
			}
		}	
	}
	
	//asking all players if they want to discard
	public void discardCycle(){
		for (int i = 0; i<playersIn.size(); i++) {

			if(playersIn.get(i).isHuman()){
				System.out.println(playersIn.get(i).hand.toString());
				tweet+="Discard? (enter n or 0,1,3): ";
				System.out.println("Discard cards? (enter n or 0,1,3): ");
				
				sendReply(tweetId, tweet);
				tweet = "";
				
			    lastUserReplyTweet = findReply(botLastTweetId); 
				    
				
				boolean validInput = false;
				while(!validInput){
					validInput = ((HumanPokerPlayer) playersIn.get(i)).discard(lastUserReplyTweet.getText());
					if(!validInput){
						tweet+="\n\nInvalid input, enter n or a sequence of numbers (Ex: 0,1,3): ";
						sendReply(lastUserReplyTweet, tweet);
					    lastUserReplyTweet = findReply(botLastTweetId); 
						
						
						System.out.println("Invalid input, enter n or a sequence of numbers (Ex: 0,1,3): ");
					}
					else if(validInput){
						tweet+="\n" + playersIn.get(i).hand.toString();
					}
					
					
				}
			} else {
				((AutomatedPokerPlayer) playersIn.get(i)).discard();
			}
		}
	}

	public void bettingRound() {
		
		
		//going to cycle through all players for a round of betting
		for (int i = 0; i<playersIn.size(); i++) {
			
			if(playersIn.size()==1){
				cleanRound=true;
				return;
			}
			
			//means everyone has called in this round and betting will stop
			if(clean >= playersIn.size()){
				cleanRound = true;
				return;
			}
			
			System.out.println(playersIn.get(i).getName() + " has " + playersIn.get(i).getNumberOfChips() + " chips");
			
			//if the betting is already open or if player can open betting
			if(playersIn.get(i).canOpenBetting() || open){
				
				//all their remaining chips are invested in this round
				if(playersIn.get(i).getNumberOfChips()<=0){
					clean++;
				}
				
				//still have chips left so have a choice to bet/fold/see
				else if(playersIn.get(i).getNumberOfChips()!=0){
					
					//stores value that current player would need to call with
					needToCall = playersIn.get(i).amountToCall;
					
					//if their call value is more than their chips
					if(needToCall>=playersIn.get(i).numberOfChips){
						//call value will become all of their chips (all in)
						needToCall = playersIn.get(i).numberOfChips;
						System.out.println(playersIn.get(i).getName()+" see/call to go all in with = " + needToCall + "chips");
					}
					
					//if they have more chips than call value
					else{
						//if betting has been opened
						if(open){
							System.out.println(playersIn.get(i).getName()+" call/see amount = " + needToCall + " chip(s)");
						}
						if(!open){
							System.out.println("Betting hasn't been opened yet.");
						}
					}
						
					//state will become either -1,0,1
					
					if(playersIn.get(i).isHuman()){
						boolean validInput = false;
						tweet+="\n"+playersIn.get(i).numberOfChips+" Chips";
						System.out.println("You have: " + playersIn.get(i).numberOfChips + " chips");
						if (!open){
							tweet+="\nraise or fold?";
							//System.out.print("Would you like to raise or fold?");
						} else {
							tweet+="\nSee(" + playersIn.get(i).amountToCall + " chip(s)), raise or fold?";
							//System.out.print("Would you like to raise, see or fold: ");
						}
						
						sendReply(lastUserReplyTweet,tweet);
						tweet = "";
						
						while (!validInput){
							lastUserReplyTweet = findReply(botLastTweetId);
							state = ((HumanPokerPlayer) playersIn.get(i)).getBet(lastUserReplyTweet.getText(), playersIn.get(i).amountToCall, open);
							if (state==-2){
								tweet = "";
								tweet+="\n\nInvalid input!";
								System.out.println("Invalid Input! ");
								System.out.println("You have: " + playersIn.get(i).numberOfChips + " chips");
								if (!open){
									tweet+=" enter 'raise' or 'fold': ";
									System.out.print(">> Would you like to raise or fold: ");
								} else {
									tweet+=" enter 'raise', 'see' or 'fold': ";
									System.out.print(">> Would you like to raise, see or fold: ");
								}
								sendReply(lastUserReplyTweet, tweet);
							    lastUserReplyTweet = findReply(botLastTweetId);
							} else {
								validInput = true;
							}
						}
					} else {
						state = ((AutomatedPokerPlayer) playersIn.get(i)).getBet(playersIn.get(i).amountToCall, open);
					}
					//player wants to raise
					if(state==1){
						//betting becomes open
						open = true;
						//pot is increased by their call value + 1 (they raised)
						pot+=needToCall+1;
					
						//increasing all players call value except their own
						for (int j = 0; j < playersIn.size(); j++) {
							if(j!=i){
								playersIn.get(j).amountToCall++;
							}
						}
					
						//they now don't need to call anything
						playersIn.get(i).amountToCall = 0;
						tweet+="\n" + playersIn.get(i).getName() + ": raised";
						System.out.println(playersIn.get(i).getName() + ": raised");
						clean = 0;
					}
					
					//player has called
					else if(state==0){
						pot+=needToCall;
						//now don't need to call anything
						playersIn.get(i).amountToCall = 0;
						tweet+="\n" + playersIn.get(i).getName() + ": called";
						System.out.println(playersIn.get(i).getName() + ": called");
						clean++;
					}
					
					//player has folded
					else if(state==-1){
						playersIn.get(i).amountToCall = 0;
						tweet+="\n" + playersIn.get(i).getName() + ": folded";
						System.out.println(playersIn.get(i).getName() + " has folded");
						playersIn.remove(i);
						i--;
						clean++;
					}
				}
			}
			
			
			//if the player can't open betting and it isn't open yet
			else if(!playersIn.get(i).canOpenBetting() && !open){
				
				System.out.println(playersIn.get(i).getName()+" cant open betting!");
				//counts amount of players who can't open
				cantOpen++;
				
				//if nobody is able to open
				if(cantOpen>=playersIn.size()){
					System.out.println("Nobody wants to open, round over!");
					roundOver = true;
				}
				
			}
			
		}
	
	}

	public void showCards() {
		
		System.out.println("\nEND OF ROUND\n");
		tweet ="\nRound Over";
		
		for (int i = 0; i<playersIn.size(); i++) {
			System.out.println(playersIn.get(i).name + ": " + playersIn.get(i).hand.toString());
			tweet+="\n" + playersIn.get(i).name + ": " + playersIn.get(i).hand.toString();
		}
	}

	public int decideWinner() {
		
		int max = 0;
		int winner=0;
		
		for (int i = 0; i < playersIn.size(); i++) {
			if(playersIn.get(i).hand.getGameValue()>max){
				max = playersIn.get(i).hand.getGameValue();
				winner = i;
			}
		}
		
		return winner;
			
	}
	
	public static void sendReply(Status replyingTo, String message){
		
		while(message.length()+userName.length() > 130){
			
			System.out.println(message.length() + "\n\n\n\n\n\n");
			System.out.println(message);
			
			
			int i = message.indexOf("\n");

			message = message.substring(i+1);
			

			
		}
		
		message = "..." + message;	
		
		
		String newTweet = ("@" + userName + message);
		StatusUpdate replyStatus = new StatusUpdate(newTweet);
		replyStatus.setInReplyToStatusId(replyingTo.getId());
		 
	    try {
			currentStatus = twit.updateStatus(replyStatus);	
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    botLastTweetId = currentStatus.getId();
		
	}
	
	
	
	
	
	
	
	//will return the tweet replying to Id
	public static Status findReply(long currentId) {
		
		long searchingFor = currentId;
        
        
        ResponseList<Status> tweets = null;
		
        while (true) {
        	
    		try {
    			tweets = twit.getMentionsTimeline();
    		} catch (TwitterException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
            //get the most recent tweet
            for(int i = 0; i < tweets.size(); i++) {

                if(tweets.get(i).getInReplyToStatusId()==searchingFor) {
	                    return tweets.get(i);
                    }
                }

            //wait 10 seconds
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
                  //Handle exception

           }
        }
    }
	
	
	
	
}