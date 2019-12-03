package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import game.Card;
import server.ClientSession;

public class BoardCards extends JPanel {
    private Card card1, card2, card3, card4, card5;
    private ImageIcon card1Image, card2Image, card3Image, card4Image, card5Image;
    private JLabel card1Slot, card2Slot, card3Slot, card4Slot, card5Slot;
    private ClientSession session;

    public BoardCards(Card card1, Card card2, Card card3, Card card4, Card card5, ClientSession session, Integer state) {
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.card4 = card4;
        this.card5 = card5;
        this.session = session;
        this.setLayout(new GridBagLayout());

        displayBoardCards(state);

        this.setBackground(new Color(12, 107, 17));
    }

    public void displayBoardCards(Integer state) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
    

        // Flop state - Only the first three cards need to be displayed, hide the last two
        if(state == 0){
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
        String suitnumber = Integer.toString(card.suit());
        String cardLetter = "";

        switch(suitnumber){
            case "1":
                cardLetter = "D";
            case "2":
                cardLetter = "H";
            case "3":
                cardLetter = "C";
            case "4":
                cardLetter = "S";
        }

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
