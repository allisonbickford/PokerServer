package server;

import gui.CardPanel;
import game.Card;


/**********************************************************************
The Player class is used to create an instance of poker player. The
poker player has the following attributes:
- name                 - host name
- money                - current bet
- last action          - if it is the player's turn
- role                 - if the player has folded
- two cards

Getters and setters are used to access the properties of the Player
class.

@author Allison Bickford
@author R.J. Hamilton
@author Johnathon Kileen
@author Michelle Vu
@version December 2019
 **********************************************************************/
public class Player {
    private String name;
    private String hostName;
    private int money;
    private String lastAction;
    private String role;
    private CardPanel cardPanel;
    private boolean hasFolded;
    private boolean isTurn;
    private Card firstCard;
    private Card secondCard;
    private int currentBet;


    public Player(String name, String hostName) {
        this.name = name;
        this.hostName = hostName;
        this.money = 100;
        this.lastAction = "";
        this.role = "";
        this.cardPanel = new CardPanel();
        this.hasFolded = false;
        this.currentBet = 0;
        this.isTurn = false;
    }

    public void reset() {
        this.lastAction = "";
        this.role = "";
        this.cardPanel = new CardPanel();
        this.hasFolded = false;
        this.currentBet = 0;
        this.isTurn = false;
    }

    public void addMoney(int money) {
        this.money += money;
    }

    public void removeMoney(int money) {
        this.money -= money;
    }

    public void setLastAction(String action) {
        this.lastAction = action;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setTurn(boolean turn) {
        this.isTurn = turn;
    }

    public void setCards(Card first, Card second) {
        firstCard = first;
        secondCard = second;
    }

    public void showCards() {
        if (firstCard != null && secondCard != null) {
            this.cardPanel.show(firstCard, secondCard);
        }
    }

    public void fold() {
        this.hasFolded = true;
        this.cardPanel.fold();
    }

    public void setFolded(boolean folded) {
        this.hasFolded = false;
    }

    public void setCurrentBet(int bet) {
        this.currentBet = bet;
    }

    public String getName() {
        return this.name;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getMoney() {
        return this.money;
    }

    public String getLastAction() {
        return this.lastAction;
    }

    public String getRole() {
        return this.role;
    }

    public CardPanel getPanel() {
        return this.cardPanel;
    }

    public Card[] getCards() {
        return new Card[]{firstCard, secondCard};
    }

    public boolean hasFolded() {
        return this.hasFolded;
    }

    public boolean isTurn() {
        return this.isTurn;
    }

    public int getCurrentBet() {
        return this.currentBet;
    }
}