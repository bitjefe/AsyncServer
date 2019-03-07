/*

1. Jeff Wiand / 3-7-19
2. Java 1.8
3. Compilation Instructions:
    > javac AsyncJokeClient.java

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
    * This is the basic version of AsyncJokeClient. Compile and run the AsyncJokeClient / AsyncJokeServer / AsyncJokeAdminClient (optional) in any order
    * Wait for the server to connect to the server ports and Enter your name
    * Then press enter to receive a joke or proverb
    * A prompt will come up for doing work on the client (adding 2 input numbers) while the joke or proverb is retrieved asynchronously from the server
    * The server connects back with UDP, sending back three pieces of information:  Joke/Proverb, jokeIndex, proverbIndex
    * Wait for the program to prompt you to "Press Enter to receive a joke or proverb", then press enter for another joke or proverb.
    * Repeat this process for as long as you would like.
    * To Switch modes. Hit enter to toggle modes on the AsyncJokeAdminClient window
    *
    *

    ***ONE BUG: If you wait too long after the joke is returned from the server, to enter the sum that is, then the program will handle and not prompted for the next joke
                To reproduce this bug. Hit enter for a Joke. Look on the AsyncJokeServer window and wait for it to go through the progression:
                "sleeping for 40 seconds"
                "Joke or Proverb here"
                "we slept for 40 seconds"

                Then hit submit for the sum of two numbers.
 */


import java.io.*;               //Pull in the Java Input - Output libraries for AsyncJokeClient.java use
import java.net.*;              //Pull in the Java networking libraries for AsyncJokeClient.java use
import java.util.*;             //Pull in the Java utility libraries for AsyncJokeClient.java use


public class AsyncJokeClient {                                                                       // AsyncJokeClient class declaration

