package com.dxymf.lut.generating;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.dxymf.Move;

import java.io.*;

public class CornerLUTGenerator {

    private static final int N = 8; // Total number of corners on the cube
    private static final int K = 7; // Number of corners being observed

    private byte[] lut = new byte[88179840];

    // Array of precomputed values for binomial coefficients instead of binomialCoefficient() function
    private int[] picks = new int[K];

    // Array containing the number of "1" bits in the index
    private int[] onesCountLookup = new int[(1 << N) - 1];

    // Array of powers of 3
    private int[] powers = new int[K + 1];
    

    public CornerLUTGenerator() {

        // Populate picks with binomial coefficients
        for(int i = 0; i < K; i++) {
            picks[i] = pick(N - 1 - i, K - 1 - i);
        }

        // Populate onesCountLookup
        for(int i = 0; i < (1 << N) - 1; i++) {
            onesCountLookup[i] = count1Bits(i);
        }

        // Populate powers with the powers of 3
        for(int i = 0; i < K + 1; i++) {
            powers[i] = pow(3, i);
        }
    }


    // Generate PDB
    public void generateLUT() {
        // Fill LUT with -1 (unexplored states)
        Arrays.fill(lut, (byte) -1);

        System.out.println("Starting generating Corner LUT");
        long t1 = System.currentTimeMillis();

        // Create a solved starting state
        CornerState startingState = CornerState.getSolvedState();

        // Add starting state to LUT with depth 0
        lut[encode(startingState)] = 0;

        Queue<CornerState> queue = new LinkedList<>();
        queue.offer(startingState);

        // BFS to generate all possible states of corners and the corresponding depth
        int newStates = 1;
        while(!queue.isEmpty()) {

            CornerState currentState = queue.poll();
            byte prevMoveType = currentState.getPrevMove();

            int currentDepth = lut[encode(currentState)];

            // Apply all possible moves
            for(byte moveType = 1; moveType <= 6; moveType++) {

                // Simple move pruning
                if(moveType == prevMoveType) continue;
    
                // Apply moves that haven't been pruned
                for(byte moveDirection = 1; moveDirection <= 3; moveDirection++) {
    
                    CornerState clonedState = currentState.clone();
    
                    Move move = new Move(moveType, moveDirection);
                    clonedState.applyMove(move);
                    clonedState.setPrevMove(moveType);
    
                    int cornersEncoded = encode(clonedState);

                    // If the new state has not been seen before -> record it to LUT and add it to queue
                    if((lut[cornersEncoded] != -1)) continue;

                    lut[cornersEncoded] = (byte) (currentDepth + 1);
                    newStates++;
                    queue.offer(clonedState);
                }
            }
        }
        
        System.out.println("Done generating Corner LUT");
        System.out.println("Time taken: " + (System.currentTimeMillis() - t1) + "ms");
        System.out.println("Number of unique states: " + newStates);
    }


    // Save LUT to binary file
    public void saveLUT(String fileName) throws IOException {

        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName))) {

            for(byte moveCount : lut) {

                dos.writeByte(moveCount);
            }
        }
    }


    // Generate key from a cube state using lexicographic ranking
    private int encode(CornerState state) {

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


    // Linear algorithm for indexing a permutation using Lehmer codes
    private int lehmerRankLinear(byte[] perm) {

        // The first digit of the Lehmer code is always the first digit of the permutation
        int index = perm[0] * picks[0];

        // We use an int instead of a BitSet because Java BitSets cannot be converted
        // to an int in linear time
        int seen = 1 << (N - 1 - perm[0]);

        for(int i = 1; i < K; i++) {
            // Set bit at position N - 1 - perm[i] of seen to 1
            seen += 1 << (N - 1 - perm[i]);

            // onesCountLookup[seen >> (N - perm[i])] shift seen by N - perm[i] bits to the right
            // and count the number of remaining bits which is equal to the number of smaller
            // elements to the left in the permutation
            index += (perm[i] - onesCountLookup[seen >> (N - perm[i])]) * picks[i];
        }

        return index;
    }
    

    // n pick k
    private int pick(int n, int k) {

        return factorial(n) / factorial(n - k);
    }

    // Factorial of n
    private static int factorial(int n) {
    
        if(n <= 1) return 1;

        return n * factorial(n - 1);
    }

    // Count the number of 1 bits in an integer
    private static int count1Bits(int n) {
        int remaining = n;
        int numOnes = 0;

        for(int i = 1 << N; i > 0; i = i >> 1) {
            if(i <= remaining) {
                numOnes++;
                remaining -= i;
            }
        }
        return numOnes;
    }

    // b to the power of e
    private static int pow(int b, int e) {
        if(e == 0) return 1;

        return b * pow(b, e - 1);
    }
}
