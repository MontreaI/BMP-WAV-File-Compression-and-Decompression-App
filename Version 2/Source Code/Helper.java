package wavbmp;
import java.util.BitSet;
/**
 * 
 * Java file containing miscellaneous methods that are used by the main program WavBmp.java
 * 
 */

import javax.swing.JPanel;

import wavbmp.Bresenham;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.ByteBuffer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Node {
    Node leftNode, rightNode;
    byte sample;
    int sampleCount, parentIdentifier;
}


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

/*
class EncodedString {

}
*/
public class Helper extends JPanel{

    //private int[] zigzag = {0, 1, 8, 16, 9, 2, 3, 10, 17, 24, 32, 25, 18, 11, 4, 5, 12, 19, 26, 33, 40, 48, 41, 34, 27, 20, 13, 6, 7, 14, 21, 28, 35, 42, 49, 56, 57, 50, 43, 36, 29, 22, 15, 23, 30, 37, 44, 51, 58, 59, 52, 45, 38, 31, 34, 39, 46, 53, 60, 61, 54, 47, 55, 62, 63};
    // Matrix is used for quantization of luminance values
    private static double[][] luminanceMatrix = {
        {16, 11, 10, 16, 24, 40, 51, 61},
        {12, 12, 14, 19, 26, 58, 60, 55},
        {14, 13, 16, 24, 40, 57, 69, 56},
        {14, 17, 22, 29, 51, 87, 80, 62},
        {18, 22, 37, 56, 68, 109, 103, 77},
        {24, 35, 55, 64, 81, 104, 113, 92},
        {49, 64, 78, 87, 103, 121, 120, 101},
        {72, 92, 95, 98, 112, 100, 103, 99}
    };
    // Matrix is used for quantization of chrominance values
    private static double[][] chrominanceMatrix = {
        {17, 18, 24, 47, 99, 99, 99, 99},
        {18, 21, 26, 66, 99, 99, 99, 99},
        {24, 26, 56, 99, 99, 99, 99, 99},
        {47, 66, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 99, 99, 99, 99, 99},
        {99, 99, 99, 99, 99, 99, 99, 99}
    };
    // DCT matrix
    private static double[][] dctMatrix = {
        {1/(2*Math.sqrt(2)), 1/(2*Math.sqrt(2)), 1/(2*Math.sqrt(2)), 1/(2*Math.sqrt(2)), 1/(2*Math.sqrt(2)),  1/(2*Math.sqrt(2)),  1/(2*Math.sqrt(2)),  1/(2*Math.sqrt(2))},
        {(0.5)*Math.cos(Math.PI/16), (0.5)*Math.cos(3*Math.PI/16), (0.5)*Math.cos(5*Math.PI/16), (0.5)*Math.cos(7*Math.PI/16), (0.5)*Math.cos(9*Math.PI/16),  (0.5)*Math.cos(11*Math.PI/16),  (0.5)*Math.cos(13*Math.PI/16),  (0.5)*Math.cos(15*Math.PI/16)},
        {(0.5)*Math.cos(2*Math.PI/16), (0.5)*Math.cos(6*Math.PI/16), (0.5)*Math.cos(10*Math.PI/16), (0.5)*Math.cos(14*Math.PI/16), (0.5)*Math.cos(18*Math.PI/16),  (0.5)*Math.cos(22*Math.PI/16),  (0.5)*Math.cos(26*Math.PI/16),  (0.5)*Math.cos(30*Math.PI/16)},
        {(0.5)*Math.cos(3*Math.PI/16), (0.5)*Math.cos(9*Math.PI/16), (0.5)*Math.cos(15*Math.PI/16), (0.5)*Math.cos(21*Math.PI/16), (0.5)*Math.cos(27*Math.PI/16),  (0.5)*Math.cos(33*Math.PI/16),  (0.5)*Math.cos(39*Math.PI/16),  (0.5)*Math.cos(45*Math.PI/16)},
        {(0.5)*Math.cos(4*Math.PI/16), (0.5)*Math.cos(12*Math.PI/16), (0.5)*Math.cos(20*Math.PI/16), (0.5)*Math.cos(28*Math.PI/16), (0.5)*Math.cos(36*Math.PI/16),  (0.5)*Math.cos(44*Math.PI/16),  (0.5)*Math.cos(52*Math.PI/16),  (0.5)*Math.cos(60*Math.PI/16)},
        {(0.5)*Math.cos(5*Math.PI/16), (0.5)*Math.cos(15*Math.PI/16), (0.5)*Math.cos(25*Math.PI/16), (0.5)*Math.cos(35*Math.PI/16), (0.5)*Math.cos(45*Math.PI/16),  (0.5)*Math.cos(55*Math.PI/16),  (0.5)*Math.cos(65*Math.PI/16),  (0.5)*Math.cos(75*Math.PI/16)},
        {(0.5)*Math.cos(6*Math.PI/16), (0.5)*Math.cos(18*Math.PI/16), (0.5)*Math.cos(30*Math.PI/16), (0.5)*Math.cos(42*Math.PI/16), (0.5)*Math.cos(54*Math.PI/16),  (0.5)*Math.cos(66*Math.PI/16),  (0.5)*Math.cos(78*Math.PI/16),  (0.5)*Math.cos(90*Math.PI/16)},
        {(0.5)*Math.cos(7*Math.PI/16), (0.5)*Math.cos(21*Math.PI/16), (0.5)*Math.cos(35*Math.PI/16), (0.5)*Math.cos(49*Math.PI/16), (0.5)*Math.cos(63*Math.PI/16),  (0.5)*Math.cos(77*Math.PI/16),  (0.5)*Math.cos(91*Math.PI/16),  (0.5)*Math.cos(105*Math.PI/16)}
    };
    // Transpose of the DCT matrix
    private static double[][] idcMatrix = {
        {1/(2*Math.sqrt(2)), (0.5)*Math.cos(Math.PI/16), (0.5)*Math.cos(2*Math.PI/16), (0.5)*Math.cos(3*Math.PI/16), (0.5)*Math.cos(4*Math.PI/16),(0.5)*Math.cos(5*Math.PI/16),(0.5)*Math.cos(6*Math.PI/16), (0.5)*Math.cos(7*Math.PI/16)},
        {1/(2*Math.sqrt(2)), (0.5)*Math.cos(3*Math.PI/16), (0.5)*Math.cos(6*Math.PI/16), (0.5)*Math.cos(9*Math.PI/16), (0.5)*Math.cos(12*Math.PI/16),(0.5)*Math.cos(15*Math.PI/16), (0.5)*Math.cos(18*Math.PI/16), (0.5)*Math.cos(21*Math.PI/16)},
        {1/(2*Math.sqrt(2)), (0.5)*Math.cos(5*Math.PI/16), (0.5)*Math.cos(10*Math.PI/16), (0.5)*Math.cos(15*Math.PI/16), (0.5)*Math.cos(20*Math.PI/16),(0.5)*Math.cos(25*Math.PI/16), (0.5)*Math.cos(30*Math.PI/16), (0.5)*Math.cos(35*Math.PI/16)},
        {1/(2*Math.sqrt(2)), (0.5)*Math.cos(7*Math.PI/16), (0.5)*Math.cos(14*Math.PI/16), (0.5)*Math.cos(21*Math.PI/16), (0.5)*Math.cos(28*Math.PI/16),(0.5)*Math.cos(35*Math.PI/16), (0.5)*Math.cos(42*Math.PI/16), (0.5)*Math.cos(49*Math.PI/16)},
        {1/(2*Math.sqrt(2)), (0.5)*Math.cos(9*Math.PI/16), (0.5)*Math.cos(18*Math.PI/16), (0.5)*Math.cos(27*Math.PI/16), (0.5)*Math.cos(36*Math.PI/16),(0.5)*Math.cos(45*Math.PI/16), (0.5)*Math.cos(54*Math.PI/16), (0.5)*Math.cos(63*Math.PI/16)},
        {1/(2*Math.sqrt(2)), (0.5)*Math.cos(11*Math.PI/16), (0.5)*Math.cos(22*Math.PI/16), (0.5)*Math.cos(33*Math.PI/16), (0.5)*Math.cos(44*Math.PI/16),(0.5)*Math.cos(55*Math.PI/16), (0.5)*Math.cos(66*Math.PI/16), (0.5)*Math.cos(77*Math.PI/16)},
        {1/(2*Math.sqrt(2)), (0.5)*Math.cos(13*Math.PI/16), (0.5)*Math.cos(26*Math.PI/16), (0.5)*Math.cos(39*Math.PI/16), (0.5)*Math.cos(52*Math.PI/16),(0.5)*Math.cos(65*Math.PI/16), (0.5)*Math.cos(78*Math.PI/16), (0.5)*Math.cos(91*Math.PI/16)},
        {1/(2*Math.sqrt(2)), (0.5)*Math.cos(15*Math.PI/16), (0.5)*Math.cos(30*Math.PI/16), (0.5)*Math.cos(45*Math.PI/16), (0.5)*Math.cos(60*Math.PI/16),(0.5)*Math.cos(75*Math.PI/16), (0.5)*Math.cos(90*Math.PI/16), (0.5)*Math.cos(105*Math.PI/16),}
    };

