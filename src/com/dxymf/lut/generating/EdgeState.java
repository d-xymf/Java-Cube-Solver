package com.dxymf.lut.generating;

import com.dxymf.Move;

public class EdgeState {

    private byte[] edgePositions;
    private byte[] edgeOrientations;

    private byte prevMove = 0;

    public EdgeState(byte[] edgePositions, byte[] edgeOrientations) {
        this.edgePositions = edgePositions;
        this.edgeOrientations = edgeOrientations;
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
        byte[] newEdgeOrientations = edgeOrientations.clone();

        byte type = move.getType();
        byte direction = move.getDirection();

        byte[] edgePositionChanges;
        byte[] edgeOrientationChanges;

        switch(type) {
            // R move
            case 1:
                {
                    edgePositionChanges = new byte[] {-1, 5, -1, -1, -1, 9, 1, -1, -1, 6, -1, -1};
                    edgeOrientationChanges = new byte[] {0, 1};
                }
                break;

            // U move
            case 2:
                {
                    edgePositionChanges = new byte[] {1, 2, 3, 0, -1, -1, -1, -1, -1, -1, -1, -1};
                    edgeOrientationChanges = new byte[] {0, 1};
                }
                break;

            // F move
            case 3:
                {
                    edgePositionChanges = new byte[] {-1, -1, 6, -1, -1, -1, 10, 2, -1, -1, 7, -1};
                    edgeOrientationChanges = new byte[] {1, 0};
                }
                break;

            // L move
            case 4:
                {
                    edgePositionChanges = new byte[] {-1, -1, -1, 7, 3, -1, -1, 11, -1, -1, -1, 4};
                    edgeOrientationChanges = new byte[] {0, 1};
                }
                break;

            // D move
            case 5:
                {
                    edgePositionChanges = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, 11, 8, 9, 10};
                    edgeOrientationChanges = new byte[] {0, 1};
                }
                break;

            // B move
            case 6:
                {
                    edgePositionChanges = new byte[] {4, -1, -1, -1, 8, 0, -1, -1, 5, -1, -1, -1};
                    edgeOrientationChanges = new byte[] {1, 0};
                }
                break;
            
            // Default case that initializes the arrays so compiler doesn't complain
            default:
                {
                    edgePositionChanges = new byte[12];
                    edgeOrientationChanges = new byte[2];
                }
        }

        // direction is the number of times a clockwise turn should be applied
        // (direction = 1 -> clockwise)
        // (direction = 2 -> double turn)
        // (direction = 3 -> anti-clockwise)
        for(int i = 0; i < direction; i++) {

            // Apply changes to edges
            for(int j = 0; j < edgePositions.length; j++) {

                if(edgePositionChanges[edgePositions[j]] == -1) continue;

                newEdgePositions[j] = edgePositionChanges[edgePositions[j]];
                newEdgeOrientations[j] = edgeOrientationChanges[edgeOrientations[j]];
            }

            edgePositions = newEdgePositions.clone();
            edgeOrientations = newEdgeOrientations.clone();
        }
    }

    public EdgeState clone() {
        EdgeState clone = new EdgeState(edgePositions.clone(), edgeOrientations.clone());

        clone.prevMove = prevMove;
        
        return clone;
    }

    public byte[] getEdgePositions() {
        return edgePositions.clone();
    }

    public byte[] getEdgeOrientations() {
        return edgeOrientations.clone();
    }

    public byte getPrevMove() {
        return prevMove;
    }

    public void setPrevMove(byte move) {
        prevMove = move;
    }
}