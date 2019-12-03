package server;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Map.Entry;
import java.util.Map;
import java.util.AbstractMap;

public class PlayersObservable extends Observable {
  private String gameHost = "";
  private ArrayList<Player> players = new ArrayList<>();
  private Integer currentPot = new Integer(0);
  private Entry<String, String> lastAction = new AbstractMap.SimpleEntry<String, String>("", "Small Blind - $1");
  private String lastPlayerToBet = "";

  public void setHost(String host) {
    this.gameHost = host;
    setChanged();
    notifyObservers("host");
  }

  public void setPlayers(ArrayList<Player> players) {
    this.players = players;
    setChanged();
    notifyObservers("players");
  }

  public void setPot(int money) {
    this.currentPot = money;
    setChanged();
    notifyObservers("pot");
  }

  public void addToPot(int money) {
    this.currentPot += money;
    setChanged();
    notifyObservers("pot");
  }

  public void setLastAction(String hostName, String action) {
    this.lastAction = new AbstractMap.SimpleEntry<String, String>(hostName, action);
    // get player who just made an action
    int lastPlayerIndex = 0;
    for (int i = 0; i < this.players.size(); i++) {
      if (this.players.get(i).getHostName().equals(hostName)) {
        lastPlayerIndex = i;
        break;
      }
    }
    this.players.get(lastPlayerIndex).setLastAction(action);
    
    while (this.players.get(lastPlayerIndex).hasFolded()) { // skip people who folded
      lastPlayerIndex = (lastPlayerIndex + 1) % this.players.size();
    }
    this.players.get(lastPlayerIndex).setTurn(false);
    // get index of next person to play
    int nextPlayerIndex = (lastPlayerIndex + 1) % this.players.size();
    while (this.players.get(nextPlayerIndex).hasFolded()) { // skip folders
      nextPlayerIndex = (nextPlayerIndex + 1) % this.players.size();
    }
    // turn goes to next player
    this.players.get(nextPlayerIndex).setTurn(true);
    setChanged();
    notifyObservers("action");
  }

  public void setLastPlayerToBet(String hostName) {
    this.lastPlayerToBet = hostName;
    setChanged();
    notifyObservers("lastPlayerToBet");
  }

  public String getHost() {
    return this.gameHost;
  }

  public ArrayList<Player> getPlayers() {
    return this.players;
  }

  public String getTurnHostName() {
    Player currentPlayer = this.players.stream().filter(player -> player.isTurn()).findAny().orElse(null);
    if (currentPlayer != null) {
      return currentPlayer.getHostName();
    }
    return "";
  }

  public Integer getPot() {
    return this.currentPot;
  }

  public Entry<String, String> getLastAction() {
    return this.lastAction;
  }

  public String getLastPlayerToBet() {
    return this.lastPlayerToBet;
  }

  public int getHighestBet() {
    return Integer.parseInt(
      this.getLastAction().getValue().replaceAll("[^\\d.]", "") // remove "Bet " from action
    );
  }
}