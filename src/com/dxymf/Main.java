package com.dxymf;

import java.util.Random;

import com.dxymf.lut.CornerLUT;
import com.dxymf.lut.EdgeLUT;
import com.dxymf.lut.EdgePosLUT;
import com.dxymf.lut.generating.CornerLUTGenerator;
import com.dxymf.lut.generating.EdgeLUTGenerator;
import com.dxymf.lut.generating.EdgePosLUTGenerator;

public class Main {

    public static void main(String[] args) {
        // Change this depending on what you want to do with the programm
        // solveFromConsole() -> User enters a state through console and program solves that state
        // solveRandomScramble(int moveCount) -> A random scramble is generated with a random walk then solved
        // moveCount is the number of moves for the random walk
        // generateXLUT(String filename) -> Generates and saves that LUT
        // With edge LUTs pass in byte array of the edges that will be used for generating
        // Example: generateEdgeLUT("edgelutsmall", new byte[] {0, 1, 2, 3, 4, 5});
        solveRandomScramble(15);
    }

    private static void solveFromConsole() {
        // Initialize LUTs
        CornerLUT cornerlut = new CornerLUT();
        EdgeLUT edgelut1 = new EdgeLUT(new byte[] {1, 3, 4, 6, 7, 8, 10}); // Change byte array if you want to use luts with different edge pieces
        EdgeLUT edgelut2 = new EdgeLUT(new byte[] {0, 2, 5, 6, 7, 9, 11}); // Change byte array if you want to use luts with different edge pieces
        EdgePosLUT edgeposlut = new EdgePosLUT();

        // Get the state from console input
        CubeState state = CubeState.fromInput();

        // Print the entered state
        System.out.println("The entered CubeState: ");
        System.out.println(state.toString());

        // Load LUTs from files
        try{
            System.out.println("Loading LUTs");

            cornerlut.loadLUT("corner_lut");
            edgelut1.loadLUT("7_edge_lut_1");
            edgelut2.loadLUT("7_edge_lut_2");
            edgeposlut.loadLUT("12_edge_pos_lut");

        } catch(Exception e) {
            
            System.out.println("Could not load LUTs");
            System.out.println(e);
            return;
        }
        
        // Solve the cube
        Solver solver = new Solver(cornerlut, edgelut1, edgelut2, edgeposlut);

        Move[] solution = solver.solve(state);

        // Convert the solution to a string
        String solutionString = "";
        int solutionLength = 0;
        for(Move move : solution) {
            if(move != null) {
                solutionString += move.toString() + " ";
                solutionLength++;
            }
        }

        System.out.println("Solution: " + solutionString);
        System.out.println("Length: " + solutionLength);
    }

    private static void generateCornerLUT(String filename) {
        CornerLUTGenerator lutGenerator = new CornerLUTGenerator();

        lutGenerator.generateLUT();

        try{
            System.out.println("Saving LUT");

            lutGenerator.saveLUT(filename);

        } catch(Exception e) {
            
            System.out.println("Could not save LUT");
            System.out.println(e);
            return;
        }
    }

    private static void generateEdgeLUT(String filename, byte[] edges) {
        EdgeLUTGenerator lutGenerator = new EdgeLUTGenerator(edges);

        lutGenerator.generateLUT();

        try{
            System.out.println("Saving LUT");

            lutGenerator.saveLUT(filename);

        } catch(Exception e) {
            
            System.out.println("Could not save LUT");
            System.out.println(e);
            return;
        }
    }

    private static void generateEdgePosLUT(String filename) {
        EdgePosLUTGenerator lutGenerator = new EdgePosLUTGenerator();

        lutGenerator.generateLUT();

        try{
            System.out.println("Saving LUT");

            lutGenerator.saveLUT(filename);

        } catch(Exception e) {
            
            System.out.println("Could not save LUT");
            System.out.println(e);
            return;
        }
    }

    private static void solveRandomScramble(int moveCount) {
        Random rand = new Random(System.currentTimeMillis());

        // Initialize LUTs
        CornerLUT cornerlut = new CornerLUT();
        EdgeLUT edgelut1 = new EdgeLUT(new byte[] {1, 3, 4, 6, 7, 8, 10});
        EdgeLUT edgelut2 = new EdgeLUT(new byte[] {0, 2, 5, 6, 7, 9, 11});
        EdgePosLUT edgeposlut = new EdgePosLUT();

        CubeState state = CubeState.getSolvedState();

        for(int i = 0; i < moveCount; i++) {
            state.applyMove(randomMove(rand));
        }

        try{
            System.out.println("Loading LUTs");

            cornerlut.loadLUT("corner_lut");
            edgelut1.loadLUT("7_edge_lut_1");
            edgelut2.loadLUT("7_edge_lut_2");
            edgeposlut.loadLUT("12_edge_pos_lut");

        } catch(Exception e) {
            
            System.out.println("Could not load LUTs");
            System.out.println(e);
            return;
        }

        System.out.println(state.toString());
        
        // Solve cube
        Solver solver = new Solver(cornerlut, edgelut1, edgelut2, edgeposlut);

        Move[] solution = solver.solve(state);

        // Convert solution to string
        String solutionString = "";
        int solutionLength = 0;
        for(Move move : solution) {
            if(move != null) {
                solutionString += move.toString() + " ";
                solutionLength++;
            }
        }

        System.out.println("Solution: " + solutionString);
        System.out.println("Length: " + solutionLength);
    }

    private static Move randomMove(Random rand) {
        byte moveType = (byte)(rand.nextInt(5) + 1);
        byte moveDir = (byte)(rand.nextInt(2) + 1);
        return new Move(moveType, moveDir);
    }
}
