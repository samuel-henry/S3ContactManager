package samuelh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3ContactManager {
	private static final String LINE_SEPARATOR = "------------------";
	private static final String BUCKET_NAME_VALID_REG_EX = "[a-z\\d.-]";
	private static final String PERIOD_REG_EX = "[.]";
	private static final String DASH_STRING = "-";
	private static final String CREDENTIALS_PATH = new File("AwsCredentials.properties").getAbsolutePath();
	
	private static AmazonS3 s3client;
	private static Scanner scn = new Scanner(System.in);;
	
	public static void main(String[] args) {
		String bucketName = "";
		
		System.out.println("Welcome to the S3 Contact Manager");
		System.out.println(LINE_SEPARATOR);
		System.out.println("***Make sure you have edited AwsCredentials.properties to include your AWS access keys before continuing***");
		System.out.println("***AwsCredentials.properties is at " + CREDENTIALS_PATH);
		//get the S3 client
		s3client = getS3Client();
		
		System.out.println("To begin, please enter the name of an S3 bucket:");
		bucketName = scn.nextLine();
		if (validateBucketName(bucketName)) {
			
			
			// let user interact with selected bucket as many times as they want
			while (true) {
				System.out.println("Please select an option below by entering the corresponding number and pressing enter");
				System.out.println();
				System.out.println("0 Exit the program");
				System.out.println("1 List bucket contents - display a list of " + bucketName + "'s contents");
				System.out.println("2 Delete an object in " + bucketName);
				System.out.println("3 Create a new object in " + bucketName);
				System.out.println("4 Edit an object in " + bucketName);
				handleUserChoice(scn.nextLine(), bucketName);
			}
			
		}
		
	}

	private static AmazonS3 getS3Client() {
		AWSCredentials myCredentials;
		AmazonS3 s3client = null;
		
		try {
			myCredentials = new PropertiesCredentials(new File(CREDENTIALS_PATH));
			s3client = new AmazonS3Client(myCredentials); 
			System.out.println(s3client.getS3AccountOwner().getDisplayName());
		} catch (Exception ex) {
			System.out.println("There was a problem reading your credentials");
			ex.printStackTrace();
		}
		
		return s3client;
	}

	private static boolean validateBucketName(String bucketName) {
		//check format
		if (!validateBucketNameFormat(bucketName)) {
			return false;
		}
		
		//check exists
		if (!validateBucketNameUsable(bucketName)) {
			return false;
		}
		
		return true;
	}

	//validate the format of a bucket name
	private static boolean validateBucketNameFormat(String bucketName) {
		int numAllNumericLabels = 0;
		
		
		//check valid length
		if (bucketName.length() < 3 || bucketName.length() > 255) {
			System.out.println("ERROR: Bucket name must be between 3 and 255 characters long, inclusive");
			return false;
		}
		
		String invalidCharsInBucketName = bucketName.replaceAll(BUCKET_NAME_VALID_REG_EX, "");
		
		//check only contains valid characters (lower case letters, numbers, dashes, periods)
		if (invalidCharsInBucketName.length() > 0) {
			System.out.println("ERROR: The following characters in your bucket name are invalid: " + invalidCharsInBucketName);
			return false;
		}
		
		//get labels by splitting on periods
		//TODO: check that we do not have successive periods
		String[] bucketNameLabels = bucketName.split(PERIOD_REG_EX);
		//System.out.println("Number of labels in " + bucketName + ": " + bucketNameLabels.length);
		
		String currLabel = "";
		String firstCharInLabelAsString = "";
		String lastCharInLabelAsString = "";
		
		//check valid labels
		for (int i = 0; i < bucketNameLabels.length; i++) {
			currLabel = bucketNameLabels[i];
			firstCharInLabelAsString = currLabel.substring(0,1);
			lastCharInLabelAsString = currLabel.substring(currLabel.length());
			
			//check that label does not begin or end with dash
			if (firstCharInLabelAsString.equals(DASH_STRING) || lastCharInLabelAsString.equals(DASH_STRING)) {
				System.out.println("ERROR: Labels may only begin with lower case letters or numbers - not dashes");
				return false;
				
			}
			
			
			//see if label is all numeric to check against IP Address format
			try {
				//try parsing the label into an integer
				Integer.parseInt(currLabel);
				
				//no exception thrown, this label is all numeric. increment number of all numeric labels
				numAllNumericLabels++;
			} catch (NumberFormatException ex) {
				//bucket is not IP address formatted
				numAllNumericLabels = Integer.MIN_VALUE;
			}
		}
		
		//if all labels in bucket are numeric, the bucket is formatted like an IP address, which is not allowed
		if (numAllNumericLabels == bucketNameLabels.length) {
			System.out.println("ERROR: Bucket name may not be formatted like an IP address (e.g. 192.168.5.4)");
			return false;
		}
		
		//bucket name is of a valid format
		return true;
	}
	
	//check that a bucket name is usable (i.e. either owned by the account or able to be (and then) created
	private static boolean validateBucketNameUsable(String bucketName) {
		if (bucketNameAlreadyOwnedByUs(bucketName)) {
			return true;
		}
		
		if (otherAccountOwnsBucketName(bucketName)) {
			return false;
		} else {
			//can create a bucket of this name. create it!
			return createBucket(bucketName);
		}
	}

	//check if another account owns the bucket of this name
	private static boolean otherAccountOwnsBucketName(String bucketName) {
		// check if bucket of this name is already owned by someone else
		System.out.println("ERROR: Another account already owns bucket " + bucketName);
		return false;
	}

	//create the specified bucket
	private static boolean createBucket(String bucketName) {
		// create the bucket. return false if there is a problem creating this bucket (e.g. it's been created in the time since we checked)
		
		//create this S3 bucket
		System.out.println("ERROR: There was a problem creating this bucket. Please try again.");
		return false;
	}

	//check if our account already owns this bucket
	private static boolean bucketNameAlreadyOwnedByUs(String bucketName) {
		
		return false;
	}
	
	//handle user's input for bucket options
	private static void handleUserChoice(String userInput, String bucketName) {
		int choice = -1;
		
		try {
			choice = Integer.parseInt(userInput);
		} catch (NumberFormatException ex) {
			System.out.println(userInput + " is invalid. Please enter only the number of the option you want");
		}
		
		System.out.println("1 List bucket contents - display a list of " + bucketName + "'s contents");
		System.out.println("2 Delete an object in " + bucketName);
		System.out.println("3 Create a new object in " + bucketName);
		System.out.println("4 Edit an object in " + bucketName);
		
		switch(choice) {
		case 0:
			System.out.println("Thank you for using S3 contact manager. Goodbye.");
			System.exit(0);
			break;
		case 1:
			listContentsInBucket(bucketName);
			break;
		case 2:
			deleteObjectInBucket(bucketName);
			break;
		case 3:
			createObjectInBucket(bucketName);
			break;
		case 4:
			editObjectInBucket(bucketName);
			break;
		default:
			System.out.println(choice + " is not a valid option. Please enter one of the numbers given");
		}
		
	}

	//edit an object in the specified bucket
	private static void editObjectInBucket(String bucketName) {
		// TODO Auto-generated method stub
		
	}

	//create an object in the specified bucket
	private static void createObjectInBucket(String bucketName) {
		// TODO Auto-generated method stub
		
	}

	//delete an object in the specified bucket
	private static void deleteObjectInBucket(String bucketName) {
		// TODO Auto-generated method stub
		
	}

	//list the contents of the specified bucket
	private static void listContentsInBucket(String bucketName) {
		// TODO Auto-generated method stub
		
	}
}
