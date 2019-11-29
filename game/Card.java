package game;

public class Card {
    private int number;
    private Suit suit;

    public Card(int number, Suit suit) {
        this.number = number;
        this.suit = suit;
    }

    public int getNumber() {
        return this.number;
    }

    public Suit getSuit() {
        return this.suit;
    }

    @Override
    public String toString() {
        String suitString = this.suit.toString().substring(0, 1).toUpperCase() + this.suit.toString().substring(1).toLowerCase();
        switch (this.number) {
            case 1: return "Ace of " + suitString;
            case 11: return "Jack of " + suitString;
            case 12: return "Queen of " + suitString;
            case 13: return "King of " + suitString;
            default: return this.number + " of " + suitString;
        }
    }

}