/*

1. Jeff Wiand / 1-27-19
2. Java 1.8
3. Compilation Instructions:
    > javac AsyncJokeServer.java

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

    *Jokes taken from http://pun.me/pages/dad-jokes.php
    *Proverbs taken from https://web.sonoma.edu/users/d/daniels/chinaproverbs.html
    *
    * Slight bug, inefficiency in re=randomization of the joke/proverb order occurs when the "complete message" is sent
    * the arrayList re-randomizes twice in this instance. This is an unnecessary server calculation but it still functions correctly.
    * This would be low-hanging fruit to optimize the code in a refactor
 */

import java.io.*;       //Pull in the Java Input - Output libraries for AsyncJokeServer.java use
import java.net.*;      //Pull in the Java networking libraries for AsyncJokeServer.java use
import java.util.*;     //Pull in the Java utility libraries for AsyncJokeServer.java use


class Worker extends Thread {                               // Class declaration for Worker which will be a subclass of Thread class
    Socket sock;                                            // local Worker definition for sock of type Socket

    Worker(Socket s) {
        sock = s;                               // constructor to accept the incoming sockets and set to local Socket definition called "sock"
    }

    public void run() {                         // method launched with the .start() call in AsyncJokeServer class

        String clientNameAndOrderString;                                                        // local definition for clientNameAndOrderString of type String
        String jokeIndexString;                                                                 // local definition for jokeIndexString of type String
        String proverbIndexString;                                                              // local definition for proverbIndex of type String
        String jokeOrderString;                                                                 // local definition for jokeOrderString of type String
        String proverbOrderString;                                                              // local definition for proverbOrderString of type String

        int jokeIndex=0;                                                                         // local definition for jokeIndex of type int, initialized to zero
        int proverbIndex = 0;                                                                    // local definition for proverbIndex of type int, initialized to zero
        PrintStream out = null;                                                                  // sets our output stream to null. PrintStream's can be flushed and don't throw IOExceptions
        BufferedReader in = null;                                                                // sets our input to null
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));                      // launched new objects to obtain our input
            out = new PrintStream(sock.getOutputStream());                                              // launched new object to print our output

            try {                                                                                       // start of error checking with first try block
                String userName;                                                                        // local definition for userName (What the client enters for their name) of type String
                String userId;                                                                          // local definition for userID (UUID) of type String

                userId = in.readLine();                                                                 // read in the userId (UUID) from the AsyncJokeClient
                clientNameAndOrderString = in.readLine();                                               // read in the userName, Joke Order, Proverb Ordered from the AsyncJokeClient

                String[] clientNameAndOrderArray = clientNameAndOrderString.split(":");           // split above string on colons and add to clientNameAndOrderArray

                userName = clientNameAndOrderArray[0];                                                  // assign userName to first index of clientNameAndOrderArray
                jokeOrderString = clientNameAndOrderArray[1];                                           // assign the string representing the joke order to the second index of clientNameAndOrderArray
                proverbOrderString =  clientNameAndOrderArray[2];                                       // assign the string representing the proverb order to the third index of clientNameAndOrderArray

                jokeIndexString = in.readLine();                                                        // read in the string representing the joke index from the AsyncJokeClient
                jokeIndex = Integer.parseInt(jokeIndexString);                                          // parse jokeIndexString into an integer, jokeIndex, for use in the server processing code "getJokeProverb"

                proverbIndexString = in.readLine();                                                     // read in the string representing the joke index from the AsyncJokeClient
                proverbIndex = Integer.parseInt(proverbIndexString);                                    // parse proverbIndexString into an integer, proverbIndex, for use in the server processing code "getJokeProverb"

                // break connection to client here. Do we also do a sock.close ???
                in.close();
                out.close();

                System.out.println("sleeping for 40 seconds");
                //sleep for 40 seconds = 40000 milliseconds (using 4000 = 4 secs for testing)
                Thread.sleep(4000);

                ConnectUDP();


                System.out.println("we slept for 40 seconds");


                //getJokeProverb(userName, userId, jokeOrderString, proverbOrderString, jokeIndex, proverbIndex, out);         // call getJokeProverb custom method to return the correct joke or proverb and state back to the AsyncJokeClient

            } catch (IndexOutOfBoundsException | InterruptedException x) {                                                     // if there's an IndexOutOfBoundsException...do the following below:
                System.out.println("Server read error");                                                // handles the IndexOutOfBoundsException's and displays the error trail to the client
                x.printStackTrace();
            }
            sock.close();                                                                               // closes only the current connection
        } catch (IOException ioe) {                                                                     // if there's an IOException...do the following below:
            System.out.println(ioe);                                                                    // handles the IOException's and displays the error to the client
        }
    }

    private void ConnectUDP() throws IOException {

        DatagramSocket newupdSocket = new DatagramSocket();

        byte[] udpBuffer = new byte[256];
        //DatagramPacket udpPacket = new DatagramPacket(udpBuffer, udpBuffer.length);
        //updSocket.receive(udpPacket);

        String response = "JOKEA";
        udpBuffer = response.getBytes();
        DatagramPacket udpPacket = new DatagramPacket(udpBuffer, udpBuffer.length, InetAddress.getByName("localhost"), 49000);
        newupdSocket.send(udpPacket);

        System.out.println("sent");

        newupdSocket.close();
    }

    static void getJokeProverb(String userName, String userId, String jokeOrderString, String proverbOrderString, Integer jokeIndex, Integer proverbIndex, PrintStream out) {          //custom method to return joke or proverb to the client

        // initializes all the ArrayLists needed to process the incoming joke/proverb order and states
        List<String> userIdArray = new ArrayList<>();
        List<String> jokeOrderList = new ArrayList<>();
        List<String> proverbOrderList = new ArrayList<>();

        // parse the random order of jokes sent from the AsyncJokeClient and set equal to joke number of type String (**Refactor opportunity: create own function to handle this parsing /adding)
        String joke1 = String.valueOf(jokeOrderString.charAt(0));
        String joke2 = String.valueOf(jokeOrderString.charAt(1));
        String joke3 = String.valueOf(jokeOrderString.charAt(2));
        String joke4 = String.valueOf(jokeOrderString.charAt(3));

        // build our jokeOrder ArrayList
        jokeOrderList.add(joke1);
        jokeOrderList.add(joke2);
        jokeOrderList.add(joke3);
        jokeOrderList.add(joke4);

        // parse the random order of proverbs sent from the AsyncJokeClient and set equal to proverb number of type String (**Refactor opportunity: create own function to handle this parsing /adding)
        String proverb1 = String.valueOf(proverbOrderString.charAt(0));
        String proverb2 = String.valueOf(proverbOrderString.charAt(1));
        String proverb3 = String.valueOf(proverbOrderString.charAt(2));
        String proverb4 = String.valueOf(proverbOrderString.charAt(3));

        // build our proverbOrder ArrayList
        proverbOrderList.add(proverb1);
        proverbOrderList.add(proverb2);
        proverbOrderList.add(proverb3);
        proverbOrderList.add(proverb4);


        // build our static array of jokes including the userName template requirement
        String [][] jokesArr = {

                {"A", "JA " + userName + ": 5/4 of people admit that they’re bad with fractions."},
                {"B", "JB " + userName + ": Why did the coffee file a police report? It got mugged"},
                {"C", "JC " + userName + ": What do you call an elephant that doesn't matter? An irrelephant"},
                {"D", "JD " + userName + ": Why did the scarecrow win an award? Because he was outstanding in his field."}
        };

        // build our static array of proverbs including the userName template requirement
        String [][] proverbsArr = {

                {"A", "PA " + userName + ": Good is the Enemy of Great"},
                {"B", "PB " + userName + ": Wonder is the beginning of wisdom. "},
                {"C", "PC " + userName + ": To have principles first have courage"},
                {"D", "PD " + userName + ": Determination tempers the sword of your character."}
        };

        if(AsyncJokeServer.JokeMode){                                                    // Start of Joke Mode = true conditional process
            try {                                                                   // start of error checking with first try block
                if(userIdArray.contains(userId))                                    // checks to see if the UUID has been added to the serverList. This is the only information stored on state (for future refactoring use only, doesn't effect the code)
                    System.out.println("user already exists");
                else
                    userIdArray.add(userId);                                        // UUID is added to the userIdArray (however the state is not recorded in the list with it since its only an arrayList. Refactor to array?

                if (jokeIndex == 4) {                                               // if all the jokes have been sent the client send the client a message saying "Joke Cycle Complete" and reset the index to the first joke
                    System.out.println(jokeOrderList);
                    Collections.shuffle(jokeOrderList);

                } else {                                                            // if the jokeIndex is between 0-3, do the following:
                    if(jokeIndex ==0) {                                             // if the jokeIndex equals 0:
                        if(jokeOrderList.get(0).equals("A")){                       // This set of if-else statements looks to match the the first random joke in jokeOrderList to "A","B","C", or "D" and send the corresponding joke back to the AsyncJokeClient
                            out.println(jokesArr[0][1]);
                            System.out.println((jokesArr[0][1]));
                        } else if(jokeOrderList.get(0).equals("B")){
                            out.println(jokesArr[1][1]);
                            System.out.println((jokesArr[1][1]));
                        } else if(jokeOrderList.get(0).equals("C")) {
                            out.println(jokesArr[2][1]);
                            System.out.println((jokesArr[2][1]));
                        } else if(jokeOrderList.get(0).equals("D")) {
                            out.println(jokesArr[3][1]);
                            System.out.println((jokesArr[3][1]));
                        }
                    }

                    else if(jokeIndex ==1) {                                        // if the jokeIndex equals 1:
                        if(jokeOrderList.get(1).equals("A")){                       // This set of if-else statements looks to match the the second random joke in jokeOrderList to "A","B","C", or "D" and send the corresponding joke back to the AsyncJokeClient
                            out.println(jokesArr[0][1]);
                            System.out.println((jokesArr[0][1]));
                        } else if(jokeOrderList.get(1).equals("B")){
                            out.println(jokesArr[1][1]);
                            System.out.println((jokesArr[1][1]));
                        } else if(jokeOrderList.get(1).equals("C")) {
                            out.println(jokesArr[2][1]);
                            System.out.println((jokesArr[2][1]));
                        } else if(jokeOrderList.get(1).equals("D")) {
                            out.println(jokesArr[3][1]);
                            System.out.println((jokesArr[3][1]));
                        }
                    }

                    else if(jokeIndex ==2) {                                        // if the jokeIndex equals 2
                        if(jokeOrderList.get(2).equals("A")){                       // This set of if-else statements looks to match the the third random joke in jokeOrderList to "A","B","C", or "D" and send the corresponding joke back to the AsyncJokeClient
                            out.println(jokesArr[0][1]);
                            System.out.println((jokesArr[0][1]));
                        } else if(jokeOrderList.get(2).equals("B")){
                            out.println(jokesArr[1][1]);
                            System.out.println((jokesArr[1][1]));
                        } else if(jokeOrderList.get(2).equals("C")) {
                            out.println(jokesArr[2][1]);
                            System.out.println((jokesArr[2][1]));
                        } else if(jokeOrderList.get(2).equals("D")) {
                            out.println(jokesArr[3][1]);
                            System.out.println((jokesArr[3][1]));
                        }
                    }
                    else if(jokeIndex ==3) {                                        // if the jokeIndex equals 3
                        if(jokeOrderList.get(3).equals("A")){                       // This set of if-else statements looks to match the the fourth random joke in jokeOrderList to "A","B","C", or "D" and send the corresponding joke back to the AsyncJokeClient
                            out.println(jokesArr[0][1]);
                            System.out.println((jokesArr[0][1]));
                        } else if(jokeOrderList.get(3).equals("B")){
                            out.println(jokesArr[1][1]);
                            System.out.println((jokesArr[1][1]));
                        } else if(jokeOrderList.get(3).equals("C")) {
                            out.println(jokesArr[2][1]);
                            System.out.println((jokesArr[2][1]));
                        } else if(jokeOrderList.get(3).equals("D")) {
                            out.println(jokesArr[3][1]);
                            System.out.println((jokesArr[3][1]));
                        }
                    }

                    jokeIndex++;                                                    // increment jokeIndex by 1
                }
            } catch (IndexOutOfBoundsException ex) {                                // if there's an IndexOutOfBoundsException...do the following below:
                out.println("Failed in attempt to look up " + userId);              // handles the IndexOutOfBoundsException's and displays the error to the client
            }
        }


        else {                                                                      // If JokeMode is false (Proverb Mode is true), start this conditional processing
            try {                                                                   // start of error checking with first try block

                if(userIdArray.contains(userId))                                    // checks to see if the UUID has been added to the serverList. This is the only information stored on state (for future refactoring use only, doesn't effect the code)
                    System.out.println("user already exists");
                else
                    userIdArray.add(userId);                                        // UUID is added to the userIdArray (however the state is not recorded in the list with it since its only an arrayList. Refactor to array?

                if (proverbIndex == 4) {                                            // if all the proverbs have been sent the client send the client a message saying "Proverb Cycle Complete" and reset the index to the first proverb
                    System.out.println(proverbOrderList);
                    Collections.shuffle(proverbOrderList);

                } else {                                                            // if the proverbIndex is between 0-3, do the following:
                    if(proverbIndex ==0) {                                          // if the proverbIndex equals 0:
                        if(proverbOrderList.get(0).equals("A")){                    // This set of if-else statements looks to match the the first random proverb in proverbOrderList to "A","B","C", or "D" and send the corresponding proverb back to the AsyncJokeClient
                            out.println(proverbsArr[0][1]);
                            System.out.println((proverbsArr[0][1]));
                        } else if(proverbOrderList.get(0).equals("B")){
                            out.println(proverbsArr[1][1]);
                            System.out.println((proverbsArr[1][1]));
                        } else if(proverbOrderList.get(0).equals("C")) {
                            out.println(proverbsArr[2][1]);
                            System.out.println((proverbsArr[2][1]));
                        } else if(proverbOrderList.get(0).equals("D")) {
                            out.println(proverbsArr[3][1]);
                            System.out.println((proverbsArr[3][1]));
                        }
                    }

                    else if(proverbIndex ==1) {                                     // if the proverbIndex equals 1:
                        if(proverbOrderList.get(1).equals("A")){                    // This set of if-else statements looks to match the the second random proverb in proverbOrderList to "A","B","C", or "D" and send the corresponding proverb back to the AsyncJokeClient
                            out.println(proverbsArr[0][1]);
                            System.out.println((proverbsArr[0][1]));
                        } else if(proverbOrderList.get(1).equals("B")){
                            out.println(proverbsArr[1][1]);
                            System.out.println((proverbsArr[1][1]));
                        } else if(proverbOrderList.get(1).equals("C")) {
                            out.println(proverbsArr[2][1]);
                            System.out.println((proverbsArr[2][1]));
                        } else if(proverbOrderList.get(1).equals("D")) {
                            out.println(proverbsArr[3][1]);
                            System.out.println((proverbsArr[3][1]));
                        }
                    }

                    else if(proverbIndex ==2) {                                     // if the proverbIndex equals 2:
                        if(proverbOrderList.get(2).equals("A")){                    // This set of if-else statements looks to match the the third random proverb in proverbOrderList to "A","B","C", or "D" and send the corresponding proverb back to the AsyncJokeClient
                            out.println(proverbsArr[0][1]);
                            System.out.println((proverbsArr[0][1]));
                        } else if(proverbOrderList.get(2).equals("B")){
                            out.println(proverbsArr[1][1]);
                            System.out.println((proverbsArr[1][1]));
                        } else if(proverbOrderList.get(2).equals("C")) {
                            out.println(proverbsArr[2][1]);
                            System.out.println((proverbsArr[2][1]));
                        } else if(proverbOrderList.get(2).equals("D")) {
                            out.println(proverbsArr[3][1]);
                            System.out.println((proverbsArr[3][1]));
                        }
                    }
                    else if(proverbIndex ==3) {                                     // if the proverbIndex equals 3:
                        if(proverbOrderList.get(3).equals("A")){                    // This set of if-else statements looks to match the the fourth random proverb in proverbOrderList to "A","B","C", or "D" and send the corresponding proverb back to the AsyncJokeClient
                            out.println(proverbsArr[0][1]);
                            System.out.println((proverbsArr[0][1]));
                        } else if(proverbOrderList.get(3).equals("B")){
                            out.println(proverbsArr[1][1]);
                            System.out.println((proverbsArr[1][1]));
                        } else if(proverbOrderList.get(3).equals("C")) {
                            out.println(proverbsArr[2][1]);
                            System.out.println((proverbsArr[2][1]));
                        } else if(proverbOrderList.get(3).equals("D")) {
                            out.println(proverbsArr[3][1]);
                            System.out.println((proverbsArr[3][1]));
                        }
                    }

                    proverbIndex++;                                                 // increment the proverbIndex by 1
                }
            } catch (IndexOutOfBoundsException ex) {                                // if there's an IndexOutOfBoundsException...do the following below:
                out.println("Failed in attempt to look up " + userId);              // handles the IndexOutOfBoundsException's and displays the error to the client
            }
        }

        out.println(jokeIndex);                                                     // send the jokeIndex integer back to the AsyncJokeClient to record it's current joke state.
        out.println(proverbIndex);                                                  // send the proverbIndex integer back to the AsyncJokeClient to record it's current proverb state
                                                                                    // Neither of these indexes (state of the client) are stored on the server. Simply received, processed, and sent back to the AsyncJokeClient
    }
}


