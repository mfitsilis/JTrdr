package Jtrdr;

import javax.swing.JOptionPane;

/**
 * Displays a message box - can be useful for debugging too
 */
public class MessageBox
{
	/**
	 * @param s1 message
	 * @param s2 title
	 */
    public MessageBox(String s1, String s2) { //message, title
    	JOptionPane.showMessageDialog(null, s1, s2, JOptionPane.INFORMATION_MESSAGE);
	}
}