package ReliableClientServerCommunication;

import java.io.*;
import java.net.*;

public class Phone_Server {
	public static void main(String args[]) throws UnknownHostException, IOException {
		
		String fileName = "phonebook.txt";
		String request = null;
		String line = null;
		Boolean exit = false;
		PrintWriter out = null;
		BufferedReader in = null;
		
		try {
			ServerSocket myServerSocket = new ServerSocket(1234);
			; // opens server side socket
			Socket connectedClientSocket = null;// create uninitialized client socket
		
			while (true) { // keep server on
			
				connectedClientSocket = myServerSocket.accept(); // wait for client to connect
				out = new PrintWriter(connectedClientSocket.getOutputStream(), true);// print writer to client
				in = new BufferedReader(new InputStreamReader(connectedClientSocket.getInputStream())); // reads from
																										// client
				while (exit == false) { // loops until exit
				
					out.println("\n"); // creates a two line gap
					menu(out);
					request = in.readLine(); // reads entry
					
					switch (request) {
					case "1":
						addNumber(out, in);
						break;
					case "2": // runs the required function
						delNumber(out, in);
						break;
					case "3":
						chngNumber(out, in);
						break;
					case "4":
						listNames(out, in);
						break;
					case "5":
						exit = true;
						break;
					default:
						out.println("Invalid Input");
						break;
					}
				}
				connectedClientSocket.close(); // closes connection to the client
				exit = false;
			}
		} catch (FileNotFoundException ex) {
			System.out.println(ex);
		}

		catch (IOException ex) {
			System.out.println(ex);
		}
	}

	static void menu(PrintWriter tout) {
		tout.println("Menu"); // simple prints menu
		tout.println("1) Add Number");
		tout.println("2) Delete Number");
		tout.println("3) Change Number");
		tout.println("4) List Names");
		tout.println("5) Exit");
	}

