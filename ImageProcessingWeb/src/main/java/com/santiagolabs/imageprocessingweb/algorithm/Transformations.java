package com.santiagolabs.imageprocessingweb.algorithm;

import com.santiagolabs.imageprocessingweb.Utils;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.Random;

@Component
public class Transformations {

    //************************************
    // Transformation functions
    //************************************

    // Logarithmic transformation
    // Create look up table and populate it transforming each pixel value with logarithmic function
    // Compress and stretch the dyanmic range, dark pixels are expanded
    // Value of constant c adjust the image enhancement
    public BufferedImage getLogTransformationImage(BufferedImage bufferedImage, int c){
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int[] LUT =  new int[256];

        for(int k = 0; k <= 255; k++){
            // 1+k because if pixel is 0 log(0) = infinity
            LUT[k] = (int)(Math.log(1 + k) * c / Math.log(256));
        }

        int[][][] imageArray = Utils.getRGBArray(bufferedImage);
        int[][][] resultImage = new int[imageWidth][imageHeight][4];

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                resultImage[width][height][0] = LUT[imageArray[width][height][0]];
                resultImage[width][height][1] = LUT[imageArray[width][height][1]];
                resultImage[width][height][2] = LUT[imageArray[width][height][2]];
                resultImage[width][height][3] = LUT[imageArray[width][height][3]];
            }
        }

        return Utils.getBufferedImage(resultImage);
    }

    // Power-law Transformation
    // The value of gamma varies the enhancement of the image
    public BufferedImage getPowerLawImage(BufferedImage bufferedImage, float gamma){
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int[] LUT =  new int[256];

        // Populate LUT
        for(int k = 0; k<= 255; k++){
            LUT[k] = (int)(Math.pow(255, 1-gamma)*Math.pow(k,gamma));
        }

        int[][][] imageArray = Utils.getRGBArray(bufferedImage);
        int[][][] result = new int[imageWidth][imageHeight][4];

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                // Apply LUT to each pixel of the image
                result[width][height][0] = LUT[imageArray[width][height][0]];
                result[width][height][1] = LUT[imageArray[width][height][1]];
                result[width][height][2] = LUT[imageArray[width][height][2]];
                result[width][height][3] = LUT[imageArray[width][height][3]];
            }
        }

        return Utils.getBufferedImage(result);
    }

    // Apply randomly generated LUT to image

    public BufferedImage getRandomLutImage(BufferedImage bufferedImage){
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int[] LUT =  new int[256];

        Random rand = new Random();
        for(int k = 0; k<= 255; k++){
            LUT[k] = rand.nextInt(256);
        }

        int[][][] imageArray = Utils.getRGBArray(bufferedImage);
        int[][][] result = new int[imageWidth][imageHeight][4];

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                // Apply LUT to each pixel of the image
                result[width][height][0] = LUT[imageArray[width][height][0]];
                result[width][height][1] = LUT[imageArray[width][height][1]];
                result[width][height][2] = LUT[imageArray[width][height][2]];
                result[width][height][3] = LUT[imageArray[width][height][3]];
            }
        }

        return Utils.getBufferedImage(result);
    }
}
