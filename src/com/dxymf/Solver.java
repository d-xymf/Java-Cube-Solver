package com.dxymf;

import java.util.PriorityQueue;
import java.util.Stack;

import com.dxymf.lut.CornerLUT;
import com.dxymf.lut.EdgeLUT;
import com.dxymf.lut.EdgePosLUT;

public class Solver {

    private static final int INFINITY = Integer.MAX_VALUE;

    private CornerLUT cornerLUT;
    private EdgeLUT edgeLUT1;
    private EdgeLUT edgeLUT2;
    private EdgePosLUT edgePosLUT;

    public Solver(CornerLUT cLUT, EdgeLUT eLUT1, EdgeLUT eLUT2, EdgePosLUT ePosLUT) {
        cornerLUT = cLUT;
        edgeLUT1 = eLUT1;
        edgeLUT2 = eLUT2;
        edgePosLUT = ePosLUT;
    }

    public Move[] solve(CubeState rootState) {

        System.out.println("Starting solving");
        long t1 = System.currentTimeMillis();
        
        // Array of moves to get to the current node and the solution once solving is complete
        Move[] solutionPath = new Move[20]; // Length of solution can't be more than 20 (god's number)

        Stack<Node> stack = new Stack<>();

        Node root = new Node(rootState, 0, 0, null);
        Node currentNode;

        boolean solved = false;

        // Total number of states that have been generated
        long exploredStates = 0;

        // Bound for the current iteration (0 to begin with because we haven't started the first iteration yet)
        int bound = 0;
        // Bound for the next iteration (Minimum of all pruned nodes' f-scores)
        int nextBound = heuristic(root.state); // Here it is going to be the bound for the first iteration

        while(!solved) {
            /*
             * The stack is empty
             * -> either this is the first iteration
             * -> or the current iteration is completed
             * in either case we must start a new iteration with the root state in the stack
             * and the new bound set
             */
            if(stack.empty()) {
                stack.push(root);

                bound = nextBound;
                System.out.println("New Iteration with bound: " + bound + " after " + (System.currentTimeMillis() - t1) + "ms");
                nextBound = INFINITY;
            }

            currentNode = stack.pop();

            // Update solutionPath
            if(currentNode.depth != 0) {
                solutionPath[currentNode.depth - 1] = currentNode.prevMove;
            }
            solutionPath[currentNode.depth] = null;

            // Only check if cube is solved when depth == bound (Estimated remaining number of moves is 0)
            if(currentNode.depth == bound) {
                if(currentNode.state.isSolved()) {
                    solved = true;
                }

            } else {
                // PriorityQueue with the successors of currentNode so they can be ordered by their f-scores
                PriorityQueue<Node> successors = new PriorityQueue<>();

                // Apply all possible moves
                for(byte moveType = 1; moveType <= 6; moveType++) {

                    // Simple move pruning
                    if(currentNode.prevMove != null) {
                        // Don't repeat moves of the same type
                        if(moveType == currentNode.prevMove.getType()) continue;
                        // Only allow commutative moves in a certain order
                        if((moveType <= 3) && (moveType == currentNode.prevMove.getType() - 3)) continue;
                    }
        
                    // Apply moves that haven't been pruned
                    for(byte moveDirection = 1; moveDirection <= 3; moveDirection++) {
                        // Clone the current node
                        CubeState clonedState = currentNode.state.clone();
        
                        // Apply the move
                        Move move = new Move(moveType, moveDirection);
                        clonedState.applyMove(move);

                        exploredStates++;

                        // Calculate f-score
                        int f = currentNode.depth + 1 + heuristic(clonedState);
                        // Create a successor node with the cloned state
                        Node successor = new Node(clonedState, currentNode.depth + 1, f, move);

                        // Check if the successors f-score exceeds the bound
                        if(f <= bound) {
                            // If not add it to the PriorityQueue so it can be added to the stack
                            successors.add(successor);
                        } else if(f < nextBound) {
                            // This results in nextBound being the minimum of all pruned nodes' f-scores
                            nextBound = f;
                        }
                    }
                }
                // Add the successors to the stack one by one 
                // Lowest f-score gets added to the stack last so it will be searched first
                while(!successors.isEmpty()) {
                    stack.push(successors.poll());
                }
            }
        }

        System.out.println("Time taken: " + (System.currentTimeMillis() - t1) + "ms");
        System.out.println("Explored states: " + exploredStates);
        System.out.println("States per Second: " + (1000 * exploredStates / (System.currentTimeMillis() - t1)));

        return solutionPath;
    }

    private int heuristic(CubeState state) {
        // Return the maximum of all the values of the LUTs
        return Math.max(cornerLUT.getMoveCount(state), Math.max(edgeLUT1.getMoveCount(state), Math.max(edgeLUT2.getMoveCount(state), edgePosLUT.getMoveCount(state))));
    }
}