	static void addNumber(PrintWriter tout, BufferedReader tin) throws IOException {
		String fileName = "phonebook.txt";
		tout.println("Which person would you like to add a number for?");
		int listlength = listNames(tout, tin); // lists names and returns length of list
		listlength++; // increments length for following
		tout.println(listlength + ") Add new name"); // adds option to add name to end of list
		int req = Integer.parseInt(tin.readLine()); // reads what the user would like to do
		
		if (req == listlength) { // if user adding new name
			tout.println("Enter Name:"); // requests name
			String newname = tin.readLine();
			tout.println("Enter Number:");
			String newnum = tin.readLine(); // requests number
			FileWriter fw = new FileWriter(fileName, true); // the true will append the new data
			fw.write("\n" + newname + ": " + newnum);// appends the string to the file
			fw.close();
		} else {
			tout.println("Enter Number:"); // asks for new number
			String newnum = tin.readLine();
			File file = new File(fileName);
			File temp = File.createTempFile("temp-file-name", ".tmp"); // temporary file
			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(temp));
			String line;
			int lineCount = 0;
			while ((line = br.readLine()) != null) {
				if (lineCount == req - 1) { // finds line and appends new number to the end of it
					pw.println(line + " " + newnum);
				} else {
					pw.println(line); // otherwise just adds line to file
				}
				lineCount++;
			}
			br.close();
			pw.close();
			file.delete(); // replaces file with temp file
			temp.renameTo(file);
		}
	}

	static void delNumber(PrintWriter tout, BufferedReader tin) throws IOException {
		
		String fileName = "phonebook.txt";
		tout.println("Which person would you like to delete a number for?");
		listNames(tout, tin); // lists all names
		
		int req = Integer.parseInt(tin.readLine()); // retrieves int input for name
		int phoneind = 0;
		String newline;
		
		tout.println("Which Number");
		listNumbers(tout, tin, req); // prints all that users numbers
		int num = Integer.parseInt(tin.readLine());// retrives int input for number
		
		File file = new File(fileName); // opens the file
		File temp = File.createTempFile("temp-file-name", ".tmp"); // opens temp file
		BufferedReader br = new BufferedReader(new FileReader(file)); // reads file line at a time
		PrintWriter pw = new PrintWriter(new FileWriter(temp)); // prints to temp file
		String line; // current line
		int lineCount = 0;
		
		while ((line = br.readLine()) != null) { // steps through file
			if (lineCount == req - 1) { // finds requested name
				newline = "" + line.charAt(0); // initializes the replacement line
				for (int j = 1; j < line.length(); j++) { // steps through line
					newline += line.charAt(j); // adding each character to newline as it does
					if ((line.charAt(j) == ' ') && Character.isDigit(line.charAt(j + 1))) {
						//finds a space followed by a digit
						phoneind++; // increments how many numbers found
						if (phoneind == num) { // if number is the number requested for del
							j++;
							while ((j < line.length()) && (line.charAt(j) != ' ')) { // steps through number not adding
								j++; // it to replacement file
							}
						}
					}
				}
				pw.println(newline); // prints replacement line to file
			} else {
				pw.println(line); // prints line to file
			}
			lineCount++;
		}
		br.close();
		pw.close(); // closses reader and writer
		file.delete();
		temp.renameTo(file); // deletes old file and renames replacement file
	}

	static void chngNumber(PrintWriter tout, BufferedReader tin) throws IOException {
		String fileName = "phonebook.txt";
		tout.println("Which person would you like to edit a number for?");// all same as delete accept where specified
		listNames(tout, tin);
		
		int req = Integer.parseInt(tin.readLine());
		int phoneind = 0;
		String newline;
		String newnum;
		tout.println("Which Number would you like to edit?");
		
		listNumbers(tout, tin, req);
		int num = Integer.parseInt(tin.readLine());
		tout.println("Enter correct number:");
		newnum = tin.readLine(); // enter new number
		
		File file = new File(fileName);
		File temp = File.createTempFile("temp-file-name", ".tmp");
		BufferedReader br = new BufferedReader(new FileReader(file));
		PrintWriter pw = new PrintWriter(new FileWriter(temp));
		String line;
		int lineCount = 0;
		
		while ((line = br.readLine()) != null) {
			if (lineCount == req - 1) {
				newline = "" + line.charAt(0);
				for (int j = 1; j < line.length(); j++) {
					newline += line.charAt(j);
					if ((line.charAt(j) == ' ') && Character.isDigit(line.charAt(j + 1))) {
						phoneind++;
						if (phoneind == num) {
							j++;
							newline += newnum; // adds new new number then ignores copying old num
							while ((j < line.length()) && (line.charAt(j) != ' ')) {
								j++;
							}
						}
					}
				}
				pw.println(newline);
			} else {
				pw.println(line);
			}
			lineCount++;
		}
		br.close();
		pw.close();
		file.delete();
		temp.renameTo(file);
	}

	static int listNumbers(PrintWriter tout, BufferedReader tin, int lineNum) throws IOException {
		String fileName = "phonebook.txt";
		String line = null;
		int tmp = 1;
		int phoneind = 1;
		FileReader fileReader = new FileReader(fileName); // reads file
		BufferedReader bufferedReader = new BufferedReader(fileReader); // buffered reader so it reads a line at a time
		
		while ((line = bufferedReader.readLine()) != null) { // reads through entire list
			if (tmp == lineNum) {
				String newtext;
				newtext = "" + line.charAt(0);
				for (int j = 1; j < line.length(); j++) {
					newtext += line.charAt(j);
					if ((line.charAt(j) == ' ') && Character.isDigit(line.charAt(j + 1))) {
						newtext += phoneind + ")";
						phoneind++;
					}
				}
				tout.println(newtext); // prints the lines to client
			}
			tmp++;
		}
		bufferedReader.close();
		tmp--;
		phoneind--;
		return phoneind; // returns length of list
	}

	static int listNames(PrintWriter tout, BufferedReader tin) throws IOException {
		String fileName = "phonebook.txt";
		String line = null;
		int tmp = 1;
		FileReader fileReader = new FileReader(fileName); //reads file
		BufferedReader bufferedReader = new BufferedReader(fileReader); //buffered reader so it reads a line at a time
		while((line = bufferedReader.readLine()) != null) { //reads through entire list
			tout.println(tmp + ") " + line); //prints the lines to client
			tmp++;
		}
		bufferedReader.close();
		tmp--;
		return tmp; //returns length of list
	}
}