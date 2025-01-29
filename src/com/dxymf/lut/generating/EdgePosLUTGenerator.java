package com.dxymf.lut.generating;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.dxymf.Move;

public class EdgePosLUTGenerator {

    private static final int N = 12; // Total number of edges on the cube
    private static final int K = 11; // Number of edges being observed

    // Data of the LUT
    public byte[] lut = new byte[pick(N, K)];

    // Look up array of precomputed values for pick(N - 1 - i, K - 1 - i)
    private int[] picks = new int[K];

    // Look up array containing the number of "1" bits in the index
    private int[] onesCountLookup = new int[(1 << N) - 1];


    public EdgePosLUTGenerator() {

        // Populate picks with binomial coefficients
        for(int i = 0; i < K; i++) {
            picks[i] = pick(N - 1 - i, K - 1 - i);
        }

        // Populate onesCountLookup
        for(int i = 0; i < (1 << N) - 1; i++) {
            onesCountLookup[i] = count1Bits(i);
        }
    }


    // Generate PDB
    public void generateLUT() {
        // Fill LUT with -1 (unexplored states)
        Arrays.fill(lut, (byte) -1);

        System.out.println("Starting generating Edge LUT");
        long t1 = System.currentTimeMillis();

        // Create a solved starting state
        EdgePosState startingState = EdgePosState.getSolvedState();

        // Add starting state to LUT with depth 0
        lut[encode(startingState)] = 0;

        Queue<EdgePosState> queue = new LinkedList<>();
        queue.offer(startingState);

        // BFS to generate all possible states of edges and the corresponding depth
        int newStates = 1;
        while(!queue.isEmpty()) {

            EdgePosState currentState = queue.poll();

            byte prevMoveType = currentState.getPrevMove();

            int currentDepth = lut[encode(currentState)];

            // Apply all possible moves
            for(byte moveType = 1; moveType <= 6; moveType++) {

                // Simple move pruning
                if(moveType == prevMoveType) continue;
    
                // Apply moves that haven't been pruned
                for(byte moveDirection = 1; moveDirection <= 3; moveDirection++) {
    
                    EdgePosState clonedState = currentState.clone();

                    clonedState.applyMove(new Move(moveType, moveDirection));
                    clonedState.setPrevMove(moveType);
    
                    int edgesEncoded = encode(clonedState);

                    // If the new state has not been seen before -> record it to LUT and add it to queue
                    if((lut[edgesEncoded] != -1)) continue;

                    lut[edgesEncoded] = (byte) (currentDepth + 1);
                    newStates++;
                    queue.offer(clonedState);
                }
            }
        }
        
        System.out.println("Done generating Edge LUT");
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
    private int encode(EdgePosState state) {

        byte[] positions = Arrays.copyOf(state.getEdgePositions(), K);

        // Combine encoded positions and encoded orientations to a unique integer
        // positionsEncoded is scaled by 2^K because orientationsEncoded is at most 2^K - 1
        return lehmerRankLinear(positions);
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
    private static int pick(int n, int k) {

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
}
