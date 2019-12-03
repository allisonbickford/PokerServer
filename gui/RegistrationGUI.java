package gui;

import javax.swing.*;
import java.awt.*;
import java.net.UnknownHostException;

import server.ClientSession;

public class RegistrationGUI extends JFrame {
    private JPanel regPanel = new JPanel();
    private JLabel cHostName, cPort, username,regText;
    private JTextField cHostN_field,cPort_field, username_field;
    private JButton connect;

    public RegistrationGUI() {
        //initialize fields
        cHostN_field = new JTextField("localhost", 20);
        cPort_field = new JTextField("1200", 5);
        username_field = new JTextField(20);
        //initialize labels
        cHostName = new JLabel("Central Server Hostname: ");
        cPort = new JLabel("Central Server Port Number: ");
        username = new JLabel("Username");
        regText = new JLabel("Register into a Central Server");
        //initialize buttons
        connect = new JButton("Connect");

        this.setBounds(200, 200, 640, 480);
        this.regPanel.setLayout(new GridBagLayout());
        GridBagConstraints regCons = new GridBagConstraints();
        this.setLayout(new GridBagLayout());
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
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        this.add(this.regPanel, regCons);
        this.setTitle("Project 3");
        this.pack();
        this.setSize(250, 250);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.connect.addActionListener(e -> {
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
                        new ClientSession(
                            this.username_field.getText(),
                            this.cHostN_field.getText()
                        );
                        this.setVisible(false);
                    } catch (UnknownHostException uhe) {
                        JOptionPane.showMessageDialog(
                            new JFrame(), 
                            "Please check your server hostname.",
                            "Invalid Host", JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
    }
}
