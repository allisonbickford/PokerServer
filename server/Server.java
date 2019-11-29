package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

import game.Deck;

class Server implements Runnable {
    public int port;
    public String address;
    public String userName = "";
    public PlayersObservable playersObservable = new PlayersObservable();

    public Server(int port, String userName) {
        this.port = port;
        this.userName = userName;
    }

    public void run() {
        try {
            address = InetAddress.getLocalHost().getHostAddress();
            ServerSocket welcomeSocket = new ServerSocket(this.port, 0, InetAddress.getLocalHost());
            System.out.println("Listening at " + welcomeSocket.getInetAddress() + ":" + port);
            while(true) {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Victim at: " + connectionSocket.getPort());
                new Thread(new ClientHandler(connectionSocket, userName, playersObservable)).start();
            }
        } catch(Exception e) {
             e.printStackTrace();
        }
    }

    public void setPlayers(ArrayList<Entry<String, String>> updatedPlayers) {
        playersObservable.setPlayers(updatedPlayers);
    }

    public PlayersObservable getObservable() {
        return this.playersObservable;
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private String userName;
    private PlayersObservable players;

    public ClientHandler(Socket socket, String userName, PlayersObservable players) {
        this.socket = socket;
        this.userName = userName;
        this.players = players;
    }

    @Override
    public void run() {
        String fromClient;
        String clientCommand;
        String frstln;
        int port = this.socket.getPort();
        Deck deck = null;

        while (true) {
            try {
                DataOutputStream outToClient = new DataOutputStream(this.socket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                fromClient = inFromClient.readLine();
                System.out.println(fromClient);
                if (fromClient == null) { // deal with closing socket or output stream
                    System.out.println("Connection ended with victim " + this.socket.getPort());
                    this.socket.close();
                    break;
                }

                StringTokenizer tokens = new StringTokenizer(fromClient);
                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);
                clientCommand = tokens.nextToken();

                if (clientCommand.equals("close")) {
                    System.out.println("Connection ended with victim " + this.socket.getPort());
                    this.socket.close();
                    break;
                } else if (clientCommand.startsWith("players")) {
                    ArrayList<Entry<String, String>> updatedPlayers = new ArrayList<>();
                    while (tokens.hasMoreTokens()) {
                        String player = tokens.nextToken();
                        String[] info = player.split("\0");
                        updatedPlayers.add(Map.entry(info[0], info[1]));
                    }
                    players.setPlayers(updatedPlayers);
                } else if (clientCommand.startsWith("STARTING!")) {
                    tokens.nextToken(); // Players:
                    int amtOfPlayers = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken(); // Host:
                    String host = tokens.nextToken();
                    System.out.println("Dealer is: " + host);
                    ArrayList<Entry<String, String>> playerInfo = players.getPlayers();
                    int dealerIndex = 0;
                    for (int i = 0; i < playerInfo.size(); i++) {
                        if (playerInfo.get(i).getKey().contains(host)) {
                            dealerIndex = i;
                        }
                    }
                    Entry firstToAct = playerInfo.get((dealerIndex + 2) % playerInfo.size());
                    if (playerInfo.size() == 2) { // heads up game has different rules
                        firstToAct = playerInfo.get((dealerIndex + 1) % playerInfo.size());
                    }
                    players.setTurn(firstToAct);
                } else {
                    Socket dataSocket = new Socket(this.socket.getInetAddress(), port);
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

                    // command handling goes here 

                    dataSocket.close();
                    System.out.println("Data Socket closed");
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