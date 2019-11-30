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


    public Player(String name, String hostName) {
        this.name = name;
        this.hostName = hostName;
        this.money = 100;
        this.lastAction = "";
        this.role = "";
        this.cardPanel = new CardPanel();
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

    public void showCards(Card first, Card second) {
        this.cardPanel.show(first, second);
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
}
