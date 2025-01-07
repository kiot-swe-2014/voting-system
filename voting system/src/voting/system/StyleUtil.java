/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package voting.system;

/**
 *
 * @author NUREDIN
 */

import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;

public class StyleUtil {

    public static final Color PRIMARY_COLOR = new Color(9, 16, 87);
    public static final Color SECONDARY_COLOR = new Color(2, 76, 170);
    public static final Color ACCENT_COLOR = new Color(236, 131, 5);
    public static final Color TEXT_COLOR = new Color(219, 211, 211);

    public static final Font HEADER_FONT = new Font("Rockwell", Font.BOLD, 24);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    public static void stylePanel(JPanel panel, Color backgroundColor) {
        panel.setBackground(backgroundColor);
    }
}

