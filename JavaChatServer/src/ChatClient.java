import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ChatClient enables client to server and server to client (two-way) interaction
 *
 * @author Joseph Hall
 * @see Socket
 */
public class ChatClient {
    //Opens and stores server socket so client can connect to server
    private Socket server;
    private static boolean is_logged_in;

    /**
     * Client attempts to connect to server
     *
     * @param address The default address of the server
     * @param port The default server port
     * @throws IOException Terminates execution and prints error if server host in undetected (unknown)
     */
    public ChatClient(String address, int port) {
        try {
            server = new Socket(address, port);
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Method to close client connection
     */
    public void CloseConnection() throws IOException {
        server.close();
        System.out.println("Connection Closed");
    }


    /**
     * Method starts chat interaction with server
     */
    public void startChat(){
        is_logged_in = true;
        try {
            //Creates buffered reader called user_in to read user input from console
            BufferedReader user_in = new BufferedReader(new InputStreamReader(System.in));
            //Creates print writer called serverOut to write to server via output stream
            PrintWriter client_out = new PrintWriter(server.getOutputStream(), true);
            //Creates new instance of CheckForIncoming class and passes server information into it
            CheckForIncoming check_for_incoming = new CheckForIncoming(server);
            /*Creates Thread of check_for_incoming object and starts it so that client
            can constantly check for incoming messages */
            new Thread(check_for_incoming).start();
            System.out.println("Connected to Server, you can now start chatting with other clients also connected.");
            System.out.println("Commands:");
            System.out.println("quit - disconnects from server");
            String user_input = null;
            /* While loop checks that user message does not equal "quit" and writes it to server.
            If user message equals "quit" client closes connection with server
             */
            client_out.println("CLIENT HAS ENTERED THE CHAT");
            while (!"quit".equals(user_input)&& is_logged_in) {
                user_input = user_in.readLine();
                client_out.println(user_input);

            }
            is_logged_in = false;
            CloseConnection();
            //server.close();
            //System.out.println("Connection Closed");
        }catch (IOException e){
            e.printStackTrace();
        }
        //If connection fails, close connection.
        finally {
            try{
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Class implements Runnable which checks for incoming messages from server simultaneously
     * while start chat sends user input to server
     */
    private static class CheckForIncoming implements Runnable{
        //Stores socket of server
        private final Socket serverSocket;

        /**
         *Sets up server connection for client thread by passing serverSocket to thread
         *
         * @param serverSocket serverSocket used to set up client and server interaction
         */
        private CheckForIncoming(Socket serverSocket){
            //ServerSocket to equal server Socket
            this.serverSocket = serverSocket;
        }

        @Override
        //Runs Check for incoming. Client now receives messages from server.
        public void run() {
            String serverIncoming = null;
            while (true){
                try {
                    //Creates Buffered reader to read input in from server stream
                    BufferedReader ServerIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                    //Creates print writer called serverOut to write to server via output stream
                    PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
                    //Reads input from buffered reader
                    serverIncoming = ServerIn.readLine();
                    /*If input equals disconnect code, then send "quit" to server and disconnect.
                    Else print input(message) from server */
                    if (serverIncoming.equals("$ClientDisconnect$code8008")){
                        System.out.println("Server shutting down.");
                        serverOut.println("quit");
                        is_logged_in = false;
                        System.out.println("Disconnected by server.");
                        serverSocket.close();
                        break;
                    }
                    else{
                        System.out.println(serverIncoming);
                    }
                } catch (IOException e) {
                    try {
                        serverSocket.close();
                        break;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            System.exit(0);
        }
    }

    /**
     * Setup client with server address and port so it is ready for two-way interaction
     *
     * @param args Command line arguments, java ChatClient -ccp [port] -cca [address]:
     *             -ccp [port] The optional server port which the client is assigned
     *             -cca [address] The optional server address which the client will attempt to connect
     */
    public static void main(String[] args) {
        //Default server address
        String address = "localhost";
        //Default server port
        int serverPort = 14001;
        //Loops through execution commands to check for a server port or/and port to be assigned, if not uses default port
        for (int i = 0; i<args.length; i++){
            if(args[i].equals("-ccp")){
                try {
                    serverPort = Integer.parseInt(args[i+1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(args[i].equals("-cca")){
                try {
                    address = args[i+1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Creates new instance of Client class and runs startChat() function
        new ChatClient(address, serverPort).startChat();
    }
}