    private static BufferedImage canvas;
    private static final double SIXTEENBIT = 32768.0;
    private static final double BRIGHTNESS = 1.5;
    private static final int EIGHTBIT = 256;
    private static HashMap<Byte, String> encodeMap  = new HashMap<Byte, String>(); // byte is key, String is the code, e.g. 101001 for encoding.
    private static HashMap<String, Byte> decodeMap = new HashMap<String, Byte>();
    private static HashMap<String, String> LZWMap;
    private static StringBuilder sb;
    private static String encodedString = "";
    private static String huffmanString = "";
    private static int yu = 0;
    private static int sizers = 0;
    private static byte[] arraytemp;
    private static String decodedString = "";
    private static HashMap<Byte, Integer> dctHashMap;
    private static byte[] compressedArray;
    private static double lossyRatio;

    private static long encodeStartTime;
    private static long encodeEndTime;
    private static long decodeStartTime;
    private static long decodeEndTime;

    class HuffmanComparator implements Comparator<Node> {
        public int compare(Node x, Node y)
        {
            return x.sampleCount - y.sampleCount;
        }
    }    

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
        //System.out.println("Byte to long: " + result);
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
        int largest;
        largest = 0;
        for (int i = pos; i < b.length; i += 2) {

            int test = convertBytesToInt(b, i);
            if (test > largest) {
                largest = test;
            }
        }

