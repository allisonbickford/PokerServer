package server;

import gui.CardPanel;
import game.Card;

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


    public Player(String name, String hostName) {
        this.name = name;
        this.hostName = hostName;
        this.money = 100;
        this.lastAction = "";
        this.role = "";
        this.cardPanel = new CardPanel();
        this.hasFolded = false;
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


}
