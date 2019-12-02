package game;

import java.util.Stack;
import java.util.Collections;

public class Deck {
    Stack<Card> cards = new Stack<>();

    public Deck() {
        for (int j = 1; j <=4; j++) {
            for (int i = 1; i <= 13; i++) {
                this.cards.push(new Card(j,i));
                //System.out.println(cards.toString());
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
