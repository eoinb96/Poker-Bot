package poker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


 
public class NamexTweet {
	
    private final static String CONSUMER_KEY = "gUu7VHKkQleLeV39U1d3GAsgd";
    private final static String CONSUMER_KEY_SECRET = "y7THkPkqvLa1Svp6yfNZjbTGrbbWg1OkXMiL6cuzqAhwTm0lAc";
    public static String name = "";
    public static String userName = "";
	public static int entry = 0;
	public static int botNum = 0;
	
	
	
    public void start() throws TwitterException, IOException {	
    	    
        	ConfigurationBuilder builder = new ConfigurationBuilder();
        	builder.setOAuthConsumerKey(CONSUMER_KEY);
        	builder.setOAuthConsumerSecret(CONSUMER_KEY_SECRET);
        	Configuration configuration = builder.build();
        	TwitterFactory factory = new TwitterFactory(configuration);
        	Twitter twitter = factory.getInstance();
        	
        	
        	
        	 String accessToken = "852105995459448833-Fh2rnK3hjvZSpP9BUJUNsNVCCA9h9fO";
        	 String accessTokenSecret = "5EEg95y3yGuLy2G2muuVlhdCSPBeumtfgq1BHTjy70y5n";
        	 
        	 AccessToken oathAccessToken = new AccessToken(accessToken, accessTokenSecret);
        	 
        	 twitter.setOAuthAccessToken(oathAccessToken);        	
        	 
        	 TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();
     	     StatusListener listener = new StatusListener() {

     	        @Override
     	        public void onStatus(Status status) {
     	                
     	        	name = status.getUser().getName();
     	       		  StatusUpdate replyStatus = new StatusUpdate("@"+status.getUser().getScreenName()+" Welcome to the Automated Poker Machine! How many bots would you like to play against (1-4)?");
     	       		 replyStatus.setInReplyToStatusId(status.getId());
     	     	    try {
    					twitter.updateStatus(replyStatus);	
    					
    				} catch (TwitterException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
     	     	    
     	     	    
     	     	    
     	     	    first(twitter,configuration);
     	     	    
     	        
     	        }
     	       
     	        
     	        
     	        @Override
     	        public void onException(Exception ex) {
     	            ex.printStackTrace();
     	        }

     	        @Override
     	        public void onDeletionNotice(StatusDeletionNotice arg0) {
     	                  // TODO Auto-generated method stub

     	        }

     	        @Override
     	        public void onScrubGeo(long arg0, long arg1) {

     	        }

     	        @Override
     	        public void onStallWarning(StallWarning arg0) {
     	            // TODO Auto-generated method stub
     	            System.out.println(arg0);
     	        }

     	        @Override
     	        public void onTrackLimitationNotice(int arg0) {
     	            // TODO Auto-generated method stub
     	            System.out.println(arg0);
     	        }

     	    };

     	    twitterStream.addListener(listener);
     	    FilterQuery filterQuery = new FilterQuery();
     	    filterQuery.track("#dealmeindhk");
     	    
     	    twitterStream.filter(filterQuery);
    }
    
    public void first(Twitter twitter, Configuration configuration){
    	
    	TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();
	    StatusListener listener = new StatusListener() {

	        @Override
	        public void onStatus(Status status) {
	        //here you do whatever you want with the tweet
	        UserMentionEntity[] mentions = new UserMentionEntity[status.getUserMentionEntities().length];
	        	
	        if(mentions.length>0){ //Means its is a reply, not the entry hashtag
		    	  String botNumString = status.getText();
		    	  userName = status.getUser().getScreenName();
  		       		botNumString = botNumString.substring(14);
  		       		char botNumChar = botNumString.charAt(0);
  		       		if(botNumChar<'1'||botNumChar>'4'||botNumString.length()>1){
			    		 StatusUpdate replyStatus = new StatusUpdate("@"+userName+" Invalid number of bots! Select a number between 1 and 4!");
	     	       		 replyStatus.setInReplyToStatusId(status.getId());
	     	     	    try {
	    					twitter.updateStatus(replyStatus);	
	    					
	    				} catch (TwitterException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}     			    	}
			    	else{
			    		// 	System.out.println(botNum);
			    		botNum = Character.getNumericValue(botNumChar); 
			    	twitterStream.clearListeners();
			    		GameOfPoker game = new GameOfPoker(name, botNum, userName, status, twitter, configuration);
			    		try {
							game.playGame();
							 twitterStream.cleanUp();
							 twitterStream.shutdown();
							return;
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}
	        }
	       
	        }
	        
	        @Override
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }

	        @Override
	        public void onDeletionNotice(StatusDeletionNotice arg0) {
	                  // TODO Auto-generated method stub

	        }

	        @Override
	        public void onScrubGeo(long arg0, long arg1) {

	        }

	        @Override
	        public void onStallWarning(StallWarning arg0) {
	            // TODO Auto-generated method stub
	            System.out.println(arg0);
	        }

	        @Override
	        public void onTrackLimitationNotice(int arg0) {
	            // TODO Auto-generated method stub
	            System.out.println(arg0);
	        }

	    };

	    twitterStream.addListener(listener);
	    FilterQuery filterQuery = new FilterQuery();
	    filterQuery.track("@DHK_pokerBot");
	    
	    twitterStream.filter(filterQuery);
	 
    
    }
    
    public static void main(String[] args) throws Exception {
    	NamexTweet nameX = new NamexTweet();// run the Twitter client
    	nameX.start();
    	return;
    }
    	
}


     	 