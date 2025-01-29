package com.dxymf.lut;

import com.dxymf.CubeState;

public class EdgeLUT extends LUT {

    // Subset of Edges that is being observed
    private byte[] subset;

    public EdgeLUT(byte[] subset) {
        super(12, subset.length);

        this.subset = subset;

        lut = new byte[pick(N, K) * (1 << K)];
    }

    // Generate key from a cube state using lexicographic ranking
    protected int encode(CubeState state) {

        byte[] positions = CubeState.extractElementsFromArray(state.getEdgePositions(), subset);
        byte[] orientations = CubeState.extractElementsFromArray(state.getEdgeOrientations(), subset);

        int orientationsEncoded = 0;
        int positionsEncoded = 0;

        // Get and encode orientations and positions of the edges
        for(int i = 0; i < K; i++) {
            // Encode orientations into the digits of a binary number
            orientationsEncoded += orientations[i] * (1 << i);
        }

        positionsEncoded = lehmerRankLinear(positions);

        // Combine encoded positions and encoded orientations to a unique integer
        // positionsEncoded is scaled by 2^K because orientationsEncoded is at most 2^K - 1
        return positionsEncoded * (1 << K) + orientationsEncoded;
    }
}
