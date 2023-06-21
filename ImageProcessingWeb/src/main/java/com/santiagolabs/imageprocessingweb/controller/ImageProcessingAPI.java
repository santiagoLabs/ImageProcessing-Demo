package com.santiagolabs.imageprocessingweb.controller;

import com.santiagolabs.imageprocessingweb.AlgorithmEnum;
import com.santiagolabs.imageprocessingweb.Utils;
import com.santiagolabs.imageprocessingweb.algorithm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/imageProcessing")
@CrossOrigin
public class ImageProcessingAPI {

    @Autowired
    private MixedAlgorithms mixedAlgorithms;

    @Autowired
    private BitOperations bitOperations;

    @Autowired
    private ArithmeticOperations arithmeticOperations;

    @Autowired
    private Convolutions convolutions;

    @Autowired
    private Transformations transformations;

    @RequestMapping(value = "/process", method = POST)
    @ResponseBody
    public byte[] processImage(@RequestParam("image") MultipartFile image, @RequestParam("algorithm") String algorithm, @RequestParam("scalingFactor") Optional<String> scalingFactor,  @RequestParam("shiftingValue") Optional<String> shiftingValue, @RequestParam("nBit") Optional<String> nBit)
            throws IOException
    {
        AlgorithmEnum type = AlgorithmEnum.fromDescription(algorithm);
        byte[] imageData = image.getBytes();
        float scalingFactorFloat = Float.parseFloat(scalingFactor.orElse("0"));
        int shiftingValueInt = Integer.parseInt(shiftingValue.orElse("0"));
        int nBitInt = Integer.parseInt(nBit.orElse("0"));

        switch (type) {
            case NEGATIVE -> {
                 return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(mixedAlgorithms.getNegativeImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case RESCALING -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(mixedAlgorithms.getRescaledImage(Utils.byteArrayToBufferedImage(imageData), scalingFactorFloat)));
            }
            case SHIFT_AND_RESCALE -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(mixedAlgorithms.getShiftedAndRescaledImage(Utils.byteArrayToBufferedImage(imageData), shiftingValueInt, scalingFactorFloat)));
            }
            case BIT_PLANE_SLICING -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(mixedAlgorithms.getBitplaneSlicingImage(Utils.byteArrayToBufferedImage(imageData), nBitInt)));
            }
            case SALT_AND_PEPPER_NOISE -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(mixedAlgorithms.saltPepper(Utils.byteArrayToBufferedImage(imageData))));
            }
        }

        return null;
    }

    @RequestMapping(value = "/process/arithmeticOperations", method = POST)
    @ResponseBody
    public byte[] processArithmeticOperations(@RequestParam("image") MultipartFile image, @RequestParam("algorithm") String algorithm, @RequestParam("secondImage") MultipartFile secondImage)
            throws IOException {
        AlgorithmEnum type = AlgorithmEnum.fromDescription(algorithm);
        byte[] imageData = image.getBytes();
        byte[] imageData2 = secondImage.getBytes();

        switch (type) {
            case ADDITION -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(arithmeticOperations.getAdditionOpImage(Utils.byteArrayToBufferedImage(imageData), Utils.byteArrayToBufferedImage(imageData2))));
            }
            case SUBTRACTION -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(arithmeticOperations.getSubstractionOpImage(Utils.byteArrayToBufferedImage(imageData), Utils.byteArrayToBufferedImage(imageData2))));
            }
            case MULTIPLICATION -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(arithmeticOperations.getMultiplicationOpImage(Utils.byteArrayToBufferedImage(imageData), Utils.byteArrayToBufferedImage(imageData2))));
            }
            case DIVISION -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(arithmeticOperations.getDivisionOpImage(Utils.byteArrayToBufferedImage(imageData), Utils.byteArrayToBufferedImage(imageData2))));
            }
        }

        return null;
    }

    @RequestMapping(value = "/process/bitOperations", method = POST)
    @ResponseBody
    public byte[] processBitOperations(@RequestParam("image") MultipartFile image, @RequestParam("algorithm") String algorithm, @RequestParam("secondImage") Optional<MultipartFile> secondImage)
            throws IOException {
        AlgorithmEnum type = AlgorithmEnum.fromDescription(algorithm);
        byte[] imageData = image.getBytes();
        if (algorithm.equalsIgnoreCase(AlgorithmEnum.BITWISE_NOT.toString()))
            return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(bitOperations.getBitwiseNotImage(Utils.byteArrayToBufferedImage(imageData))));

        if (secondImage.isEmpty())
            return null;

        byte[] imageData2 = secondImage.get().getBytes();
        switch (type) {
            case BITWISE_AND -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(bitOperations.getBitwiseAndImage(Utils.byteArrayToBufferedImage(imageData), Utils.byteArrayToBufferedImage(imageData2))));
            }
            case BITWISE_OR -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(bitOperations.getBitwiseOrImage(Utils.byteArrayToBufferedImage(imageData), Utils.byteArrayToBufferedImage(imageData2))));
            }
            case BITWISE_XOR -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(bitOperations.getBitwiseXorImage(Utils.byteArrayToBufferedImage(imageData), Utils.byteArrayToBufferedImage(imageData2))));
            }
        }

        return null;
    }

    @RequestMapping(value = "/process/transformations", method = POST)
    @ResponseBody
    public byte[] processTransformations(@RequestParam("image") MultipartFile image, @RequestParam("algorithm") String algorithm, @RequestParam("param") Optional<String> parameter)
            throws IOException {
        AlgorithmEnum type = AlgorithmEnum.fromDescription(algorithm);
        int param = Integer.parseInt(parameter.orElse("0"));
        byte[] imageData = image.getBytes();

        switch (type) {
            case LOGARITHMIC_TRANSFORMATION -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(transformations.getLogTransformationImage(Utils.byteArrayToBufferedImage(imageData), param)));
            }
            case POWER_LAW -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(transformations.getPowerLawImage(Utils.byteArrayToBufferedImage(imageData), param)));
            }
            case RANDOM_LUT -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(transformations.getRandomLutImage(Utils.byteArrayToBufferedImage(imageData))));
            }
        }
        return null;
    }

    @RequestMapping(value = "/process/convolution", method = POST)
    @ResponseBody
    public byte[] processConvolution(@RequestParam("image") MultipartFile image, @RequestParam("algorithm") String algorithm)
            throws IOException {
        AlgorithmEnum type = AlgorithmEnum.fromDescription(algorithm);
        byte[] imageData = image.getBytes();

        switch (type) {
            case CONVOLUTION_AVERAGING -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getAveragingMaskImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_WEIGHTED_AVERAGING -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getWeightedAveragingMaskImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_FOUR_NEIGHBOUR_LAPLACIAN -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getFourNeighbourLaplacianImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_EIGHT_NEIGHBOUR_LAPLACIAN -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getEightNeighbourLaplacianImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_FOUR_NEIGHBOUR_LAPLACIAN_ENHANCEMENT -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getFourNeighbourLaplacianEnhancementImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_EIGHT_NEIGHBOUR_LAPLACIAN_ENHANCEMENT -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getEightNeighbourLaplacianEnhancementImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_ROBERTS_ONE -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getRobertsOneMaskImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_ROBERTS_TWO -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getRobertsTwoMaskImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_SOBEL_X -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getSobelXMaskImage(Utils.byteArrayToBufferedImage(imageData))));
            }
            case CONVOLUTION_SOBEL_Y -> {
                return Base64.getEncoder().encode(Utils.bufferedImageToByteArray(convolutions.getSobelYMaskImage(Utils.byteArrayToBufferedImage(imageData))));
            }
        }
        return null;
    }
}
