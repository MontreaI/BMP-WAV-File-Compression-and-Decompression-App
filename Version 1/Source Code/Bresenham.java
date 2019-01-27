package wavbmp;

/**
 * 
 * Java file to draw lines, specifically used for the wavforms of thw WAV files...
 * 
 * This code has been ADAPTED from my previous C++ course -
 * Intro to Graphics - CMPT361 with Tom Shermer. 
 * 
 */

import javax.swing.JPanel;
import java.awt.image.BufferedImage;

public class Bresenham extends JPanel {

    private static final int INCREMENT = 1;
    private static final int DECREMENT = -1;

    public void drawBresenham(int x1, int y1, int x2, int y2, int color, BufferedImage canvas) {
        canvas.setRGB(x1, y1, color);

        int temp, y_increment, x_increment;
        int flag = 0; // Tells you if we need to plot with 'x' and 'y' swapped...
        int dx = (x2 - x1);
        int dy = (y2 - y1);
        y_increment = x_increment = INCREMENT;

        if (Math.abs(dx) >= Math.abs(dy)) {
            if ((x2 - x1) >= 0) {
                if ((y2 - y1) < 0) {
                    y_increment = DECREMENT;
                }
            } else if ((x2 - x1) <= 0) {
                x_increment = DECREMENT;
                if ((y2 - y1) < 0) {
                    y_increment = DECREMENT;
                }
            }
        } else if (Math.abs(dy) > Math.abs(dx)) {
            if ((x2 - x1) > 0) {
                if ((y2 - y1) < 0) {
                    x_increment = DECREMENT;
                }
            } else if ((x2 - x1) < 0) {
                y_increment = DECREMENT;
                if ((y2 - y1) < 0) {
                    x_increment = DECREMENT;
                }
            } else {
                if (dx == 0) {
                    if (dy < 0) {
                        x_increment = DECREMENT;
                    }
                }
            }
            temp = x2;
            x2 = y2;
            y2 = temp;

            temp = x1;
            x1 = y1;
            y1 = temp;

            dx = (x2 - x1);
            dy = (y2 - y1);
            flag = 1;
        }

        int base = 2 * (Math.abs(dy)) - (Math.abs(dx));
        int diagonal = 2 * (Math.abs(dy)) - 2 * (Math.abs(dx));
        int horizontal = 2 * (Math.abs(dy));
        int p_n = base;

        int y = y1;
        int x = x1;
        while (x != (x2)) {
            if ((p_n < 0)) {
                p_n = p_n + horizontal;
                x = x + x_increment;
            } else {
                x = x + x_increment;
                y = y + y_increment;
                p_n = p_n + diagonal;

            }
            if (flag == 0) {
                canvas.setRGB(x, y, color);
            } else {
                canvas.setRGB(y, x, color);
            }
        }
    }

}