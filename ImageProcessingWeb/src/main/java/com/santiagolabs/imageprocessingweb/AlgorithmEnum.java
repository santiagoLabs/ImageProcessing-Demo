package com.santiagolabs.imageprocessingweb;

public enum AlgorithmEnum {
    NEGATIVE("Negative"),
    RESCALING("Rescaling"),
    SHIFT_AND_RESCALE("Shift&Rescale"),
    ADDITION("Addition"),
    SUBTRACTION("Substraction"),
    MULTIPLICATION("Multiplication"),
    DIVISION("Division"),
    BITWISE_NOT("Bitwise Not"),
    BITWISE_AND("Bitwise And"),
    BITWISE_OR("Bitwise Or"),
    BITWISE_XOR("Bitwise Xor"),
    LOGARITHMIC_TRANSFORMATION("Logarithmic Transformation"),
    POWER_LAW("Power Law"),
    RANDOM_LUT("Random LUT"),
    BIT_PLANE_SLICING("Bit Plane Slicing"),
    CONVOLUTION_AVERAGING("Convolution - Averaging"),
    CONVOLUTION_WEIGHTED_AVERAGING("Convolution - Weighted averaging"),
    CONVOLUTION_FOUR_NEIGHBOUR_LAPLACIAN("Convolution - Four Neighbour Laplacian"),
    CONVOLUTION_EIGHT_NEIGHBOUR_LAPLACIAN("Convolution - Eight Neighbour Laplacian"),
    CONVOLUTION_FOUR_NEIGHBOUR_LAPLACIAN_ENHANCEMENT("Convolution - Four Neighbour Laplacian Enhancement"),
    CONVOLUTION_EIGHT_NEIGHBOUR_LAPLACIAN_ENHANCEMENT("Convolution - Eight Neighbour Laplacian Enhancement"),
    CONVOLUTION_ROBERTS_ONE("Convolution - Roberts One"),
    CONVOLUTION_ROBERTS_TWO("Convolution - Roberts Two"),
    CONVOLUTION_SOBEL_X("Convolution - Sobel X"),
    CONVOLUTION_SOBEL_Y("Convolution - Sobel Y"),
    SALT_AND_PEPPER_NOISE("Salt&Pepper noise");

    private final String description;

    AlgorithmEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static AlgorithmEnum fromDescription(String description) {
        for (AlgorithmEnum type : values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid image processing algorithm: " + description);
    }
}