package game;

/**********************************************************************
Encoding:
Suit: 4 = Spade
3 = Heart
2 = Club
1 = Diamond
Rank:  A = 1
2 = 2
...
J = 11
Q = 12
K = 13
Card:
byte cardSuit;                -- contain 1, 2, 3, or 4
byte cardRank;                -- contain 2, 3, ... 13, 14

source: https://github.com/ckurtm/Poker/blob/master/src/main/
java/com/peirr/poker/models/Card.java
 **********************************************************************/
public class Card {
    public static final int SPADE = 4;
    public static final int HEART = 3;
    public static final int CLUB = 2;
    public static final int DIAMOND = 1;

    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;
    public static final int ACE = 14;

    // * , Diamonds , Club , Heart , Spade
    private static final String[] Suit = {
        "*", 
        String.valueOf('\u2666'),   // Diamonds
        String.valueOf('\u2663'),   // Clubs
        String.valueOf('\u2764'),   // Hearts
        String.valueOf('\u2660')    // Spades
    };

    private static final String[] Rank = {"*", "*", "2", "3", "4","5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    private byte cardSuit;
    private byte cardRank;

    public Card(int suit, int rank) {
        if (rank == 1){
            cardRank = 14;     // Give Ace the rank 14
        }
        else {
            cardRank = (byte) rank;
        }
        cardSuit = (byte) suit;
    }

    public Card(int number, Suit suit){
        if (number == 1){
            cardRank = 14;
        }
        else{
            cardRank = (byte) number;
        }
        
        switch(suit){
            case DIAMONDS:
                cardSuit = DIAMOND;
            case CLUBS:
                cardSuit = CLUB;
            case HEARTS:
                cardSuit = HEART;
            case SPADES:
                cardSuit = SPADE;
        }
    }

    public int suit() {
        return (cardSuit);
    }

    public String suitStr() {
        return (Suit[cardSuit]);
    }

    public String suitLetter() {
        switch (cardSuit) {
            case 1:
                return "D";   // Diamonds
            case 2:
                return "C";   // Clubs
            case 3:
                return "H";   // Hearts
            default:    
                return "S";   // Spades
        }
    }

    public int rank() {
        return (cardRank);
    }

    public String toString() {
        return (Rank[cardRank] + Suit[cardSuit]);
    }
}

enum Suit {
    SPADES, CLUBS, HEARTS, DIAMONDS
}