public class AsyncJokeServer {

    static boolean JokeMode = true;                                                 // sets a boolean called JokeMode to true initially.  It will be used to toggle Joke and Proverb mode
    //static boolean shutdown = false;

    public static void main(String a[]) throws IOException {                        // AsyncJokeServer main
        int q_len = 6;                                                              // the amount of requests to hold in line before not accepting more requests, set to 6
        int port = 43000;                                                           // Use port=43000 since it's not listed in Apple Support but high enough to avoid issues.
        Socket sock;                                                                // Local AsyncJokeServer definition "sock" of type Socket

        AdminAsync AA = new AdminAsync();                                           // instantiates a new thread that will run the logic in the AdminAsync class
        Thread thread = new Thread(AA);
        thread.start();                                                             // launch the run method in AdminAsync class, which will listen at port 45000

        ServerSocket servsock = new ServerSocket(port, q_len);                      // Local AsyncJokeServer object declaration "servsock" as type ServerSocket that will wait for requests at port 43000 with possible 6 incoming connections

        System.out.println("Joke Server starting up, listening at port 43000. \n");
        while (true) {
            sock = servsock.accept();                                               // continuously listening to set incoming connections to feed into our worker object
            new Worker(sock).start();                                               // launches a new worker object with the incoming connection
        }
    }
}

