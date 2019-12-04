package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import game.Card;


/**********************************************************************
JPnael object used to represent card objects on the user interface.

@author Allison Bickford
@author R.J. Hamilton
@author Johnathon Kileen
@author Michelle Vu
@version December 2019
 **********************************************************************/
public class CardPanel extends JPanel {
    Card firstCard;
    Card secondCard;
    JLabel firstCardLabel = new JLabel();
    JLabel secondCardLabel = new JLabel();

    public CardPanel() {
        super();
        this.firstCardLabel.setIcon(new ImageIcon(cardBack()));
        this.secondCardLabel.setIcon(new ImageIcon(cardBack()));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(this.firstCardLabel, gbc);

        gbc.gridx = 1;
        this.add(this.secondCardLabel, gbc);
        this.setVisible(true);
    }

    private Image getCardImage(Card card) {
        int cardNumber = card.rank();
        String cardLetter = card.suitLetter();

        try {
            return ImageIO.read(new File("./CardImages/" + cardNumber + cardLetter + ".png")).getScaledInstance(45, 70, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    private Image cardBack() {
        try {
            return ImageIO.read(new File("./CardImages/purple_back.png")).getScaledInstance(45, 70, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    public void show(Card firstCard, Card secondCard) {
        this.firstCard = firstCard;
        this.secondCard = secondCard;
        this.firstCardLabel.setIcon(new ImageIcon(getCardImage(this.firstCard)));
        this.secondCardLabel.setIcon(new ImageIcon(getCardImage(this.secondCard)));
    }

    public void fold() {
        try {
            Image grayBack = ImageIO.read(new File("./CardImages/gray_back.png")).getScaledInstance(45, 70, Image.SCALE_SMOOTH);
            firstCardLabel.setIcon(new ImageIcon(grayBack));
            secondCardLabel.setIcon(new ImageIcon(grayBack));
        } catch (Exception e) {}
    }
}
