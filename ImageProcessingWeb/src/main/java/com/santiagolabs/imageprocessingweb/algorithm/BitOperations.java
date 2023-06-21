package com.santiagolabs.imageprocessingweb.algorithm;

import com.santiagolabs.imageprocessingweb.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class BitOperations {
    @Autowired
    private MixedAlgorithms mixedAlgorithms;

    public BufferedImage getBitwiseNotImage(BufferedImage bufferedImage){

        int imageHeight = bufferedImage.getHeight();
        int imageWidth = bufferedImage.getWidth();

        int[][][] imageArray = Utils.getRGBArray(bufferedImage);
        int[][][] bitwiseNotImage = new int[imageWidth][imageHeight][4];

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                int red = imageArray[width][height][1];
                int green = imageArray[width][height][2];
                int blue = imageArray[width][height][3];
                bitwiseNotImage[width][height][1] = (~ red) & 0xFF;
                bitwiseNotImage[width][height][2] = (~ green) & 0xFF;
                bitwiseNotImage[width][height][3] = (~ blue) & 0xFF;
            }
        }
        return Utils.getBufferedImage(bitwiseNotImage);
    }

    public BufferedImage getBitwiseAndImage(BufferedImage bufferedImage1, BufferedImage bufferedImage2) {
        int maxHeight = Math.max(bufferedImage1.getHeight(), bufferedImage2.getHeight());
        int maxWidth = Math.max(bufferedImage1.getWidth(), bufferedImage2.getWidth());
        int[][][] imageArray1 = Utils.getRGBArray(bufferedImage1);
        int[][][] imageArray2 = Utils.getRGBArray(bufferedImage2);
        int[][][] resultImage = new int[maxWidth][maxHeight][4];

        for (int width = 0; width < maxWidth; width++) {
            for (int height = 0; height < maxHeight; height++) {
                if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()
                        && width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1] & imageArray2[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2] & imageArray2[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3] & imageArray2[width][height][3];
                } else if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3];
                } else if (width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = imageArray2[width][height][1];
                    resultImage[width][height][2] = imageArray2[width][height][2];
                    resultImage[width][height][3] = imageArray2[width][height][3];
                }
            }
        }

        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(resultImage), 0, 1f);
    }

    public BufferedImage getBitwiseOrImage(BufferedImage bufferedImage1, BufferedImage bufferedImage2) {
        int maxHeight = Math.max(bufferedImage1.getHeight(), bufferedImage2.getHeight());
        int maxWidth = Math.max(bufferedImage1.getWidth(), bufferedImage2.getWidth());
        int[][][] imageArray1 = Utils.getRGBArray(bufferedImage1);
        int[][][] imageArray2 = Utils.getRGBArray(bufferedImage2);
        int[][][] resultImage = new int[maxWidth][maxHeight][4];

        for (int width = 0; width < maxWidth; width++) {
            for (int height = 0; height < maxHeight; height++) {
                if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()
                        && width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1] | imageArray2[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2] | imageArray2[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3] | imageArray2[width][height][3];
                } else if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3];
                } else if (width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = imageArray2[width][height][1];
                    resultImage[width][height][2] = imageArray2[width][height][2];
                    resultImage[width][height][3] = imageArray2[width][height][3];
                }
            }
        }

        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(resultImage), 0, 1f);
    }

    public BufferedImage getBitwiseXorImage(BufferedImage bufferedImage1, BufferedImage bufferedImage2) {
        int maxHeight = Math.max(bufferedImage1.getHeight(), bufferedImage2.getHeight());
        int maxWidth = Math.max(bufferedImage1.getWidth(), bufferedImage2.getWidth());
        int[][][] imageArray1 = Utils.getRGBArray(bufferedImage1);
        int[][][] imageArray2 = Utils.getRGBArray(bufferedImage2);
        int[][][] resultImage = new int[maxWidth][maxHeight][4];

        for (int width = 0; width < maxWidth; width++) {
            for (int height = 0; height < maxHeight; height++) {
                if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()
                        && width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1] ^ imageArray2[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2] ^ imageArray2[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3] ^ imageArray2[width][height][3];
                } else if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3];
                } else if (width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = imageArray2[width][height][1];
                    resultImage[width][height][2] = imageArray2[width][height][2];
                    resultImage[width][height][3] = imageArray2[width][height][3];
                }
            }
        }

        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(resultImage), 0, 1f);
    }
}
