package com.dxymf.lut;

import java.util.Arrays;

import com.dxymf.CubeState;

public class CornerLUT extends LUT {

    // Array of powers of 3
    private int[] powers;

    public CornerLUT() {
        super(8, 7);

        lut = new byte[factorial(N) * pow(3, K)];
        powers = new int[K + 1];

        // Populate powers with the powers of 3
        for(int i = 0; i < K + 1; i++) {
            powers[i] = pow(3, i);
        }
    }


    // Generate key from a cube state using lexicographic ranking
    protected int encode(CubeState state) {

        byte[] pieces = Arrays.copyOf(state.getCornerPositions(), K);
        byte[] orientations = Arrays.copyOf(state.getCornerOrientations(), K);

        int orientationsEncoded = 0;
        int piecesEncoded = 0;

        // Get and encode orientations and positions of the corner pieces
        for(int i = 0; i < K; i++) {
            // Encode orientations into the digits of a binary number
            orientationsEncoded += orientations[i] * powers[i];
        }

        piecesEncoded = lehmerRankLinear(pieces);

        // Combine encoded pieces and encoded orientations to a unique integer
        // piecesEncoded is scaled by 3^K because orientationsEncoded is at most 3^K - 1
        return piecesEncoded * powers[K] + orientationsEncoded;
    }


    // b to the power of e
    private static int pow(int b, int e) {
        if(e == 0) return 1;

        return b * pow(b, e - 1);
    }
}
