package com.dxymf.lut.generating;

import com.dxymf.Move;

public class CornerState {

    private byte[] cornerPositions;
    private byte[] cornerOrientations;

    private byte prevMove = 0;

    public CornerState(byte[] cornerPositions, byte[] cornerOrientations) {
        this.cornerPositions = cornerPositions;
        this.cornerOrientations = cornerOrientations;
    }

    public static byte[] extractElementsFromArray(byte[] array, byte[] positions) {

        byte[] extracted = new byte[positions.length];

        for(int i = 0; i < positions.length; i++) {
            extracted[i] = array[positions[i]];
        }

        return extracted;
    }

    public void applyMove(Move move) {

        byte[] newCornerPositions = cornerPositions.clone();
        byte[] newCornerOrientations = cornerOrientations.clone();

        byte type = move.getType();
        byte direction = move.getDirection();

        byte[] cornerPositionChanges;
        byte[] cornerOrientationChanges;

        switch(type) {
            // R move
            case 1:
                {
                    cornerPositionChanges = new byte[] {-1, 5, 1, -1, -1, 6, 2, -1};
                    cornerOrientationChanges = new byte[] {1, 0, 2};
                }
                break;

            // U move
            case 2:
                {
                    cornerPositionChanges = new byte[] {1, 2, 3, 0, -1, -1, -1, -1};
                    cornerOrientationChanges = new byte[] {0, 2, 1};
                }
                break;

            // F move
            case 3:
                {
                    cornerPositionChanges = new byte[] {-1, -1, 6, 2, -1, -1, 7, 3};
                    cornerOrientationChanges = new byte[] {2, 1, 0};
                }
                break;

            // L move
            case 4:
                {
                    cornerPositionChanges = new byte[] {3, -1, -1, 7, 0, -1, -1, 4};
                    cornerOrientationChanges = new byte[] {1, 0, 2};
                }
                break;

            // D move
            case 5:
                {
                    cornerPositionChanges = new byte[] {-1, -1, -1, -1, 7, 4, 5, 6};
                    cornerOrientationChanges = new byte[] {0, 2, 1};
                }
                break;

            // B move
            case 6:
                {
                    cornerPositionChanges = new byte[] {4, 0, -1, -1, 5, 1, -1, -1};
                    cornerOrientationChanges = new byte[] {2, 1, 0};
                }
                break;
            
            // Default case that initializes the arrays so compiler doesn't complain
            default:
                {
                    cornerPositionChanges = new byte[8];
                    cornerOrientationChanges = new byte[3];
                }
        }

        // direction is the number of times a clockwise turn should be applied
        // (direction = 1 -> clockwise)
        // (direction = 2 -> double turn)
        // (direction = 3 -> anti-clockwise)
        for(int i = 0; i < direction; i++) {

            // Apply changes to corners
            for(int j = 0; j < 8; j++) {

                if(cornerPositionChanges[cornerPositions[j]] == -1) continue;

                newCornerPositions[j] = cornerPositionChanges[cornerPositions[j]];
                newCornerOrientations[j] = cornerOrientationChanges[cornerOrientations[j]];
            }

            cornerPositions = newCornerPositions.clone();
            cornerOrientations = newCornerOrientations.clone();
        }
    }

    public String toString() {

        // Array of colors to easily look up the colors of a certain piece
        String[] cornerColors = {"WBO", "WBR", "WGR", "WGO", "YBO", "YBR", "YGR", "YGO"};

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


        String[] cornerOrientationStrings = {"T", "F", "S"};

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
        
        return pieceString + orientationString;
    }

    public CornerState clone() {
        CornerState clone = new CornerState(cornerPositions.clone(), cornerOrientations.clone());

        clone.prevMove = prevMove;
        
        return clone;
    }

    public byte[] getCornerPositions() {
        return cornerPositions.clone();
    }

    public byte[] getCornerOrientations() {
        return cornerOrientations.clone();
    }

    public byte getPrevMove() {
        return prevMove;
    }

    public void setPrevMove(byte move) {
        prevMove = move;
    }

    public static CornerState getSolvedState() {
        byte[] cPSolved = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] cOSolved = {0, 0, 0, 0, 0, 0, 0, 0};

        return new CornerState(cPSolved, cOSolved);
    }
}