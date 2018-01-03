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

  private static final userThread[] threads = new userThread[maxUsersCount];

  public static void main(String args[]) {

    // The default port number.
    int portNumber = 8000;

    //handles user input for port number and max number of users/threads

    if (args.length < 1) {
       System.out.println("Now using port number= " + portNumber);
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
            threads[i] = new userThread(userSocket, threads);
            threads[i].start();
            
            break;
          }
        }

        //make sure there is only max amount of users allowed

        if (i >= maxUsersCount) {

          // sends to the server and server will echo the message to the user.

          output_stream = new PrintStream(userSocket.getOutputStream());
          output_stream.println("Server too busy. Try later.");
         
          // close the port for the user
         
          output_stream.close();
          userSocket.close();
        }
      } catch (IOException e) {
        
      }
    }
  } 
}

/* create threads to read from the user */

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

  //constructor

  public userThread(Socket userSocket, userThread[] threads) {
    this.userSocket = userSocket;
    this.threads = threads;
    maxUsersCount = threads.length;
  }

  // overwrites the run method from Thread 
 
  public void run() {

    //just particular thread or user's variable

    int maxUsersCount = this.maxUsersCount;
    userThread[] threads = this.threads;

    try {
          input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
          output_stream = new PrintStream(userSocket.getOutputStream());
      while (true) { 
           output_stream.println("Enter your name.");  
           userName = input_stream.readLine();
           break;    
      } 

      output_stream.println("#welcome " + userName);
     
     // Using synchronized to prevent error parallel threading;
  
      synchronized (this) {
        //for new users
        for (int i = 0; i < maxUsersCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].output_stream.println("#newuser " + userName);
         
          }
        }
      }
     
      while (true) {
        String message = input_stream.readLine();
        if (message.startsWith("Exit")) {
          break;
        }
         
         //send message to all the users in the socket  
        
          if (message.startsWith("#status")) {
              message = message.replace("#status", "");
              synchronized (this) {
              for (int i = 0; i < maxUsersCount;  i++) {
                 if (threads[i] != null && threads[i] != this && threads[i].userName != null) {
                   threads[i].output_stream.println("#newStatus" + "<" + userName + "> " + message);
                   this.output_stream.println("#statusPosted");
              }
            }
          }
        } else {
            this.output_stream.println("Wrong format, try again. Send message beginning with '#status'");
        }
        
      } // end of the while  

      //tell all users when someone leaves

      synchronized (this) {
        for (int i = 0; i < maxUsersCount; i++) {
          if (threads[i] != null && threads[i] != this && threads[i].userName != null) {
            threads[i].output_stream.println("#Leave" + userName);
              }
            }
        }

      //send to the leaving user.

      output_stream.println("#Bye " + userName);

      //clean up, make thread null so a new user can join

      synchronized (this) {
        for (int i = 0; i < maxUsersCount; i++) {
                if (threads[i] == this) {
                    // removing the thread 
                    threads[i] = null;
                  
               }
             }
         }


    //close all streams and sockets

      input_stream.close();
      
      userSocket.close();
      
      output_stream.close();
      
    } catch (Exception e) {
        System.out.println(e);
    }
  }
} 
