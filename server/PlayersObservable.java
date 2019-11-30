package server;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Map.Entry;
import java.util.Map;

public class PlayersObservable extends Observable {
  private ArrayList<Player> players = new ArrayList<>();
  private Player turn = null;
  private int currentPot = 0;

  public void setPlayers(ArrayList<Player> players) {
    synchronized (this) {
      this.players = players;
    }
    setChanged();
    notifyObservers(players);
  }

  public void setTurn(int index) {
    synchronized (this) {
      this.turn = this.players.get(index);
    }
    setChanged();
    notifyObservers(this.players.get(index));
  }

  public void setPot(int money) {
    synchronized (this) {
      this.currentPot = money;
    }
    setChanged();
    notifyObservers();
  }

  public synchronized ArrayList<Player> getPlayers() {
    return players;
  }

  public synchronized Player getTurn() {
    return turn;
  }

  public synchronized int getPot() {
    return this.currentPot;
  }
}