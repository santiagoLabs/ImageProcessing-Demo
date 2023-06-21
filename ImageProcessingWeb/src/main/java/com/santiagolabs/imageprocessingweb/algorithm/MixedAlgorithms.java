package com.santiagolabs.imageprocessingweb.algorithm;

import com.santiagolabs.imageprocessingweb.Utils;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.Random;

@Component
public class MixedAlgorithms{

    public BufferedImage getNegativeImage(BufferedImage bufferedImage){
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();

        int [][][] imageArray = Utils.getRGBArray(bufferedImage);
        int [][][] negativeImage = new int[imageWidth][imageHeight][4];

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                negativeImage[width][height][1] = 255 - imageArray[width][height][1];
                negativeImage[width][height][2] = 255 - imageArray[width][height][2];
                negativeImage[width][height][3] = 255 - imageArray[width][height][3];
            }
        }

        return Utils.getBufferedImage(negativeImage);
    }

    public BufferedImage getRescaledImage(BufferedImage bufferedImage, float scalingFactor){

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int [][][] imageArray = Utils.getRGBArray(bufferedImage);
        int [][][] rescaledImage = new int[imageWidth][imageHeight][4];

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                rescaledImage[width][height][1] = Math.round(scalingFactor * (imageArray[width][height][1]));
                rescaledImage[width][height][2] = Math.round(scalingFactor * (imageArray[width][height][2]));
                rescaledImage[width][height][3] = Math.round(scalingFactor * (imageArray[width][height][3]));

                // Normalization of pixel values
                if (rescaledImage[width][height][1] < 0) { rescaledImage[width][height][1] = 0; }
                if (rescaledImage[width][height][2] < 0) { rescaledImage[width][height][2] = 0; }
                if (rescaledImage[width][height][3] < 0) { rescaledImage[width][height][3] = 0; }
                if (rescaledImage[width][height][1] > 255) { rescaledImage[width][height][1] = 255; }
                if (rescaledImage[width][height][2] > 255) { rescaledImage[width][height][2] = 255; }
                if (rescaledImage[width][height][3] > 255) { rescaledImage[width][height][3] = 255; }
            }
        }
        return Utils.getBufferedImage(rescaledImage);
    }

    // Contrast
    public BufferedImage getShiftedAndRescaledImage(BufferedImage bufferedImage, int shifting, float scalingFactor){
        int redMin, redMax, greenMin, greenMax, blueMin, blueMax;

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int [][][] imageArray = Utils.getRGBArray(bufferedImage);
        int [][][] shiftedAndRescaled = new int[imageWidth][imageHeight][4];

        redMin = Math.round(scalingFactor * (imageArray[0][0][1] + shifting));
        redMax = redMin;
        greenMin = Math.round(scalingFactor * (imageArray[0][0][2] + shifting));
        greenMax = greenMin;
        blueMin = Math.round(scalingFactor * (imageArray[0][0][3] + shifting));
        blueMax = blueMin;

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                shiftedAndRescaled[width][height][1] = Math.round(scalingFactor * (imageArray[width][height][1] + shifting));
                shiftedAndRescaled[width][height][2] = Math.round(scalingFactor * (imageArray[width][height][2] + shifting));
                shiftedAndRescaled[width][height][3] = Math.round(scalingFactor * (imageArray[width][height][3] + shifting));

                if (redMin > shiftedAndRescaled[width][height][1])
                    redMin = shiftedAndRescaled[width][height][1];

                if (greenMin > shiftedAndRescaled[width][height][2])
                    greenMin = shiftedAndRescaled[width][height][2];

                if (blueMin > shiftedAndRescaled[width][height][3])
                    blueMin = shiftedAndRescaled[width][height][3];

                if (redMax < shiftedAndRescaled[width][height][1])
                    redMax = shiftedAndRescaled[width][height][1];

                if (greenMax < shiftedAndRescaled[width][height][2])
                    greenMax = shiftedAndRescaled[width][height][2];

                if (blueMax < shiftedAndRescaled[width][height][3])
                    blueMax = shiftedAndRescaled[width][height][3];
            }
        }

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                shiftedAndRescaled[width][height][1]= 255 * (shiftedAndRescaled[width][height][1] - redMin) / (redMax - redMin);
                shiftedAndRescaled[width][height][2]= 255 * (shiftedAndRescaled[width][height][2] - greenMin) / (greenMax - greenMin);
                shiftedAndRescaled[width][height][3]= 255 * (shiftedAndRescaled[width][height][3] - blueMin) / (blueMax - blueMin);
            }
        }
        return Utils.getBufferedImage(shiftedAndRescaled);
    }

    // Bit plane slicing slices the image in 8 bit planes
    // 8th bit plane has the most significant bits of the image
    // 1st bit plane has the least significant bits of the image
    // nBit indicates what plane you want to obtain
    public BufferedImage getBitplaneSlicingImage(BufferedImage bufferedImage, int nBit){
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();

        int[][][] imageArray = Utils.getRGBArray(bufferedImage);
        int[][][] result = new int[imageWidth][imageHeight][4];

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                int red = imageArray[width][height][1];
                int green = imageArray[width][height][2];
                int blue = imageArray[width][height][3];
                result[width][height][1] = (red >> nBit) & 1;
                result[width][height][2] = (green >> nBit) & 1;
                result[width][height][3] = (blue >> nBit) & 1;

                if(result[width][height][1] == 1)
                    result[width][height][1] = 255;

                if(result[width][height][2] == 1)
                    result[width][height][2] = 255;

                if(result[width][height][3] == 1)
                    result[width][height][3] = 255;
            }
        }

        return Utils.getBufferedImage(result);
    }

    public BufferedImage saltPepper(BufferedImage bufferedImage){
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();

        int[][][] result = Utils.getRGBArray(bufferedImage);
        Random rand = new Random();

        for(int width = 0; width < imageWidth; width++){
            for(int height = 0; height < imageHeight; height++){
                int randNum = rand.nextInt(10);
                if(randNum == 0){
                    result[width][height][1] = 255;
                    result[width][height][2] = 255;
                    result[width][height][3] = 255;
                }
                else if(randNum == 1){
                    result[width][height][1] = 0;
                    result[width][height][2] = 0;
                    result[width][height][3] = 0;
                }
            }
        }

        return Utils.getBufferedImage(result);
    }
}
