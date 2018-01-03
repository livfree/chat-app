/*File:Server.java
 *Author:Olivia Liberti
 */

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;


public class Server {
  //create a server socket
  private static ServerSocket serverSocket = null;
  //create a user socket
  private static Socket userSocket = null;
  //create output stream
  private static PrintStream output_stream = null;
  //max amount of users on server
  private static int maxUsersCount = 5;

  //keep track of pairs of friends and pair of friend requests
  private static ArrayList<ArrayList<String>> friends = new ArrayList<>();
  private static ArrayList<ArrayList<String>> requests = new ArrayList<>();
  
  //create thread array
  private static final userThread[] threads = new userThread[maxUsersCount];

  public static void main(String args[]) {

    // The default port number.
    int portNumber = 8000;

    //handles user inputs for port number and max number of users/threads

    if (args.length < 1) {
      System.out.println("Usage: java Server <portNumber>\n" + "Now using port number= " + portNumber);
      System.out.println("Maximum user count= "+ maxUsersCount);
    } else {   
      portNumber = Integer.valueOf(args[0]).intValue();
      maxUsersCount = Integer.valueOf(args[1]).intValue();
      System.out.println("Now using port number= " + portNumber);
      System.out.println("Maximum user count= "+ maxUsersCount);
    }
      
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (Exception e) {
         System.out.println(e);      
    }
    
    //begin multithreading  

    while (true) {
      try {
        int i = 0;
        userSocket = serverSocket.accept();
        
        for (i = 0; i < maxUsersCount; i++) {
          if (threads[i] == null) {
            //create a thread 
            threads[i] = new userThread(userSocket, threads, friends,requests);
            threads[i].start();
            
            break;
          }
        }

        //make sure there is only max amount of users allowed

        if (i >= maxUsersCount) {

          // sends to the server and server will echo the message to the user.

          output_stream = new PrintStream(userSocket.getOutputStream());
          output_stream.println("#busy");

          // close the port for the user

          output_stream.close();
          userSocket.close();
        }
      } catch (IOException e) {
        
      }
    }
  } 
}

/* userThread class,controls what is done with incomming user messages for all threads */

class userThread extends Thread {
  //the name of the messaging user 
  private String userName = null;
  //the input stream
  private BufferedReader input_stream = null;
  //the output stream
  private PrintStream output_stream = null;
  //the user socket
  private Socket userSocket = null;
  //thread array (holds all current users)
  private final userThread[] threads;
  //max amount of users allowed
  private int maxUsersCount;

  //for keeping track of friends and their requests

  ArrayList<ArrayList<String>> friends = new ArrayList<>();
  ArrayList<ArrayList<String>> requests = new ArrayList<>();
 
  //constructor 
 
  public userThread(Socket userSocket, userThread[] threads, ArrayList<ArrayList<String>> friends, ArrayList<ArrayList<String>> requests) {
    this.userSocket = userSocket;
    this.threads = threads;
    this.friends = friends;
    this.requests = requests;
    maxUsersCount = threads.length;
 }

  public void run() {
    int maxUsersCount = this.maxUsersCount;
    userThread[] threads = this.threads;

    //start input and output streams
  
   try {
          input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
          output_stream = new PrintStream(userSocket.getOutputStream());
      while (true) { 
           output_stream.println("Enter your name.");   
           userName = input_stream.readLine();    
           break;   
       }

      output_stream.println("#welcome " + userName + "\nTo leave enter Exit in a newline.");
     
      //synchronized to stop errors in parallel threading
      //For new chat room users
 
       //synchronized(this) {
        for (int i = 0; i < maxUsersCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].output_stream.println("#newuser " + userName);
            
         // }
        }
      }
     