        return largest;
    }

    public double getLZWCompressionRate(byte b[]){
        LZWMap = new HashMap<String, String>();
        int sizeN = 1024; // 2^12
        int value = 0;//-128;
        int tableSize = 256;
        int counter = 0;
        boolean isSkip = false;
        for(int i = 1; i <= tableSize; i++) {
            LZWMap.put(Integer.toString(value), Integer.toString(i)); 
            value++;
        }
        String codeString = Integer.toString(b[0] & 0xFF);
        String codeSymbol = "";
        for(int j = 1; j < b.length; j++) {
            if(isSkip) {
                codeString = Integer.toString(b[j] & 0xFF);
                isSkip = false;
                continue;
            }
            codeSymbol = Integer.toString(b[j] & 0xFF);
            if(LZWMap.get(codeString+codeSymbol) != null) {
               
                codeString = codeString + codeSymbol;
            }
            else {
                counter++;
                if(tableSize < sizeN) {
                    tableSize++;
                    LZWMap.put(codeString+codeSymbol, Integer.toString(tableSize));
                    
                }
                else {
                    LZWMap.clear();
                    tableSize = 256;
                    value = 0;
                    for(int i = 1; i <= tableSize; i++) {
                        LZWMap.put(Integer.toString(value), Integer.toString(i)); //255:256
                        value++;
                    }
                    tableSize++;
                    LZWMap.put(codeString+codeSymbol, Integer.toString(tableSize));
                }
                codeString = codeSymbol; // need to reset the S and C to new shit...
            }
        }
        counter+=1;
        double compressionRate = (b.length*8.0)/((counter)*(10.0));
        //System.out.println("Counter " + counter + " compressed bites " + (b.length*8) + ":" + (LZWMap.size()-256) + ":" + compressionRate);
        return compressionRate;
    }

    public static void buildCode(Node root, String s)
    {
        if (root.leftNode == null && root.rightNode == null && root.parentIdentifier == 0) {
            
            //encodedString +=s;
            sb.append(s);
            encodeMap.put(root.sample, s);
            decodeMap.put(s, root.sample);
 
            return;
        }

        buildCode(root.leftNode, s + "0");
        buildCode(root.rightNode, s + "1");
    }

    public double getHuffmanCompressionRate(byte b[], HashMap<Byte, Integer> sampleCount) {
        int totalBits = 0;
        int count = 0;
        int bits = 0;
        for(byte m : sampleCount.keySet()){
            bits = encodeMap.get(m).length();
            count = sampleCount.get(m);
            totalBits += (bits*count);
        }
        System.out.println("Total bits " + totalBits );
        System.out.println("Compression rate: " + (b.length * 8.0) / (totalBits)*1.0);
        return (b.length * 8.0) / (totalBits)*1.0;
    }

    public HashMap<Byte, Integer> getSampleCount(byte[] b, int pos) {
        HashMap<Byte, Integer> sampleCount = new HashMap<Byte, Integer>();
        byte sample;
        //System.out.println("Length of array " + b.length );
        for (int i = pos; i < b.length; i++) {
            sample = b[i];
            if(sampleCount.containsKey(sample)) {
                sampleCount.put(sample, sampleCount.get(sample) + 1);
            }
            else{
                sampleCount.put(sample, 1);
            }
        }
        System.out.println("Length of Hash " + sampleCount.size());
        return sampleCount;
    }

    public Node compressionRate(HashMap<Byte, Integer> sampleCount, byte[] wav) {
        Node root = null;
        PriorityQueue<Node> huffmanPriorityQueue = new PriorityQueue<Node>(sampleCount.size(), new HuffmanComparator());
        for(byte m : sampleCount.keySet()){
            Node node = new Node();
 
            node.sample = m;
            node.sampleCount = sampleCount.get(m);
            node.parentIdentifier = 0;
            node.leftNode = null;
            node.rightNode = null;

            huffmanPriorityQueue.add(node);
           }
           while (huffmanPriorityQueue.size() > 1) {

            Node left = huffmanPriorityQueue.peek();
            huffmanPriorityQueue.poll();
            Node right = huffmanPriorityQueue.peek();
            huffmanPriorityQueue.poll();

            Node node = new Node();
            node.sampleCount = left.sampleCount + right.sampleCount;
            node.parentIdentifier = 1;
            node.leftNode = left;
            node.rightNode = right;
 
            root = node;

            huffmanPriorityQueue.add(node);
        }
        sb = new StringBuilder();
        //encodedString = ""; // encode to bitset then to byte array then to file with the length
        buildCode(root, "");
        encodedString = sb.toString();
        return root;
    }

    public byte[] convertIntToByteArray(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    public byte[] readFromFile(String fileName) {
        byte[] array = null;
        try{
         array = Files.readAllBytes(new File(fileName).toPath());
        }catch(Exception ex) {
            // if an error occurs
            ex.printStackTrace();
         }
            return array;
    }

    public void compressHuff(HashMap<Byte, Integer> sampleCount, byte[] wav, String fName, boolean isIM3) {
        encodeStartTime = System.currentTimeMillis();
        Node root = null;
        byte fileId = 22;
        PriorityQueue<Node> huffmanPriorityQueue = new PriorityQueue<Node>(sampleCount.size(), new HuffmanComparator());
        for (byte m : sampleCount.keySet()) {
            Node node = new Node();

            node.sample = m;
            node.sampleCount = sampleCount.get(m);
            node.parentIdentifier = 0;
            node.leftNode = null;
            node.rightNode = null;

            huffmanPriorityQueue.add(node);
        }
        while (huffmanPriorityQueue.size() > 1) {

            Node left = huffmanPriorityQueue.peek();
            huffmanPriorityQueue.poll();
            Node right = huffmanPriorityQueue.peek();
            huffmanPriorityQueue.poll();

            Node node = new Node();
            node.sampleCount = left.sampleCount + right.sampleCount;
            node.parentIdentifier = 1;

            node.leftNode = left;
            node.rightNode = right;
            root = node;

            huffmanPriorityQueue.add(node);
        }
        sb = new StringBuilder();
        //encodedString = ""; // encode to bitset then to byte array then to file with the length
        buildCode(root, "");
        encodedString = sb.toString();
        StringBuilder stringBuilder = new StringBuilder();
        String encoders = "";
        for (int i = 0; i < wav.length; i++) {
            if (encodeMap.get(wav[i]) != null) {
                stringBuilder.append(encodeMap.get(wav[i]));
            }
        }
        encoders = stringBuilder.toString();
        BitSet bitSet = new BitSet(encoders.length());
        int bitcounter = 0;
        for (Character c : encoders.toCharArray()) {
            if (c.equals('1')) {
                bitSet.set(bitcounter);
            }
            bitcounter++;
        }
        byte[] encodedStringBytes = bitSet.toByteArray();

        huffmanString = "";
        buildTree(root, false); // create the preorder tree into a string, encode to bitset then to byte array
                                    // then to file with length
        bitSet = new BitSet(huffmanString.length());
        bitcounter = 0;
        for (Character c : huffmanString.toCharArray()) {
            if (c.equals('1')) {
                bitSet.set(bitcounter);
            }
            bitcounter++;
        }
        byte[] huffmanStringOriginalLength = new byte[2];
        huffmanStringOriginalLength[0] = (byte) (huffmanString.length() >> 8);
        huffmanStringOriginalLength[1] = (byte) huffmanString.length();

        byte[] huffmanStringBytes = bitSet.toByteArray();

        arraytemp = new byte[huffmanString.length()];
        buildTree(root, true);

        byte[] huffmanStringBytesLength = convertIntToByteArray(huffmanStringBytes.length); 
        byte[] huffmanTreeBytesLength = convertIntToByteArray(arraytemp.length);
        byte[] encodedSringBytesLength = convertIntToByteArray(encodedStringBytes.length);
        if(isIM3){
            fileId = 21;
        }
        byte[] fileIdentifier = { fileId };
        File fileTemp = new File(fName);
        if (fileTemp.exists()){
            fileTemp.delete();
        }
        writeToFile(fName, fileIdentifier);
        writeToFile(fName, huffmanStringBytesLength);
        writeToFile(fName, huffmanStringOriginalLength);
        writeToFile(fName, huffmanStringBytes);
        writeToFile(fName, arraytemp);
        writeToFile(fName, encodedSringBytesLength);
        writeToFile(fName, encodedStringBytes);
        sizers = 0;
        encodeEndTime = System.currentTimeMillis();
        System.out.println("@@@Compression@@@: " + (encodeEndTime - encodeStartTime));
    }
 
    public int[][] deCompressHuff(byte[] bytes, String fName, boolean isIM3) {
        decodeStartTime = System.currentTimeMillis();
        // First 4 bytes contains info on length of huffman tree data telling us which
        // is leaf or internal node
        int metaData1 = bytes[1] << 24 | (bytes[2] & 0xff) << 16 | (bytes[3] & 0xff) << 8 | (bytes[4] & 0xff);
        System.out.println("Size of first data is " + metaData1);
        byte[] data1 = new byte[metaData1];
        int dataEnd = 0; //
        int dataEnd2 = 0;
        for (int i = 0; i < data1.length; i++) {
            data1[i] = bytes[7 + i];
            dataEnd = 7 + i;
        }
        dataEnd++;
        BitSet bs = BitSet.valueOf(data1);
        String binaryString = "";
        sb = new StringBuilder();
        for (int j = 0; j < bs.length(); j++) {
            if (bs.get(j)) {
                sb.append("1");
                //binaryString += "1";
            } else {
                sb.append("0");
                //binaryString += "0";
            }
        }
        binaryString = sb.toString();
        short huffmanStringOriginalLength = (short) (bytes[5] << 8 | bytes[6] & 0xFF);
        int temp = binaryString.length();
        for (int j = 0; j < huffmanStringOriginalLength - temp; j++)
            binaryString += "0";

        byte[] data2 = new byte[huffmanStringOriginalLength];
        System.out.println("dataEnd " + dataEnd + ":" + bytes.length);
        for (int i = 0; i < huffmanStringOriginalLength; i++) {
            data2[i] = bytes[dataEnd + i];
        }
        for (int i = 0; i < huffmanStringOriginalLength; i++) {
            data2[i] = bytes[dataEnd + i];
            dataEnd2 = dataEnd + i;
        }
        dataEnd2++;

        int metaData2 = bytes[dataEnd2] << 24 | (bytes[dataEnd2 + 1] & 0xff) << 16 | (bytes[dataEnd2 + 2] & 0xff) << 8
                | (bytes[dataEnd2 + 3] & 0xff);
        dataEnd2 += 4;

        byte[] data3 = new byte[metaData2];

        for (int i = 0; i < data3.length; i++) {
            data3[i] = bytes[dataEnd2 + i];
        }
        bs = BitSet.valueOf(data3);
        sb = new StringBuilder();
        String binaryString2 = "";
        for (int j = 0; j < bs.length(); j++) {
            if (bs.get(j)) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }
        binaryString2 = sb.toString();
        yu = 0;
        Node node = preOrderTraversal(binaryString, data2);
        decodeMap.clear();
        decodedString = "";
        sb = new StringBuilder();
        buildTable(node, "");
        decodedString = sb.toString();
        int ui = 0;
        File fileTemp = new File(fName);
        if (fileTemp.exists()){
            fileTemp.delete();
        }
        if(!isIM3){
            try {
                FileOutputStream fos = new FileOutputStream(new File(fName), true);
                for (int i = 0; i <= binaryString2.length(); i++) {
                    if (decodeMap.get(binaryString2.substring(ui, i)) != null) {
                        fos.write(decodeMap.get(binaryString2.substring(ui, i)));
                        ui = i;
                    }
    
                }
                fos.flush();
                fos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            decodeEndTime = System.currentTimeMillis();
            System.out.println("@@@Decompression@@@ " + (decodeEndTime-decodeStartTime));
            return null;
        }
        else{
            int length = 0;
            for (int i = 0; i <= binaryString2.length(); i++) {
                if (decodeMap.get(binaryString2.substring(ui, i)) != null) {
                    length+=1;
                    ui = i;
                }

            }
            byte[] decodedArray = new byte[length];
            System.out.println("Length of decoded array " + decodedArray.length);
            int arrayIndexer = 0;
            ui = 0;
            for (int i = 0; i <= binaryString2.length(); i++) {
                if (decodeMap.get(binaryString2.substring(ui, i)) != null) {
                    decodedArray[arrayIndexer] = decodeMap.get(binaryString2.substring(ui, i));
                    arrayIndexer+=1;
                    ui = i;
                }

            }
            int x = 0;
            int dataOffset = (int)convertBytesToLong(decodedArray, 10, 4);
            int dWidth = (int)convertBytesToLong(decodedArray, 18, 4);
            int  dHeight = (int)convertBytesToLong(decodedArray, 22, 4);
            double[][] luminanceArr2 = new double[dHeight][dWidth];
            double[][] uArr2 = new double[dHeight][dWidth];
            double[][] vArr2 = new double[dHeight][dWidth];
            //System.out.println("dHeight * dWidth" +  dHeight*dWidth);
            for(int i = 0; i < dHeight; i++){
                for(int j = 0; j < dWidth; j++){
                    luminanceArr2[i][j] =  decodedArray[dataOffset + x];
                    uArr2[i][j] = decodedArray[(dHeight*dWidth) + dataOffset + x];
                    vArr2[i][j] = decodedArray[(2*(dHeight*dWidth)) + dataOffset + x];
                    x+=1;
                }
            }

            byte red, blue, green;
            x = 0;
            int[][] colorArray  = reverseDCT(dHeight, dWidth, luminanceArr2, uArr2, vArr2);
            for(int i = 0; i < dHeight; i++){
                for(int j = 0; j < dWidth; j++){
                    red = (byte) (((colorArray[i][j] >> 16) & 0xFF));
                    green = (byte) (((colorArray[i][j] >> 8) & 0xFF));
                    blue = (byte) (((colorArray[i][j]) & 0xFF));
                    decodedArray[dataOffset + x] = blue;
                    decodedArray[dataOffset + x + 1] = green;
                    decodedArray[dataOffset + x + 2] = red;
                    x+=3;
                }
            }
            try{
                FileOutputStream fos=new FileOutputStream(new File(fName),true);
                //for(int j =0; j < b.length; j++) {
                    fos.write(decodedArray);
                //}
                fos.flush();
                fos.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                 }
                 decodeEndTime = System.currentTimeMillis();
                 System.out.println("@@@Decompression@@@ " + (decodeEndTime-decodeStartTime));
            return colorArray;
        }

    }

    public void writeToFile(String fileName, byte[] b) {
        try{
        FileOutputStream fos=new FileOutputStream(new File(fileName),true);
        //for(int j =0; j < b.length; j++) {
            fos.write(b);
        //}
        fos.flush();
        fos.close();
        } catch(Exception ex) {
            ex.printStackTrace();
         }
    }

    public void buildTable(Node root, String s)
    {
        if (root.leftNode == null && root.rightNode == null && root.parentIdentifier == 0) {
            sb.append("s");// +=s;
            decodeMap.put(s, root.sample);
 
            return;
        }
        buildCode(root.leftNode, s + "0");
        buildCode(root.rightNode, s + "1");
    }

    

    public void buildTree(Node node, boolean data)
    {
        if (node == null)
            return;
 
        if(node.parentIdentifier == 0) {
          if(!data) {
            huffmanString += "0";
          }  
          else{
            arraytemp[sizers] = node.sample;
            sizers++;
          }
        }
        else{
            if(!data) {
                huffmanString += "1";
            }
            else{
                arraytemp[sizers] = 1;
                sizers++;
            }
            
        }
 
        buildTree(node.leftNode, data);
        buildTree(node.rightNode, data);
    } 

    public Node preOrderTraversal(String b, byte[] c){ // restore the preorder 
        Node root = new Node();
        if (b.charAt(yu) != '1') {
            root.sample = c[yu];
            root.parentIdentifier = 0;
            root.leftNode = null;
            root.rightNode = null;
            yu++;
        }
        else{
            yu++;
            root.parentIdentifier = 1;
            root.leftNode = preOrderTraversal(b, c);
            root.rightNode = preOrderTraversal(b, c);
        }
        return root;
    }

    public byte[] returnByte(String test) {
            int moduloz = test.length()%8;
            StringBuilder _sb;
            if(moduloz != 0) {
                _sb = new StringBuilder(test);
                for(int i = 0; i < (8-moduloz); i++) {
                    _sb.insert(0, "0");
                }
                test = _sb.toString();
            }
            byte tester; 
            byte[] arr = new byte[test.length()/8];
            //byte tester1 = Byte.parseByte(test, 2);
            int start, end;
            start = 0; end = 8;
            for(int j =0; j < test.length()/8; j++) {
                tester = Byte.parseByte(test.substring(start, end), 2);
                arr[j] = tester;
                start+=8;
                end+=8;
            }
            return arr;

    }
    @Override
	public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }
    
    public void newPanel(byte[] wav, String frameName){
        BmpFile myBmpFile = new BmpFile();
        // Grab the BMP header information and store it into an object...
        myBmpFile.bitsPerPixel = convertBytesToInt(wav, myBmpFile.bitsPerPixel);
        myBmpFile.width = convertBytesToLong(wav, (int) myBmpFile.width, 4);
        myBmpFile.height = convertBytesToLong(wav, (int) myBmpFile.height, 4);
        myBmpFile.numPadding = (myBmpFile.width * 3) % 4;
        myBmpFile.dataOffSet = convertBytesToLong(wav, (int) myBmpFile.dataOffSet, 4);

        JFrame frame = new JFrame(frameName);
		frame.add(new Helper());
		frame.setSize((int)myBmpFile.width+1, (int)myBmpFile.height+1);
		frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        canvas = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int red, blue, green;
        Color  pixelColor;
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
                    // Sets pixel of original image.
                    canvas.setRGB(j, (int) myBmpFile.height - y, pixelColor.getRGB());
                    x += 3;
                }
            }
        }
        //canvas = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        repaint();
    }

    public int[][] reverseDCT(int height, int width, double[][] luminanceArr, double[][] uArr, double[][] vArr){
        int[][] bmpColorArray = new int[height][width];
        double[][] luminanceArr2 = new double[8][8];
        double[][] uArr2 = new double[8][8];
        double[][] vArr2 = new double[8][8];
        double[][] temp, temp1, temp2;
        int red, blue, green, Y, U, V;
        Color pixelColor;
        for(int h = 0; h < height; h+=8) {
            for(int w = 0; w < width; w+=8) {
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++){

                        luminanceArr2[i][j] = luminanceArr[h+i][w+j];
                        uArr2[i][j] = uArr[h+i][w+j];
                        vArr2[i][j] = vArr[h+i][w+j];

                    }
                }

                temp = multiply(multiply(idcMatrix, luminanceBlock(luminanceArr2, false)), dctMatrix);
                temp1 = multiply(multiply(idcMatrix, chrominanceBlock(uArr2, false)), dctMatrix);
                temp2 = multiply(multiply(idcMatrix, chrominanceBlock(vArr2, false)), dctMatrix);
                
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++){
                        Y = (int)temp[i][j] + 128;
                        U = (int)temp1[i][j];
                        V = (int)temp2[i][j];

                        red = (int)(Y + 1.140*V);
                        green = (int)(Y - 0.395*U - 0.581*V);
                        blue = (int)(Y + 2.032*U);
                        
                        pixelColor = new Color(boundColor(red), boundColor(green), boundColor(blue));                    
                        bmpColorArray[h+i][w+j] = pixelColor.getRGB();
                    }
                }

            }
        }
        return bmpColorArray;
    }

    public int boundColor(int channel) {
        if(channel < 0){
            return 0;
        }
        if(channel > 255) {
            return 255;
        }
        return channel;
    }

    public int[][] applyDCT(byte[] wav, long dataOffset, int[][] arr, int height, int width, String fileName){

        compressedArray = new byte[wav.length];
        for(int i = 0; i < dataOffset; i++){
            compressedArray[i] = wav[i];
        }

        String binaryString2 = "";
        int red, blue, green, luminance, U, V;
        int[][] colorBlock = new int [8][8];

        double[][] luminanceArr = new double[8][8];
        double[][] uArr = new double[8][8];
        double[][] vArr = new double[8][8];

        double[][] luminanceArr2 = new double[height][width];
        double[][] uArr2 = new double[height][width];
        double[][] vArr2 = new double[height][width];
        double[][] temp, temp1, temp2, ay, by, ey, uy;
        for(int h = 0; h < height; h+=8) {
            for(int w = 0; w < width; w+=8) {
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++){
                        red = (int) (((arr[h+i][w+j]>> 16) & 0xFF));
                        green = (int) (((arr[h+i][w+j]>> 8) & 0xFF));
                        blue = (int) (((arr[h+i][w+j]) & 0xFF));

                        
                        luminance = (int) (((0.299 * (red)) + (int) (0.587 * (green)) + (int) (0.114 * (blue))));
                        U = (int) (0.492 *(blue-luminance));
                        V = (int) (0.877 *(red-luminance));

                        luminanceArr[i][j] = luminance - 128;
                        uArr[i][j] = U;
                        vArr[i][j] = V;

                    }
                }

                temp = luminanceBlock(multiply(multiply(dctMatrix, luminanceArr), idcMatrix), true);
                temp1 = chrominanceBlock(multiply(multiply(dctMatrix, uArr), idcMatrix), true);
                temp2 = chrominanceBlock(multiply(multiply(dctMatrix, vArr), idcMatrix), true);
                
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++){
                        luminanceArr2[h+i][w+j] = Math.round(temp[i][j]);
                        uArr2[h+i][w+j] = Math.round(temp1[i][j]);
                        vArr2[h+i][w+j] = Math.round(temp2[i][j]);
                    }
                }

            }
        }

        // Zigzag array and store inside??
        //[Original Data up to data][Y][U][V]]
        int count1 = appendToArray((int)dataOffset,luminanceArr2, height, width);
        int count2 = appendToArray(count1,uArr2, height, width);
        int count3 = appendToArray(count2,vArr2, height, width);

        for(int i = count3; i < wav.length; i++){
            compressedArray[i] = wav[i];
        }
    
        dctHashMap = getSampleCount(compressedArray, 0);
        compressHuff(dctHashMap, compressedArray, fileName + ".IM3", true);
        byte[] be = readFromFile(fileName + ".IM3");
        int[][] testerr  = deCompressHuff(be, fileName + ".bmp", true);
        lossyRatio = getHuffmanCompressionRate(compressedArray, dctHashMap);
        System.out.println("lossy Ratio " + lossyRatio);

       return testerr;
    }

    public double getLossyRatio() {
        return lossyRatio;
    }

    public int appendToArray(int start, double[][] arr, int height, int width){
        int pos = start;
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                compressedArray[pos] = (byte)arr[i][j];
                pos += 1;
            }
        }
        return pos;
    }

    static double[][] luminanceBlock(double[][] matrix, boolean isEncode) {
        double[][] arr = new double[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(isEncode){
                    arr[i][j] = matrix[i][j]/luminanceMatrix[i][j];
                }
                else{
                    arr[i][j] = matrix[i][j]*luminanceMatrix[i][j];
                }
            }
        }
        return arr;
    }

    static double[][] chrominanceBlock(double[][] matrix, boolean isEncode) {
        double[][] arr = new double[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(isEncode){
                    arr[i][j] = matrix[i][j]/chrominanceMatrix[i][j];
                }
                else{
                    arr[i][j] = matrix[i][j]*chrominanceMatrix[i][j];
                }
            }
        }
        return arr;
    }

    public static double[][] multiply(double[][] a, double[][] b) {
        int rowsA = a.length;
        int colsA = a[0].length; 
        int colsB = b[0].length;

        double[][] c = new double[rowsA][colsB];
        
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    c[i][j] = c[i][j] + a[i][k] * b[k][j];
                }
                c[i][j] = Math.round(c[i][j]);
            }
        }
        return c;
    }
}