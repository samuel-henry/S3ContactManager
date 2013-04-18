NOTE: Per TA email to another student, I request that the user modify the included AwsCredentials.properties file with their keypair.

Scenario: A business wants to store contact information for its agents on individual web pages that are stored in S3. Your job is to write a utility that allows very basic administration of the pages. The pages should be very simple but well-formed HTML that contain:
A table with three columns and two rows
The first row is a header row and contain the labels:
 First Name
 Last Name
 Phone Number
The second row contains the corresponding data
Note that your program is a console (text-based) utility and does not need to render the HTML at any point.

Your program should do the following:
Use local credentials to access AWS
 Request a bucket name from the user
 Create the bucket if it does not already exist
Give the user the option to do any or all the following, zero or more times:
 List bucket contents – display a list of bucket contents on the screen
Delete an object in the bucket – user specifies object by name
Create a new object in the bucket – request the contact information (name, phone number), generate the HTML page, store it in the bucket
 Edit an object in the bucket – allow changes to the first name, last name or phone number in an existing object. The users should NOT be able to edit anything else in the file, such as HTML tags or column headers.
Allow the user to exit the program gracefully when done
The name of the objects should be meaningful to the user or they won’t know which object corresponds to which person.
