package FileTransferApp;

import java.io.*;
import java.net.*;

public class File_Client {
	static BufferedReader clientIn = null;
	static DataOutputStream ServerOut = null;
	static BufferedReader ServerIn = null;
	static final String host = "localhost";
	static final int port = 1234;

	public static void main(String[] args) throws IOException {
		try {
			String request = null;
			boolean quit = false;
			clientIn = new BufferedReader(new InputStreamReader(System.in));
			Socket myClientSocket = new Socket(host, port);// creates the client socket
			ServerOut = new DataOutputStream(myClientSocket.getOutputStream());// creates output to server
			ServerIn = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));// in from server
			while (quit == false) {// for closing client
				System.out.println("input command:");
				request = clientIn.readLine();// reads command from user
				String[] rAry = request.split(" ");// splits command into string array
				switch (rAry[0]) {// sees what first string in command is
				case "ls":
					ls(); // list files function
					break;
				case "put":
					if (rAry.length == 3)
						put(rAry[1], rAry[2]);// send file to server
					else
						System.out.println("invalid request");
					break;
				case "get":
					if (rAry.length == 3)
						get(rAry[1], rAry[2]);// get file from server
					else
						System.out.println("invalid request");
					break;
				case "quit":
					quit = true;// so that client is disconnected
					break;
				default:
					System.out.println("invalid request");// if a bad request
					break;
				}
			}
			ServerOut.writeBytes("server_quit");// disconnects from server
			ServerOut.close();
			ServerIn.close(); // closes everything
			myClientSocket.close();
		} catch (FileNotFoundException ex) {
			System.out.println(ex);
		}
	}

	private static void get(String filename, String filepath) throws IOException {
		String line = null;
		File newfile = new File(filepath + "\\" + filename);// creates file
		FileWriter Filewriter = new FileWriter(newfile);// to write to file
		Boolean filerecieve = false; // if file is recieved
		ServerOut.writeBytes("server_get " + filename + "\n");// sends get command to server
		while (((line = ServerIn.readLine()).equals("null")) != true) { // reads through entire file
			filerecieve = true;// indicates a file was received
			Filewriter.write(line + "\r\n");// writes input to file
		}
		Filewriter.close();
		if (filerecieve)
			System.out.println("file recieved");
		else {
			System.out.println("File not found");
			newfile.delete();// deletes empty file
		}
	}

	private static void put(String filename, String filepath) throws IOException {
		String line = null;
		ServerOut.writeBytes("server_put " + filename + "\n");// sends put command
		FileReader fileReader = new FileReader(filepath + "\\" + filename);// reads file
		BufferedReader bufferedfileReader = new BufferedReader(fileReader);
		while ((line = bufferedfileReader.readLine()) != null) { // reads through file
			ServerOut.writeBytes(line + "\n");// prints the lines to server
		}
		ServerOut.writeBytes("null\n");// to indicate file sent
		System.out.println("file sent");
		bufferedfileReader.close();
	}

	static void ls() throws IOException {
		String line;
		ServerOut.writeBytes("server_ls" + '\n'); // sends list command
		while ((line = ServerIn.readLine()).equals("null") != true) {// recives line from server
			System.out.println(line);// prints line to user
		}
	}
}