package view;

import javax.swing.JComponent;
import javax.swing.JFrame;

import controller.CadastroClienteJF;


public class CondoTechView {

    private static void createAndShowGUI() {
    	   
    	//Create and set up the window.
        JFrame frame = new JFrame("Cadastro de clientes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new CadastroClienteJF();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
	
}
