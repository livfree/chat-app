/*
 *File: User.java
 *Author: Olivia Liberti
 */

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class User extends Thread {    
  //The user socket
  private static Socket userSocket = null;
  //The output stream
  private static PrintStream output_stream = null;
  //The input stream
  private static BufferedReader input_stream = null;

  private static BufferedReader inputLine = null;
  private static boolean closed = false;

   public static void main(String[] args){

      //default port number
      int portNumber = 8000;
      String host = "localhost";
      User user = new User();
  

      //handles the user inputs for host and portnumber

      if (args.length < 1) {
             System.out.println("Using default host and port number.");
      } else if(args.length < 2){
          portNumber = Integer.valueOf(args[0]).intValue();
          System.out.println("Now using port number= "+portNumber);
          System.out.println("Using default local host.");
      } else {
          host = args[0];
          portNumber = Integer.valueOf(args[1]).intValue();
          System.out.println("Now using host= " + host);
          System.out.println("Now using port number= "+portNumber);
        }
    
        /*
         * Open a socket on a given host and port. Open input and output streams.
         */
        try {
               userSocket = new Socket(host,portNumber);
               inputLine = new BufferedReader(new InputStreamReader(System.in));
               output_stream = new PrintStream(userSocket.getOutputStream());
               input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));    
        } catch (UnknownHostException e) {
              System.err.println("Don't know about host " + host);
        } catch (IOException e) {
              System.err.println("Couldn't get I/O for the connection to the host " + host);
        }

        // multithreading
        user.start();
    
        if (userSocket != null && output_stream != null && input_stream != null) {
            try {
               while(true){

               //send user inputs to server
 
               String userInput = inputLine.readLine().trim(); 
               if (!closed ){
                  if (userInput.contains("@connect")) {
                     String [] msg = userInput.split(" ", 2);
                     String svrmsg = "#friendme " + msg[1];
                     output_stream.println(svrmsg);
               }  else if (userInput.contains("@friend")) {
                     String [] msg = userInput.split(" ", 2);
       	       	     String svrmsg = "#friends " + msg[1];
                     output_stream.println(svrmsg);
               }  else if (userInput.contains("@deny")) {
                     String [] msg = userInput.split(" ", 2);
                     String svrmsg = "#DenyFriendRequest " + msg[1];
                     output_stream.println(svrmsg);
               }  else if (userInput.contains("@disconnect")) {
                     String [] msg = userInput.split(" ", 2);
                     String svrmsg = "#unfriend " + msg[1];
                     output_stream.println(svrmsg);
               }  else if (!userInput.contains("@")) {
                     output_stream.println(userInput);      
               }  else {
                      System.out.println("Input is invalid, try again");
               }
           }   else{ 
                      break;
             } 
           } 

         //close the streams and socket

         input_stream.close();
         output_stream.close();
         userSocket.close();
       } catch (IOException e) {
            System.err.println("IOException: " + e);
        }
      }
  }

/*
 * Create a thread to read from the server
 */

public void run(){
   /*
    * Keep on reading from the socket untill we receive a Bye from the server.
    * Once we receive that, we break
    */

   String responseLine;
 
   //make sure user only prints valid inputs from the server.    
 
   try{
        while((responseLine = input_stream.readLine()) !=null) {
            if (responseLine.contains("#newStatus")) {
               responseLine = responseLine.replace("#newStatus", "");
               System.out.println(responseLine);
         } else if (responseLine.contains("#welcome")) {
               System.out.println("Connection has been established with the server");
         } else if (responseLine.contains("#busy")) {
               System.out.println("Server is too busy, try again later.");
               break;
         } else if (responseLine.contains("#statusPosted")) {
               System.out.println("Your status was posted to your friends.");
         } else if (responseLine.contains("#OKfriends")) {
               responseLine =responseLine.replace("#OKfriends", "");
               responseLine = responseLine + " are now friends";
               System.out.println(responseLine);
         } else if (responseLine.contains("#FriendRequestDenied")) {
               responseLine = responseLine.replace("#FriendRequestDenied", "");
               responseLine = responseLine + " rejected your friend request.";
               System.out.println(responseLine);  
         } else if (responseLine.contains("#NotFriends")) {
               responseLine = responseLine.replace("#NotFriends", "");
               responseLine = responseLine + " are no longer friends";
               System.out.println(responseLine); 
         } else if (responseLine.contains("#Bye")) {
               responseLine = responseLine.replace("#Bye", "");
               System.out.println("Goodbye " + responseLine);
               break;
         } else if (responseLine.contains("#Leave")) {
              responseLine = responseLine.replace("#Leave", "");
              System.out.println(responseLine + " is leaving the chat.");
         } else if (responseLine.contains("Wrong input")) {
              System.out.println(responseLine);
         } else if (responseLine.contains("Enter your name")) {
             System.out.println(responseLine);
         } else if (responseLine.contains("#newuser")) {
            responseLine = responseLine.replace("#newuser", "");
            System.out.println("New user" + "'" + responseLine + "'" + " type '@connect' if you want to friend them.");
         } else if(responseLine.contains("#friendme")) {
            responseLine = responseLine.replace("#friendme", "");
            System.out.println(responseLine + " wants to be friends. Type @friend " + responseLine + " to accept. Type @ deny " + responseLine + " to deny.");
         } 
      }   
           closed= true;
           input_stream.close();
           userSocket.close();
           output_stream.close();
           
        } catch(Exception e){
            System.err.println("IOException:  " + e);        
            System.exit(0);
            
        }  
    
}




} // end of user
