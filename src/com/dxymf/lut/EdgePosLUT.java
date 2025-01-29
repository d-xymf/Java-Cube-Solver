package com.dxymf.lut;

import java.util.Arrays;

import com.dxymf.CubeState;

public class EdgePosLUT extends LUT {

    public EdgePosLUT() {
        super(12, 11);

        lut = new byte[factorial(N)];
    }

    // Generate key from a cube state using lexicographic ranking
    protected int encode(CubeState state) {

        byte[] positions = Arrays.copyOf(state.getEdgePositions(), K);

        // Encode positions
        return lehmerRankLinear(positions);
    }
}