    public static void main (String args[]) {                                                       // main function that will execute when AsyncJokeClient is run
        String serverName;                                                                          // Local AsyncJokeClient definition "serverName" of type String
        int jokeIndex=0;                                                                            // Local AsyncJokeClient definition "jokeIndex" of type int, set equal to zero
        int proverbIndex=0;                                                                         // Local AsyncJokeClient definition "proverbIndex" of type int, set equal to zero
        ArrayList<Integer> indexArray = new ArrayList<>();                                          // Local AsyncJokeClient instantiation of indexArray ArrayList

        if (args.length < 1) serverName = "localhost";                                              // Sets serverName to localhost if no client input on the initial execution of AsyncJokeClient
        else serverName = args[0];                                                                  // Sets serverName to the first index of the client input

        System.out.println("Now Communicating with : " + serverName + ", Port: 43000");             // print statement to the console that tell the host name and port number
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));                   // launches one new BufferReader object to handle client input

        List<String> order = new ArrayList<>();
        List<String> jokeRandOrder = new ArrayList<>();                                             // create a random joke order ArrayList and add "A", "B", "C", "D" to it
        jokeRandOrder.add("A");
        jokeRandOrder.add("B");
        jokeRandOrder.add("C");
        jokeRandOrder.add("D");

        Collections.shuffle(jokeRandOrder);                                                         // randomize our jokes arrayList

        StringBuilder jokeOrderString = new StringBuilder(jokeRandOrder.size());                    // build the random order string of jokes to be sent to JokeServer for processing
        for(String s: jokeRandOrder){
            jokeOrderString.append(s);
        }

        order.add(jokeOrderString.toString());

        List<String> proverbRandOrder = new ArrayList<>();                                          // create a random proverb order ArrayList and add "A", "B", "C", "D" to it
        proverbRandOrder.add("A");
        proverbRandOrder.add("B");
        proverbRandOrder.add("C");
        proverbRandOrder.add("D");

        Collections.shuffle(proverbRandOrder);                                                      //randomize our proverbs array

        StringBuilder proverbOrderString = new StringBuilder(proverbRandOrder.size());              // build the random order string of proverbs to be sent to JokeServer for processing
        for(String s: proverbRandOrder){
            proverbOrderString.append(s);
        }

        order.add(proverbOrderString.toString());

        try {                                                                                       // start of error catching with try block
            String userName;                                                                        // local definition of user's name of type String
            String anotherJoke;                                                                     // local definition of anotherJoke of type String

            System.out.print("Enter your name please, (quit) to end: ");                            // Ask the user for their name once.
            System.out.flush();                                                                     // clears out the "out buffer"
            userName = in.readLine();                                                               // store user response as userName type string outside the loop

            System.out.print("\nPress Enter to receive a joke or proverb: \n ");
            System.out.flush();

            String userId = UUID.randomUUID().toString();                                           // generate UUID and cast to String for future refactors. It is only stored on the Server but doesn't effect this version of the code implementation

            do {
                anotherJoke = in.readLine();                                                             // if the client hits enter, it will print another joke in the server
                indexArray.clear();                                                                      // clear out the indexArray ArrayList each loop
                // instantiates a new thread that will run the logic in the AdminAsync class
                if(anotherJoke.indexOf("quit") < 0 && userName.indexOf("quit")<0) {                                                                                     // if the client doesn't initially type quit or type quit in the console in any subsequent iterations, execute the function call

                    System.out.println("Getting a joke or proverb from server...");
                    indexArray = getJokeProverb(userName, userId, order.get(0), order.get(1), jokeIndex, proverbIndex, serverName, in);      // set indexArray equal to return value of getJokeProverb (jokeIndex in first indexArray, proverbIndex in second indexArray)

                    jokeIndex = indexArray.get(0);                                                           // set jokeIndex to the first indexArray of arrayList named indexArray
                    proverbIndex = indexArray.get(1);


                    if(jokeIndex==4){
                        jokeIndex=0;

                        jokeRandOrder.clear();

                        for(int i=0;i<jokeOrderString.length();i++){
                            jokeRandOrder.add(String.valueOf(jokeOrderString.charAt(i)));
                        }
                        Collections.shuffle(jokeRandOrder);
                        System.out.println(jokeRandOrder);          //comment this out

                        StringBuilder newJokeOrderString = new StringBuilder(jokeRandOrder.size());                    // build the random order string of jokes to be sent to JokeServer for processing
                        for(String s: jokeRandOrder){
                            newJokeOrderString.append(s);
                        }

                        order.set(0,newJokeOrderString.toString());
                        System.out.println("new jokeOrder = "+order.get(0));            //comment this out

                    }

                    if(proverbIndex==4){
                        proverbIndex=0;

                        proverbRandOrder.clear();

                        for(int i=0;i<proverbOrderString.length();i++){
                            proverbRandOrder.add(String.valueOf(proverbOrderString.charAt(i)));
                        }
                        Collections.shuffle(proverbRandOrder);
                        System.out.println(proverbRandOrder);                   //comment this out

                        StringBuilder newProverbOrderString = new StringBuilder(proverbRandOrder.size());                    // build the random order string of jokes to be sent to JokeServer for processing
                        for(String s: proverbRandOrder){
                            newProverbOrderString.append(s);
                        }

                        order.set(1,newProverbOrderString.toString());
                        System.out.println("new Proverb order = "+order.get(1));                //comment this out
                    }
                }

                System.out.print("\nPress Enter to receive a joke or proverb: \n ");


            } while (anotherJoke.indexOf("quit") < 0 && userName.indexOf("quit")<0);                        // continue the loop until the user types quit on the initial prompt or in any subsequent joke/proverb iterations
            System.out.println ("Cancelled by user request.");

        } catch (IOException | InterruptedException x) {x.printStackTrace ();}                                                     // handles any IOExceptions and prints the error trail to the client
    }

    static ArrayList<Integer> getJokeProverb(String userName, String userId, String jokeOrderString, String proverbOrderString,Integer jokeIndex, Integer proverbIndex, String serverName, BufferedReader in) throws InterruptedException {             //custom method that returns the appropriate joke or proverb from AsyncJokeServer

        Socket sock;                                                                    // local definition of sock of type Socket
        BufferedReader fromServer;                                                      // local definition of fromServer of type BufferedReader
        PrintStream toServer;                                                           // local definition of toServer of type PrintStream
        String textFromServer;                                                          // local definition of textFromServer of type String

        ArrayList<Integer> index = new ArrayList<>();                                   // instantiate index of type ArrayList to handle the incoming jokeIndex and proverbIndex states
        index.clear();                                                                  // make sure the index is empty at the start of each call

        try{
            sock = new Socket(serverName, 43000);                                                      // Declare a new socket object and bind our new communication channel to port 43000

            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));                  //Launch new BufferedReader object and set equal to locally defined fromServer
            toServer = new PrintStream(sock.getOutputStream());                                             //Launch new PrintStream object and set equal to locally defined toServer

            toServer.println(userId);                                                                       // sends UUID string to JokeServer
            toServer.println(userName+":"+jokeOrderString+":"+proverbOrderString);                          // sends username, jokeOrder, and proverbOrder in one string to JokeServer
            toServer.println(jokeIndex);                                                                    // sends jokeIndex integer to JokeServer
            toServer.println(proverbIndex);                                                                 // sends proverbIndex integer to JokeServer

            toServer.flush();                                                                               // clears out the toServer buffer

            AsyncUDPWorker asyncUDP = new AsyncUDPWorker();                                                 //launch AsyncWorker thread to handle joke or proverb coming in from the AsyncJokeServer at port 49000
            Thread thread = new Thread(asyncUDP);
            thread.start();

            //wait for the joke or proverb. Ask for 2 integers to sum while we wait
            while(asyncUDP.receivedString == null){
                System.out.println("Enter 2 numbers to sum (separated by a spaces): ");
                String twoNumInput = in.readLine();
                String[] twoNumSplit = twoNumInput.split(" ");
                int sum = Integer.parseInt(twoNumSplit[0]) + Integer.parseInt(twoNumSplit[1]);
                System.out.println("Your sum = "+ sum);
            }

            String jokeProverb = asyncUDP.receivedString;                                                          // define the String jokeProverb as the joke or proverb coming from AsyncJokeServer over UDP
            System.out.println(jokeProverb);                                                                       // print the joke or proverb String to the AsyncJokeClient console


            AsyncUDPWorker2 asyncUDPjokeIndex = new AsyncUDPWorker2();                                     //launch AsyncWorker2 thread to handle jokeIndex coming in from the AsyncJokeServer at port 49001
            Thread thread2 = new Thread(asyncUDPjokeIndex);
            thread2.start();

            while(asyncUDPjokeIndex.receivedString == null){                                                // wait for the UDP response for jokeIndex from AsyncJokeServer
                Thread.sleep(100);
            }

            String jokeIndexStringNew = asyncUDPjokeIndex.receivedString;                                   // define the String jokeIndexStringNew as the jokeIndex coming from AsyncJokeServer over UDP
            int jokeIndexNew = Integer.parseInt(jokeIndexStringNew);                                        // parse its integer value

            AsyncUDPWorker3 asyncUDPproverbIndex = new AsyncUDPWorker3();                                   //launch AsyncWorker3 thread to handle jokeIndex coming in from the AsyncJokeServer at port 49002
            Thread thread3 = new Thread(asyncUDPproverbIndex);
            thread3.start();

            while(asyncUDPproverbIndex.receivedString == null){                                         // wait for the UDP response for proverbIndex from AsyncJokeServer
                Thread.sleep(100);
            }

            String proverbIndexStringNew = asyncUDPproverbIndex.receivedString;                         // define the String proverbIndexStringNew as the proverbIndex coming from AsyncJokeServer over UDP
            int proverbIndexNew = Integer.parseInt(proverbIndexStringNew);                              // parse its integer value


            //conditional block to handle when the joke or proverb cycle completes. Adds the joke or proverb index to the arraylist of indices for processing where we are in the randomized order
            if(jokeIndexNew==4){
                System.out.println("Joke Cycle Complete");
                index.add(jokeIndexNew);
            } else{
                index.add(jokeIndexNew);
            }

            if(proverbIndexNew==4){
                System.out.println("Proverb Cycle Complete");
                index.add(proverbIndexNew);
            } else{
                index.add(proverbIndexNew);
            }

            // RESET ALL RECEIVED STRINGS TO NULL HERE.  This allows the next loop through this method to work correctly
            asyncUDP.receivedString = null;
            asyncUDPjokeIndex.receivedString = null;
            asyncUDPproverbIndex.receivedString = null;

            sock.close();                                                                       // closes only the current connection
        }
        catch(IOException x) {                                                                  //handles any IOException then displays the error trail to the client
            System.out.println ("Socket error.");
            x.printStackTrace ();
        }

        return index;                                                                           // return the index arrayList contain our joke and proverb states as indices 0 and 1 respectively
    }
}


