package com.dxymf;

public class Move {

    private byte moveType; // 1-6   -   R, U, F, L, D, B
    private byte moveDirection; // 1-3   -   1-clockwise, 2-double move, 3-counter-clockwise

    public Move(byte type, byte direction) {
        moveType = type;
        moveDirection = direction;
    }

    public byte getType() {
        return moveType;
    }

    public byte getDirection() {
        return moveDirection;
    }

    public Move clone() {
        return new Move(moveType, moveDirection);
    }

    // Convert to standard move notation (example: (1, 3) -> "U'")
    public String toString() {
        String[] typeConversion = {"R", "U", "F", "L", "D", "B"};
        String typeString = typeConversion[moveType - 1];

        String[] dirConversion = {"", "2", "'"};
        String dirString = dirConversion[moveDirection - 1];

        return typeString + dirString;
    }

    public static Move fromString(String str) {
        byte type = 0;
        byte dir = 0;

        char[] strArray = str.toCharArray();

        if(strArray.length == 1) {
            dir = 1;
        } else {
            switch(strArray[1]) {
                case '2':
                    dir = 2;
                    break;

                case 39:
                    dir = 3;
                    break;
            }
        }

        switch(strArray[0]) {
            case 'R':
                type = 1;
                break;

            case 'U':
                type = 2;
                break;

            case 'F':
                type = 3;
                break;

            case 'L':
                type = 4;
                break;

            case 'D':
                type = 5;
                break;

            case 'B':
                type = 6;
                break;
        }
        
        return new Move(type, dir);
    }

}
