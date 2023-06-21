package com.santiagolabs.imageprocessingweb;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils {

    private static final String FORMAT = "PNG";

    public static BufferedImage byteArrayToBufferedImage(byte[] imageData)
            throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        return ImageIO.read(inputStream);
    }

    public static byte[] bufferedImageToByteArray(BufferedImage image)
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, FORMAT, outputStream);
        return outputStream.toByteArray();
    }

    public static int[][][] getRGBArray(BufferedImage image){
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int[][][] RGBArray = new int[imageWidth][imageHeight][4];

        for (int height = 0; height < imageHeight; height++) {
            for (int width = 0; width < imageWidth; width++) {
                int pixel = image.getRGB(width, height);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                RGBArray[width][height][0] = alpha;
                RGBArray[width][height][1] = red;
                RGBArray[width][height][2] = green;
                RGBArray[width][height][3] = blue;
            }
        }
        return RGBArray;
    }

    public static BufferedImage getBufferedImage(int[][][] RGBArray){

        int imageWidth = RGBArray.length;
        int imageHeight = RGBArray[0].length;

        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        for(int height = 0; height < imageHeight; height++){
            for(int width = 0; width < imageWidth; width++){
                int alpha = RGBArray[width][height][0];
                int red = RGBArray[width][height][1];
                int green = RGBArray[width][height][2];
                int blue = RGBArray[width][height][3];

                int pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                bufferedImage.setRGB(width, height, pixel);
            }
        }
        return bufferedImage;
    }
}
