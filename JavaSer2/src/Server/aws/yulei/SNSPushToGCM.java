package Server.aws.yulei;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeletePlatformApplicationRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.sns.samples.mobilepush.SNSMobilePush;
import com.amazonaws.sns.samples.tools.SampleMessageGenerator.Platform;

public class SNSPushToGCM {
	
	
	public static void push(String mymessage) throws IOException{
		AmazonSNS sns = new AmazonSNSClient(new PropertiesCredentials(
				SNSMobilePush.class
						.getResourceAsStream("AwsCredentials.properties")));
	
	
		//sns.setEndpoint("https://sns.us-west-2.amazonaws.com");
		sns.setEndpoint("https://sns.us-east-1.amazonaws.com");

		// TODO: Please fill in following values for your application. You can
		// also change the notification payload as per your preferences using
		// the method
		// com.amazonaws.sns.samples.tools.SampleMessageGenerator.getSampleAndroidMessage()
		String serverAPIKey = "AIzaSyBuwesjgdcLUR87z7RGQ94uHD-ZmY_lG1g";
		String applicationName = "com.amazonaws.androidtest";
		String registrationId = "APA91bFocqvacjzoMtn7p35uVZhqRlPsnEwK4BmEcC3sy01Z519TfJpKi5uqZnI51hOwHM7i0YVwLm4n3RIwPQg_uGw-rbklXbrpUqpk-tN-W-DMOIlHQJ_78xvBtIcUpjfwKgYJ-Up5Z-Ld-s6e52qn07R9kntc0JA2lBMHAGd4gk4wDvkqCPI";
		
		// Create Platform Application. This corresponds to an app on a
		// platform.
		//CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(
		//		applicationName, Platform.GCM, "", registrationId);	
	
		CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("PlatformPrincipal", "");
		attributes.put("PlatformCredential", serverAPIKey);
		platformApplicationRequest.setAttributes(attributes);
		platformApplicationRequest.setName(applicationName);
		platformApplicationRequest.setPlatform(Platform.GCM.name());	
		CreatePlatformApplicationResult platformApplicationResult =sns.createPlatformApplication(platformApplicationRequest);
		String platformApplicationArn = platformApplicationResult
				.getPlatformApplicationArn();
		
		CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
		platformEndpointRequest.setCustomUserData("asa");
		platformEndpointRequest.setToken(registrationId);
		platformEndpointRequest.setPlatformApplicationArn(platformApplicationArn);
	
		CreatePlatformEndpointResult platformEndpointResult =sns.createPlatformEndpoint(platformEndpointRequest);		
		
		PublishRequest publishRequest = new PublishRequest();
		
		//publishRequest.setMessageStructure("json");
		
		String message = mymessage;
		publishRequest.setTargetArn(platformEndpointResult.getEndpointArn());
		publishRequest.setMessage(message);
		PublishResult publishResult = sns.publish(publishRequest);
		System.out.println("Published! \n{MessageId="
				+ publishResult.getMessageId() + "}");
		// Delete the Platform Application since we will no longer be using it.
		DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest();
		request.setPlatformApplicationArn(platformApplicationArn);
		
		sns.deletePlatformApplication(request);
	}
	
	

	
	
	
	
	
	
}
