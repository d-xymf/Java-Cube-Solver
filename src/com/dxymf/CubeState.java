package com.dxymf;

import java.util.Arrays;
import java.util.Scanner;

public class CubeState {

    private byte[] cornerPositions;
    private byte[] cornerOrientations;
    private byte[] edgePositions;
    private byte[] edgeOrientations;

    public CubeState(byte[] cP, byte[] cO, byte[] eP, byte[] eO) {
        cornerPositions = cP;
        cornerOrientations = cO;
        edgePositions = eP;
        edgeOrientations = eO;
    }

    public void applyMove(Move move) {

        // Clone the whole state
        byte[] newCornerPositions = cornerPositions.clone();
        byte[] newCornerOrientations = cornerOrientations.clone();
        byte[] newEdgePositions = edgePositions.clone();
        byte[] newEdgeOrientations = edgeOrientations.clone();

        byte type = move.getType();
        byte direction = move.getDirection();

        // These will be a kind of LUT for how the orientations and positions of pieces should change
        byte[] cornerPositionChanges;
        byte[] cornerOrientationChanges;
        byte[] edgePositionChanges;
        byte[] edgeOrientationChanges;

        // Set these LUTs depending on the type of move
        switch(type) {
            // R move
            case 1:
                {
                    cornerPositionChanges = new byte[] {-1, 5, 1, -1, -1, 6, 2, -1};
                    cornerOrientationChanges = new byte[] {1, 0, 2};

                    edgePositionChanges = new byte[] {-1, 5, -1, -1, -1, 9, 1, -1, -1, 6, -1, -1};
                    edgeOrientationChanges = new byte[] {0, 1};
                }
                break;

            // U move
            case 2:
                {
                    cornerPositionChanges = new byte[] {1, 2, 3, 0, -1, -1, -1, -1};
                    cornerOrientationChanges = new byte[] {0, 2, 1};

                    edgePositionChanges = new byte[] {1, 2, 3, 0, -1, -1, -1, -1, -1, -1, -1, -1};
                    edgeOrientationChanges = new byte[] {0, 1};
                }
                break;

            // F move
            case 3:
                {
                    cornerPositionChanges = new byte[] {-1, -1, 6, 2, -1, -1, 7, 3};
                    cornerOrientationChanges = new byte[] {2, 1, 0};

                    edgePositionChanges = new byte[] {-1, -1, 6, -1, -1, -1, 10, 2, -1, -1, 7, -1};
                    edgeOrientationChanges = new byte[] {1, 0};
                }
                break;

            // L move
            case 4:
                {
                    cornerPositionChanges = new byte[] {3, -1, -1, 7, 0, -1, -1, 4};
                    cornerOrientationChanges = new byte[] {1, 0, 2};

                    edgePositionChanges = new byte[] {-1, -1, -1, 7, 3, -1, -1, 11, -1, -1, -1, 4};
                    edgeOrientationChanges = new byte[] {0, 1};
                }
                break;

            // D move
            case 5:
                {
                    cornerPositionChanges = new byte[] {-1, -1, -1, -1, 7, 4, 5, 6};
                    cornerOrientationChanges = new byte[] {0, 2, 1};

                    edgePositionChanges = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, 11, 8, 9, 10};
                    edgeOrientationChanges = new byte[] {0, 1};
                }
                break;

            // B move
            case 6:
                {
                    cornerPositionChanges = new byte[] {4, 0, -1, -1, 5, 1, -1, -1};
                    cornerOrientationChanges = new byte[] {2, 1, 0};

                    edgePositionChanges = new byte[] {4, -1, -1, -1, 8, 0, -1, -1, 5, -1, -1, -1};
                    edgeOrientationChanges = new byte[] {1, 0};
                }
                break;
            
            // Default case that initializes the arrays so compiler doesn't complain
            default:
                {
                    cornerPositionChanges = new byte[8];
                    cornerOrientationChanges = new byte[3];
            
                    edgePositionChanges = new byte[12];
                    edgeOrientationChanges = new byte[2];
                }
        }

