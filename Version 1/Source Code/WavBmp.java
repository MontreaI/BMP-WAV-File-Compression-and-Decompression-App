package wavbmp;

/**
 *  Read .wav file and draw the waveform on the screen...This is also the main entry point of the program
 * 
 * Requirements:
 * 
 *  - "open file" dialog box for loading wave file
 *  - Show total number of samples
 *  - Show maximum value among the samples in the wavefile
 * 
 * Commands to create an executable JAR called WavBmp.jar
 * 
 * javac -d ./ WavBmp.java Bresenham.java Helper.java
 * jar cfe WavBmp.jar wavbmp.WavBmp wavbmp/*.class
 **/

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.scene.layout.Border;
//import sun.net.www.content.audio.wav;

import javax.swing.JFileChooser;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.*;

class WavFile {
    public int bitsPerSample = 34; // 34-35
    public int blockAlign = 32; // 32-33 numChannels * BitsPerSample/8
    public long sampleRate = 24; // 24-27
    public int numChannels = 22; // assumed to be mono for project
    public static final int dataChunk = 44;
    public long subChunk2Size = 40;
    public long numSamples = 0;
    // use to store shit
}

class BmpFile {
    public int bitsPerPixel = 28; // 2 byes
    public long width = 18; // 4 bytes
    public long height = 22; // 4 bytes
    public int fileSize = 2; // 4 bytes
    public int imageSize = 34; // 4 bytes
    public long dataOffSet = 10; // 4 bytes
    public long numPadding = 0;
}

class SampleLine {
    public int x1 = 0;
    public int y1 = 0;
    public int x2 = 0;
    public int y2 = 0;
}

public class WavBmp extends JPanel {

    private static final int JFRAME_W = 800;
    private static final int JFRAME_H = 600;
    private static final int CONTENT_PANEL_W = 770;
    private static final int CONTENT_PANEL_H = 470;
    private static final int CONTENT_PADDING = 50; // 50 pixels
    private static int reFresh = 0;
    private static BufferedImage canvas;
    private static int[][] ditherMatrix = { { 0, 23, 2, 10, 26 }, { 12, 22, 14, 6, 17 }, { 3, 11, 1, 9, 24 },{ 15, 7, 13, 5, 19 }, { 18, 20, 21, 4, 8 } };

    private static JButton openFile;
    private static JButton nextBtn;
    private static JPanel wavFormPanel;
    private static WavFile myWavFile;
    private static BmpFile myBmpFile;
    private static SampleLine sampleLine;
    private int[][] bmpColorArray;
    private int[][] grayScale;
    private static JLabel sampleLabel;
    private static JLabel valueLabel;
    private static JFrame audioFrame;
    private Helper helper;
    private Bresenham line;
    private static int widthCenter;
    private static int heightCenter;

