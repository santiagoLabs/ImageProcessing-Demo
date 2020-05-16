//************************************
// Developed by Santiago Giraldo
//************************************

import java.io.*;
import java.util.Scanner;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.ArrayList; 
import java.lang.Math;
import java.util.Random;
import java.util.Arrays;

public class Demo extends Component implements ActionListener {
    
    //************************************
    // List of the options correspond to the cases:
    //************************************
  
    private String descs[] = {
        "Original", 
        "Negative",
        "Rescaling",
        "Shift&Rescale",
        "Addition",
        "Substraction",
        "Multiplication",
        "Division",
        "Bitwise Not",
        "Bitwise And",
        "Bitwise Or",
        "Bitwise Xor",
        "Logarithmic Transformation",
        "Power Law",
        "Random LUT",
        "Bit Plane Slicing",
        "Equalized Histogram",
        "Convolution - Averaging",
        "Convolution - Weighted averaging",
        "Convolution - Four Neighbour Laplacian",
        "Convolution - Eight Neighbour Laplacian",
        "Convolution - Four Neighbour Laplacian Enhancement",
        "Convolution - Eight Neighbour Laplacian Enhancement",
        "Convolution - Roberts One",
        "Convolution - Roberts Two",
        "Convolution - Sobel X",
        "Convolution - Sobel Y",
        "Salt&Pepper noise",
        "Min Filtering",
        "Max Filtering",
        "MidPoint Filtering",
        "Median Filtering",
        "Simple Thresholding",
    };

    // Histogram for RGB images, each array contains the number of occurences for each pixel value in the image
    private int[] histogramR = new int[256];
    private int[] histogramG = new int[256];
    private int[] histogramB = new int[256];

    private float[][] mask = new float[3][3];

    // Masks for convolution
    private float[][] averageMask = {{1f, 1f, 1f}, {1f, 1f, 1f}, {1f,1f,1f}};
    private float[][] weightedMask = {{1f,2f,1f},{2f,4f,2f},{1f,2f,1f}};
    private float[][] fourNL = {{0f, -1f, 0f},{-1f, 4f, -1f}, {0f, -1f, 0f}};
    private float[][] eightNL = {{-1f, -1f, -1f},{-1f, 8f, -1f}, {-1f, -1f, -1f}};
    private float[][] fourNLE = {{0f, -1f, 0f},{-1f, 5f, -1f}, {0f, -1f, 0f}};
    private float[][] eightNLE = {{-1f, -1f, -1f},{-1f, 9f, -1f}, {-1f, -1f, -1f}};
    private float[][] robertsOne = {{0f, 0f, 0f},{0f, 0f, -1f}, {0f, 1f, 0f}};
    private float[][] robertsTwo = {{0f, 0f, 0f},{0f, -1f, 0f}, {0f, 0f, 1f}};
    private float[][] sobelX = {{-1f, 0f, 1f},{-2f, 0f, 2f}, {-1f, 0f, 1f}};
    private float[][] sobelY = {{-1f, -2f, -1f},{0f, 0f, 0f}, {1f, 2f, 1f}};
 
    //option index for processing algorithm
    private int opIndex;  
    private int lastOp;

    // Contains the images loaded
    private ArrayList<BufferedImage> arrayImages = new ArrayList<BufferedImage>();
    // Contains the processed images
    private ArrayList<BufferedImage> arrayImagesFiltered = new ArrayList<BufferedImage>();
    // Contains width of images
    private ArrayList<Integer> arrayWidth = new ArrayList<Integer>();
    // Containes height of images
    private ArrayList<Integer> arrayHeight = new ArrayList<Integer>();
    // Size of graphic obejct to draw on screen
    private int posterWidth;
    private int posterHeight;
    private Graphics big;

    private int firstImage, secondImage;

    // width and height of temporary image matrixes used in the processing algorithms
    private int width, height;
    private int[][][] ImageArray1, ImageArray2, ImageArray3;
    
    public Demo() {
        posterWidth = 1000;
        posterHeight = 1000;
        BufferedImage poster = new BufferedImage(posterWidth, posterHeight, BufferedImage.TYPE_INT_RGB);
        big = poster.getGraphics();
    }                       
    
    // Return size of Poster
    public Dimension getPreferredSize() {
         return new Dimension(posterWidth, posterHeight);
    }
 
    // Return text descriptions of processing algorithms
    String[] getDescriptions() {
        return descs;
    }

