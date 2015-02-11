package Server.aws.yulei;



import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;




//import src.String;
//import src.AWSCredentials;
//import src.AmazonClientException;
//import src.Exception;
//import src.ProfileCredentialsProvider;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.User;

import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;


public class Stream {
	private Connection con;
	private PreparedStatement pstm;
	public static String dbName = "base1"; 
	public static String userName = "user1";
	public static String password = "90289028";
	public static String hostname = "example3.cpfleo5mk9b3.us-east-1.rds.amazonaws.com";
	public static String port = "3306";
	static String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName +
			"?user=" + userName + "&password=" + password;
	static String connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName;
	public static Connection connection ;
	public static Statement command;
	public static TwitterStream twitterStream; 
	public static AmazonSQS sqs =null;
	public static String myQueueUrl = null;
	 
	

	
		public static void main(String[] args) throws SQLException{
			initialization();
			startCollect();
		}
		public static void initialization(){
			 /*
	         * The ProfileCredentialsProvider will return your [default]
	         * credential profile by reading from the credentials file located at
	         * ().
	         */
	        AWSCredentials credentials = null;
	        try {
	            credentials = new ProfileCredentialsProvider("default").getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (/Users/daniel/.aws/credentials), and is in valid format.",
	                    e);
	        }
	        sqs = new AmazonSQSClient(credentials);
	        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
	        sqs.setRegion(usEast1);

	        System.out.println("===========================================");
	        System.out.println("Getting Started with Amazon SQS");
	        System.out.println("===========================================\n");
	        try {
	            // Create a queue
	            System.out.println("Creating a new SQS queue called MyQueue.\n");
	            CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");
	            myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

	            // List queues
	            System.out.println("Listing all queues in your account.\n");
	            for (String queueUrl : sqs.listQueues().getQueueUrls()) {
	                System.out.println("  QueueUrl: " + queueUrl);
	            }
	            System.out.println();
	        }catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which means your request made it " +
	                    "to Amazon SQS, but was rejected with an error response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	            
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means the client encountered " +
	                    "a serious internal problem while trying to communicate with SQS, such as not " +
	                    "being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
		}
	
		public static void startCollect() throws SQLException{
		    
			//connection =DriverManager.getConnection(jdbcUrl);
			//command = connection.createStatement();
			
			StatusListener listener = new StatusListener(){
		    	
		        public void onStatus(Status status) {
		        	if (status.getGeoLocation()!=null) {
		        	User user = status.getUser();
	                
		        	
	                String username = status.getUser().getScreenName();

	                Double latitude = status.getGeoLocation().getLatitude();
	                Double longitude = status.getGeoLocation().getLongitude();	                
	                String content = status.getText();
	                String time = status.getCreatedAt().toString();
	                String keyword = "nnon";
	                content = content.replaceAll("'", "\"");
	                System.out.println(username+"########"+content+"#######" + latitude + "########" + longitude + "####" + time);
	               /* try {
						command.execute("INSERT INTO Test (Name, Content, Latitude, Longtitude, Time, Keyword) VALUES ('"+ username+"', '" + content+"', '"+latitude + "', '"+ longitude +"','"+ time +"', '"+ keyword +"')");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  */
	                
	                
	                JSONObject obj = new JSONObject();
	                obj.put("userName", username);
	                obj.put("content", content);
	                obj.put("time", time);
	                obj.put("latitude", latitude);
	                obj.put("longitude", longitude);
	                StringWriter sw = new StringWriter();
	        		try {
						obj.writeJSONString(sw);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		String jsonText = sw.toString();
	        		System.out.println("Sending a message to MyQueue.\n");
	                /*sqs.sendMessage(new SendMessageRequest(myQueueUrl, jsonText));
	                try {
						SimpleSNSPush.push(jsonText);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	               
	                try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} */
	                SQSOperation.sendSQS(jsonText);
	                new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							SQSOperation.receiveSQS();
							
						}
	                	
	                }).start();
	                
	                
	                SQSOperation.receiveSQS();
		        	} 
		        }
		        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
		        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
		        public void onException(Exception ex) {
		            ex.printStackTrace();
		        }
				@Override
				public void onScrubGeo(long arg0, long arg1) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onStallWarning(StallWarning arg0) {
					// TODO Auto-generated method stub
					
				}
		    };
		    
		    twitterStream = new TwitterStreamFactory().getInstance();
		    FilterQuery fq = new FilterQuery();
		    String keywords[] = {"new york"};
		    //fq.track(keywords);
		    fq.locations(new double[][] { { -180, -90 }, { 180, 90 } });
		   // String[] mm = {"movie"};
		   // fq.track(mm);	    
		    twitterStream.addListener(listener);
		    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
		    //twitterStream.sample();
		    twitterStream.filter(fq); 
		
		}
		public static void streamEnd() {
			twitterStream.shutdown();
		}
}
