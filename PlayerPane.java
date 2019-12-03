import javax.swing.*;

import game.Card;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class PlayerPane extends JPanel {
    private boolean currentPlayer = false;
    private boolean myTurn = false;
    private Card firstCard, secondCard;
    private ImageIcon firstCardImg, secondCardImg;
    private JLabel firstCardSlot, secondCardSlot;
    private JButton checkBtn, betBtn, foldBtn;

    public PlayerPane(boolean currentPlayer, Card firstCard, Card secondCard) {
        this.currentPlayer = currentPlayer;
        this.firstCard = firstCard;
        this.secondCard = secondCard;
        this.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        this.checkBtn = new JButton("Check");
        this.betBtn = new JButton("Bet");
        this.foldBtn = new JButton("Fold");
        if (currentPlayer) {
            // image shows number/suit
            this.firstCardImg = new ImageIcon(getCardImage(firstCard));
            this.secondCardImg = new ImageIcon(getCardImage(secondCard));

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 0;
            this.add(this.checkBtn, cons);

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 1;
            cons.gridy = 0;
            this.add(this.betBtn, cons);

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 2;
            cons.gridy = 0;
            this.add(this.foldBtn);
            
            this.firstCardSlot = new JLabel(this.firstCardImg);
            this.secondCardSlot = new JLabel(this.secondCardImg);
            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 1;
            cons.gridwidth = 2;
            this.add(this.firstCardSlot, cons);

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 1;
            cons.gridy = 1;
            cons.gridwidth = 2;
            this.add(this.secondCardSlot, cons);
        } else {
            // image shows cardback
            this.firstCardImg = new ImageIcon(cardBack());
            this.secondCardImg = new ImageIcon(cardBack());
            this.firstCardSlot = new JLabel(new RotatedIcon(this.firstCardImg, RotatedIcon.Rotate.UP));
            this.secondCardSlot = new JLabel(new RotatedIcon(this.secondCardImg, RotatedIcon.Rotate.UP));

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 0;
            this.add(this.firstCardSlot, cons);

            cons.fill = GridBagConstraints.HORIZONTAL;
            cons.gridx = 0;
            cons.gridy = 1;
            this.add(this.secondCardSlot, cons);
        }

        this.setBackground(new Color(12, 107, 17));
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

    public void showCards() {
        this.firstCardImg = new ImageIcon(getCardImage(this.firstCard));
        this.secondCardImg = new ImageIcon(getCardImage(this.secondCard));
    }

    public void play() {
        this.myTurn = true;
    }

    public void stop() {
        this.myTurn = false;
    }
}