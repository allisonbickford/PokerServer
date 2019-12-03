package server;

import java.io.*;
import java.net.*;
import java.util.*;

import java.util.List;
import java.util.Map.Entry;
import java.util.regex.*;

class CentralServer {
    private static Map<String, String> word = new LinkedHashMap<>(); // <"addr:port", username>
    private static Boolean gameRunning = false;
    private static String gameHost = "";

    public static void main(String argv[]) throws Exception {
        int port = 1200;
        ServerSocket welcomeSocket = new ServerSocket(port, 0, InetAddress.getLoopbackAddress());
        System.out.println("Central Server running at " + welcomeSocket.getInetAddress() + ":" + port);
        while(true) {
            Socket connectionSocket = null;
            try {
                connectionSocket = welcomeSocket.accept();
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                System.out.println("Victim at: " + connectionSocket.getPort());
                new Thread(new SubServerHandler(connectionSocket, outToClient, inFromClient)).start();
            } catch(Exception e) {
                connectionSocket.close();
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends message to all connected servers
     * 
     * @param message to send to servers
     */
    public static void broadcast(String message) {
        for (String value: word.keySet()) {
            String[] serverInfo = value.split(":");
            try {
                Socket tmpSocket = new Socket(serverInfo[0], Integer.parseInt(serverInfo[1]));
                DataOutputStream dos = new DataOutputStream(tmpSocket.getOutputStream());
                dos.writeBytes(message + "\n");
                dos.flush();
                dos.close();
                tmpSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * Adds player to hashmap
     * 
     * @param user name of user/player
     * @param host addr:port of their server
     */
    public static void createword(String user, String host) {
        word.put(host, user);
    }


    /**
     * Creates a list of player usernames
     * 
     * @return all player usernames
     */
    public static ArrayList<Entry<String, String>> getPlayers() {
        ArrayList<Entry<String, String>> players = new ArrayList();
        for (Entry<String, String> entry: word.entrySet()) {
            players.add(Map.entry(entry.getKey(), entry.getValue()));
        }
        return players;
    }

    /**
     * Starts game by setting gameRunning variable to true
     */
    public static void startGame(String host) {
        gameHost = host;
        gameRunning = true;
    }


    /**
     * Returns the amount of players that are currently connected
     * 
     * @return the amount of players
     */
    public static int getNumberOfPlayers() {
        return word.size();
    }


    /**
     * Returns the host of the game with the format host:port
     * @return host of the game
     */
    public static String getGameHost() {
        return gameHost;
    }

    /** 
     * Stops the current game
     */
    public static void endGame() {
        gameRunning = false;
        gameHost = "";
    }
    

    /**
     * Removes a user/player from the list of players
     * @param addr their server host:port combination string
     */
    public static void deregister(String addr){
        word.entrySet().forEach(entry -> {
            if (entry.getKey().contains(addr)) {
                word.remove(entry.getKey());
            }
        });
    }
}

class SubServerHandler implements Runnable {

    private Socket socket;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    public SubServerHandler(Socket socket, DataOutputStream out, BufferedReader in) {
        this.socket = socket;
        this.outToClient = out;
        this.inFromClient = in;
    }

    //runs the central server
    @Override
    public void run() {
        String fromClient = "";
        String clientCommand;
        byte[] data;
        String[] commands;
        int port = this.socket.getPort();
        String username="";
        int phase = 0;

        while (true) {
            try {
                try {
                    fromClient = inFromClient.readLine();
                } catch(IOException io) {
                    System.out.println("client disconnected");
                    break;
                }

                System.out.println("Received command: " + fromClient);
                commands = fromClient.split(" ");
                clientCommand = (commands[0]);

                //dereg users
                if (clientCommand.equals("close")) {
                    System.out.println("Connection ended with victim " + this.socket.getPort());
                    CentralServer.deregister(commands[1]);
                    outToClient.close();
                    this.socket.close();
                    break;
                } else if (clientCommand.startsWith("start")) {
                    CentralServer.startGame(commands[1]);
                    CentralServer.broadcast(this.socket.getPort() + " STARTING! Host: " + commands[1]);
                } else if (clientCommand.startsWith("quit")) {
                    if (commands[1] == CentralServer.getGameHost()) {
                        CentralServer.endGame();
                        CentralServer.broadcast("GAMEOVER");
                    }
                } else if (clientCommand.startsWith("newuser:")) {
                    username = commands[1];
                    CentralServer.createword(username, commands[2]);
                    System.out.println("Registered new player " + username);
                    Thread.sleep(250); // wait for server to open
                    String playerMessage = String.format("%d players:", this.socket.getPort());
                    for (Entry player : CentralServer.getPlayers()) {
                        playerMessage += String.format(" %s\0%s", player.getKey(), player.getValue());
                    }
                    CentralServer.broadcast(playerMessage);
                }else if(clientCommand.startsWith("endPhase")){
                    //TODO: update cards on table
                    System.out.println("The phase has ended");
                    String message = this.socket.getPort() + " endPhase ";
                    CentralServer.broadcast(message);
                    phase++;

                }else if(clientCommand.startsWith("endRound")){
                    //TODO: Calculate the winner and their earnings, reset board
                    String winner = "";
                    float earnings = 0;

                    System.out.println("The round has ended. Winner: " + winner + " won $" + earnings);
                    String message = this.socket.getPort() + " endRound " + winner + " " + earnings;
                    CentralServer.broadcast(message);
                    phase = 0;

                }else if(clientCommand.startsWith("bet:")){
                    //1 = user 2 == bet amount
                    System.out.println("New bet from: " + commands[1] +" for $"+ commands[2]);
                    String message = this.socket.getPort() +" bet " + commands[1] + " " + commands[2];
                    CentralServer.broadcast(message);

                }else if(clientCommand.startsWith("raise:")){
                    //1 = user 2 == bet amount
                    System.out.println("New raise from: " + commands[1] +" for $"+ commands[2]);
                    String message = this.socket.getPort() +" raise " + commands[1] + " " + commands[2];
                    CentralServer.broadcast(message);
                }
                else if(clientCommand.startsWith("check:")){
                    //1 = user 2 == bet amount
                    System.out.println("Check from: " + commands[1]);
                    String message = this.socket.getPort() +" check " + commands[1];
                    CentralServer.broadcast(message);
                }else if(clientCommand.startsWith("call:")){
                    //1 = user 2 == bet amount
                    System.out.println("call from: " + commands[1]);
                    String message = this.socket.getPort() +" call " + commands[1];
                    CentralServer.broadcast(message);
                }else if(clientCommand.startsWith("fold:")){
                    //1 = user 2 == bet amount
                    System.out.println("fold from: " + commands[1]);
                    String message = this.socket.getPort() +" fold " + commands[1];
                    CentralServer.broadcast(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("There was a problem, " +
                "so we are closing the connection to victim " + this.socket.getPort());
                try {
                    this.socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }
}

