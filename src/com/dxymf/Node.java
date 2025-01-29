package com.dxymf;

public class Node implements Comparable<Node> {
    public CubeState state;
    public int depth = 0;
    public int f = 0;
    public Move prevMove;

    public Node(CubeState state, int depth, int f, Move prevMove) {
        this.state = state;
        this.depth = depth;
        this.f = f;
        this.prevMove = prevMove;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(other.f, this.f);
    }

    public String toString() {
        return f + "";
    }
}
