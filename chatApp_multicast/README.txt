Olivia Liberti
README
Adding Friendship/Multicast Capability to the Social Media App

To compile:

javac filename.java

To run:

For Server.java:
  java Server <port number> <max number of users>
     or
  java Server [default port number and max number of users will be used]

For User.java:
  java User <hostname> <port number>
     or
  java User <port number> [default host name will be used]
     or
  java User               [default host name and port number will be used]

The port numbers should be the same for both Server and User, and should be between 58000-58990.

After compiling all .java files, run Server first and then User. 

Server.java:
The server uses the userThread class to send broadcast messages to all of the client threads.
The server accepts up to a certain number of users and replys to the client #welcome if the number
of users is below threshold, and #busy if there are not. If a new user joins, the server sends a #newuser
to all of the other users. 

If a user friend requests someone, the server recieves a #friendme <username>, and sends that
request to the client that was requested. If the client accepts, the server updates the list of friends,
and sends both clients "OKfriends user1 user2". If the client denies, the server just sends the requester
#FriendRequestDenied <username of denier>.

If the server receives #unfriend <username> it updates the list of friends by removing the friend pair, 
and sends both clients #NotFriends user1 user2.

If a user leaves the groupchat, the server updates the list of friends by removing all friend pairs with 
the leaving user, and notifies the user's friends with: #Leave userleaving, and makes the thread null
so more users can join. 

The friends and	requests array lists hold a list of arrays that	have pairs of users, either pairs
of friends for the friend array, or pairs of requests in the request array.

Synchronized statements are used to make sure messages and threads do not get interrupted.

The server will only accept certain inputs from a user, as specified above. If an input is
invalid, the server will have the client print out a message asking the user to try again. 


User.java:
New users are prompted to enter their name. Once doing so, they will receive a welcome message.
All other users will also be notified that a new user has joined: #newuser <username>.
If the server is busy, the client will receive a message telling them to try again later.

If a user wants to send a message, they must begin the message with '#status', so the server
can recognize it and send it to the other users. The user sending a message will receive a #statusPosted
message from the server if their message was sent successfully.

If a user wants to send a message to a specific user, they have to type: @connect<username>. Doing this
causes the client program to send the server a message #friendme <username>, and the server sends that
to the user who was friended. The receiver of the friend request then types @friend <requester username>
to accept, and then both clients will be sent #OKfriends user1 user2 from the server, 
and their console will print 'user1 and user2 are now friends'.

To deny a friend request, the receiver types @deny<requester username>. The server will then send
#FriendRequestDenied <username> to the requester, and the user1 will see: 'user2 rejected your
friend request'. 

To unfriend someone, a user must type @disconnect <username>, and the program sends the server
#unfriend <username>. The friend being unfriended gets #NotFriends user1 user2 from the server, and
both ex-friends' consoles will print 'user1 and user2 are no longer friends'.

If a user wants to leave the groupchat, they must type 'Exit', and the server will respond with a
goodbye message (#Bye <username>), and send a '#Leave userleaving' to the users friends,
and then the connection is closed by the server.

The user will only print out messages if they begin with specific key words from the server, as specified
above. 

Assumptions
I am assuming that every input is in the correct order(@connect, then @friend), and I am assuming
that no two users have the same name.
Perhaps in the future to avoid duplicate usernames or incorrect order,
I could update the User program to only accept a certain order of commands, and keep track of the user names in an array to make sure a new user doesn't
use the same name as an existing user.

Tradeoffs and Extensions (parts1-3)
The more threads we have, the slower the program is. So, it is a balancing act of seeing how
many threads/users we can have (we want as much as possible so we can talk to all of our friends),
but also making sure everything is efficient. Obviously nobody will want to use the
social media app if it takes a day for your message to send. On the other hand, people won't
want to use the app if they can only talk to three friends. 

To extend this program, we could introduce pictures of users(similar to facebook), having the user
upload a photo of themselves when they enter the app. We could also introduce biographies of
each user, having them write a little bit about themselves before they enter the app. 

Improvements to this project, especially part 3, could be made by making less for loops and 
conditional statements. Doing so would speed up the response time and help make it
run more efficiently, perhaps allowing more threads to join without detracting as much from speed. 
