package server;

import java.io.*;
import java.net.*;
import java.util.*;
import game.Card;
import game.Deck;
import gui.GUI;


/**********************************************************************
Manages most of the fucntionality used by a player in a poker game. The
ClientSession class is responisble for broadcasting information from
clients to the central server.

@author Allison Bickford
@author R.J. Hamilton
@author Johnathon Kileen
@author Michelle Vu
@version December 2019
 **********************************************************************/
public class ClientSession {
    private int port;
    private int mutablePort;
    private String address;
    private Socket centralSocket;
    private Socket controlSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private GUI gui;

    public ClientSession(String username, String centralAddress) throws UnknownHostException {
        try {
            //connects to central server
            this.centralSocket = new Socket(centralAddress, 1200);
            this.address = InetAddress.getLoopbackAddress().getHostAddress();
            this.port = this.centralSocket.getLocalPort();
            this.mutablePort = this.port + 7;

            String connMessage = String.format("newuser: %s %s:%d \n", username, this.address, this.port);
            System.out.println("Registering with central: " + connMessage);
            DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
            centralDOS.writeBytes(connMessage);

            this.gui = new GUI(this);
            new Thread(
                new Server(this.port, this)
            ).start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            if (e instanceof UnknownHostException) {
                throw new UnknownHostException(e.getMessage());
            }
            e.printStackTrace();
            System.out.println("Wow that sucked");
        }
    }

