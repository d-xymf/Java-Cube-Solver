package com.dxymf.lut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;

import com.dxymf.CubeState;

public abstract class LUT {
    protected int N; // Total number of corners/edges on the cube
    protected int K; // Number of corners/edges being observed

    // Data of the LUT
    protected byte[] lut;

    // Look up array of precomputed values for pick(N - 1 - i, K - 1 - i)
    protected int[] picks;

    // Look up array containing the number of "1" bits in the index
    protected int[] onesCountLookup;
    
    public LUT(int N, int K) {
        this.N = N;
        this.K = K;

        picks = new int[K];
        onesCountLookup = new int[(1 << N) - 1];

        // Populate picks
        for(int i = 0; i < K; i++) {
            picks[i] = pick(N - 1 - i, K - 1 - i);
        }

        // Populate onesCountLookup
        for(int i = 0; i < (1 << N) - 1; i++) {
            onesCountLookup[i] = count1Bits(i);
        }
    }


    // Load LUT from binary file
    public void loadLUT(String fileName) throws IOException {

        lut = Files.readAllBytes(Path.of(fileName));
    }


    // Retrieve value from LUT
    public int getMoveCount(CubeState state) {

        return lut[encode(state)];
    }


    // Generate key from a cube state using lexicographic ranking
    protected abstract int encode(CubeState state);


    // Linear algorithm for indexing a permutation using Lehmer codes
    // Using ints instead of BitSets
    protected int lehmerRankLinear(byte[] perm) {

        // The first digit of the Lehmer code is always the first digit of the permutation
        int index = perm[0] * picks[0];

        // We use an int instead of a BitSet because it is faster
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


    // Linear algorithm for indexing a permutation using Lehmer codes
    // With BitSets
    protected int lehmerRankBitSets(byte[] perm) {

        // The first digit of the Lehmer code is always the first digit of the permutation
        int index = perm[0] * picks[0];

        // BitSet to keep track of the seen elements
        BitSet seen = new BitSet(N);
        seen.set(N - 1 - perm[0]);

        for(int i = 1; i < K; i++) {
            // Set bit at position N - 1 - perm[i]
            seen.set(N - 1 - perm[i]);

            // onesCountLookup[(int)(seen.toLongArray()[0]) >> (N - perm[i])]
            // -> convert seen to an int
            // shift int by N - perm[i] bits to the right
            // and count the number of remaining bits which is equal to the number of smaller
            // elements to the left in the permutation
            index += (perm[i] - onesCountLookup[(int)(seen.toLongArray()[0]) >> (N - perm[i])]) * picks[i];
        }

        return index;
    }


    // Count the number of 1 bits in an integer
    protected int count1Bits(int n) {
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


    // n pick k
    protected static int pick(int n, int k) {

        return factorial(n) / factorial(n - k);
    }

    // Factorial of n
    protected static int factorial(int n) {
    
        if(n <= 1) return 1;

        return n * factorial(n - 1);
    }
}