class AsyncUDPWorker extends Thread {                                                           // class definition of AsyncUDPWorker to handle UDP communication over port 49000
    byte[] udpBufferReceived = new byte[256];                                                   // definition of variables udpBufferReceived, receivedString, udpSocket, udpPacket
    public static String receivedString = null;
    public static DatagramSocket udpSocket;
    public static DatagramPacket udpPacket;

    public void run() {                                                                         // run() method that executes on a thread.start() call
        try {
            udpSocket = new DatagramSocket(49000);                                        // connect to port 49000
            udpPacket = new DatagramPacket(udpBufferReceived, udpBufferReceived.length);        // create packet that is filled with the joke or proverb from AsyncJokeServer
            udpSocket.receive(udpPacket);                                                       // receive the packet

            receivedString = new String(udpPacket.getData(), 0, udpPacket.getLength());     // redefine receivedString as the joke or proverb from AsyncJokeServer

            udpSocket.close();                                                                  //close the connection

        } catch (SocketException e) {                                                       //handles errors here
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class AsyncUDPWorker2 extends Thread {                                                      // class definition of AsyncUDPWorker2 to handle UDP communication over port 49001
    byte[] udpBufferReceived = new byte[256];                                               // definition of variables udpBufferReceived, receivedString, udpSocket, udpPacket
    public static String receivedString = null;
    public static DatagramSocket udpSocket;
    public static DatagramPacket udpPacket;

    public void run() {                                                             // run() method that executes on a thread.start() call
        try {
            udpSocket = new DatagramSocket(49001);                                      //connect to port 49001
            udpPacket = new DatagramPacket(udpBufferReceived, udpBufferReceived.length);      // create the packet that is filled with the jokeIndex from the AsyncJokeServer
            udpSocket.receive(udpPacket);                                                     // receive the packet

            receivedString = new String(udpPacket.getData(), 0, udpPacket.getLength());   // redefine receivedString as the jokeIndex from AsyncJokeServer

            udpSocket.close();                                                                   // close the connection

        } catch (SocketException e) {                                                   //handles errors here
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class AsyncUDPWorker3 extends Thread {                                                      // class definition of AsyncUDPWorker to handle UDP communication over port 49002
    byte[] udpBufferReceived = new byte[256];                                               // definition of variables udpBufferReceived, receivedString, udpSocket, udpPacket
    public static String receivedString = null;
    public static DatagramSocket udpSocket;
    public static DatagramPacket udpPacket;

    public void run() {                                                                             // run() method that executes on a thread.start() call
        try {
            udpSocket = new DatagramSocket(49002);                                              //connect to port 49002
            udpPacket = new DatagramPacket(udpBufferReceived, udpBufferReceived.length);             // create the packet that is filled with the proverbIndex from the AsyncJokeServer
            udpSocket.receive(udpPacket);                                                           // receive the packet

            receivedString = new String(udpPacket.getData(), 0, udpPacket.getLength());         // redefine receivedString as the proverbIndex from AsyncJokeServer

            udpSocket.close();                      // close the connection

        } catch (SocketException e) {               //handles errors here
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}



