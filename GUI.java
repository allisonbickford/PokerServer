import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.*;

class GUI extends JFrame implements ActionListener{
    JPanel panel = new JPanel();
    JPanel regPanel = new JPanel();
    private ClientSession clientSession;
    private JLabel cHostName, cPort, username,regText;
    private JTextField cHostN_field,cPort_field,username_field;
    private JButton connect;
    JFrame frame;

    public GUI() {
        super("Poker");
        this.panel.setBackground(new Color(12, 107, 17));
        this.panel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        Deck deck = new Deck();
        PlayerPane player1 = new PlayerPane(true, deck.draw(), deck.draw());
        panel.add(player1);
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 3;
        cons.gridy = 5;
        cons.anchor = GridBagConstraints.PAGE_END;
        cons.gridwidth = 2;
        this.panel.add(player1, cons);

        PlayerPane player2 = new PlayerPane(false, deck.draw(), deck.draw());
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridx = 0;
        cons.gridy = 0;
        this.panel.add(player2, cons);
        
        this.add(this.panel);
        this.setBounds(200, 200, 640, 480);
       // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeRegGUI();
        //this.setVisible(true);
    }

    public void initializeRegGUI(){
         //initialize fields
         cHostN_field = new JTextField(20);
         cPort_field = new JTextField(5);
         username_field = new JTextField(20);
         //initialize labels
         cHostName = new JLabel("Central Server Hostname: ");
         cPort = new JLabel("Central Server Port Number: ");
         username = new JLabel("Username");
         regText = new JLabel("Register into a Central Server");
         //initialize buttons
         connect = new JButton("Connect");
 
         //this.regPanel.setBackground(new Color(12, 107, 17));
         this.regPanel.setLayout(new GridBagLayout());
         GridBagConstraints regCons = new GridBagConstraints();
         frame = new JFrame();
         frame.setLayout(new GridBagLayout());
        // frame.setBackground(new Color(12, 107, 17));
         regCons.fill = GridBagConstraints.HORIZONTAL;
         regCons.gridx = 1;
         regCons.gridy = 1;
         regCons.anchor = GridBagConstraints.PAGE_END;
         regCons.gridwidth = 1;
         this.regPanel.add(regText, regCons);
 
         regCons.gridx = 1;
         regCons.gridy=4;
         this.regPanel.add(cHostName, regCons);
 
         regCons.gridx = 1;
         regCons.gridy=5;
         this.regPanel.add(cHostN_field, regCons);
 
       
         regCons.gridx = 1;
         regCons.gridy = 7;
         this.regPanel.add(cPort, regCons);
 
         regCons.gridx = 1;
         regCons.gridy = 8;
         this.regPanel.add(cPort_field, regCons);
 
         regCons.gridx = 1;
         regCons.gridy = 10;
         this.regPanel.add(username, regCons);
 
         regCons.gridx = 1;
         regCons.gridy = 11;
         this.regPanel.add(username_field, regCons);
 
         regCons.gridx = 1;
         regCons.gridy = 13;
         this.regPanel.add(connect, regCons);
 
         this.add(this.regPanel);
         this.setBounds(200, 200, 640, 480);
     //    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       //  this.setVisible(true);
         
         frame.add(this.regPanel,regCons);
         frame.setTitle("Project 3");
         frame.pack();
         frame.setSize(250, 250);
         frame.setVisible(true);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         connect.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        JComponent pressed = (JComponent) e.getSource();
        //if connect run gui and create a Host and central server
        if (pressed == this.connect) {
            if(this.connect.getText() == "Connect"){
                if (this.cHostN_field.getText().isEmpty() ||
                    this.cPort_field.getText().isEmpty() ||
                    this.username_field.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        new JFrame(), 
                        "Please make sure you've filled in both hostnames and your username.",
                        "Invalid Inputs", JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    
                    try {
                        this.clientSession = new ClientSession(
                            this.username_field.getText(),
                            this.cHostN_field.getText()
                        );
                        this.cHostN_field.setEditable(false);
                        this.cPort_field.setEditable(false);
                        this.username_field.setEditable(false);
                        this.connect.setText("Disconnect");
                        frame.getContentPane().removeAll();
                        frame.getContentPane().add(this.panel);
                        frame.pack();
                      
                        
                    } catch (UnknownHostException uhe) {
                        JOptionPane.showMessageDialog(
                            new JFrame(), 
                            "Please check your server hostname.",
                            "Invalid Host", JOptionPane.ERROR_MESSAGE
                        );
                    }
                    
                    System.out.println("Connected");
                    
                }
            

        }else{
            this.clientSession.close();
            this.cHostN_field.setEditable(true);
            this.cPort_field.setEditable(true);
            this.username_field.setEditable(true);
            this.connect.setText("Connect");
        }
    }

    }


}