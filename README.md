# Priority Split File Transfer Scaffolding Project
A program that has 2 appraoches to sending over information from a user to a server through HTTP Post Requests using Apache Jmeter

## Run Instructions
1. Install apache-jmeter-5.6.3 libraries
2. Copy code
3. In the environment you want your server to be run the upload_server.py file in the Files folder with the command "python3 upload_server.py" and the server will run on port 8000
4. Run either of the appraoch.java files in the src folder

## GUI Apache JMeter TestPlans
In the Files folder are the .jmx Apache Jmeter test plan files that do the same as the code but allow you to have a better visual of the results you open them in of Apache JMeter 5.6.3.

## GUI Apache JMeter TestPlans Run Instructions
To do so simply open the ApacheJMeter.jar in "apache-jmeter-5.6.3\bin" where you installed ApacheJemeter 5.6.3 and once the program opens click File in the top right then Open, now open either of the .jmx files and then once loaded change the File Path under File Upload in the HTTP requests to be where the files are placed on your computer.

Then change the HTTP request's IP address to be your server's (the IP of the machine you are running it on if you use the upload_server.py). Set the amount of thread users and Ramp up time in Thread Group To run the test plan click on with the Green arrow on the bar near the top of the window that should say start when you hover over it.

## Requirements
JDK 17
apache-jmeter-5.6.3