    // Constructor
    public WavBmp(int width, int height) {
        helper = new Helper();
        line = new Bresenham();
        openFile = new JButton("Open File...");
        nextBtn = new JButton("Next...");
        nextBtn.setEnabled(false);
        openFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openFileBrowser();
            }
        });
        // Code to handle the repainting/refreshing of images required for question 3 of assignment. It uses switch/case to do it.
        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas = new BufferedImage(audioFrame.getWidth(), audioFrame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                switch (reFresh) {
                // Original image untouched loaded to screen
                case 0:
                    for (int y = 0; y < myBmpFile.height; y++) {
                        for (int x = 0; x < (myBmpFile.width); x++) {
                            canvas.setRGB(x + widthCenter, (int) myBmpFile.height - 1 - y + CONTENT_PADDING,
                                    bmpColorArray[x][(int) myBmpFile.height - 1 - y]);
                        }
                    }
                    repaint();
                    reFresh += 1;
                    break;
                    // Case 1 to 3 draws the histograms of the RGB channels
                case 1:
                    helper.drawHistogram(bmpColorArray, 16, (Color.RED).getRGB(), myBmpFile, canvas, sampleLine);
                    reFresh += 1;
                    repaint();
                    break;
                case 2:
                    helper.drawHistogram(bmpColorArray, 8, (Color.GREEN).getRGB(), myBmpFile, canvas, sampleLine);
                    reFresh += 1;
                    repaint();
                    break;
                case 3:
                    helper.drawHistogram(bmpColorArray, 0, (Color.BLUE).getRGB(), myBmpFile, canvas, sampleLine);
                    reFresh += 1;
                    repaint();
                    break;
                case 4:
                    // Make the original image 1.5 times brighter...
                    int red, green, blue;
                    Color colour;
                    for (int y = 0; y < myBmpFile.height; y++) {
                        for (int x = 0; x < (myBmpFile.width); x++) {
                            red = (int) (((bmpColorArray[x][(int) myBmpFile.height - 1 - y] >> 16) & 0xFF) * 1.5);
                            green = (int) (((bmpColorArray[x][(int) myBmpFile.height - 1 - y] >> 8) & 0xFF) * 1.5);
                            blue = (int) (((bmpColorArray[x][(int) myBmpFile.height - 1 - y]) & 0xFF) * 1.5);
                            colour = new Color(helper.preventUnderOverFlow(red), helper.preventUnderOverFlow(green), helper.preventUnderOverFlow(blue));
                            canvas.setRGB(x + widthCenter, (int) myBmpFile.height - y + CONTENT_PADDING, colour.getRGB());
                        }
                    }
                    repaint();
                    reFresh += 1;
                    break;
                case 5:
                   // Creates the grayscale image of the loaded BMP
                    for (int y = 0; y < myBmpFile.height; y++) {
                        for (int x = 0; x < (myBmpFile.width); x++) {
                            canvas.setRGB(x + widthCenter, (int) myBmpFile.height - 1 - y + CONTENT_PADDING,
                                    grayScale[x][(int) myBmpFile.height - 1 - y]);
                        }
                    }
                    repaint();
                    reFresh += 1;
                    break;
                case 6:
                    // This code creates the dithered image of the grayscle image
                    int[][] ditheredImaged = helper.orderedDither(ditherMatrix, 5, grayScale, myBmpFile);
                    for (int y = 0; y < myBmpFile.height; y++) {
                        for (int x = 0; x < (myBmpFile.width); x++) {
                            if (ditheredImaged[x][(int) myBmpFile.height - 1 - y] == 1) {
                                canvas.setRGB(x + widthCenter, (int) myBmpFile.height - 1 - y + CONTENT_PADDING,
                                        Color.BLACK.getRGB());
                            }
                        }
                    }
                    repaint();
                    reFresh = 0;
                    break;
                }
            }
        });
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
    /* 
    This code primarily deals with opening a file browser dialog for users to select a BMP or WAV file and it also handles the initial drawing of the loaded image.
    Please refer to the switch statement inside the actionlistner code for the implentation of refreshing the screen with different pictures according to question 3 of assignment.
    */
    public void openFileBrowser() {
        int color = (Color.RED).getRGB();
        final JFileChooser fc = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("File", "wav", "bmp");
        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            reFresh = 0;
            canvas = new BufferedImage(audioFrame.getWidth(), audioFrame.getHeight(), BufferedImage.TYPE_INT_ARGB);
            File selectedFile = fc.getSelectedFile();
            byte[] wav = new byte[(int) selectedFile.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                fileInputStream.read(wav);
                fileInputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            int fileDesripter = (int) (wav[0] & 0xff);
            sampleLine = new SampleLine();
            if (fileDesripter == 82) { // Check if it is a WAV file where 82 tells us it is a RIFF file because 82 in ASCII is "R"
                nextBtn.setEnabled(false);
                myWavFile = new WavFile();

                // Grab the WAV header information and store it into an object...
                myWavFile.bitsPerSample = (int) (wav[myWavFile.bitsPerSample] & 0xff);
                myWavFile.blockAlign = helper.convertBytesToInt(wav, myWavFile.blockAlign);
                myWavFile.numChannels = helper.convertBytesToInt(wav, myWavFile.numChannels);
                myWavFile.sampleRate = helper.convertBytesToLong(wav, (int) myWavFile.sampleRate, 4);
                myWavFile.subChunk2Size = helper.convertBytesToLong(wav, (int) myWavFile.subChunk2Size, 4);
                myWavFile.numSamples = myWavFile.subChunk2Size / (myWavFile.numChannels * (myWavFile.bitsPerSample / 8));

                double[] normalizedData = helper.normalizeData(wav, myWavFile.dataChunk, (int) myWavFile.numSamples);
                
                int maxSampleValue = helper.getMaxSampleValue(wav, myWavFile.dataChunk, (int) myWavFile.numSamples);

                System.out.println("Width is " + wavFormPanel.getWidth());
                // The main code to draw the loaded WAV file wavform, the for loop contains the logic.
                for (int i = 0; i < normalizedData.length - 1; i++) {
                    // We create two points, "(x1, y1)" and "(X2, y2)" and then we connect both points into a line.
                    sampleLine.x1 = helper.mapRange(normalizedData.length - 1- i, 0, normalizedData.length, wavFormPanel.getWidth() - CONTENT_PADDING, CONTENT_PADDING);
                    sampleLine.y1 = helper.mapRange(normalizedData[i], -1, 1, CONTENT_PADDING, wavFormPanel.getHeight() - CONTENT_PADDING);
                    sampleLine.x2 = helper.mapRange(normalizedData.length - 1 - i + 1, 0, normalizedData.length, wavFormPanel.getWidth() - CONTENT_PADDING, CONTENT_PADDING);
                    sampleLine.y2 = helper.mapRange(normalizedData[i + 1], -1, 1, CONTENT_PADDING, wavFormPanel.getHeight() - CONTENT_PADDING);
                    line.drawBresenham(sampleLine.x1, sampleLine.y1, sampleLine.x2, sampleLine.y2, color, canvas);
                }
                sampleLabel.setText("Number of Samples: " + myWavFile.numSamples);
                valueLabel.setText("Maximum Sample Value: " + maxSampleValue);

            } else { // BMP file
                sampleLabel.setText("Number of Samples: N/A");
                valueLabel.setText("Maximum Sample Value: N/A");
                nextBtn.setEnabled(true);

                reFresh = 1;
                myBmpFile = new BmpFile();
                // Grab the BMP header information and store it into an object...
                myBmpFile.bitsPerPixel = helper.convertBytesToInt(wav, myBmpFile.bitsPerPixel);
                myBmpFile.width = helper.convertBytesToLong(wav, (int) myBmpFile.width, 4);
                myBmpFile.height = helper.convertBytesToLong(wav, (int) myBmpFile.height, 4);
                myBmpFile.numPadding = (myBmpFile.width * 3) % 4;
                myBmpFile.dataOffSet = helper.convertBytesToLong(wav, (int) myBmpFile.dataOffSet, 4);
                
                if (myBmpFile.height > CONTENT_PANEL_H) {
                    audioFrame.setMinimumSize(
                            new Dimension(JFRAME_H + (int) myBmpFile.height - CONTENT_PANEL_H, JFRAME_W));
                }
                // Calculate center to attempt to center the image being drawn to display
                widthCenter = ((audioFrame.getBounds().width) / 2) - ((int) myBmpFile.width / 2);
                heightCenter = ((wavFormPanel.getBounds().height) / 2) - (int) (myBmpFile.height * 0.4);
                // Initializations for arrays and canvas to draw on
                canvas = new BufferedImage(audioFrame.getWidth(), audioFrame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                bmpColorArray = new int[(int) myBmpFile.height][(int) myBmpFile.width];
                grayScale = new int[(int) myBmpFile.height][(int) myBmpFile.width];

                int red, blue, green, luminance;
                Color grayScalePixel, pixelColor;
                int x = 0;
                if (myBmpFile.numPadding == 0) {
                    for (int y = 0; y < myBmpFile.height; y++) {
                        for (int j = 0; j < (myBmpFile.width); j++) {
                            //Grab the blue, green, and red color channels which are 3 consecutive bytes
                            blue = (int) (wav[(int)myBmpFile.dataOffSet  + x] & 0xff);
                            green = (int) (wav[(int)myBmpFile.dataOffSet  + x + 1] & 0xff);
                            red = (int) (wav[(int)myBmpFile.dataOffSet  + x + 2] & 0xff);
                            pixelColor = new Color(red, green, blue);
                            // This is the matrix operations needed to get Y when you have all three color channels R, G, B.
                            luminance = (int) (0.299 * (red)) + (int) (0.587 * (green)) + (int) (0.114 * (blue));
                            // Store the "Y / luminance" of the pixel and the colors of each pixel into seperate 2D arrays for later use.
                            grayScalePixel = new Color(luminance, luminance, luminance);
                            grayScale[j][(int) myBmpFile.height - 1 - y] = grayScalePixel.getRGB();
                            bmpColorArray[j][(int) myBmpFile.height - 1 - y] = pixelColor.getRGB();
                            // Sets pixel of original image.
                            canvas.setRGB(j + widthCenter, (int) myBmpFile.height - y + CONTENT_PADDING,
                                    pixelColor.getRGB());
                            x += 3;
                        }
                    }
                }
            }
            repaint();
        }
    }
    // Need to override this so that repaint() works to show the BufferedImage canvas I drew on
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }
    // Adds the components such as JPanel, buttons, and labels onto the GUI
    public static void addComponentsToPane(Container pane) {
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        wavFormPanel = new WavBmp(JFRAME_W, JFRAME_H - 100);
        wavFormPanel.setLayout(new BorderLayout());
        TitledBorder border = new TitledBorder("WAV / BMP ");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        wavFormPanel.setBorder(border);
        c.weightx = 1.0;
        c.weighty = 0.7;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.ipady = 500;
        wavFormPanel.add(nextBtn, BorderLayout.PAGE_END);
        pane.add(wavFormPanel, c);

        EmptyBorder labelBorder = new EmptyBorder(10, 0, 0, 0);
        sampleLabel = new JLabel("Number of Samples: N/A");
        sampleLabel.setBorder(labelBorder);
        valueLabel = new JLabel("Maximum Sample Value: N/A");
        // valueLabel.setBorder(labelBorder);

        JPanel detailPanel = new JPanel();

        detailPanel.setLayout(new BorderLayout());
        detailPanel.add(openFile, BorderLayout.PAGE_END);
        detailPanel.add(sampleLabel, BorderLayout.PAGE_START);
        detailPanel.add(valueLabel, BorderLayout.CENTER);
        border = new TitledBorder("Details");
        detailPanel.setBorder(border);
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 100; // make this component tall
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.3;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        pane.add(detailPanel, c);
    }

    private static void createAndShowGUI() {

        audioFrame = new JFrame("WavBmp");
        audioFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(audioFrame.getContentPane());
        audioFrame.setMinimumSize(new Dimension(JFRAME_W, JFRAME_H));
        audioFrame.setVisible(true);

    }

    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}