class AdminAsync implements Runnable {                                              // AdminAsync class that runs the thread defined above in AsyncJokeServer
    public static boolean adminSwitch = true;                                       // sets a boolean called adminControlSwitch equal to true

    public void run() {                                                             // Running the Admin listen loop
        int q_len = 6;                                                              // the amount of requests to hold in line before not accepting more requests, set to 6
        int port = 45000;                                                           // Listen at port 45000 for asynchronous Joke and Proverb switching calls from AsyncJokeAdminClient
        Socket sock;

        try {
            ServerSocket servsock = new ServerSocket(port, q_len);                          // Local AdminAsync object declaration "servsock" as type ServerSocket that will wait for requests at port 43000 with possible 6 incoming connections
            System.out.println("Mode Server starting up, listening at port 45000. \n");
            while (adminSwitch) {
                sock = servsock.accept();                                                   // continuously listening to set incoming connections to feed into our AdminWorker object
                new AsyncJokeAdminClient.AdminWorker(sock).start();                         // while waiting for the AsyncJokeAdminClient connection, launch the AdminWorker class for processes asynch. calls
            }
        } catch (IOException ioe) {                                                        // if there's an IOException...do the following below:
            System.out.println(ioe);                                                       // handles the IOException's and displays the error to the AdminClient
        }
    }
}
















