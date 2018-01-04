Writing a Client-Server Application

To compile:

javac filename.java

To run:

For Server.java:
  java Server <port number>
    or
  java Server               [default host name and port number will be used]

For User.java
  java User <host name> <port number>
    or
  java User                 [default host name and port number will be used]


The port numbers should be the same for both Server and User, and should be between 58000-58990.

After compiling and running all .java files, the client side can send a message to the server. The client 
messages to the server must start with '#status' (no quotes) followed by a user status, which
the server will then print(without the '#status'. The server will send a #statusPosted message back to the client if
its message has been printed/accepted. Once the client has received the #statusPosted message, it will
close its connection and exit. 

The code closely follows the skeleton code provided on the handout.

