package players;

import snake.GameState;
import snake.Snake;

import java.util.HashMap;
import java.util.Map;

public class ParanoidPlayer extends BaseMinimaxPlayer {

    private int moveCalculationTimeoutMs;


    public ParanoidPlayer(GameState state, int index, Snake game, int moveCalculationTimeoutMs) {
        super(state, index, game);
        this.moveCalculationTimeoutMs = moveCalculationTimeoutMs;
    }


    @Override
    public void doMove() {
        int direction = getBestMoveUsingIterativeDeepening(moveCalculationTimeoutMs);
        state.setOrientation(index, direction);
    }


    private int getBestMoveUsingIterativeDeepening(long maxTimeMs) {
        long startTime = System.currentTimeMillis();
        Map<MinimaxNode, Double> moveValues = new HashMap<>();

        // Initialise moveValues with all possible moves
        for (MinimaxNode node : new MinimaxNode(state, index).GenerateChildNodes())
            moveValues.put(node, 0d);

        int depth = 1;
        while (System.currentTimeMillis() - startTime < maxTimeMs) {

            MinimaxNode fallbackBestMove = null;
            Double fallbackBestValue = Double.NEGATIVE_INFINITY;
            boolean livingMoveFoundThisIteration = false;
            for (MinimaxNode move : moveValues.keySet()) {

                // If a move ends in death, it does not need to be explored at further depths.
                if (moveValues.get(move) == Double.NEGATIVE_INFINITY)
                    continue;

                Double value = getMiniMaxValue(move, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth);

                if (value == Double.NEGATIVE_INFINITY) {
                    Double oldValue = moveValues.get(move);
                    // Remember the best move that ended in death this iteration.
                    // If all moves end in death this iteration, this move is used.
                    if (oldValue > fallbackBestValue) {
                        fallbackBestMove = move;
                        fallbackBestValue = oldValue;
                    }
                } else
                    livingMoveFoundThisIteration = true;
                moveValues.put(move, value);

                // If time is up, break out of the loop
                if ((System.currentTimeMillis() - startTime) >= maxTimeMs)
                    break;
            }
            // If all evaluated moves end in death, terminate the search
            if (!livingMoveFoundThisIteration)
                // If there is a fallback best move from last iteration, use that
                // Even though dying is inevitable, the player goes for the best pre-death situation in-case things play out differently.
                if (fallbackBestMove != null)
                    return fallbackBestMove.getGameState().getLastOrientation(index);
                else
                // Otherwise just go north (this only happens if all moves end in immediate death).
                    return GameState.NORTH;

            depth++;
        }

        // Find move with best value;
        MinimaxNode bestMove = null;
        Double bestValue = Double.NEGATIVE_INFINITY;
        for (MinimaxNode move : moveValues.keySet()) {
            Double value = moveValues.get(move);
            if (value > bestValue) {
                bestMove = move;
                bestValue = value;
            }
        }
        return bestMove.getGameState().getLastOrientation(index);
    }


    // min, max, and chance node handling combined into one method
    private Double getMiniMaxValue(MinimaxNode node, Double alpha, Double beta, int targetDepth) {
        if (node.getGameState().isDead(index) || (targetDepth == 1 && !node.isChanceNode()))
            return evaluateState(node.getGameState(), index);

        MinimaxNode[] childNodes = node.GenerateChildNodes();
        if (childNodes.length < 1)
            return evaluateState(node.getGameState(), index);

        // Node is a chance node
        // Sum up and take the average of the values of all the children (all outcomes have equal probability).
        if (node.isChanceNode()) {
            Double totalValue = 0d;
            for (MinimaxNode child : childNodes)
                totalValue += getMiniMaxValue(child, alpha, beta, targetDepth);
            return totalValue / childNodes.length;
        }

        // This player's turn to move
        if (node.getCurrentPlayer() == index) {
            Double value = Double.NEGATIVE_INFINITY;
            for (MinimaxNode child : childNodes) {
                value = Math.max(value, getMiniMaxValue(child, alpha, beta, targetDepth - 1));
                alpha = Math.max(value, alpha);
                if (beta <= alpha) break;
            }
            return value;
        }

        // Enemy player's turn to move
        Double value = Double.POSITIVE_INFINITY;
        for (MinimaxNode child : childNodes) {
            value = Math.min(value, getMiniMaxValue(child, alpha, beta, targetDepth - 1));
            beta = Math.min(value, beta);
            if (beta <= alpha || value == Double.NEGATIVE_INFINITY) break;
        }
        return value;
    }
}