package players;

import snake.GameState;
import snake.Snake;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public abstract class BaseMinimaxPlayer extends AStarPlayer {
    protected BaseMinimaxPlayer(GameState state, int index, Snake game) {
        super(state, index, game);
    }


    // Evaluation function
    protected Double evaluateState(GameState state, int playerIndex) {
        if (state.isDead(playerIndex))
            return Double.NEGATIVE_INFINITY;
        return getLengthDifferenceWithLongestEnemy(state, playerIndex) + 1d / searchTargetDistance(state, playerIndex);
    }


    // Returns the signed difference in length between the specified player and its longest enemy
    protected int getLengthDifferenceWithLongestEnemy(GameState state, int playerIndex) {
        int length = state.getSize(playerIndex);
        int largestEnemySize = 0;
        for (int i = 0; i < state.getNrPlayers(); i++)
            if (i != playerIndex && !state.isDead(i))
                largestEnemySize = Math.max(largestEnemySize, state.getSize(i));

        return length - largestEnemySize;
    }


    // Finds the shortest distance to the current disk using a variant of a* search.
    protected int searchTargetDistance(GameState state, int playerIndex) {
        initPositions(state);
        PriorityQueue<Node> frontier = new PriorityQueue();
        // Used to store the best distance found for each position
        // This is worth it because we can ignore so many of the equal length paths that arise from grid movement
        Map<Position, Integer> closed = new HashMap();

        // Add initial node to frontier
        frontier.add(new Node(state.getPlayerX(playerIndex).get(0), state.getPlayerY(playerIndex).get(0), state.getTargetX(), state.getTargetY()));

        while (!frontier.isEmpty()) {
            // Take the highest priority node from the frontier
            Node n = frontier.poll();

            // Enumerate possible next positions in the path
            Position[] potentialPositions = {
                    new Position(n.x + 1, n.y),
                    new Position(n.x - 1, n.y),
                    new Position(n.x, n.y + 1),
                    new Position(n.x, n.y - 1),
            };
            // Iterate over potential positions
            for (Position position : potentialPositions) {

                // If the position is the target point, we have found the distance
                if (position.x == state.getTargetX() && position.y == state.getTargetY())
                    return n.distanceTravelled + 1;

                // If the position is occupied, stop considering the node.
                if (isOccupied(position.x, position.y, n.depth, state))
                    continue;

                Integer lowestDistanceTravelled = closed.get(position);
                int newDistanceTravelled = n.distanceTravelled + 1;

                // Add the node only if:
                //     No node has been added to the frontier at this position
                // OR
                //     The current path length is shorter than that of the previous node added to the frontier in this position
                if (lowestDistanceTravelled == null || newDistanceTravelled < lowestDistanceTravelled) {
                    frontier.add(new Node(position.x, position.y, n));
                    closed.put(position, newDistanceTravelled);
                }
            }
        }

        // If the frontier search space is exhausted without reaching the target, return the largest path length possible
        // This is more convenient than NULL because it can be easily compared with other paths lengths, (it is longer than any possible valid path so will always be considered worse).
        return Integer.MAX_VALUE;
    }
}
