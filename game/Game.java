package game;

import java.util.ArrayList;

import game.HandEvaluator;
import server.Player;

enum Phase {
    PREFLOP,
    FLOP,
    TURN,
    RIVER,
    END
}

public class Game {
    private ArrayList<Player> players;
    private int pot = 0;
    private int currentBet = 0;
    private int bigBlind = 2;
    private int turnIndex = 0;
    private int dealerIndex = 0;
    private int endOfRoundIndex = 0;
    private Phase currentPhase = Phase.PREFLOP;
    private boolean endOfPhase = false;
    private Deck deck;
    private ArrayList<Card> board = new ArrayList<>();

    public Game(ArrayList<Player> players, int dealerIndex) {
        this.players = players;
        this.dealerIndex = dealerIndex;
        if (this.players.size() == 2) {
            /** set up dealer */
            this.players.get(dealerIndex).setRole("D/BB");
            this.players.get(dealerIndex).removeMoney(bigBlind);
            this.players.get((dealerIndex) % this.players.size()).setLastAction("Big Blind - $" + bigBlind);
            this.players.get((dealerIndex + 1) % this.players.size()).setRole("SB");

            /** set up small blind */
            this.players.get((dealerIndex + 1) % this.players.size()).setLastAction("Small Blind - $" + (bigBlind / 2));
            this.players.get((dealerIndex + 1) % this.players.size()).removeMoney(bigBlind / 2);
            this.players.get((dealerIndex + 1) % this.players.size()).setCurrentBet(bigBlind / 2);
            this.turnIndex = (dealerIndex + 1) % this.players.size();
            this.endOfRoundIndex = dealerIndex;
        } else {
            this.players.get(dealerIndex).setRole("D");

            /** set up small blind */
            this.players.get((dealerIndex + 1) % this.players.size()).setRole("SB");
            this.players.get((dealerIndex + 1) % this.players.size()).setLastAction("Small Blind - $" + (bigBlind / 2));
            this.players.get((dealerIndex + 1) % this.players.size()).removeMoney(bigBlind / 2);
            this.players.get((dealerIndex + 1) % this.players.size()).setCurrentBet(bigBlind / 2);

            /** set up big blind */
            this.players.get((dealerIndex + 2) % this.players.size()).setRole("BB");
            this.players.get((dealerIndex + 2) % this.players.size()).setLastAction("Big Blind - $" + bigBlind);
            this.players.get((dealerIndex + 2) % this.players.size()).removeMoney(bigBlind);
            this.players.get((dealerIndex + 2) % this.players.size()).setCurrentBet(bigBlind);
            this.turnIndex = (dealerIndex + 3) % this.players.size();
            this.endOfRoundIndex = (dealerIndex + 2) % this.players.size();
        }
        this.players.get(turnIndex).setTurn(true);
        this.pot = bigBlind + (bigBlind / 2);
        this.currentBet = bigBlind;
    }

    public void check(String host) {
        int playerIndex = findUserIndex(host);
        this.players.get(playerIndex).setLastAction("Check");
        this.nextTurn();
    }

    public void bet(String host, int amount) {
        this.pot += amount;
        this.currentBet = amount;
        int playerIndex = findUserIndex(host);
        this.players.get(playerIndex).removeMoney(amount);
        this.players.get(playerIndex).setCurrentBet(amount);
        this.players.get(playerIndex).setLastAction("Bet $" + amount);
        this.endOfRoundIndex = playerIndex;
        this.nextTurn();
    }

    public void raise(String host, int amount) {
        this.pot += amount;
        this.currentBet += amount;
        int playerIndex = findUserIndex(host);
        this.players.get(playerIndex).removeMoney(amount);
        this.players.get(playerIndex).setCurrentBet(this.currentBet);
        this.players.get(playerIndex).setLastAction("Raise to $" + this.currentBet);
        this.endOfRoundIndex = playerIndex;
        this.nextTurn();
    }

    public void call(String host) {
        int playerIndex = findUserIndex(host);
        int amountToCall = this.currentBet - this.players.get(playerIndex).getCurrentBet();
        this.players.get(playerIndex).removeMoney(amountToCall);
        this.players.get(playerIndex).setLastAction("Call $" + this.currentBet);
        this.pot += amountToCall;
        this.nextTurn();
    }

