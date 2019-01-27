package wavbmp;

/**
 * 
 * Java file containing miscellaneous methods that are used by the main program WavBmp.java
 * 
 */

import javax.swing.JPanel;
import wavbmp.Bresenham;
import java.awt.image.BufferedImage;

public class Helper extends JPanel{

    private static final double SIXTEENBIT = 32768.0;
    private static final double BRIGHTNESS = 1.5;
    private static final int EIGHTBIT = 256;
    // Bound the brightness when we are increasing it or decreasing it by a value such as 1.5
    public int preventUnderOverFlow(int value) {
        
        if (value >= EIGHTBIT -1 ) {
            return EIGHTBIT - 1;
        } else if (value <= 0) {
            return 0;
        } else {
            // return the original value if no underflow or overflow.
            return value;
        }
    }
    // returns a BMP that has been through ordered dithering
    public int[][] orderedDither(int[][] ditherMatrix, int n, int[][] bmp, BmpFile myBmpFile) {
        int red, green, blue, luminance, result;
        System.out.println("N is " + n);
        int[][] ditheredBmp = new int[(int) myBmpFile.height][(int) myBmpFile.width];
        int i, j;
        for (int x = 0; x < myBmpFile.width; x++) {
            for (int y = 0; y < myBmpFile.height; y++) {
                i = x % n;
                j = y % n;
                red = (int) (((bmp[x][y] >> 16) & 0xFF) * BRIGHTNESS);
                green = (int) (((bmp[x][y] >> 8) & 0xFF) * BRIGHTNESS);
                blue = (int) (((bmp[x][y]) & 0xFF) * BRIGHTNESS);

                luminance = (int) (0.299 * (red)) + (int) (0.587 * (green)) + (int) (0.114 * (blue));
                result = (int) (luminance / (EIGHTBIT / (n * n + 1)));
                if (result > ditherMatrix[i][j]) {
                    ditheredBmp[x][y] = 0;
                } else {
                    ditheredBmp[x][y] = 1;
                }
            }
        }
        return ditheredBmp;
    }
    // Returns an array with the number of pixels with the values between 0-255 for the histogram
    public int[] countHistogram(int[][] arr, int offset, BmpFile myBmpFile) {
        int[] histogramVal = new int[EIGHTBIT];
        for (int u = 0; u < myBmpFile.width; u++) {
            for (int t = 0; t < myBmpFile.height; t++) {
                int channel = (arr[u][t] >> offset) & 0xFF;
                histogramVal[channel] += 1;
            }
        }
        return histogramVal;
    }
    // Draws the histogram of a certain color channel
    public void drawHistogram(int[][] arr, int offset, int color, BmpFile myBmpFile, BufferedImage canvas, SampleLine sampleLine) {
        Bresenham line = new Bresenham();
        int[] histogram = countHistogram(arr, offset, myBmpFile);
        int g = 0;
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > g) {
                g = histogram[i];
            }
        }
        
        for (int k = 0; k < histogram.length; k++) {
            sampleLine.x1 = mapRange(k, 0, EIGHTBIT, 10, 780);
            sampleLine.y1 = mapRange(histogram[k], 0, g, 10, 380);
            sampleLine.x2 = mapRange((k), 0, EIGHTBIT, 10, 780);
            sampleLine.y2 = mapRange(0, 0, g, 10, 380);
            line.drawBresenham(sampleLine.x1, 400-sampleLine.y1, sampleLine.x2, 400-sampleLine.y2, color, canvas);
        }

        repaint();
    }
    // Important method for mapping the range of what I am wanting to draw to the range of the drawing canvas/gui window
    public int mapRange(double x, int x1, int y1, int x2, int y2) {

        return (int) (Math.ceil(((x - x1) / (y1 - x1) * (y2 - x2) + x2)));

    }
    // Convert bytes to int
    public static int convertBytesToInt(byte[] b, int pos) {

        long low = b[pos] & 0xff;
        long high = b[pos + 1];
        return (int) (high << 8 | low);
    }
    // Converts bytes to long
    public static long convertBytesToLong(byte[] b, int pos, int length) {

        byte[] arr = new byte[length];
        int index = 0;
        for (int i = pos; i < (pos + length); i++)
        {
            arr[index] = b[i];
            index++;
        }
        index = 0;
        long result = 0;
        for (int i = 0; i < 32; i += 8) {
            result = result | ((long) (arr[index] & 0xff)) << i;
            index++;
        }
        System.out.println("Byte to long: " + result);
        return result;
    }
    // Normalizes the data specifically for 16bit pixels, but can be easily changed to be more general in future project
    public static double[] normalizeData(byte[] b, int pos, int length) {

        double result[] = new double[(((length)) + 1)];
        int g = 0;
        for (int i = pos; i < b.length; i += 2) {
            result[g] = (convertBytesToInt(b, i) / SIXTEENBIT); // Data is 16bits (-32678 to 32677) according to professor Jiang which is why I divide by 32678 to normalize data
            g += 1;
        }

        return result;
    }

    public int getMaxSampleValue(byte[] b, int pos, int length) {
        int largest, temp;
        largest = 0;
        for (int i = pos; i < b.length; i += 2) {

            int test = convertBytesToInt(b, i);
            if (test > largest) {
                largest = test;
            }
        }

        return largest;
    }
}