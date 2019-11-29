package server;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Map.Entry;
import java.util.Map;

public class PlayersObservable extends Observable {
  private ArrayList<Entry<String, String>> players = new ArrayList<>();
  private Entry<String, String> turn = Map.entry("", "");

  public void setPlayers(ArrayList<Entry<String, String>> players) {
    synchronized (this) {
      this.players = players;
    }
    setChanged();
    notifyObservers(players);
  }

  public void setTurn(Entry host) {
    synchronized (this) {
      this.turn = host;
    }
    setChanged();
    notifyObservers(host);
  }

  public synchronized ArrayList<Entry<String, String>> getPlayers() {
    return players;
  }

  public synchronized Entry getTurn() {
    return turn;
  }
}