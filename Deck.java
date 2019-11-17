import java.util.Stack;
import java.util.Collections;

class Deck {
    Stack<Card> cards = new Stack<>();

    public Deck() {
        for (Suit suit : Suit.values()) {
            for (int i = 1; i <= 13; i++) {
                this.cards.push(new Card(i, suit));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Card draw() {
        return this.cards.pop();
    }

}
