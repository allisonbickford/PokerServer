package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

import game.Deck;
import game.Card;
import game.Game;

public class Server implements Runnable {
    public int port;
    public String address;
    private static Game game;
    private static ArrayList<Player> registeredPlayers = new ArrayList<>();
    private ClientSession clientSession;

    public Server(int port, ClientSession clientSession) {
        this.port = port;
        this.clientSession = clientSession;
    }

    public void run() {
        try {
            address = InetAddress.getLoopbackAddress().getHostAddress();
            ServerSocket welcomeSocket = new ServerSocket(this.port, 0, InetAddress.getLoopbackAddress());
            System.out.println("Listening at " + welcomeSocket.getInetAddress() + ":" + port);
            while(true) {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Victim at: " + connectionSocket.getPort());
                new Thread(new ClientHandler(connectionSocket, this.clientSession, this.address + ":" + this.port)).start();
            }
        } catch(Exception e) {
             e.printStackTrace();
        }
    }

    public static Game getGame() {
        return game;
    }

    public static ArrayList<Player> getGamePlayers() {
        return game.getPlayers();
    }

    public static void setPlayers(ArrayList<Player> players) {
        registeredPlayers = players;
    }

    public static ArrayList<Player> getRegisteredPlayers() {
        return registeredPlayers;
    }

    public static void startGame(int dealerIndex) {
        game = new Game(registeredPlayers, dealerIndex);
    }

    public static void advanceGamePhase() {
        game.nextPhase();
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PlayersObservable playersObservable;
    private String address;
    private ClientSession clientSession;

    public ClientHandler(Socket socket, ClientSession clientSession, String myAddress) {
        this.socket = socket;
        this.clientSession = clientSession;
        this.address = myAddress;
    }

    @Override
    public void run() {
        String fromClient;
        String clientCommand;
        String frstln;
        int port = this.socket.getPort();

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
                    ArrayList<Player> updatedPlayers = new ArrayList<>();
                    while (tokens.hasMoreTokens()) {
                        String player = tokens.nextToken();
                        String[] info = player.split("\0");
                        updatedPlayers.add(
                            new Player(info[1], info[0])
                        );
                    }
                    Server.setPlayers(updatedPlayers);
                    clientSession.getGUI().updatePlayerLobby(updatedPlayers);
                } else if (clientCommand.startsWith("STARTING!")) {
                    tokens.nextToken(); // Host:
                    String host = tokens.nextToken();
                    System.out.println("Dealer is: " + host);
                    int myIndex = 0;
                    int dealerIndex = 0;
                    for (int i = 0; i < Server.getRegisteredPlayers().size(); i++) {
                        if (Server.getRegisteredPlayers().get(i).getHostName().contains(host)) {
                            dealerIndex = i;
                        }
                        if (Server.getRegisteredPlayers().get(i).getHostName().contains(clientSession.getHostName())) {
                            myIndex = i;
                        }
                    }

                    Server.startGame(dealerIndex);
                    if (myIndex == dealerIndex) {
                        Deck deck = new Deck();
                        clientSession.dealCards(deck);
                    }
                } else if (clientCommand.startsWith("yourcards:")) {
                    Card card1 = new Card(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));
                    Card card2 = new Card(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));
                    System.out.println("cards: " + card1.rank() + " " + card1.suitStr() + " " + card2.rank() + " " + card2.suitStr());
                    int myIndex = Server.getGame().findUserIndex(clientSession.getHostName());
                    Server.getGamePlayers().get(myIndex).setCards(card1, card2);
                    clientSession.getGUI().initializeGameGUI();
                    clientSession.updatePot();
                } else if (clientCommand.startsWith("winner")) {
                    String host = tokens.nextToken();
                    int winnerIndex = Server.getGame().findUserIndex(host);
                    clientSession.getGUI().showWinnerDialog(Server.getGamePlayers().get(winnerIndex).getName());
                    clientSession.getGUI().initializeGameGUI();
                    Server.getGame().reset();
                } else if (clientCommand.startsWith("cards:")) {
                    String host = tokens.nextToken();
                    Card card1 = new Card(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));
                    Card card2 = new Card(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));

                    for (Player player : Server.getGamePlayers()) {
                        if (player.getHostName().contains(host)) {
                            player.setCards(card1, card2);
                            player.showCards();
                            break;
                        }
                    }
                } else if (clientCommand.startsWith("endPhase")) {
                    ArrayList<Card> cards = new ArrayList<>();
                    while (tokens.hasMoreTokens()) {
                        String cardSuit = tokens.nextToken();
                        String cardRank = tokens.nextToken();
                        cards.add(new Card(Integer.parseInt(cardSuit), Integer.parseInt(cardRank)));
                    }
                    Server.getGame().nextPhase();
                    switch (Server.getGame().phaseString()) {
                        case "flop":
                            clientSession.getGUI().getGameGUI().flop(new Card[]{cards.get(0), cards.get(1), cards.get(2)});
                            break;
                        case "turn":
                            clientSession.getGUI().getGameGUI().turn(cards.get(0));
                            break;
                        case "river":
                            clientSession.getGUI().getGameGUI().river(cards.get(0));
                            break;
                        }
                        Server.getGame().addToBoard(cards);
                        clientSession.getGUI().getGameGUI().setButtonsToBeginningOfRound();
                    } else if(clientCommand.startsWith("endRound")){
                        clientSession.sendCards();
                        Server.getGame().reset();
                } else if (clientCommand.startsWith("action:")) {
                    String playerHost = tokens.nextToken(); // Host name of player that just acted
                    String action = tokens.nextToken(); // Bet, Check, Call, or Fold
                    if (action.equals("Bet")) {
                        String betAmount = tokens.nextToken(); // amount of $ bet
                        Server.getGame().bet(playerHost, Integer.parseInt(betAmount));
                    } else if (action.equals("Raise")) {
                        String raiseAmount = tokens.nextToken(); // amount of $ raised
                        Server.getGame().raise(playerHost, Integer.parseInt(raiseAmount));
                    } else if (action.equals("Call")) {
                        Server.getGame().call(playerHost);
                    } else if (action.equals("Check")) {
                        Server.getGame().check(playerHost);
                    } else if (action.equals("Fold")) {
                        Server.getGame().fold(playerHost);
                    }
                    clientSession.updatePot();
                    clientSession.updateMyPanel(action);
                    int myIndex = Server.getGame().findUserIndex(clientSession.getHostName());
                    if (Server.getGamePlayers().get(myIndex).getLastAction().contains("Big")) {
                        clientSession.getGUI().getGameGUI().setButtonsToBeginningOfRound();
                    }
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