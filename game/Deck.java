package game;

import java.util.Stack;
import java.util.Collections;


/**********************************************************************
A deck of cards is created from the Card class and stored as a Stack.
This class also allows for the cards to be shuffled and drawn from.

@author Allison Bickford
@author R.J. Hamilton
@author Johnathon Kileen
@author Michelle Vu
@version December 2019
 **********************************************************************/
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
