package players;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import snake.GameState;

/**
 *
 * @author steven
 */
class Node extends Position implements Comparable<Node> {

    Node previous;
    int depth;
    int targetX;
    int targetY;
    int priority;
    int distanceTravelled;

    public Node(int x, int y, int targetX, int targetY) {
        super(x, y);
        depth = 0;
        this.targetX = targetX;
        this.targetY = targetY;
        priority = Math.abs(x - targetX) + Math.abs(y - targetY);
        distanceTravelled = 0;
    }

    public Node(int x, int y, Node previous) {
        super(x, y);
        this.previous = previous;
        targetX = previous.targetX;
        targetY = previous.targetY;
        depth = previous.depth + 1;
        distanceTravelled = previous.distanceTravelled + 1;
        priority = Math.abs(x - targetX) + Math.abs(y - targetY) + distanceTravelled;
    }

    public int getFirstMove() {
        try {
            if (previous.previous == null) {
                if (x == previous.x + 1 && y == previous.y) {
                    return GameState.EAST;
                }
                else if (x == previous.x - 1 && y == previous.y) {
                    return GameState.WEST;
                }
                else if (x == previous.x && y == previous.y + 1) {
                    return GameState.SOUTH;
                }
                else if (x == previous.x && y == previous.y - 1) {
                    return GameState.NORTH;
                }
                else {
                    throw new Exception();
                }
            }
            else {
                return previous.getFirstMove();
            }
        } catch (Exception e) {
            System.err.println("Impossible first move in AStarPlayer.Node.getFirstMove()");
            System.err.println("x = " + x);
            System.err.println("previous.x = " + previous.x);
            System.err.println("y = " + y);
            System.err.println("previous.y = " + previous.y);
            return -1;
        }
    }

    public int hashCode() {
        if (previous != null) {
            return super.hashCode() + 1024 * previous.hashCode();
        }
        else {
            return super.hashCode();
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof Position) || !super.equals(0)) {
            return false;
        }

        Node node = (Node) o;
        return (previous != null && node.previous != null && previous.equals(node.previous))
                || (previous == null && node.previous == null);
    }

    public int compareTo(Node o) {
        return priority - o.priority;
    }
}