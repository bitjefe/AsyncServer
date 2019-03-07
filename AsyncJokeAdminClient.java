/*

1. Jeff Wiand / 3-7-19
2. Java 1.8
3. Compilation Instructions:
    > javac AsyncJokeAdminClient.java

4. Run Instructions
    > java AsyncJokeServer
    > java AsyncJokeClient
    > java AsyncJokeAdminClient

   List of files needed for running the program
    - checklist.html
    - AsyncJokeServer.java
    - AsyncJokeClient.java
    - AsyncJokeAdminClient.java

5. My Notes
    * This is the exact same AdminClient from my JokeServer submission. Works the exactly the same with the Asynchronous additions
 */

import java.io.*;       //Pull in the Java Input - Output libraries for AsyncJokeAdminClient.java use
import java.net.*;      //Pull in the Java networking libraries for AsyncJokeAdminClient.java use

public class AsyncJokeAdminClient {                                                                                      // AsyncJokeAdminClient class definition

    public static void main(String args[]) throws IOException {
        String serverName;                                                                                          // Local AsyncJokeAdminClient definition "serverName" of type String
        if (args.length < 1) serverName = "localhost";                                                              // Sets serverName to localhost if no client input on the initial execution of AsyncJokeAdminClient
        else serverName = args[0];                                                                                  // Sets serverName to the first index of the client input

        System.out.println("Now Communicating with : " + serverName + ", Port: 45000");                             // print statement to the console that tell the host name and port number
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));                                   // launches one new BufferReader object to handle AdminClient input

        try {
            String mode;                                                                                            // local definition of anotherMode to handle enter keyboard events
            String anotherMode;                                                                                     // local definition of anotherMode to handle enter keyboard events
            System.out.println("Press Enter to Toggle Modes");
            do {
                anotherMode = in.readLine();                                                                          // set another mode to empty string when the adminClient hits enter

                if(anotherMode.indexOf("quit") < 0) {
                    mode = toggleMode(serverName);                                                                    // calls our custom toggleMode method to toggle the mode and return the new mode
                    if(mode.equals("JOKE")){                                                                          // if in JokeMode and toggled, tell the AdminClient now in ProverbMode
                        System.out.println("Switched to Proverb Mode");
                    }else if(mode.equals("PROVERB")){                                                                 // if in were in ProverbMode and toggled, tell the AdminClient now in JokeMode
                        System.out.println("Switched to Joke Mode");
                    }
                }
            } while (anotherMode.indexOf("quit") < 0);                                                                // runs as long as the user doesn't type "quit" into the console
            System.out.println ("Cancelled by user request.");

        } catch (IOException x) {x.printStackTrace ();}                                                               // handles any IOExceptions and prints the error trail to the AdminClient
    }

    static String toggleMode(String serverName){                                                                     // custom toggleMode method to switch modes between Joke and Proverb
        Socket sock;                                                                                                 // local definition of sock of type Socket
        PrintStream toAdminWorker;                                                                                        // local definition of toServer of type PrintStream
        BufferedReader fromAdminWorker;                                                                                   // local definition of fromServer of type BufferedReader
        String newMode = "";                                                                                         // local definition of newMode to empty string

        try{
            sock = new Socket(serverName, 45000);                                                              // Declare a new socket object and bind our new communication channel to port 45000

            toAdminWorker = new PrintStream(sock.getOutputStream());                                                     // Launch new PrintStream object and set equal to locally defined toAdminWorker
            fromAdminWorker = new BufferedReader(new InputStreamReader(sock.getInputStream()));                          //Launch new BufferedReader object and set equal to locally defined from AdminWorker

            toAdminWorker.println(newMode);                                                                              // send our empty string as our call to toggle the state
            newMode = fromAdminWorker.readLine();                                                                        // read in the new Mode string that has been processed by AdminWorker

            sock.close();                                                                                           // closes the current connection
        }
        catch(IOException x) {                                                                                      // handles any IOExceptions and prints the error trail to the AdminClient
            System.out.println ("Socket error.");
            x.printStackTrace ();
        }

        return newMode;                                                                                     // return the newMode string to the AsyncJokeAdminClient main function to tell the AdminClient what mode they are switching to
    }

    public static class AdminWorker extends Thread {                                                        // class definition of AdminWorker

        Socket sock;                                                                                        // local AdminWorker definition of sock of type Socket

        public AdminWorker(Socket s) { sock = s;}                                                           // AdminWorker constructor to accept the incoming sockets and set to local Socket definition called "sock"

        public void run() {                                                                                 // method launched with the .start() call in JokeServer class
            PrintStream out = null;                                                                         // sets our output stream to null. PrintStream's can be flushed and don't throw IOExceptions
            BufferedReader in = null;                                                                       // sets our input to null
            try {
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));                      // Launch new BufferedReader object and set equal to locally defined in, not used in this implementation of code but possible in a refactor
                out = new PrintStream(sock.getOutputStream());                                              // Launch new PrintStream object and set equal to locally defined out

                try {                                                                                       // start of try block for error catching
                    if(AsyncJokeServer.JokeMode == true) {                                                       // if the JokeMode state is true:
                        System.out.println("Switched to Proverb Mode");                                     // Print to JokeServer console that we've switched to Proverb Mode
                        AsyncJokeServer.JokeMode = false;                                                        // update JokeMode to false (proverbMode)
                        out.println("JOKE");                                                                // Send a string of "JOKE" back to AsyncJokeAdminClient to update the console of it's state
                    }
                    else{                                                                                   // if the JokeMode state is false:
                        System.out.println("Switched to Joke Mode");                                        // Print to JokeServer console that we've switched to Joke Mode
                        AsyncJokeServer.JokeMode = true;                                                         // update JokeMode to false (proverbMode)
                        out.println("PROVERB");                                                             // Send a string of "JOKE" back to AsyncJokeAdminClient to update the console of it's state
                    }

                } catch (IndexOutOfBoundsException x) {                                                     // handles any IndexOutOfBoundsException and prints the error trail to the JokeServer
                    System.out.println("Server read error");
                    x.printStackTrace();
                }
                sock.close();                                                                               // closes the current connection
            } catch (IOException ioe) {                                                                     // handles any IOExceptions and prints the error to the JokeServer
                System.out.println(ioe);
            }
        }
    }
}




