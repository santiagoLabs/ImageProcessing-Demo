package com.santiagolabs.imageprocessingweb.algorithm;

import com.santiagolabs.imageprocessingweb.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class ArithmeticOperations {
    @Autowired
    private MixedAlgorithms mixedAlgorithms;

    public BufferedImage getAdditionOpImage(BufferedImage bufferedImage1, BufferedImage bufferedImage2) {
        int maxHeight = Math.max(bufferedImage1.getHeight(), bufferedImage2.getHeight());
        int maxWidth = Math.max(bufferedImage1.getWidth(), bufferedImage2.getWidth());
        int[][][] imageArray1 = Utils.getRGBArray(bufferedImage1);
        int[][][] imageArray2 = Utils.getRGBArray(bufferedImage2);
        int[][][] resultImage = new int[maxWidth][maxHeight][4];

        for (int width = 0; width < maxWidth; width++) {
            for (int height = 0; height < maxHeight; height++) {
                if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3];
                }
                if (width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] += imageArray2[width][height][1];
                    resultImage[width][height][2] += imageArray2[width][height][2];
                    resultImage[width][height][3] += imageArray2[width][height][3];
                }
            }
        }

        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(resultImage), 0, 1f);
    }

    public BufferedImage getSubstractionOpImage(BufferedImage bufferedImage1, BufferedImage bufferedImage2) {
        int maxHeight = Math.max(bufferedImage1.getHeight(), bufferedImage2.getHeight());
        int maxWidth = Math.max(bufferedImage1.getWidth(), bufferedImage2.getWidth());
        int[][][] imageArray1 = Utils.getRGBArray(bufferedImage1);
        int[][][] imageArray2 = Utils.getRGBArray(bufferedImage2);
        int[][][] resultImage = new int[maxWidth][maxHeight][4];

        for (int width = 0; width < maxWidth; width++) {
            for (int height = 0; height < maxHeight; height++) {
                if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()
                        && width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1] - imageArray2[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2] - imageArray2[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3] - imageArray2[width][height][3];
                } else if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3];
                } else if (width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = -imageArray2[width][height][1];
                    resultImage[width][height][2] = -imageArray2[width][height][2];
                    resultImage[width][height][3] = -imageArray2[width][height][3];
                }
            }
        }

        return mixedAlgorithms.getShiftedAndRescaledImage(Utils.getBufferedImage(resultImage), 0, 1f);
    }

    public BufferedImage getMultiplicationOpImage(BufferedImage bufferedImage1, BufferedImage bufferedImage2) {
        int maxHeight = Math.max(bufferedImage1.getHeight(), bufferedImage2.getHeight());
        int maxWidth = Math.max(bufferedImage1.getWidth(), bufferedImage2.getWidth());
        int[][][] imageArray1 = Utils.getRGBArray(bufferedImage1);
        int[][][] imageArray2 = Utils.getRGBArray(bufferedImage2);
        int[][][] resultImage = new int[maxWidth][maxHeight][4];

        for (int width = 0; width < maxWidth; width++) {
            for (int height = 0; height < maxHeight; height++) {
                if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()
                        && width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    resultImage[width][height][1] = imageArray1[width][height][1] * imageArray2[width][height][1];
                    resultImage[width][height][2] = imageArray1[width][height][2] * imageArray2[width][height][2];
                    resultImage[width][height][3] = imageArray1[width][height][3] * imageArray2[width][height][3];
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

    public BufferedImage getDivisionOpImage(BufferedImage bufferedImage1, BufferedImage bufferedImage2) {
        int maxHeight = Math.max(bufferedImage1.getHeight(), bufferedImage2.getHeight());
        int maxWidth = Math.max(bufferedImage1.getWidth(), bufferedImage2.getWidth());
        int[][][] imageArray1 = Utils.getRGBArray(bufferedImage1);
        int[][][] imageArray2 = Utils.getRGBArray(bufferedImage2);
        int[][][] resultImage = new int[maxWidth][maxHeight][4];

        for (int width = 0; width < maxWidth; width++) {
            for (int height = 0; height < maxHeight; height++) {
                if (width < bufferedImage1.getWidth() && height < bufferedImage1.getHeight()
                        && width < bufferedImage2.getWidth() && height < bufferedImage2.getHeight()) {
                    if (imageArray1[width][height][1] == 0)
                        resultImage[width][height][1] = imageArray2[width][height][1];
                    else if (imageArray2[width][height][1] == 0)
                        resultImage[width][height][1] = imageArray1[width][height][1];
                    else
                        resultImage[width][height][1] = Math.max(imageArray1[width][height][1], imageArray2[width][height][1]) / Math.min(imageArray1[width][height][1], imageArray2[width][height][1]);

                    if (imageArray1[width][height][2] == 0)
                        resultImage[width][height][2] = imageArray2[width][height][2];
                    else if (imageArray2[width][height][2] == 0)
                        resultImage[width][height][2] = imageArray1[width][height][2];
                    else
                        resultImage[width][height][2] = Math.max(imageArray1[width][height][2], imageArray2[width][height][2]) / Math.min(imageArray1[width][height][2], imageArray2[width][height][2]);

                    if (imageArray1[width][height][3] == 0)
                        resultImage[width][height][3] = imageArray2[width][height][3];
                    else if (imageArray2[width][height][3] == 0)
                        resultImage[width][height][3] = imageArray1[width][height][3];
                    else
                        resultImage[width][height][3] = Math.max(imageArray1[width][height][3], imageArray2[width][height][3]) / Math.min(imageArray1[width][height][3], imageArray2[width][height][3]);
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