      String message;
      while (true) {
        message = input_stream.readLine();
        if (message.startsWith("Exit")) {
          break;
       }

        //for friend requests
        //store [requester, requested user] in array every time a user sends requests, then remove them once they are accepted or denied

        if (message.contains("#friendme")){
          String[] responseLine = message.split(" ", 2);
          if (responseLine[1] != null && !responseLine[1].isEmpty()) {
            ArrayList<String> req = new ArrayList<String>();
            req.add(userName);
            req.add(responseLine[1]);
            requests.add(req);        //request now added to the request list
              synchronized(this){
                for (int i = 0; i < maxUsersCount; i++) { 
                   for (int j = 0; i < requests.size(); i++) {
                       //if the thread equals the requested user, send them the friend request
                       if (threads[i].userName.equals(requests.get(i).get(1)) && threads[i] != null) {
                          threads[i].output_stream.println("#friendme" + "<" + requests.get(i).get(0)+ ">");
                           break;
                     }
                  }
              }
           }
       }
          //for accepting requests, remove the friend request once they have become friends
           //if 2 ppl become friends, add them to each other's friend list
           
        } else if (message.startsWith("#friends")) {
               String[] responseLine = message.split("\\s",2);
               synchronized (this) {
                 for (int i = 0; i < maxUsersCount;  i++) {
                   if (threads[i] != null && threads[i].userName != null && threads[i] != this && threads[i].userName.equals(responseLine[1])) {
                      ArrayList<String> list = new ArrayList<String>();
                      list.add(responseLine[1]);
                      list.add(userName);
                      friends.add(list);     //two friends now added in form of [friend1, friend2] in the friendship arraylist
                      threads[i].output_stream.println("#OKfriends" + "<" + userName + "> " + "<" + responseLine[1] + ">");
                      this.output_stream.println("#OKfriends" + "<" + userName + "> " + "<" + responseLine[1] + ">");
                      for (int j = 0; j < requests.size(); j++) {
                         if (requests.get(j).get(0).equals(responseLine[1]) && requests.get(j).get(1).equals(userName)) {
                            requests.remove(requests.get(j));     //remove the friend request because the users are now friends
                            break;
                         }
                    }
                    break;
                 }
            }
          }

         //if someone denies a friend request, remove the friend request and inform the requester

      }  else if (message.startsWith("#DenyFriendRequest")) {
               String[] responseLine = message.split("\\s",2);
               synchronized (this) {
                 for (int i = 0; i < maxUsersCount;  i++) {
                   if (threads[i] != null && threads[i].userName.equals(responseLine[1])) {
                      threads[i].output_stream.println("#FriendRequestDenied" + "<" + userName + ">");
       	              for (int j = 0; j < requests.size(); j++) {
                         if (requests.get(j).get(0).equals(responseLine[1]) && requests.get(j).get(1).equals(userName)) {
                            requests.remove(requests.get(j));
                            break;
       	       	         }
                    }
             }    
           }
          }

         //if someone unfriends someone else, inform both users that they are no longer friends, and remove the pair from the friend list

     }   else if (message.startsWith("#unfriend")) {
               String[]	responseLine = message.split("\\s",2);
       	       synchronized (this) {
                 for (int i = 0; i < maxUsersCount;  i++) {
                    if (threads[i] != null && threads[i].userName.equals(responseLine[1])) {
                       threads[i].output_stream.println("#NotFriends" + responseLine[1] + " and " + userName);
                       this.output_stream.println("#NotFriends" + responseLine[1] + " and " + userName);
                       ArrayList<String> remove1 = new ArrayList<String>();
       	               remove1.add(responseLine[1]);
                       remove1.add(userName);
                       ArrayList<String> remove2 = new ArrayList<String>();
                       remove2.add(userName);
                       remove2.add(responseLine[1]);

                       //make sure the friends are removed, order depends on who friended who

                       for (int j = 0; j < friends.size(); j++) {
                            if (friends.get(j).equals(remove1)) {
                                friends.remove(friends.get(j));
                                break;
                            }
                            if (friends.get(j).equals(remove2)) {
                                friends.remove(friends.get(j));
                                break;
                             }
                  }
              }  
       	    }
       	  }   
     }

     //friends can only talk to friends, use friend lists to determine who gets sent each message
    
        else if (message.startsWith("#status")) {
           message = message.replace("#status", "");
           ArrayList<String>  threadFriends = new ArrayList<String>();
           for (int i = 0; i < friends.size(); i++) {
              if (friends.get(i).get(0).equals(userName)) {
                 threadFriends.add(friends.get(i).get(1));
                }
              if (friends.get(i).get(1).equals(userName)) {
                 threadFriends.add(friends.get(i).get(0));
                } 
              }
           synchronized(this) {
                 for (int i = 0; i < maxUsersCount; i++) {
                     if (threads[i] != null && threads[i] != this && threads[i].userName != null && threadFriends.contains(threads[i].userName)) {
                         threads[i].output_stream.println("#newStatus" + "<" + userName + ">" + message);
                         this.output_stream.println("#statusPosted");
                      }
                   } 
              }

       } 

        //if user input is not valid, tell user to try again

        else {
               this.output_stream.println("Wrong input, try again.");
         }
        }  //close while loop

      //when someone leaves, echo to only their friends that they are leaving

       ArrayList<String>  threadFriends = new ArrayList<String>();
       for (int i = 0; i < friends.size(); i++) {
          if (friends.get(i).get(0).equals(userName)) {
              threadFriends.add(friends.get(i).get(1));
              friends.remove(friends.get(i));
        }
          if (friends.get(i).get(1).equals(userName)) {
              threadFriends.add(friends.get(i).get(0));
              friends.remove(friends.get(i));
         }
       }
        synchronized(this) {
           for (int i = 0; i < maxUsersCount; i++) {
              if (threads[i] != null && threads[i].userName != null && threadFriends.contains(threads[i].userName)) {
                  threads[i].output_stream.println("#Leave " + userName);
                  }
                }
         }

      // send the message to the leaving user.

      output_stream.println("#Bye " + userName);

      //cleanup and set leaving thread to null so a new user can join

      synchronized (this) {
        for (int i = 0; i < maxUsersCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
             } 
          }
        }
    
      //close streams and socket

      input_stream.close();
      output_stream.close();
      userSocket.close();
     
    } catch (Exception e) {
    }
  }
} 
