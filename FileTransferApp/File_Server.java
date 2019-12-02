package FileTransferApp;

import java.io.*;
import java.net.*;

public class File_Server {

	public static void main(String[] args) throws IOException {

		try {
			ServerSocket myServerSocket = new ServerSocket(1234);// create a server side socket with port number 1234
			PrintWriter out = null;
			BufferedReader in = null;
			String request = null;
			boolean quit = false;

			while (true) {// so the server stays open
				Socket connectedClientSocket = myServerSocket.accept(); // accept a client that connects through the
																		// socket
				out = new PrintWriter(connectedClientSocket.getOutputStream(), true);// creates the output stream
																						// through the client socket and
																						// sets it to auto flush
				in = new BufferedReader(new InputStreamReader(connectedClientSocket.getInputStream())); // reads from
																										// client

				while (quit == false) {
					request = in.readLine();

					if ((request.length() > 8) && (request.equals("server_ls"))) { // the length test is to avoid a null
																					// pointer crash
						server_ls(out); // list files function
					} else if ((request.length() > 10) && (request.substring(0, 10).equals("server_put"))) {
						server_put(request.substring(11), in); // put file function
					} else if ((request.length() > 10) && (request.substring(0, 10).equals("server_get"))) {
						server_get(request.substring(11), out);// get file function
					} else if ((request.length() > 10) && request.equals("server_quit")) {
						quit = true;// so that client is disconnected
					} else
						out.println("invalid request");
				}
				out.close();// closes the output stream
				connectedClientSocket.close();
				quit = false;
			}
		} catch (IOException e) {// runs if an error occurs
			System.out.println("Error:" + e.getMessage());// prints error message
		}
	}

	private static void server_get(String filename, PrintWriter tout) throws IOException {// Get File
		String indFile = "index.txt";
		String line = null;
		boolean found = false;
		FileReader indexReader = new FileReader(indFile);// reads file
		BufferedReader bufferedindReader = new BufferedReader(indexReader); // buffered reader so it reads a line at a
																			// time
		while ((line = bufferedindReader.readLine()) != null) { // reads through entire list
			if (line.equals(filename))
				found = true;
		}
		if (found == true) {
			FileReader fileReader = new FileReader(filename);// reads file
			BufferedReader bufferedfileReader = new BufferedReader(fileReader); // buffered reader so it reads a line at
																				// a time
			while ((line = bufferedfileReader.readLine()) != null) { // reads through entire list
				tout.println(line);// prints the lines to client
			}
			tout.println("null");
			bufferedfileReader.close();
			found = false;
		} else {
			tout.println("null");
		}
		indexReader.close();
	}

	private static void server_put(String filename, BufferedReader tin) throws IOException {// Add File
		String line = null;
		FileWriter Filewriter = new FileWriter(filename);
		while (((line = tin.readLine()).equals("null")) != true) {
			Filewriter.write(line + "\r\n");// writes input to file
		}
		FileWriter indexwriter = new FileWriter("index.txt", true); // the true will append the new data
		indexwriter.write("\r\n" + filename);// appends the string to the file
		indexwriter.close();
		Filewriter.close();
	}

	private static int server_ls(PrintWriter tout) throws IOException {
		String fileName = "index.txt";
		String line = null;
		int indlength = 0;
		FileReader fileReader = new FileReader(fileName);// reads file
		BufferedReader bufferedReader = new BufferedReader(fileReader); // buffered reader so it reads a line at a time
		while ((line = bufferedReader.readLine()) != null) { // reads through entire list
			tout.println(line);// prints the lines to client
			indlength++;
		}
		tout.println("null");
		bufferedReader.close();
		return indlength;// returns length of list
	}
}