package com.santiagolabs.imageprocessingweb.algorithm;

import com.santiagolabs.imageprocessingweb.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class Convolutions {
    @Autowired
    private MixedAlgorithms mixedAlgorithms;

    // Masks for convolution
    private static final float[][] AVERAGING_MASK = {{1f, 1f, 1f}, {1f, 1f, 1f}, {1f,1f,1f}};
    private static final float[][] WEIGHTED_AVERAGING_MASK = {{1f,2f,1f},{2f,4f,2f},{1f,2f,1f}};
    private static final float[][] FOUR_NEIGHBOUR_LAPLACIAN = {{0f, -1f, 0f},{-1f, 4f, -1f}, {0f, -1f, 0f}};
    private static final float[][] EIGHT_NEIGHBOUR_LAPLACIAN = {{-1f, -1f, -1f},{-1f, 8f, -1f}, {-1f, -1f, -1f}};
    private static final float[][] FOUR_NEIGHBOUR_LAPLACIAN_ENHANCEMENT = {{0f, -1f, 0f},{-1f, 5f, -1f}, {0f, -1f, 0f}};
    private static final float[][] EIGHT_NEIGHBOUR_LAPLACIAN_ENHANCEMENT = {{-1f, -1f, -1f},{-1f, 9f, -1f}, {-1f, -1f, -1f}};
    private static final float[][] ROBERTS_ONE = {{0f, 0f, 0f},{0f, 0f, -1f}, {0f, 1f, 0f}};
    private static final float[][] ROBERTS_TWO = {{0f, 0f, 0f},{0f, -1f, 0f}, {0f, 0f, 1f}};
    private static final float[][] SOBEL_X = {{-1f, 0f, 1f},{-2f, 0f, 2f}, {-1f, 0f, 1f}};
    private static final float[][] SOBEL_Y = {{-1f, -2f, -1f},{0f, 0f, 0f}, {1f, 2f, 1f}};


    public BufferedImage getAveragingMaskImage(BufferedImage bufferedImage){
        int[][][] imageArray = Utils.getRGBArray(bufferedImage);
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int[][][] averageMaskImage = new int[imageWidth][imageHeight][4];
        float total = 0;

        for(int row = 0; row < 3; row++)
            for(int col = 0; col < 3; col++)
                total += AVERAGING_MASK[row][col];

        for(int width = 1; width < imageWidth-1; width++){
            for(int height = 1; height < imageHeight-1; height++){
                float red = 0;
                float green = 0;
                float blue = 0;
                for(int s = -1; s <= 1; s++){
                    for(int t = -1; t <= 1; t++){
                        red = red + AVERAGING_MASK[1 - s][1 - t] * imageArray[width + s][height + t][1];
                        green = green + AVERAGING_MASK[1 - s][1 - t] * imageArray[width + s][height + t][2];
                        blue = blue + AVERAGING_MASK[1 - s][1 -t ] * imageArray[width + s][height + t][3];
                    }
                }
                averageMaskImage[width][height][1] = Math.round(Math.abs(red / total));
                averageMaskImage[width][height][2] = Math.round(Math.abs(green / total));
                averageMaskImage[width][height][3] = Math.round(Math.abs(blue / total));
            }
        }
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(averageMaskImage), 0, 1f);
    }

    public BufferedImage getWeightedAveragingMaskImage(BufferedImage bufferedImage){
        int[][][] imageArray = Utils.getRGBArray(bufferedImage);
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int[][][] averageMaskImage = new int[imageWidth][imageHeight][4];
        float total = 0;

        for(int row = 0; row < 3; row++)
            for(int col = 0; col < 3; col++)
                total += WEIGHTED_AVERAGING_MASK[row][col];

        for(int width = 1; width < imageWidth-1; width++){
            for(int height = 1; height < imageHeight-1; height++){
                float red = 0;
                float green = 0;
                float blue = 0;
                for(int s = -1; s <= 1; s++){
                    for(int t = -1; t <= 1; t++){
                        red = red + WEIGHTED_AVERAGING_MASK[1 - s][1 - t] * imageArray[width + s][height + t][1];
                        green = green + WEIGHTED_AVERAGING_MASK[1 - s][1 - t] * imageArray[width + s][height + t][2];
                        blue = blue + WEIGHTED_AVERAGING_MASK[1 - s][1 - t] * imageArray[width + s][height + t][3];
                    }
                }
                averageMaskImage[width][height][1] = Math.round(Math.abs(red / total));
                averageMaskImage[width][height][2] = Math.round(Math.abs(green / total));
                averageMaskImage[width][height][3] = Math.round(Math.abs(blue / total));
            }
        }
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(averageMaskImage), 1, 1f);
    }

    public int[][][] applyConvolution(BufferedImage bufferedImage, float[][] convolutionMask){
        int[][][] imageArray = Utils.getRGBArray(bufferedImage);
        int[][][] result = Utils.getRGBArray(bufferedImage);
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();

        for(int width = 1; width < imageWidth-1; width++){
            for(int height = 1; height < imageHeight-1; height++){
                float red = 0;
                float green = 0;
                float blue = 0;

                for(int t = -1; t <= 1; t++){
                    for(int s = -1; s <= 1; s++){
                        red = red + convolutionMask[1 - t][1 - s] * imageArray[width + t][height + s][1];
                        green = green + convolutionMask[1 - t][1 - s] * imageArray[width + t][height + s][2];
                        blue = blue + convolutionMask[1 - t][1 - s] * imageArray[width + t][height + s][3];
                    }
                }
                result[width][height][1] = Math.round(Math.abs(red));
                result[width][height][2] = Math.round(Math.abs(green));
                result[width][height][3] = Math.round(Math.abs(blue));
            }
        }
        return result;
    }

    public BufferedImage getFourNeighbourLaplacianImage(BufferedImage bufferedImage){
        int[][][] result = applyConvolution(bufferedImage, FOUR_NEIGHBOUR_LAPLACIAN);
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(result), 0, 1f);
    }

    public BufferedImage getEightNeighbourLaplacianImage(BufferedImage bufferedImage){
        int[][][] result = applyConvolution(bufferedImage, EIGHT_NEIGHBOUR_LAPLACIAN);
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(result), 0, 1f);
    }

    public BufferedImage getFourNeighbourLaplacianEnhancementImage(BufferedImage bufferedImage){
        int[][][] result = applyConvolution(bufferedImage, FOUR_NEIGHBOUR_LAPLACIAN_ENHANCEMENT);
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(result), 0, 1f);
    }

    public BufferedImage getEightNeighbourLaplacianEnhancementImage(BufferedImage bufferedImage){
        int[][][] result = applyConvolution(bufferedImage, EIGHT_NEIGHBOUR_LAPLACIAN_ENHANCEMENT);
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(result), 0, 1f);
    }

    public BufferedImage getRobertsOneMaskImage(BufferedImage bufferedImage){
        int[][][] result = applyConvolution(bufferedImage, ROBERTS_ONE);
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(result), 0, 1f);
    }

    public BufferedImage getRobertsTwoMaskImage(BufferedImage bufferedImage){
        int[][][] result = applyConvolution(bufferedImage, ROBERTS_TWO);
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(result), 0, 1f);
    }

    public BufferedImage getSobelXMaskImage(BufferedImage bufferedImage){
        int[][][] result = applyConvolution(bufferedImage, SOBEL_X);
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(result), 0, 1f);
    }

    public BufferedImage getSobelYMaskImage(BufferedImage bufferedImage){
        int[][][] result = applyConvolution(bufferedImage, SOBEL_Y);
        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(result), 0, 1f);
    }
}
