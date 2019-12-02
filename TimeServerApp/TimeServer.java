package TimeServerApp;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date; 

public class TimeServer { 
	 
	 public static void main (String[] args) throws IOException{   
		 try {    
			 ServerSocket myServerSocket = new ServerSocket(1234);
			 						//create a server side socket with port number 1234
			 PrintWriter out = null;
			 
			 
			 
			 while(true) { 			//so the server stays open
				 
				 
				 Socket connectedClientSocket = myServerSocket.accept();
				 					//accept a client that connects through the socket
				 
				 out = new PrintWriter(connectedClientSocket.getOutputStream(), true);
				 					//creates the output stream through the client socket and sets it to auto flush   
				 String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()); 
				 					//creates a string of the date and time of the machine   
				 out.println(date); //prints the string to the output stream   
				 out.close(); 		//closes the output stream   
				 connectedClientSocket.close();   
				 } 
			 
			 } catch(IOException e) {   //runs if an error occurs    
				 System.out.println("Error:" + e.getMessage());            
				 						//prints error message    
			 }
	 }
}