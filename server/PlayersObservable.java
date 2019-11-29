package server;

import java.util.ArrayList;
import java.util.Observable;

public class PlayersObservable extends Observable {
  private ArrayList<String> players = new ArrayList<>();

  public void setPlayers(ArrayList<String> players) {
    synchronized (this) {
      this.players = players;
    }
    setChanged();
    notifyObservers();
  }

  public synchronized ArrayList<String> getPlayers() {
    return players;
  }
}