    public void startGame() {
        try {
            DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
            centralDOS.writeBytes(String.format("start %s:%d\n", this.address, this.port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dealCards(Deck deck) {
        this.mutablePort += 7;
        for (Player player: Server.getRegisteredPlayers()) {
            if (player.getHostName().contains(this.getHostName())) { continue; } // don't send to ourselves
            Card card1 = deck.draw();
            Card card2 = deck.draw();
            String message = String.format("%d yourcards: %s %s %s %s \n", 
                this.mutablePort,
                card1.suit(),
                card1.rank(),
                card2.suit(),
                card2.rank()
            );
            String[] serverInfo = player.getHostName().split(":");
            try {
                Socket tmpSocket = new Socket(serverInfo[0], Integer.parseInt(serverInfo[1]));
                DataOutputStream dos = new DataOutputStream(tmpSocket.getOutputStream());
                dos.writeBytes(message);
                dos.flush();
                dos.close();
                tmpSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Card myCard1 = deck.draw();
        Card myCard2 = deck.draw();
        System.out.println("dealt to self: " + myCard1.rank() + " " + myCard1.suitStr() + " " + myCard2.rank() + " " + myCard2.suitStr());
        int myIndex = Server.getGame().findUserIndex(this.getHostName());
        Server.getGamePlayers().get(myIndex).setCards(myCard1, myCard2);
        Server.getGame().setDeck(deck);
        this.gui.initializeGameGUI();
        updatePot();
    }

    /**
     * Send message to all other players
     * @param message message to send
     */
    public void broadcast(String message) {
        for (Player player: Server.getGamePlayers()) {
            if (player.getHostName().contains(this.getHostName())) { continue; } // don't send to ourselves
            String[] serverInfo = player.getHostName().split(":");
            try {
                Socket tmpSocket = new Socket(serverInfo[0], Integer.parseInt(serverInfo[1]));
                DataOutputStream dos = new DataOutputStream(tmpSocket.getOutputStream());
                dos.writeBytes(message);
                dos.flush();
                dos.close();
                tmpSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCheckMessage() {
        if (Server.getGame().endOfPhase() && Server.getGame().getDealerHostName().equals(this.getHostName())) {
            endPhase();
        }
        this.mutablePort += 7;
        String message = String.format("%d action: %s Check\n", this.mutablePort, this.getHostName());
        broadcast(message);
        Server.getGame().check(this.getHostName());
        this.updateMyPanel("Check");
    }

    public void sendCallMessage() {
        if (Server.getGame().endOfPhase() && Server.getGame().getDealerHostName().equals(this.getHostName())) {
            endPhase();
        }
        this.mutablePort += 7;
        String message = String.format("%d action: %s Call\n", this.mutablePort, this.getHostName());
        broadcast(message);
        Server.getGame().call(this.getHostName());
        this.updateMyPanel("Call");
        updatePot();
    }

    public void sendBetMessage(int amount) {
        if (Server.getGame().endOfPhase() && Server.getGame().getDealerHostName().equals(this.getHostName())) {
            endPhase();
        }
        this.mutablePort += 7;
        String message = String.format("%d action: %s Bet %d\n", this.mutablePort, this.getHostName(), amount);
        // give money to the pot
        Server.getGame().bet(this.getHostName(), amount);
        broadcast(message);
        this.updateMyPanel("Bet");
        updatePot();
    }

    public void sendRaiseMessage(int amount) {
        if (Server.getGame().endOfPhase() && Server.getGame().getDealerHostName().equals(this.getHostName())) {
            endPhase();
        }
        this.mutablePort += 7;
        String message = String.format("%d action: %s Raise %d\n", this.mutablePort, this.getHostName(), amount);
        // give money to the pot
        Server.getGame().raise(this.getHostName(), amount);
        broadcast(message);
        this.updateMyPanel("Raise");
        updatePot();
    }

    public void sendFoldMessage() {
        if (Server.getGame().endOfPhase() && Server.getGame().getDealerHostName().equals(this.getHostName())) {
            endPhase();
        }
        if (Server.getGamePlayers().get(Server.getGame().getTurnIndex()).getLastAction().contains("Blind")) {
            this.gui.getGameGUI().actionAfterBet();
            this.gui.getGameGUI().stopPlay();
        }
        this.mutablePort += 7;
        String message = String.format("%d action: %s Fold\n", this.mutablePort, this.getHostName());
        Server.getGame().fold(this.getHostName());
        broadcast(message);
        if (Server.getGame().playersNotFolded() == 1) {
            String host="";
            for (Player player: Server.getGame().getPlayers()) {
                if (!player.hasFolded()) {
                    host = player.getHostName();
                }
            }
            try {
                DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
                centralDOS.writeBytes(String.format("flopWin: %s ", host));
                centralDOS.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("There was a problem sending win.");
            }
        } else {
            Server.getGame().nextTurn();
        }
    }

    public void endPhase() {
        this.mutablePort += 7;
        String cardString = "";
        Deck deck = Server.getGame().getDeck();
        Server.getGame().nextPhase();
        ArrayList<Card> cards = new ArrayList<>();
        if (Server.getGame().phaseString().equals("flop")) {
            for (int i = 0; i < 3; i++) {
                Card card = deck.draw();
                cards.add(card);
                cardString += card.suit() + " " + card.rank() + " ";
            }
        } else {
            Card card = deck.draw();
            cards.add(card);
            cardString = String.format("%d %d", card.suit(), card.rank());
        }
        String message = String.format("%d endPhase %s\n", this.mutablePort, cardString);
        broadcast(message);
        Server.getGame().setDeck(deck);
        Server.getGame().addToBoard(cards);
        gui.getGameGUI().setButtonsToBeginningOfRound();
        switch (Server.getGame().phaseString()) {
            case "flop":
                this.gui.getGameGUI().flop(new Card[]{cards.get(0), cards.get(1), cards.get(2)});
                break;
            case "turn":
                this.gui.getGameGUI().turn(cards.get(0));
                break;
            case "river":
                this.gui.getGameGUI().river(cards.get(0));
                break;
            case "end":
                this.endRound();
            }
        }

    public void endRound() {
        this.mutablePort += 7;
        String message = String.format("%d endRound %s\n", this.mutablePort, this.getHostName());
        broadcast(message);
        this.sendCards();
    }

    public void sendScore(int score) {
        try {
            DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
            centralDOS.writeBytes(String.format("score: %s %d", this.getHostName(), score));
            centralDOS.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem sending your score.");
        }
    }

    public void sendCards() {
        this.mutablePort += 7;
        int myIndex = Server.getGame().findUserIndex(this.getHostName());
        if (!Server.getGamePlayers().get(myIndex).hasFolded()) {
            Card[] cards = Server.getGamePlayers().get(myIndex).getCards();
            String message = String.format("%d cards: %s %s %s %s %s\n", 
                this.mutablePort, this.getHostName(), cards[0].suit(), cards[0].rank(), cards[1].suit(), cards[1].rank());
                broadcast(message);
            this.sendScore(Server.getGame().getScore(cards));
        } else {
            this.sendScore(0); // make sure this is too low to win
        }
        this.gui.getGameGUI().stopPlay();
        Server.getGame().nextPhase();
    }

    //closes client session
    public void close() {
        if (this.controlSocket != null) {
            try {
                this.mutablePort += 7;
                String closeCommand = this.port + " close \n";
                this.out.writeBytes(closeCommand);
                this.controlSocket.close();
                this.controlSocket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } try {
            DataOutputStream centralDOS = new DataOutputStream(this.centralSocket.getOutputStream());
            centralDOS.writeBytes("close " + this.address + ":" + this.port + "\n");
        } catch(IOException d){
            d.printStackTrace();
        }
    }

    public GUI getGUI() {
        return this.gui;
    }

    public void updatePot() {
        this.gui.getGameGUI().setPotLabel(Server.getGame().getPot());
    }

    public void updateMyPanel(String lastAction) {
        this.gui.getGameGUI().changePlayerButtons(lastAction);
        int myIndex = Server.getGame().findUserIndex(this.getHostName());
        if (Server.getGamePlayers().get(myIndex).getLastAction().contains("Small")) {
            this.gui.getGameGUI().actionAfterBet();
        }
        if (Server.getGame().getTurnHostName().equals(this.getHostName()) &&
            !Server.getGame().getPlayers().get(myIndex).hasFolded()) {
            this.gui.getGameGUI().resumePlay();
        } else {
            this.gui.getGameGUI().stopPlay();
        }
        this.gui.getGameGUI().updateTable();
    }

    public boolean isConnected() {
        return this.controlSocket != null;
    }

    public Socket getSocket() {
        return this.controlSocket;
    }

    public DataOutputStream getOutputStream() {
        return this.out;
    }

    public DataInputStream getInputStream() {
        return this.in;
    }

    public String getHostName() {
        return String.format("%s:%d", this.address, this.port);
    }
}
