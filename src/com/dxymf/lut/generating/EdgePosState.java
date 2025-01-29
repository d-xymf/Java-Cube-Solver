package com.dxymf.lut.generating;

import com.dxymf.Move;

public class EdgePosState {

    private byte[] edgePositions;

    private byte prevMove = 0;

    public EdgePosState(byte[] edgePositions) {
        this.edgePositions = edgePositions;
    }

    public static byte[] extractElementsFromArray(byte[] array, byte[] positions) {

        byte[] extracted = new byte[positions.length];

        for(int i = 0; i < positions.length; i++) {
            extracted[i] = array[positions[i]];
        }

        return extracted;
    }

    public void applyMove(Move move) {

        byte[] newEdgePositions = edgePositions.clone();

        byte type = move.getType();
        byte direction = move.getDirection();

        byte[] edgePositionChanges;

        switch(type) {
            // R move
            case 1:
                {
                    edgePositionChanges = new byte[] {-1, 5, -1, -1, -1, 9, 1, -1, -1, 6, -1, -1};
                }
                break;

            // U move
            case 2:
                {
                    edgePositionChanges = new byte[] {1, 2, 3, 0, -1, -1, -1, -1, -1, -1, -1, -1};
                }
                break;

            // F move
            case 3:
                {
                    edgePositionChanges = new byte[] {-1, -1, 6, -1, -1, -1, 10, 2, -1, -1, 7, -1};
                }
                break;

            // L move
            case 4:
                {
                    edgePositionChanges = new byte[] {-1, -1, -1, 7, 3, -1, -1, 11, -1, -1, -1, 4};
                }
                break;

            // D move
            case 5:
                {
                    edgePositionChanges = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, 11, 8, 9, 10};
                }
                break;

            // B move
            case 6:
                {
                    edgePositionChanges = new byte[] {4, -1, -1, -1, 8, 0, -1, -1, 5, -1, -1, -1};
                }
                break;
            
            // Default case that initializes the arrays so compiler doesn't complain
            default:
                {
                    edgePositionChanges = new byte[12];
                }
        }

        // direction is the number of times a clockwise turn should be applied
        // (direction = 1 -> clockwise)
        // (direction = 2 -> double turn)
        // (direction = 3 -> anti-clockwise)
        for(int i = 0; i < direction; i++) {

            // Apply changes to edges
            for(int j = 0; j < 12; j++) {

                if(edgePositionChanges[edgePositions[j]] == -1) continue;

                newEdgePositions[j] = edgePositionChanges[edgePositions[j]];
            }

            edgePositions = newEdgePositions.clone();
        }
    }

    public String toString() {

        // Array of colors to easily look up the colors of a certain piece
        String[] edgeColors = {"WB", "WR", "WG", "WO", "BO", "BR", "GR", "GO", "YB", "YR", "YG", "YO"};

        // Template string of positions
        String pieceString = 
        "             X 8 X \n" + 
        "             11  W   9 \n" + 
        "             X 10 X \n" + 
        " X 11 X  X 10 X  X 9 X  X 8 X \n" + 
        " 12  O   15  15  G   14  14  R   13  13  B   12 \n" + 
        " X 19 X  X 18 X  X 17 X  X 16 X \n" + 
        "             X 18 X \n" + 
        "             19  Y   17 \n" + 
        "             X 16 X \n"
        ;

        // Replace each position of template with the colors of the piece at that position
        for(int i = 0; i < 12; i++) {
            pieceString = pieceString.replace(" "+String.valueOf(edgePositions[i] + 8)+" ", " "+edgeColors[i]+" ");
        }
        
        return pieceString;
    }

    public EdgePosState clone() {
        EdgePosState clone = new EdgePosState(edgePositions.clone());

        clone.prevMove = prevMove;

        return clone;
    }

    public byte[] getEdgePositions() {
        return edgePositions.clone();
    }

    public byte getPrevMove() {
        return prevMove;
    }

    public void setPrevMove(byte move) {
        prevMove = move;
    }

    public static EdgePosState getSolvedState() {
        byte[] ePSolved = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

        return new EdgePosState(ePSolved);
    }
}