    String[] getArrayImagefilteredSize(){
        String[] numImage = new String[arrayImagesFiltered.size()];
        for (int i = 0; i<numImage.length; i++){
            numImage[i] = String.valueOf(i+1);
        }
        return numImage;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"gif","bmp","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }
 
    void setOpIndex(int i) {
        opIndex = i;
    }

    // Populate the arrays with images 
    public void setImages(String img){
        try{
            arrayImages.add(ImageIO.read(new File(img)));  
            arrayImagesFiltered.add(ImageIO.read(new File(img))); 
            BufferedImage lastImage = arrayImages.get(arrayImages.size() - 1);
            arrayWidth.add(lastImage.getWidth());
            arrayHeight.add(lastImage.getHeight());
            posterWidth += lastImage.getWidth();

        }
        // deal with the situation that th image has problem/
        catch(IOException e) { 
            System.out.println("Image could not be read");
            System.exit(1);
        }
        repaint();
        return;
    }
    //  Repaint will call this function so the image will change
    public void paint(Graphics g) { 
        filterImage(); 
        int width = 0;
        int width1 = 0;
        int height = arrayHeight.get(0);
        int sizeArrayImages = arrayImages.size();
       

        for(int i=0; i<arrayImagesFiltered.size(); i++){
            // Print the original images on the same row
            if (i <  sizeArrayImages){
                g.drawImage(arrayImagesFiltered.get(i), width, 0, null);
                width += arrayWidth.get(i);
            }
            // Print processes images below the original images
            else{
                g.drawImage(arrayImagesFiltered.get(i), width1, height, null);
                width1 += 512;
            }
        }    
        return; 
    }

    // Undo the processing algorithm chosen. GUI will show only the orignal images
    // Removes from the array(containing the original images and the processes images) the processed images
    public void undo(){
        int processedImages = arrayImagesFiltered.size();
        int total = processedImages - arrayImages.size();;

        for(int i = 1; i<=total; i++){
            arrayImagesFiltered.remove(processedImages-i);
        }
        repaint();
        return;
    }
 
    public int getArrayImageSize(){
        return arrayImages.size();
    }

    public void setupTmpArrays(){
        this.width = arrayImages.get(firstImage-1).getWidth();
        this.height = arrayImages.get(firstImage-1).getHeight();

        this.ImageArray1 = convertToArray(arrayImages.get(firstImage -1));
        this.ImageArray2 = convertToArray(arrayImages.get(secondImage - 1));
        this.ImageArray3 = new int[height][width][4];
        return;
    }

    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        // Loop through the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x,y);
                // bits shifted bitwise AND 11111111 to get rid of the bits with value 0 before the bits wiht the value for alpha, red, green and blue
                int a = (p>>24)&0xff;
                // Shift 16 bit to get pixel values of Red channel
                int r = (p>>16)&0xff;
                // Shift 8 bit to get pixel values of Green Channel
                int g = (p>>8)&0xff;
                // pixel values of blue channel are the last 6 bits
                int b = p&0xff;

                // Alhpa channel
                result[x][y][0]=a;
                // Red
                result[x][y][1]=r;
                // Green
                result[x][y][2]=g;
                // Blue
                result[x][y][3]=b;
            }
        }
        return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];
                
                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }


    //************************************
    //Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
         //  Convert the image to array
        int [][][] ImageArray = convertToArray(timg);
        int [][][] ImageArray2 = new int[height][width][4];
        
        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                //r
                ImageArray2[x][y][1] = 255-ImageArray[x][y][1]; 
                //g 
                ImageArray2[x][y][2] = 255-ImageArray[x][y][2]; 
                //b 
                ImageArray2[x][y][3] = 255-ImageArray[x][y][3]; 
            }
        }
        // Convert the array to BufferedImage
        return convertToBimage(ImageArray2); 
    }

    //************************************
    //Rescale image (pixel values) - NOT TO CONFUSE WITH COORDINATES 
    //ADJUST IMAGE CONTRAST
    //************************************
    public BufferedImage rescaling(BufferedImage timg, float scalingFactor){

        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = new int[height][width][4];
        // scaling factor
        float s = scalingFactor;

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                //r
                ImageArray2[x][y][1] = Math.round(s*(ImageArray1[x][y][1]));
                //g
                ImageArray2[x][y][2] = Math.round(s*(ImageArray1[x][y][2]));
                //b
                ImageArray2[x][y][3] = Math.round(s*(ImageArray1[x][y][3])); 

                // Normalization of pixel values
                if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
                if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
                if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
                if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
                if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
                if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
            }
        }
        return convertToBimage(ImageArray2);
    }

    //************************************
    // Shift and rescale pixel value of an image
    //************************************
    public BufferedImage shiftRescale(BufferedImage timg, int shifting, float scalingFactor){
        int rmin, rmax, gmin, gmax, bmin, bmax;
        double s = scalingFactor;
        int t = shifting;
        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] ImageArray1 = convertToArray(timg);
        int [][][] ImageArray2 = new int[height][width][4];

        rmin = (int)Math.round(s*(ImageArray1[0][0][1]+t)); rmax = rmin;
        gmin = (int)Math.round(s*(ImageArray1[0][0][2]+t)); gmax = gmin;
        bmin =(int) Math.round(s*(ImageArray1[0][0][3]+t)); bmax = bmin;
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray2[x][y][1] = (int)Math.round(s*(ImageArray1[x][y][1]+t)); //r
                ImageArray2[x][y][2] = (int)Math.round(s*(ImageArray1[x][y][2]+t)); //g
                ImageArray2[x][y][3] = (int)Math.round(s*(ImageArray1[x][y][3]+t)); //b
                if (rmin>ImageArray2[x][y][1]) { rmin = ImageArray2[x][y][1]; }
                if (gmin>ImageArray2[x][y][2]) { gmin = ImageArray2[x][y][2]; }
                if (bmin>ImageArray2[x][y][3]) { bmin = ImageArray2[x][y][3]; }
                if (rmax<ImageArray2[x][y][1]) { rmax = ImageArray2[x][y][1]; }
                if (gmax<ImageArray2[x][y][2]) { gmax = ImageArray2[x][y][2]; }
                if (bmax<ImageArray2[x][y][3]) { bmax = ImageArray2[x][y][3]; }
            }
        }

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray2[x][y][1]=255*(ImageArray2[x][y][1]-rmin)/(rmax-rmin);
                ImageArray2[x][y][2]=255*(ImageArray2[x][y][2]-gmin)/(gmax-gmin);
                ImageArray2[x][y][3]=255*(ImageArray2[x][y][3]-bmin)/(bmax-bmin);
            }
        }
        return convertToBimage(ImageArray2);
    }
    
    // Arithmetic operations for image enhancement

    // Adds pixel values of first image to Second image to create a third image
    public BufferedImage addition(ArrayList<BufferedImage> arrayImages){
        setupTmpArrays();
        for(int y = 0; y<width; y++){
            for(int x = 0; x<height; x++){
                ImageArray3[x][y][1] = ImageArray1[x][y][1] + ImageArray2[x][y][1];
                ImageArray3[x][y][2] = ImageArray1[x][y][2] + ImageArray2[x][y][2];
                ImageArray3[x][y][3] = ImageArray1[x][y][3] + ImageArray2[x][y][3];
            }
        }
        return shiftRescale(convertToBimage(ImageArray3), 1, 1f);
    }

    // Third image is Image with pixel values obtained by substracting the pixel values of first image to pixel values of second image
    public BufferedImage substraction(ArrayList<BufferedImage> arrayImages){
        setupTmpArrays();
        for(int y = 0; y<width; y++){
            for(int x = 0; x<height; x++){
                ImageArray3[x][y][1] = ImageArray1[x][y][1] - ImageArray2[x][y][1];
                ImageArray3[x][y][2] = ImageArray1[x][y][2] - ImageArray2[x][y][2];
                ImageArray3[x][y][3] = ImageArray1[x][y][3] - ImageArray2[x][y][3];
            }
        }
        return shiftRescale(convertToBimage(ImageArray3), 4, 1.8f);
    }

    // New image created multiplicating pixel values 
    public BufferedImage multiplication(ArrayList<BufferedImage> arrayImages){
        setupTmpArrays();
        for(int y = 0; y<width; y++){
            for(int x = 0; x<height; x++){
                ImageArray3[x][y][1] = ImageArray1[x][y][1] * ImageArray2[x][y][1];
                ImageArray3[x][y][2] = ImageArray1[x][y][2] * ImageArray2[x][y][2];
                ImageArray3[x][y][3] = ImageArray1[x][y][3] * ImageArray2[x][y][3];
            }
        }

        return shiftRescale(convertToBimage(ImageArray3), 1, 1.5f);
    }

    // New image created dividing pixel values
    public BufferedImage division(ArrayList<BufferedImage> arrayImages){
        setupTmpArrays();
        for(int y = 0; y<width; y++){
            for(int x = 0; x<height; x++){
                if(ImageArray2[x][y][1] == 0){
                    ImageArray3[x][y][1] = ImageArray1[x][y][1];
                }
                else{
                    ImageArray3[x][y][1] = ImageArray1[x][y][1] / ImageArray2[x][y][1];
                }
                if(ImageArray2[x][y][2] == 0){
                     ImageArray3[x][y][2] = ImageArray1[x][y][2];
                }
                else{
                    ImageArray3[x][y][2] = ImageArray1[x][y][2] / ImageArray2[x][y][2];
                }
                if(ImageArray2[x][y][3] == 0){
                     ImageArray3[x][y][3] = ImageArray1[x][y][3];
                }
                else{
                    ImageArray3[x][y][3] = ImageArray1[x][y][3] / ImageArray2[x][y][3];
                }
                
            }
        }
        return shiftRescale(convertToBimage(ImageArray3), 1, 2f);
    }

    //************************************
    // Bitwise operations on images
    //************************************

    public BufferedImage bitwiseNot(BufferedImage timg){

        int height = timg.getHeight();
        int width = timg.getHeight();
        int r, g, b;

        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[height][width][4];

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                r = ImageArray1[x][y][1]; //r
                g = ImageArray1[x][y][2]; //g
                b = ImageArray1[x][y][3]; //b
                ImageArray2[x][y][1] = (~r)&0xFF; //r
                ImageArray2[x][y][2] = (~g)&0xFF; //g
                ImageArray2[x][y][3] = (~b)&0xFF; //b
            }
        }

        return convertToBimage(ImageArray2);
    }

    // Bitwise AND operation  on pixel values of 2 chosen images
    public BufferedImage bitwiseAnd(ArrayList<BufferedImage> arrayImages){

        setupTmpArrays();
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray3[x][y][1] = ImageArray1[x][y][1] & ImageArray2[x][y][1];
                ImageArray3[x][y][2] = ImageArray1[x][y][2] & ImageArray2[x][y][2];
                ImageArray3[x][y][3] = ImageArray1[x][y][3] & ImageArray2[x][y][3];
            }
        }

        return convertToBimage(ImageArray3);
    }

    // Bitwise OR operation  on pixel values of 2 chosen images
    public BufferedImage bitwiseOr(ArrayList<BufferedImage> arrayImages){

        setupTmpArrays();
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray3[x][y][1] = ImageArray1[x][y][1] | ImageArray2[x][y][1];
                ImageArray3[x][y][2] = ImageArray1[x][y][2] | ImageArray2[x][y][2];
                ImageArray3[x][y][3] = ImageArray1[x][y][3] | ImageArray2[x][y][3];
            }
        }

        return convertToBimage(ImageArray3);
    }

    // Bitwise XOR operation  on pixel values of 2 chosen images
    public BufferedImage bitwiseXor(ArrayList<BufferedImage> arrayImages){

        setupTmpArrays();
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray3[x][y][1] = ImageArray1[x][y][1] ^ ImageArray2[x][y][1];
                ImageArray3[x][y][2] = ImageArray1[x][y][2] ^ ImageArray2[x][y][2];
                ImageArray3[x][y][3] = ImageArray1[x][y][3] ^ ImageArray2[x][y][3];
            }
        }

        return convertToBimage(ImageArray3);
    }

    //************************************
    // Transformation functions
    //************************************

    // Logarithmic transformation
    // Create look up table and populate it transforming each pixel value with logarithmic function
    // Compress and stretch the dyanmic range, dark pixels are expanded
    // Value of constant c adjust the image enhancement
    public BufferedImage logTransformation(BufferedImage timg, int c){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[] LUT =  new int[256];

        for(int k = 0; k<= 255; k++){
            // 1+k because if pixel is 0 log(0) = infinity
            LUT[k] = (int)(Math.log(1+k)*c/Math.log(256));
        }

        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[height][width][4];

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray2[x][y][0] = LUT[ImageArray1[x][y][0]];
                ImageArray2[x][y][1] = LUT[ImageArray1[x][y][1]];
                ImageArray2[x][y][2] = LUT[ImageArray1[x][y][2]];
                ImageArray2[x][y][3] = LUT[ImageArray1[x][y][3]];
            }
        }

        return convertToBimage(ImageArray2);
    }

    // Power-law Transformation
    // The value of gamma varies the enhancement of the image
    public BufferedImage powerLaw(BufferedImage timg, float gamma){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[] LUT =  new int[256];

       
        // Populate LUT
        for(int k = 0; k<= 255; k++){
            LUT[k] = (int)(Math.pow(255, 1-gamma)*Math.pow(k,gamma));
        }

        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[height][width][4];

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                // Apply LUT to each pixel of the image
                ImageArray2[x][y][0] = LUT[ImageArray1[x][y][0]];
                ImageArray2[x][y][1] = LUT[ImageArray1[x][y][1]];
                ImageArray2[x][y][2] = LUT[ImageArray1[x][y][2]];
                ImageArray2[x][y][3] = LUT[ImageArray1[x][y][3]];
            }
        }

        return convertToBimage(ImageArray2);
    }

    // Apply randomly generated LUT to image

    public BufferedImage randomLut(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[] LUT =  new int[256];

        Random rand = new Random();
        for(int k = 0; k<= 255; k++){
            LUT[k] = rand.nextInt(256);
        }

        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[height][width][4];

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray2[x][y][0] = LUT[ImageArray1[x][y][0]];
                ImageArray2[x][y][1] = LUT[ImageArray1[x][y][1]];
                ImageArray2[x][y][2] = LUT[ImageArray1[x][y][2]];
                ImageArray2[x][y][3] = LUT[ImageArray1[x][y][3]];
            }
        }

        return convertToBimage(ImageArray2);
    }


    // Bit plane slicing slices the image in 8 bit planes
    // 8th bit plane has the most significant bits of the image
    // 1st bit plane has the least significant bits of the image
    // nBit indicates what plane you want to obtain
    public BufferedImage bitplaneSlicing(BufferedImage timg, int nBit){
        int width = timg.getWidth();
        int height = timg.getHeight();
    
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = new int[height][width][4];
        int r, g, b;

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                r = ImageArray1[x][y][1];
                g = ImageArray1[x][y][2];
                b = ImageArray1[x][y][3];
                ImageArray2[x][y][1] = (r>>nBit)&1;
                ImageArray2[x][y][2] = (g>>nBit)&1;
                ImageArray2[x][y][3] = (b>>nBit)&1;
                
                if(ImageArray2[x][y][1] == 1){
                    ImageArray2[x][y][1] = 255;
                }
                if(ImageArray2[x][y][2] == 1){
                    ImageArray2[x][y][2] = 255;
                }
                if(ImageArray2[x][y][3] == 1){
                    ImageArray2[x][y][3] = 255;
                }

            }
        }

        return convertToBimage(ImageArray2);
    }

    // Print on console histogram of the image
    public void findHistogram(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int pixel[];
        
        int r,g,b;

        for(int k = 0; k<=255; k++){
            histogramR[k] = 0;
            histogramG[k] = 0;
            histogramB[k] = 0;
        }

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                r = ImageArray1[x][y][1];
                g = ImageArray1[x][y][2];
                b = ImageArray1[x][y][3];
                histogramR[r]++;
                histogramG[g]++;
                histogramB[b]++;
            }
        }
        printHistogram();
        return;
    }


    public void printHistogram(){
        for(int k=0; k <= 255; k++){
            System.out.println("HistogramR with value "+ k +": " +histogramR[k]);
            System.out.println("HistogramG with value "+ k +": " +histogramG[k]);
            System.out.println("HistogramB with value "+ k +": " +histogramB[k]);
        }
        return;
    }

    //************************************
    // Convolution masks
    //************************************

    public int[][][] applyConvolution(BufferedImage timg, float[][] cMask){
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        float r, g, b;
        mask = cMask;

        for(int y =1; y<height-1; y++){
            for(int x= 1; x<width-1; x++){
                r = 0;
                g = 0; 
                b = 0;
                for(int s= -1; s<=1; s++){
                    for(int t= -1; t<=1; t++){
                        r = r+mask[1-s][1-t] * ImageArray1[x+s][y+t][1];
                        g = g+mask[1-s][1-t] * ImageArray1[x+s][y+t][2];
                        b = b+mask[1-s][1-t] * ImageArray1[x+s][y+t][3];
                    }
                }
                ImageArray2[x][y][1] = (int) Math.round(Math.abs(r));
                ImageArray2[x][y][2] = (int) Math.round(Math.abs(g));
                ImageArray2[x][y][3] = (int) Math.round(Math.abs(b));
            }
        }
        return ImageArray2;
    }

    public BufferedImage average(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        float r, g, b;
        mask = averageMask;
        float total = 0;

        for(int row = 0; row<3; row++){
            for(int col = 0; col < 3; col++){
                total += mask[row][col];
            }
        }

        for(int y =1; y<height-1; y++){
            for(int x= 1; x<width-1; x++){
                r = 0;
                g = 0; 
                b = 0;
                for(int s= -1; s<=1; s++){
                    for(int t= -1; t<=1; t++){
                        r = r+mask[1-s][1-t] * ImageArray1[x+s][y+t][1];
                        g = g+mask[1-s][1-t] * ImageArray1[x+s][y+t][2];
                        b = b+mask[1-s][1-t] * ImageArray1[x+s][y+t][3];
                    }
                }
                ImageArray2[x][y][1] = (int) Math.round(Math.abs(r/total));
                ImageArray2[x][y][2] = (int) Math.round(Math.abs(g/total));
                ImageArray2[x][y][3] = (int) Math.round(Math.abs(b/total));
            }
        }
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
        
    }

    public BufferedImage weightedAverage(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        float r, g, b;
        mask = weightedMask;
        float total = 0;

        for(int row = 0; row<3; row++){
            for(int col = 0; col < 3; col++){
                total += mask[row][col];
            }
        }

        for(int y =1; y<height-1; y++){
            for(int x= 1; x<width-1; x++){
                r = 0;
                g = 0; 
                b = 0;
                for(int s= -1; s<=1; s++){
                    for(int t= -1; t<=1; t++){
                        r = r+mask[1-s][1-t] * ImageArray1[x+s][y+t][1];
                        g = g+mask[1-s][1-t] * ImageArray1[x+s][y+t][2];
                        b = b+mask[1-s][1-t] * ImageArray1[x+s][y+t][3];
                    }
                }
                ImageArray2[x][y][1] = (int) Math.round(Math.abs(r/total));
                ImageArray2[x][y][2] = (int) Math.round(Math.abs(g/total));
                ImageArray2[x][y][3] = (int) Math.round(Math.abs(b/total));
            }
        }
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
        
    }

    public BufferedImage fourNL(BufferedImage timg){
        int[][][] ImageArray2 = applyConvolution(timg, fourNL);
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
    }
    
    public BufferedImage eightNL(BufferedImage timg){
        int[][][] ImageArray2 = applyConvolution(timg, eightNL);
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
    }

    public BufferedImage fourNLE(BufferedImage timg){
        int[][][] ImageArray2 = applyConvolution(timg, fourNLE);
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
    }

    public BufferedImage eightNLE(BufferedImage timg){
        int[][][] ImageArray2 = applyConvolution(timg, eightNLE);
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
    }

    public BufferedImage robertsOne(BufferedImage timg){
        int[][][] ImageArray2 = applyConvolution(timg, robertsOne);
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
    }

     public BufferedImage robertsTwo(BufferedImage timg){
        int[][][] ImageArray2 = applyConvolution(timg, robertsTwo);
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
    }

     public BufferedImage sobelX(BufferedImage timg){
        int[][][] ImageArray2 = applyConvolution(timg, sobelX);
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
    }

     public BufferedImage sobelY(BufferedImage timg){
        int[][][] ImageArray2 = applyConvolution(timg, sobelY);
        return shiftRescale(convertToBimage(ImageArray2), 1, 0.7f);
    }

    //************************************
    // Adds salt and pepper noise
    //************************************

    public BufferedImage saltPepper(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int randNum;
        Random rand = new Random();

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                randNum = rand.nextInt(10);
                if(randNum == 0){
                    ImageArray2[x][y][1] = 255;
                    ImageArray2[x][y][2] = 255;
                    ImageArray2[x][y][3] = 255;
                }
                else if(randNum == 1){
                    ImageArray2[x][y][1] = 0;
                    ImageArray2[x][y][2] = 0;
                    ImageArray2[x][y][3] = 0;
                }
            }
        }
        return convertToBimage(ImageArray2);

    }

     public BufferedImage minFiltering(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[] windowR = new int[9];
        int[] windowG = new int[9];
        int[] windowB = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
               for(int s= -1; s<=1; s++){
                    for(int t= -1; t<=1; t++){
                        windowR[k] = ImageArray1[x+t][y+s][1];
                        windowG[k] = ImageArray1[x+t][y+s][2];
                        windowB[k] = ImageArray1[x+t][y+s][3];
                        k++;
                    }
                }
                Arrays.sort(windowR);
                Arrays.sort(windowG);
                Arrays.sort(windowB);
                ImageArray2[x][y][1] = windowR[0];
                ImageArray2[x][y][2] = windowG[0];
                ImageArray2[x][y][3] = windowB[0];
            }
        }
        return convertToBimage(ImageArray2);

    }

    public BufferedImage maxFiltering(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[] windowR = new int[9];
        int[] windowG = new int[9];
        int[] windowB = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
               for(int s= -1; s<=1; s++){
                    for(int t= -1; t<=1; t++){
                        windowR[k] = ImageArray1[x+t][y+s][1];
                        windowG[k] = ImageArray1[x+t][y+s][2];
                        windowB[k] = ImageArray1[x+t][y+s][3];
                        k++;
                    }
                }
                Arrays.sort(windowR);
                Arrays.sort(windowG);
                Arrays.sort(windowB);
                ImageArray2[x][y][1] = windowR[windowR.length-1];
                ImageArray2[x][y][2] = windowG[windowG.length-1];
                ImageArray2[x][y][3] = windowB[windowB.length-1];
            }
        }
        return convertToBimage(ImageArray2);

    }

    public BufferedImage midPointFiltering(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[] windowR = new int[9];
        int[] windowG = new int[9];
        int[] windowB = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
               for(int s= -1; s<=1; s++){
                    for(int t= -1; t<=1; t++){
                        windowR[k] = ImageArray1[x+t][y+s][1];
                        windowG[k] = ImageArray1[x+t][y+s][2];
                        windowB[k] = ImageArray1[x+t][y+s][3];
                        k++;
                    }
                }
                Arrays.sort(windowR);
                Arrays.sort(windowG);
                Arrays.sort(windowB);

                ImageArray2[x][y][1] = (int)(windowR[0] + windowR[windowR.length-1]) / 2;
                ImageArray2[x][y][2] = (int)(windowG[0] + windowG[windowG.length-1]) / 2;
                ImageArray2[x][y][3] = (int)(windowB[0] + windowR[windowB.length-1]) / 2;
            }
        }
        return convertToBimage(ImageArray2);

    }

     public BufferedImage medianFiltering(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
        int[][][] ImageArray2 = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[] windowR = new int[9];
        int[] windowG = new int[9];
        int[] windowB = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
               for(int s= -1; s<=1; s++){
                    for(int t= -1; t<=1; t++){
                        windowR[k] = ImageArray1[x+t][y+s][1];
                        windowG[k] = ImageArray1[x+t][y+s][2];
                        windowB[k] = ImageArray1[x+t][y+s][3];
                        k++;
                    }
                }
                Arrays.sort(windowR);
                Arrays.sort(windowG);
                Arrays.sort(windowB);

                ImageArray2[x][y][1] = windowR[4];
                ImageArray2[x][y][2] = windowR[4];
                ImageArray2[x][y][3] = windowR[4];
            }
        }
        return convertToBimage(ImageArray2);

    }

     public BufferedImage simpleThresholding(BufferedImage timg){
        int[][][] ImageArray1 = convertToArray(timg);
     
        int width = timg.getWidth();
        int height = timg.getHeight();
        int threshold = 120;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
             if(ImageArray1[x][y][1] <= threshold || ImageArray1[x][y][2] <= threshold || ImageArray1[x][y][3] <= threshold){
                ImageArray1[x][y][1] = 0;
                ImageArray1[x][y][2] = 0;
                ImageArray1[x][y][3] = 0;
             }
             else{
                ImageArray1[x][y][1] = 255;
                ImageArray1[x][y][2] = 255;
                ImageArray1[x][y][3] = 255;
             }
            }
        }
        return convertToBimage(ImageArray1);

    }

    public void insertNumImage(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Insert number of first image to process: ");
        this.firstImage = sc.nextInt();
        System.out.print("Insert number of second image to process: ");
        this.secondImage = sc.nextInt();
        return;
    }
    

    //************************************
    // Call methods depending on the option chosen in the GUI
    //************************************
    public void filterImage() {  // Called in paint() method
        Scanner sc = new Scanner(System.in);
        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0: 
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.set(i,arrayImages.get(i));
                }
                return; 
            case 1: 
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(ImageNegative(arrayImagesFiltered.get(i)));
                
                }
                return;
            case 2: 
                for(int i = 0; i<arrayImages.size(); i++){
                    try{
                        // Scaling factor should be from 0 to 2
                        System.out.print("Insert scaling factor: ");
                        float scalingFactor = sc.nextFloat();
                        arrayImagesFiltered.add(rescaling(arrayImagesFiltered.get(i), scalingFactor));
                    }
                    catch(Exception e){
                        System.out.println(e);
                    }
                }
                return;
            case 3:
                for(int i = 0; i<arrayImages.size(); i++){
                  
                    // Shifting value can be positive or negative
                    System.out.print("Insert shifting value (pos or neg): ");
                    int shifting = sc.nextInt();
                    // Scaling factor should be from 0 to 2
                    System.out.println("Insert scaling factor (0 to 2): ");
                    float scalingFactor = sc.nextFloat();
                    arrayImagesFiltered.add(shiftRescale(arrayImagesFiltered.get(i), shifting, scalingFactor));
    
                }
                return;
            case 4:
                insertNumImage();
                arrayImagesFiltered.add(addition(arrayImages));
                return;
            case 5:
                insertNumImage();
                arrayImagesFiltered.add(substraction(arrayImages));
                return;
            case 6:
                insertNumImage();
                arrayImagesFiltered.add(multiplication(arrayImages));
                return;
            case 7:
                insertNumImage();
                arrayImagesFiltered.add(division(arrayImages));
                return;
            case 8:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(bitwiseNot(arrayImagesFiltered.get(i)));
                }
                return;
            case 9:
                insertNumImage();
                arrayImagesFiltered.add(bitwiseAnd(arrayImages));
                return;
            case 10:
                insertNumImage();
                arrayImagesFiltered.add(bitwiseOr(arrayImages));
                return;
            
            case 11:
                insertNumImage();
                arrayImagesFiltered.add(bitwiseXor(arrayImages));
                return;
            case 12:
                for(int i = 0; i<arrayImages.size(); i++){
                    System.out.print("Insert constant for log transormation: ");
                    int c = sc.nextInt();
                    arrayImagesFiltered.add(logTransformation(arrayImagesFiltered.get(i), c));
                }
                return;
            case 13:
                for(int i = 0; i<arrayImages.size(); i++){
                     // Gamma can be from 0.01 to 25
                    System.out.print("Insert gamma: ");
                    float gamma = sc.nextFloat();
                    arrayImagesFiltered.add(powerLaw(arrayImagesFiltered.get(i), gamma));
                }
                return;
            case 14:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(randomLut(arrayImagesFiltered.get(i)));
                }
                return;
            case 15:
                for(int i = 0; i<arrayImages.size(); i++){
                    System.out.print("Insert number of bitplane: ");
                    int nBit = sc.nextInt();
                    arrayImagesFiltered.add(bitplaneSlicing(arrayImagesFiltered.get(i), nBit));
                }
                return;
            case 16:
                for(int i = 0; i<arrayImages.size(); i++){
                    findHistogram(arrayImagesFiltered.get(i));
                }
                return;
            case 17:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(average(arrayImagesFiltered.get(i)));
                }
                return;
            case 18:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(weightedAverage(arrayImagesFiltered.get(i)));
                }
                return;
            case 19:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(fourNL(arrayImagesFiltered.get(i)));
                }
                return;
            case 20:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(eightNL(arrayImagesFiltered.get(i)));
                }
                return;
            case 21:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(fourNLE(arrayImagesFiltered.get(i)));
                }
                return;
            case 22:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(eightNLE(arrayImagesFiltered.get(i)));
                }
                return;
            case 23:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(robertsOne(arrayImagesFiltered.get(i)));
                }
                return;
            case 24:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(robertsTwo(arrayImagesFiltered.get(i)));
                }
                return;
            case 25:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(sobelX(arrayImagesFiltered.get(i)));
                }
                return;
            case 26:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(sobelY(arrayImagesFiltered.get(i)));
                }
                return;
            case 27:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(saltPepper(arrayImagesFiltered.get(i)));
                }
                return;
            case 28:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(minFiltering(arrayImagesFiltered.get(i)));
                }
                return;
            case 29:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(maxFiltering(arrayImagesFiltered.get(i)));
                }
                return;
            case 30:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(midPointFiltering(arrayImagesFiltered.get(i)));
                }
                return;
            case 31:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(medianFiltering(arrayImagesFiltered.get(i)));
                }
                return;
            case 32:
                for(int i = 0; i<arrayImages.size(); i++){
                    arrayImagesFiltered.add(simpleThresholding(arrayImagesFiltered.get(i)));
                }
                return;
        }
    }
 

 
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        // When processing algorithm is chosen in the GUI the repaint method is called to draw the processed images on the GUI
        if (cb.getActionCommand().equals("SetFilter")) {
            setOpIndex(cb.getSelectedIndex());
            repaint();
        }
        // Save processed images
        else if (cb.getActionCommand().equals("Formats")) {
            String format = (String)cb.getSelectedItem();
            // Only the processed images and not the originals, that's why i = getArrayImageSize()
            for(int i = getArrayImageSize(); i<arrayImagesFiltered.size(); i++){
                File saveFile = new File("savedimage."+format);
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(saveFile);
                int rval = chooser.showSaveDialog(cb);
                if (rval == JFileChooser.APPROVE_OPTION) {
                    saveFile = chooser.getSelectedFile();
                    try {
                        ImageIO.write(arrayImagesFiltered.get(i), format, saveFile);
                        
                    } 
                    catch (IOException ex) {
                    }
                }
            }

         }
        
    }
 
    public static void main(String s[]) {
        JFrame frame = new JFrame("Image Processing Demo");
        JPanel container = new JPanel();


        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        Demo demo = new Demo();


        // select images to load onto GUI
        final FileDialog fileDialog = new FileDialog(frame, "Select Image");
        Button showFile = new Button("Select image");
        showFile.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                fileDialog.setVisible(true);
                demo.setImages(fileDialog.getDirectory() + fileDialog.getFile());
            }

        });

        // When the button undo is pressed Show only the original images
        Button undoButton = new Button("Undo");
        undoButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                demo.undo();
            }
        });

       
        JComboBox choices = new JComboBox(demo.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(demo);
        JComboBox formats = new JComboBox(demo.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(demo);
        JComboBox imageToSaveSelection = new JComboBox(demo.getArrayImagefilteredSize());

        JPanel panel = new JPanel();
        // Add components to jpanel
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        panel.add(showFile);
        panel.add(undoButton);
        frame.add("North", panel);


        container.add(demo);
        JScrollPane scrPane = new JScrollPane(container);
        frame.add("Center", scrPane);
        frame.pack();
        frame.setVisible(true);
    }
}
