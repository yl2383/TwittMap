package Server.aws.yulei;


import com.alchemyapi.api.AlchemyAPI;
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
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import twitter4j.TwitterStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Sentiment {
//test
/*    public static void main(String[] args) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromString("a47635756e657c0dac0bc2d282dbde377de01513");

        // load text
        String text = "";

        // analyze text
        Document doc = alchemyObj.TextGetTextSentiment(text);

        // parse XML result
        String sentiment = doc.getElementsByTagName("type").item(0).getTextContent();
        String score = doc.getElementsByTagName("score").item(0).getTextContent();

        // print results
        System.out.println("Sentiment: " + sentiment);
        System.out.println("Score: " + score);
    }

  */  
    private volatile ArrayList<String> s;
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
	
	
    
    
    
    public ArrayList<String> sentimentTwitter(String twitter) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException, SQLException {
    	connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();
    	
    	ArrayList<String> result = new ArrayList<String>();
        String spiletTwitter[] = twitter.split("::");
        String content = spiletTwitter[1];
        String score = "0.000000";
        
    	AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromString("a47635756e657c0dac0bc2d282dbde377de01513");

        // load text
        String text = content;

        // analyze text
        Document doc = alchemyObj.TextGetTextSentiment(text);

        // parse XML result
        String sentiment = doc.getElementsByTagName("type").item(0).getTextContent();
        try{
        score = doc.getElementsByTagName("score").item(0).getTextContent();
        } catch (Exception e) {
        	
        }
        
        
        // print results
        System.out.println();
        
        
        
        System.out.println("*******Sentiment:******* " + sentiment + "+++++" + score );
//        System.out.println("Score: " + score);
        result.add(spiletTwitter[0]); 	//useID
        String username = spiletTwitter[0];
        result.add(content);			//content
        result.add(spiletTwitter[2]);	//latitude
        String latitude = spiletTwitter[2];
        result.add(spiletTwitter[3]);	//longitude
        String longitude = spiletTwitter[3];
        result.add(spiletTwitter[4]);	//time
        String time = spiletTwitter[4];
        result.add(spiletTwitter[5]);	//keyword
        String keyword = spiletTwitter[5];
        result.add(sentiment);			//sentiment
        result.add(score);				//score
        
        
/*        try {
			command.execute("INSERT INTO Test (Name, Content, Latitude, Longtitude, Time , Keyword) VALUES ('"+ username+"', '" + content+"', '"+latitude + "', '"+ longitude +"','"+ time +"', '"+keyword +"','"+ sentiment +"','"+ score+"')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        return result;
    }
   // public static void main(String[] args) throws Exception { 
    public String getSQSinformation() throws Exception {
    	String c = "10711071";
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

        AmazonSQS sqs = new AmazonSQSClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);

        System.out.println("\n===========================================");
        System.out.println("Getting Started with Amazon SQS");
        System.out.println("===========================================\n");

        try {
        	String myQueueUrl = "https://sqs.us-east-1.amazonaws.com/837022966665/sqstest";
            // Receive messages
            System.out.println("Receiving messages from MyQueue.\n");
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl)
            												.withMaxNumberOfMessages(1);
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            for (Message message : messages) {
            	
                System.out.println("  Message");
                System.out.println("    MessageId:     " + message.getMessageId());
                System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
                System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
                System.out.println("    Body:          " + message.getBody());
                String twitter = message.getBody();
                String spiletTwitter[] = twitter.split("::");
                String content = spiletTwitter[1];
                c = twitter;
                System.out.println(content);
                
                for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                    System.out.println("  Attribute");
                    System.out.println("    Name:  " + entry.getKey());
                    System.out.println("    Value: " + entry.getValue());
                }
            }
            System.out.println();
            
            System.out.println("Deleting a message.\n");
            for (Message message : messages) {
            	String messageRecieptHandle = message.getReceiptHandle();
            	System.out.println(message.getBody());
                sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageRecieptHandle));
            }
            
         
        } catch (AmazonServiceException ase) {
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
        return c;
    }
    
    //public static void main(String args[])throws Exception {
    	  public void getSQSinformation22() throws Exception {
    	String c = "10711071";
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

        AmazonSQS sqs = new AmazonSQSClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);

        System.out.println("\n===========================================");
        System.out.println("Getting Started with Amazon SQS");
        System.out.println("===========================================\n");

        try {
        	String myQueueUrl = "https://sqs.us-east-1.amazonaws.com/837022966665/sqstest";
            // Receive messages
            System.out.println("Receiving messages from MyQueue.\n");
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl)
            												.withMaxNumberOfMessages(1);
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            for (Message message : messages) {
            	
                System.out.println("  Message");
                System.out.println("    MessageId:     " + message.getMessageId());
                System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
                System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
                System.out.println("    Body:          " + message.getBody());
               // String twitter = message.getBody();
             //   String spiletTwitter[] = twitter.split("::");
            //    String content = spiletTwitter[1];
            //    c = twitter;
                System.out.println("/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/*-/");
                
                for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                    System.out.println("  Attribute");
                    System.out.println("    Name:  " + entry.getKey());
                    System.out.println("    Value: " + entry.getValue());
                }
            }
            System.out.println();
            
            System.out.println("Deleting a message.\n");
            for (Message message : messages) {
            	String messageRecieptHandle = message.getReceiptHandle();
            	System.out.println(message.getBody());
                sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageRecieptHandle));
            }
            
         
        } catch (AmazonServiceException ase) {
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
}
