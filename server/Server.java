package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

import game.Deck;
import game.Card;

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
            address = InetAddress.getLoopbackAddress().getHostAddress();
            ServerSocket welcomeSocket = new ServerSocket(this.port, 0, InetAddress.getLoopbackAddress());
            System.out.println("Listening at " + welcomeSocket.getInetAddress() + ":" + port);
            while(true) {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Victim at: " + connectionSocket.getPort());
                new Thread(new ClientHandler(connectionSocket, userName, playersObservable, this.address + ":" + this.port)).start();
            }
        } catch(Exception e) {
             e.printStackTrace();
        }
    }

    public void setPlayers(ArrayList<Player> updatedPlayers) {
        playersObservable.setPlayers(updatedPlayers);
    }

    public PlayersObservable getObservable() {
        return this.playersObservable;
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private String userName;
    private PlayersObservable playersObservable;
    private String address;

    public ClientHandler(Socket socket, String userName, PlayersObservable playersObservable, String myAddress) {
        this.socket = socket;
        this.userName = userName;
        this.playersObservable = playersObservable;
        this.address = myAddress;
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
                    ArrayList<Player> updatedPlayers = new ArrayList<>();
                    while (tokens.hasMoreTokens()) {
                        String player = tokens.nextToken();
                        String[] info = player.split("\0");
                        updatedPlayers.add(
                            new Player(info[1], info[0])
                        );
                    }
                    playersObservable.setPlayers(updatedPlayers);
                } else if (clientCommand.startsWith("STARTING!")) {
                    tokens.nextToken(); // Host:
                    String host = tokens.nextToken();
                    System.out.println("Dealer is: " + host);
                    ArrayList<Player> playerInfo = playersObservable.getPlayers();
                    int dealerIndex = 0;
                    for (int i = 0; i < playerInfo.size(); i++) {
                        if (playerInfo.get(i).getHostName().contains(host)) {
                            dealerIndex = i;
                            break;
                        }
                    }
                    playerInfo.get(dealerIndex).setRole("D"); // dealer
                    playerInfo.get((dealerIndex + 2) % playerInfo.size()).setRole("BB"); // big blind
                    playerInfo.get((dealerIndex + 2) % playerInfo.size()).setLastAction("Big Blind - $2"); // big blind
                    playerInfo.get((dealerIndex + 2) % playerInfo.size()).removeMoney(2); // put in big blind
                    playerInfo.get((dealerIndex + 3) % playerInfo.size()).setTurn(true);
                    if (playerInfo.size() == 2) {
                        playerInfo.get(dealerIndex).setRole("D/BB");
                    }
                    playerInfo.get((dealerIndex + 1) % playerInfo.size()).setRole("SB");
                    playerInfo.get((dealerIndex + 1) % playerInfo.size()).setLastAction("Small Blind - $1");
                    playerInfo.get((dealerIndex + 1) % playerInfo.size()).removeMoney(1); // put in small blind
                    playersObservable.setHost(host); // triggers start of game for gui
                    Thread.sleep(50); // trying to use the same thread?
                    playersObservable.setPot(3);
                  //  Thread.sleep(100);
                    playersObservable.setLastPlayerToBet(playerInfo.get((dealerIndex + 2) % playerInfo.size()).getHostName());
                } else if(clientCommand.startsWith("deck:")) {
                    String playerHost = tokens.nextToken(); // Host name of player that just acted
                    String action = tokens.nextToken(); // Bet, Check, Call, or Fold
                    if (action.equals("Deck")) {
                        ArrayList<Player> playerInfo = this.playersObservable.getPlayers();
                        int card1 = Integer.parseInt(tokens.nextToken());
                        int suit1 = Integer.parseInt(tokens.nextToken());
                        int card2 = Integer.parseInt(tokens.nextToken());
                        int suit2 = Integer.parseInt(tokens.nextToken());
                        System.out.println("cards: " + card1 + " " + suit1 + " " + card2 + " " + suit2);
                        String sendToHost = tokens.nextToken();
                        for (Player player : playerInfo) {
                            if (player.getHostName().contains(sendToHost)) {
                                System.out.println("cards: " + sendToHost + " " + card1 + " " + suit1 + " " + card2 + " " + suit2);
                                Card first = new Card(suit1, card1);
                                Card second = new Card(suit2, card2);
                                player.setCards(first, second);
                            }
                        }
                    }
                } else if (clientCommand.startsWith("winner")) {
                    String host = tokens.nextToken();
                    this.playersObservable.setRoundWinner(host);
                } else if (clientCommand.startsWith("cards:")) {
                    String host = tokens.nextToken();
                    Card card1 = new Card(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));
                    Card card2 = new Card(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));

                    for (Player player : this.playersObservable.getPlayers()) {
                        if (player.getHostName().contains(host)) {
                            player.setCards(card1, card2);
                            player.showCards();
                            break;
                        }
                    }
                } else if (clientCommand.startsWith("endPhase")) {
                    System.out.println("server Endphase");
                    String host = tokens.nextToken();
                    ArrayList<Card> cards = new ArrayList<>();
                    while (tokens.hasMoreTokens()) {
                        String cardRank = tokens.nextToken();
                        String cardSuit = tokens.nextToken();
                        cards.add(new Card(Integer.parseInt(cardSuit), Integer.parseInt(cardRank)));
                    }
                    this.playersObservable.addToBoard(cards);
                    this.playersObservable.nextPhase();

                } else if(clientCommand.startsWith("endRound")){
                    //TODO: DO we need logic regarding earnings here?

                    ArrayList<Player> playerInfo = playersObservable.getPlayers();
                    int dealerIndex = 0;

                    for (int i = 0; i < playerInfo.size(); i++) {
                        if (playerInfo.get(i).getRole().equals("D")) {
                            dealerIndex = i +1;
                            break;
                        }
                    }
                    String dealer = playerInfo.get(dealerIndex).getHostName();
                    System.out.println("Dealer is: " + dealer);

                    playerInfo.get(dealerIndex).setRole("D"); // dealer
                    playerInfo.get((dealerIndex + 2) % playerInfo.size()).setRole("BB"); // big blind
                    playerInfo.get((dealerIndex + 2) % playerInfo.size()).setLastAction("Big Blind - $2"); // big blind
                    playerInfo.get((dealerIndex + 2) % playerInfo.size()).removeMoney(2); // put in big blind
                    playerInfo.get((dealerIndex + 3) % playerInfo.size()).setTurn(true);
                    if (playerInfo.size() == 2) {
                        playerInfo.get(dealerIndex).setRole("D/BB");
                    }
                    playerInfo.get((dealerIndex + 1) % playerInfo.size()).setRole("SB");
                    playerInfo.get((dealerIndex + 1) % playerInfo.size()).setLastAction("Small Blind - $1");
                    playerInfo.get((dealerIndex + 1) % playerInfo.size()).removeMoney(1); // put in small blind
                    Thread.sleep(70); // trying to use the same thread?
                    playersObservable.setPot(3);
                   // Thread.sleep(100);
                    playersObservable.setLastPlayerToBet(playerInfo.get((dealerIndex + 2) % playerInfo.size()).getHostName());
                }

                else if (clientCommand.startsWith("action:")) {
                    String playerHost = tokens.nextToken(); // Host name of player that just acted
                    String action = tokens.nextToken(); // Bet, Check, Call, or Fold
                    int playerIndex = 0;
                    for (int i = 0; i < this.playersObservable.getPlayers().size(); i++) {
                        if (this.playersObservable.getPlayers().get(i).getHostName().contains(playerHost)) {
                            playerIndex = i;
                            break;
                        }
                    }
                    if (action.equals("Bet")) {
                        String betAmount = tokens.nextToken(); // amount of $ bet
                        this.playersObservable.setLastAction(playerHost, action + " " + betAmount);
                        this.playersObservable.getPlayers().get(playerIndex).setLastAction(action + " $" + betAmount);
                        this.playersObservable.addToPot(Integer.parseInt(betAmount));
                        this.playersObservable.setLastPlayerToBet(playerHost);
                        this.playersObservable.getPlayers().get(playerIndex).setCurrentBet(Integer.parseInt(betAmount));
                    } else if (action.equals("Call")) {
                        int amountToCall = this.playersObservable.getHighestBet() - this.playersObservable.getPlayers().get(playerIndex).getCurrentBet();
                        this.playersObservable.getPlayers().get(playerIndex).setCurrentBet(this.playersObservable.getHighestBet());
                        this.playersObservable.getPlayers().get(playerIndex).setLastAction(action);
                        this.playersObservable.addToPot(amountToCall);
                        this.playersObservable.setLastPlayerToBet(playerHost);
                        this.playersObservable.setLastAction(playerHost, action);
                    } else {
                        this.playersObservable.setLastAction(playerHost, action);
                        this.playersObservable.getPlayers().get(playerIndex).setLastAction(action);
                    }
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