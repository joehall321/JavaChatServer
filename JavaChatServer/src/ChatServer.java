import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


/**
 * Chat server initialises server so that it can accept client connections
 */
public class ChatServer{
    //Stores ServerSocket which clients use to connect
    private ServerSocket in;
    //Vector to store active client sockets
    static Vector<Socket> active_clients = new Vector<>();
    //Stores number of Clients Connected
    static int numof_connections;

    /**
     * ServerListener Constructor, creates a Server which listens for
     * connections made to its port number
     * @param port server port which clients will use to connect
     */
    public ChatServer(int port){
        try{
            in = new ServerSocket(port);
        }catch(IOException e){
            e.printStackTrace();
        }
        run();
    }


    /**
     * Begin looking for clients attempting to connect
     */
    public void run(){
        try {
            ServerCommands serverCommands = new ServerCommands(in);
            //Starts new thread which constantly checks for server commands
            new Thread(serverCommands).start();
            System.out.println("Shut server down by typing 'EXIT'.");
            System.out.println("Server is now Listening");
            //Infinite while loop which waits for clients to connect
            while (true){
                //Stores client port number
                Socket s = in.accept();
                //Stores Client Port once connection is made
                int client_port = s.getPort();
                System.out.println("Server accepted connection on " + in.getLocalPort() + ";" + client_port);
                //Creates new object for client, passes assigned port to ClientHandler object
                ClientHandler client = new ClientHandler(s);
                //Stores socket of Threaded client Object in activeClients
                active_clients.add(s);
                //Runs client object as new Thread
                new Thread(client).start();
                numof_connections++;
                System.out.println("Number of clients connected:    "+ numof_connections);
            }

        //Close Server connection and print error messages if Code above does not run.
        } catch (IOException e) {
            System.out.println("Server Shut down");
        }
        finally {
            try{
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Handles specific client connections
     */
    private static class ClientHandler implements Runnable{
        //Stores client socket
        private final Socket s;

        /**
         * sets socket of client connect to be s
         *
         * @param s The server socket which is assigned to each individual client
         */
        private ClientHandler(Socket s) {
        this.s = s;
        }

        @Override
        /* Sets up BufferedReader so that the server can read sent client messages
           then distribute message to all other clients via client_out PrintWriter */
        public void run() {
                try {
                    //Creates buffered reader called client so we can see input stream
                    BufferedReader client_in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //Creates string to store message
                    String sentMessage = null;
                    //While message is not equal to "quit"
                    while (true) {
                        //Initiates sent message to be next line of input stream
                        sentMessage = client_in.readLine();
                        if("quit".equals(sentMessage)){
                            this.s.close();
                            //Removes the client's socket who is disconnecting from activeClients list
                            active_clients.remove(s);
                            System.out.println("Client Disconnected");
                            numof_connections--;
                            System.out.println("Number of clients connected:    "+ numof_connections);
                            break;
                        }
                        else{
                            /*Loops through list of activeClients and sends writes to each output stream of clients
                            apart from client which sent message */
                            for(int i = 0; i< numof_connections; i++){
                                if (s != active_clients.get(i)) {
                                    //Stores socket of each client while cycling through list of active clients
                                    //when sending messages received by the server
                                    Socket ss = active_clients.get(i);
                                    PrintWriter clientOut = new PrintWriter(ss.getOutputStream(), true);
                                    clientOut.println(sentMessage);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    /**
     * Allows server commands to be executed via server console
     */
    private static class ServerCommands implements Runnable{
        //Stores server socket
        private final ServerSocket in;

        /**
         * Set up input stream from server console
         * @param in The server port
         */
        private ServerCommands(ServerSocket in){
            this.in = in;
        }

        @Override
        //Wait for server commands and then execute appropriately
        public void run() {
            String userInput = null;
            //Creates buffered reader called userIn to read user input from console
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                try {
                    //Waits for user input and sets input equal to userInput
                    userInput = userIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //If EXIT has been entered, close server down cleanly by disconnecting all clients then closing socket
                if("EXIT".equals(userInput)){
                    System.out.println("Server Shutting Down.");
                    //Loops through list of activeClients sending command to disconnect
                    for(int i = 0; i< numof_connections; i++){
                        try{
                            //Stores socket of each client while cycling through list of active clients when sending message
                            Socket ss = active_clients.get(i);
                            PrintWriter client_out = new PrintWriter(ss.getOutputStream(), true);
                            client_out.println("$ClientDisconnect$code8008");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //Close server connection
                    try {
                        in.close();
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }


    /**
     * Set up server and wait for client connections
     *
     * @param args Command line arguments, java ChatClient -ccp [port] -cca [address]:
     * -ccp [port] The optional server port which the client is assigned
     * -cca [address] The optional server address which the client will attempt to connect
     */
    public static void main(String[] args){
        //Default server port
        int serverPort = 14001;
        //Loops through execution commands to check for a server port to be assigned, if not uses default port
        for (int i = 0; i<args.length; i++){
            if(args[i].equals("-csp")){
                try {
                   serverPort = Integer.parseInt(args[i+1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Server bound to: "+ serverPort);
        new ChatServer(serverPort);
    }
}

