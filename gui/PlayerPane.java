package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import game.*;
import server.ClientSession;


/**********************************************************************
JPanel object that handles displaying many of the options represented
on the poker table for the player. This view displays the player's
two cards determined for each round. The view also displays the
player's ability to check, bet, and fold.

@author Allison Bickford
@author R.J. Hamilton
@author Johnathon Kileen
@author Michelle Vu
@version December 2019
**********************************************************************/
class PlayerPane extends JPanel {
    private Card firstCard, secondCard;
    private ImageIcon firstCardImg, secondCardImg;
    private JLabel firstCardSlot, secondCardSlot;
    private JButton checkBtn, betBtn, foldBtn;
    private JSpinner betSpinner;
    private ClientSession session;

    public PlayerPane(Card firstCard, Card secondCard, ClientSession session) {
        this.firstCard = firstCard;
        this.secondCard = secondCard;
        this.session = session;
        this.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        this.checkBtn = new JButton("Check");
        this.checkBtn.addActionListener(e -> {
            if (((JButton) e.getSource()).getText().equals("Call")) {
                int checkAmount = Integer.parseInt(
                    session.getObservable().getLastAction().getValue().replaceAll("[^\\d.]", "") // remove "Bet " from action
                );
                session.sendBetMessage(checkAmount);
            } else {
                session.sendCheckMessage();
            }
        });
        this.betBtn = new JButton("Bet");
        
        this.foldBtn = new JButton("Fold");
        this.foldBtn.addActionListener(e -> {
            this.session.sendFoldMessage();
        });
        
        // image shows number/suit
        this.firstCardImg = new ImageIcon(getCardImage(firstCard));
        this.secondCardImg = new ImageIcon(getCardImage(secondCard));

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 0;
        this.add(this.checkBtn, cons);

        // TODO: min bets!
        SpinnerNumberModel numberSpinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        betSpinner = new JSpinner(numberSpinnerModel);
        this.betBtn.addActionListener(e -> {
            session.sendBetMessage(numberSpinnerModel.getNumber().intValue());
        });
        cons.gridx = 1;
        this.add(betSpinner, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridy = 1;
        this.add(this.betBtn, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 2;
        cons.gridy = 0;
        this.add(this.foldBtn);
        
        this.firstCardSlot = new JLabel(this.firstCardImg);
        this.secondCardSlot = new JLabel(this.secondCardImg);
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 2;
        cons.gridwidth = 2;
        this.add(this.firstCardSlot, cons);

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 1;
        cons.gridy = 2;
        cons.gridwidth = 2;
        this.add(this.secondCardSlot, cons);
        this.setBackground(new Color(12, 107, 17));
    }

    private Image getCardImage(Card card) {
        int cardNumber = card.rank();
        String cardLetter = card.suitStr().toString().substring(0,1);
        if(cardNumber ==14){
            cardNumber =1;
        }
        System.out.println("card: "+ cardNumber +" "+ cardLetter);
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

    public void actionAfterBet() {
        this.checkBtn.setText("Call");
        this.betBtn.setText("Raise");
    }

    public void actionAfterCheck() {
        this.checkBtn.setText("Check");
        this.betBtn.setText("Bet");
    }

    public void play() {
        this.checkBtn.setEnabled(true);
        this.foldBtn.setEnabled(true);
        this.betBtn.setEnabled(true);
    }

    public void stop() {
        this.checkBtn.setEnabled(false);
        this.foldBtn.setEnabled(false);
        this.betBtn.setEnabled(false);
    }
}