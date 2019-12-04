package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import game.Card;


/**********************************************************************
JPanel object used to represent the cards that appear on the board of
a poker table. The view of the cards are determined by the current
state of the game. Game states can be considered as the following:
state = 0 --> Flop              3 out of 5 cards visible
= 1 --> Turn              4 out of 5 cards visible
= 2 --> River             5 ouf of 5 cards visible
= 3 --> Initial state     0 out of 5 cards visible

@author Allison Bickford
@author R.J. Hamilton
@author Johnathon Kileen
@author Michelle Vu
@version December 2019
 **********************************************************************/
public class BoardCards extends JPanel {
    private Card card1, card2, card3, card4, card5;
    private ImageIcon card1Image, card2Image, card3Image, card4Image, card5Image;
    private JLabel card1Slot, card2Slot, card3Slot, card4Slot, card5Slot;

    public BoardCards(Integer state) {
        this.setLayout(new GridBagLayout());
        displayBoardCards(state);
        this.setBackground(new Color(12, 107, 17));
    }

    public void flop(Card card1, Card card2, Card card3) {
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        System.out.println(card1.suitStr());
        displayBoardCards(0);
    }

    public void turn(Card card4) {
        this.card4 = card4;
        displayBoardCards(1);
    }

    public void river(Card card5) {
        this.card5 = card5;
        displayBoardCards(2);
    }

    public void displayBoardCards(Integer state) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        this.removeAll();

        // Flop state - Only the first three cards need to be displayed, hide the last two
        if(state == 0) {
            this.card1Image = new ImageIcon(getCardImage(card1));
            this.card2Image = new ImageIcon(getCardImage(card2));
            this.card3Image = new ImageIcon(getCardImage(card3));
            this.card4Image = new ImageIcon(cardBack());
            this.card5Image = new ImageIcon(cardBack());

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 0;

            this.card1Slot = new JLabel(this.card1Image);
            this.card2Slot = new JLabel(this.card2Image);
            this.card3Slot = new JLabel(this.card3Image);
            this.card4Slot = new JLabel(this.card4Image);
            this.card5Slot = new JLabel(this.card5Image);

            addCardsToBoard(cons);
        }

        // Turn state - Only the first four cards need to be diplayed, hide the last one
        if(state == 1){
            this.card1Image = new ImageIcon(getCardImage(card1));
            this.card2Image = new ImageIcon(getCardImage(card2));
            this.card3Image = new ImageIcon(getCardImage(card3));
            this.card4Image = new ImageIcon(getCardImage(card4));
            this.card5Image = new ImageIcon(cardBack());

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 0;

            this.card1Slot = new JLabel(this.card1Image);
            this.card2Slot = new JLabel(this.card2Image);
            this.card3Slot = new JLabel(this.card3Image);
            this.card4Slot = new JLabel(this.card4Image);
            this.card5Slot = new JLabel(this.card5Image);

            addCardsToBoard(cons);
        }

        // River state - All cards are displayed. This view is set by default.
        if(state == 2){
            this.card1Image = new ImageIcon(getCardImage(card1));
            this.card2Image = new ImageIcon(getCardImage(card2));
            this.card3Image = new ImageIcon(getCardImage(card3));
            this.card4Image = new ImageIcon(getCardImage(card4));
            this.card5Image = new ImageIcon(getCardImage(card5));

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 0;
        
            this.card1Slot = new JLabel(this.card1Image);
            this.card2Slot = new JLabel(this.card2Image);
            this.card3Slot = new JLabel(this.card3Image);
            this.card4Slot = new JLabel(this.card4Image);
            this.card5Slot = new JLabel(this.card5Image);

            addCardsToBoard(cons);
        }

        // Initial state - All cards are upside-down to start
        if(state == 3){

            this.card1Image = new ImageIcon(cardBack());
            this.card2Image = new ImageIcon(cardBack());
            this.card3Image = new ImageIcon(cardBack());
            this.card4Image = new ImageIcon(cardBack());
            this.card5Image = new ImageIcon(cardBack());

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 0;

            this.card1Slot = new JLabel(this.card1Image);
            this.card2Slot = new JLabel(this.card2Image);
            this.card3Slot = new JLabel(this.card3Image);
            this.card4Slot = new JLabel(this.card4Image);
            this.card5Slot = new JLabel(this.card5Image);

            addCardsToBoard(cons);
        }

        this.revalidate();
        this.repaint();
        this.setBackground(new Color(12, 107, 17));
    }

    private void addCardsToBoard(GridBagConstraints cons){
        cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 2;
            cons.gridwidth = 1;
            this.add(this.card1Slot, cons);

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 1;
            cons.gridy = 2;
            cons.gridwidth = 1;
            this.add(this.card2Slot, cons);

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 2;
            cons.gridy = 2;
            cons.gridwidth = 1;
            this.add(this.card3Slot, cons);

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 3;
            cons.gridy = 2;
            cons.gridwidth = 1;
            this.add(this.card4Slot, cons);

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 4;
            cons.gridy = 2;
            cons.gridwidth = 1;
            this.add(this.card5Slot, cons);
    }


    private Image getCardImage(Card card) {
        int cardNumber = card.rank();
        String cardLetter = card.suitLetter();

        try {
            return ImageIO.read(new File("./CardImages/" + cardNumber + cardLetter + ".png")).getScaledInstance(75, 100, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    private Image cardBack() {
        try {
            return ImageIO.read(new File("./CardImages/purple_back.png")).getScaledInstance(75, 100, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }
}
