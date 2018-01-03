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
    
     //handles user input for local host and port number

     if(args.length < 1) {
             System.out.println("Now using default host and portNumber");
     } else if(args.length < 2){
          portNumber = Integer.valueOf(args[0]).intValue();
          System.out.println("Now using port number= "+portNumber);
          System.out.println("Now using default local host.");
     } else {
          host = args[0];
          portNumber = Integer.valueOf(args[1]).intValue();
          System.out.println("Now using host = " + host);
          System.out.println("Now using port number= " + portNumber);        
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

        //begin multithreading

        user.start();
    
        if (userSocket != null && output_stream != null && input_stream != null) {
            try {
               while(true){
                   String userInput = inputLine.readLine().trim(); 
                   if (!userInput.equals("#Bye") || userInput != null ){
                      output_stream.println(userInput); //send name to the server      
                 } else{ 
                      break;
             } 
           } 
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
    * Keep on reading from the socket until we receive a Bye from the server.
    * Once we receive that, we break
    * Format messages printed by other users so they do not contain '#status'
    */

   //will hold response from Server.java
   String responseLine;

   try{
        while((responseLine = input_stream.readLine()) != null) {
            if (responseLine.contains("#newStatus")) {
               responseLine = responseLine.replace("#newStatus", "");
               System.out.println(responseLine);
         }  else if (responseLine.contains("#welcome")) {
               System.out.println("Connection has been established with the server");
         }  else if (responseLine.contains("#busy")) {
               System.out.println("Server is too busy, try again later.");
         }  else if (responseLine.contains("#Bye")){
               responseLine = responseLine.replace("#Bye", "");
               System.out.println("Goodbye" + responseLine);
               break;
         }  else if (responseLine.contains("#newuser")) {
               responseLine = responseLine.replace("#newuser", "");
               System.out.println("A new user " + "<" + responseLine + ">"  + " has joined the chat.");
         }  else if (responseLine.contains("#Leave")) {
               responseLine = responseLine.replace("#Leave", "");
               System.out.println("User <" + responseLine + ">" + " is leaving the chat.");
         }  else if (responseLine.contains("#statusPosted")) {
              responseLine = responseLine.replace("#statusPosted", "");
              System.out.println("Your status has been posted to the other users.");
         }  else if (responseLine.contains("Enter your name")) {
              System.out.println(responseLine);
         }  else if (responseLine.contains("Wrong format")) {
              System.out.println(responseLine);
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

} 
