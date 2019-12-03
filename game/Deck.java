package game;

import java.util.Stack;
import java.util.Collections;

public class Deck {
    Stack<Card> cards = new Stack<>();

    public Deck() {
        for (int s = 1; s <= 4; s++) {
            for (int i = 1; i <= 13; i++) {
                this.cards.push(new Card(s, i));
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
