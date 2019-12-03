package gui;


/**********************************************************************
Main class used to launch the program and begin playing the chess game.
The main method of this class launches a new graphical user interface
object that will begin setting up the poker game.

@author Allison Bickford
@author R.J. Hamilton
@author Johnathon Kileen
@author Michelle Vu
@version December 2019
**********************************************************************/
public class PlayPoker {
    public static void main(String args[]) {
        System.out.println("Welcome to Poker!");
        new gui.GUI();
    }
}
