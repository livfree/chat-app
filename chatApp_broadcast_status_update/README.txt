Implementing a Broadcast Status Update Using Multi-threading

To compile:

javac filename.java

To run:

For Server.java:
  java Server <port number> <max number of users>
     or
  java Server [default port number and max number of users will be used]

For User.java:
  java User <host name> <port number>
     or 
  java User <port number> [default host name will be used]
    or
  java User               [default host name and port number will be used]

The port numbers should be the same for both Server and User, and should be between 58000-58990.

After compiling all .java files, run Server first and the User. 

Server.java:
The server uses the userThread class to send broadcast messages to all of the client threads.
The server accepts up to a certain number of users and replys to the client #welcome if the number
of users is below threshold, and #busy if there are not.

If a new user joins, the server sends a #newuser <username> to all of the other user programs, 
which print out that the new user has joined. 

When the server receives a #status update from the client, it sends that
message as #newStatus message the other user programs, which will print the username of the status sender
and their message. The server will then send a #statusPosted message to the original sender,
to let the user know that their message was posted.

Synchronize statements are used to make sure messages and threads do not get interrupted.

If a user types 'Exit', the server sends #Bye to that user and tells the other user that the
user is leaving by sending them a #Leave <username>. The server then sets the user thread to null 
and closes the socket to leave room for more users.

The server will only accept inputs(other than the user name) beginning with #status or Exit. 
So the client must write #status if they wish to send a message, otherwise they will be asked to try again. 

User.java:
New users are prompted to enter their name. Once doing so, they will receive a welcome message. 
All other users will also be notified that a new user has joined: #newuser <username>. 
If the server is busy, the client will receive a message telling them to try again later.
If a user wants to send a message, they must begin the message with '#status', so the server 
can recognize it and send it to the other users. The user sending a message will receive a #statusPosted
message from the server if their message was sent successfully. 
If a user wants to leave the groupchat, they must type 'Exit', and the server will respond with a 
goodbye message (#Bye <username>), and close the connection. 

The user will only print out messages if they begin with specific key words from the server. For example, 
#Bye, #newStatus, #Leave, #newuser, #statusPosted, #join, #welcome, and #busy. 
Thus, the client must be specific when entering messages/commands. 

Code is based on the skeleton code under Broadcast that was provided in the handout. 


Assumptions:
No two users have the same name. 