    public void fold(String host) {
        int playerIndex = findUserIndex(host);
        this.players.get(playerIndex).fold();
        this.players.get(playerIndex).setLastAction("Fold");

        if (playersNotFolded() == 1) {
            this.currentPhase = Phase.END;

        } else {
            this.nextTurn();
        }
    }
    public void nextTurn() {
        for (int i = 0; i < this.players.size(); i++) {
            Player tmpPlayer = this.players.get(i);
            if (tmpPlayer.isTurn()) {
                this.players.get(i).setTurn(false);
                turnIndex = (i + 1) % this.players.size();
                while (this.players.get(turnIndex).hasFolded()) {
                    turnIndex = (turnIndex + 1) % this.players.size();
                }
            }
            if (this.players.size() > 2 && tmpPlayer.getRole().contains("SB")) {
                endOfRoundIndex = i;
                while (this.players.get(endOfRoundIndex).hasFolded()) {
                    endOfRoundIndex = (endOfRoundIndex + 1) % this.players.size();
                }
            } else if (this.players.size() == 2 && tmpPlayer.getRole().contains("D")) {
                endOfRoundIndex = i;
                while (this.players.get(endOfRoundIndex).hasFolded()) {
                    endOfRoundIndex = (endOfRoundIndex + 1) % this.players.size();
                }
            }
        }
        this.players.get(turnIndex).setTurn(true);
        if (turnIndex == endOfRoundIndex) {
            endOfPhase = true;
        }
    }
    public void nextPhase() {
        endOfPhase = false;
        currentBet = 0;
        switch (this.currentPhase) {
            case PREFLOP:
                this.currentPhase = Phase.FLOP;
                break;
            case FLOP:
                this.currentPhase = Phase.TURN;
                break;
            case TURN:
                this.currentPhase = Phase.RIVER;
                break;
            case RIVER:
                this.currentPhase = Phase.END;
                break;
            default:
                this.currentPhase = Phase.PREFLOP;
                break;
        }
    }

    public int getScore(Card[] cards) {
        Card[] allCards = new Card[]{
                cards[0],
                cards[1],
                board.get(0),
                board.get(1),
                board.get(2),
                board.get(3),
                board.get(4)
        };
        int maxScore = permute(allCards, 0, 0, allCards.length, 5);
        return maxScore;
    }

    // Function to print all distinct combinations of length k
    public static int permute(Card[] A, int currentMax, int i, int n, int k)
    {
        // invalid input
        if (k > n) {
            return 0;
        }

        // base case: combination size is k
        if (k == 0) {
            return currentMax;
        }

        // start from next index till last index
        for (int j = i; j < n; j++)
        {
            // add current element A[j] to solution & recur for next index
            // (j+1) with one less element (k-1)
            if (HandEvaluator.valueHand(A) > currentMax) {
                return permute(A, HandEvaluator.valueHand(A) , j + 1, n, k - 1);
            }
            return permute(A, currentMax, j + 1, n, k - 1);

        }
        return 0;
    }

    public void disperseWinnings(String winnerHostName) {
        int winnerIndex = findUserIndex(winnerHostName);
        this.players.get(winnerIndex).addMoney(this.pot);
        this.pot = 0;
    }

    public void reset() {
        this.currentPhase = Phase.PREFLOP;
        dealerIndex = (dealerIndex + 1) % this.players.size();
        for (Player player: this.players) {
            player.reset();
        }
        if (this.players.size() == 2) {
            this.players.get(dealerIndex).setRole("D/BB");
            this.players.get(dealerIndex).removeMoney(bigBlind);
            this.players.get((dealerIndex + 1) % this.players.size()).setRole("SB");
            this.players.get((dealerIndex + 1) % this.players.size()).removeMoney(bigBlind / 2);
            this.turnIndex = (dealerIndex + 1) % this.players.size();
            this.endOfRoundIndex = dealerIndex;
        } else {
            this.players.get(dealerIndex).setRole("D");
            this.players.get((dealerIndex + 1) % this.players.size()).setRole("SB");
            this.players.get((dealerIndex + 1) % this.players.size()).removeMoney(bigBlind / 2);
            this.players.get((dealerIndex + 2) % this.players.size()).setRole("BB");
            this.players.get((dealerIndex + 1) % this.players.size()).removeMoney(bigBlind);
            this.turnIndex = (dealerIndex + 3) % this.players.size();
            this.endOfRoundIndex = (dealerIndex + 2) % this.players.size();
        }
        this.pot = bigBlind + (bigBlind / 2);
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public void setBlind(int blind) {
        this.bigBlind = blind;
    }

    public int totalPlayers() {
        return this.players.size();
    }

    public int playersNotFolded() {
        int count = 0;
        for (Player player: players) {
            if (!player.hasFolded()) {
                count++;
            }
        }
        return count;
    }

    public int findUserIndex(String hostName) {
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getHostName().equals(hostName)) {
                return i;
            }
        }
        return 0;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void addToBoard(ArrayList<Card> cards) {
        this.board.addAll(cards);
    }

    public Deck getDeck() {
        return this.deck;
    }

    public int getPot() {
        return this.pot;
    }

    public int getHighestBet() {
        return this.currentBet;
    }

    public boolean endOfPhase() {
        return endOfPhase;
    }

    public String phaseString() {
        switch(currentPhase) {
            case PREFLOP:
                return "preflop";
            case FLOP:
                return "flop";
            case TURN:
                return "turn";
            case RIVER:
                return "river";
            default:
                return "end";
        }
    }

    public int getDealerIndex() {
        return this.dealerIndex;
    }

    public String getDealerHostName() {
        return this.players.get(this.dealerIndex).getHostName();
    }

    public int getTurnIndex() {
        return this.turnIndex;
    }

    public String getTurnHostName() {
        return this.players.get(this.turnIndex).getHostName();
    }

}