        // direction is the number of times a clockwise turn should be applied
        // (direction = 1 -> clockwise)
        // (direction = 2 -> double turn)
        // (direction = 3 -> anti-clockwise)
        for(int i = 0; i < direction; i++) {

            // Apply changes to corners
            for(int j = 0; j < 8; j++) {
                // Skip pieces that aren't affected by the move
                if(cornerPositionChanges[cornerPositions[j]] == -1) continue;

                // Apply changes
                newCornerPositions[j] = cornerPositionChanges[cornerPositions[j]];
                newCornerOrientations[j] = cornerOrientationChanges[cornerOrientations[j]];
            }

            // Apply changes to edges
            for(int j = 0; j < 12; j++) {
                // Skip pieces that aren't affected by the move
                if(edgePositionChanges[edgePositions[j]] == -1) continue;

                // Apply changes
                newEdgePositions[j] = edgePositionChanges[edgePositions[j]];
                newEdgeOrientations[j] = edgeOrientationChanges[edgeOrientations[j]];
            }

            // We clone the arrays once more because not doing so can cause issues
            cornerPositions = newCornerPositions.clone();
            cornerOrientations = newCornerOrientations.clone();
            edgePositions = newEdgePositions.clone();
            edgeOrientations = newEdgeOrientations.clone();
        }
    }

    public String toString() {
        // Array of colors to easily look up the colors of a certain piece
        String[] cornerColors = {"WBO", "WBR", "WGR", "WGO", "YBO", "YBR", "YGR", "YGO"};
        String[] edgeColors = {"WB", "WR", "WG", "WO", "BO", "BR", "GR", "GO", "YB", "YR", "YG", "YO"};

        // Template string of positions
        String pieceString = 
        "             0 8 1 \n" + 
        "             11  W   9 \n" + 
        "             3 10 2 \n" + 
        " 0 11 3  3 10 2  2 9 1  1 8 0 \n" + 
        " 12  O   15  15  G   14  14  R   13  13  B   12 \n" + 
        " 4 19 7  7 18 6  6 17 5  5 16 4 \n" + 
        "             7 18 6 \n" + 
        "             19  Y   17 \n" + 
        "             4 16 5 \n"
        ;

        // Replace each position of template with the colors of the piece at that position
        for(int i = 0; i < 8; i++) {
            pieceString = pieceString.replace(" "+String.valueOf(cornerPositions[i])+" ", " "+cornerColors[i]+" ");
        }
        for(int i = 0; i < 12; i++) {
            pieceString = pieceString.replace(" "+String.valueOf(edgePositions[i] + 8)+" ", " "+edgeColors[i]+" ");
        }


        String[] cornerOrientationStrings = {"T", "F", "S"};
        String[] edgeOrientationStrings = {"G", "B"};

        // Template string of positions
        String orientationString = 
        "        0 8 1 \n" + 
        "        11 W 9 \n" + 
        "        3 10 2 \n" + 
        " 0 11 3  3 10 2  2 9 1  1 8 0 \n" + 
        " 12 O 15  15 G 14  14 R 13  13 B 12 \n" + 
        " 4 19 7  7 18 6  6 17 5  5 16 4 \n" + 
        "        7 18 6 \n" + 
        "        19 Y 17 \n" + 
        "        4 16 5 \n"
        ;

        // Replace each position of template with the orientation of the piece at that position
        for(int i = 0; i < 8; i++) {
            orientationString = orientationString.replace(" "+String.valueOf(cornerPositions[i])+" ", " "+cornerOrientationStrings[cornerOrientations[i]]+" ");
        }
        for(int i = 0; i < 12; i++) {
            orientationString = orientationString.replace(" "+String.valueOf(edgePositions[i] + 8)+" ", " "+edgeOrientationStrings[edgeOrientations[i]]+" ");
        }
        
        return pieceString + orientationString;
    }

    public boolean isSolved() {
        // Arrays of a solved state
        byte[] cPSolved = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] cOSolved = {0, 0, 0, 0, 0, 0, 0, 0};
        byte[] ePSolved = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        byte[] eOSolved = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        boolean solved = true;

        // Check whether they are all equal
        solved = solved && Arrays.equals(cornerPositions, cPSolved);
        solved = solved && Arrays.equals(cornerOrientations, cOSolved);
        solved = solved && Arrays.equals(edgePositions, ePSolved);
        solved = solved && Arrays.equals(edgeOrientations, eOSolved);

        return solved;
    }

    public CubeState clone() {
        CubeState clone = new CubeState(cornerPositions.clone(), cornerOrientations.clone(), edgePositions.clone(), edgeOrientations.clone());
        
        return clone;
    }

    public byte[] getCornerPositions() {
        return cornerPositions.clone();
    }

    public byte[] getCornerOrientations() {
        return cornerOrientations.clone();
    }

    public byte[] getEdgePositions() {
        return edgePositions.clone();
    }

    public byte[] getEdgeOrientations() {
        return edgeOrientations.clone();
    }

    public static byte[] extractElementsFromArray(byte[] array, byte[] positions) {

        byte[] extracted = new byte[positions.length];

        for(int i = 0; i < positions.length; i++) {
            extracted[i] = array[positions[i]];
        }

        return extracted;
    }

    public static CubeState getSolvedState() {
        // Arrays of a solved state
        byte[] cPSolved = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] cOSolved = {0, 0, 0, 0, 0, 0, 0, 0};
        byte[] ePSolved = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        byte[] eOSolved = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        return new CubeState(cPSolved, cOSolved, ePSolved, eOSolved);
    }

    public static CubeState fromInput() {
        Scanner in = new Scanner(System.in);

        System.out.println("The following arrays must be entered with spaces as delimiter. Example: 0 3 2 8 5 4");

        // Read from console input and split into arrays of strings
        System.out.println("Enter corner positions");
        String cPString = in.nextLine();
        String[] cPStrings = cPString.split(" ");

        System.out.println("Enter corner orientations");
        String cOString = in.nextLine();
        String[] cOStrings = cOString.split(" ");

        System.out.println("Enter edge positions");
        String ePString = in.nextLine();
        String[] ePStrings = ePString.split(" ");

        System.out.println("Enter edge orientations");
        String eOString = in.nextLine();
        String[] eOStrings = eOString.split(" ");

        in.close();

        // Convert string arrays to byte arrays
        byte[] cP = new byte[8];
        byte[] cO = new byte[8];
        byte[] eP = new byte[12];
        byte[] eO = new byte[12];
        // Corners
        for(int i = 0; i < 8; i++) {
            cP[i] = Byte.parseByte(cPStrings[i]);
            cO[i] = Byte.parseByte(cOStrings[i]);
        }
        // Edges
        for(int i = 0; i < 12; i++) {
            eP[i] = Byte.parseByte(ePStrings[i]);
            eO[i] = Byte.parseByte(eOStrings[i]);
        }

        return new CubeState(cP, cO, eP, eO);
    }
}