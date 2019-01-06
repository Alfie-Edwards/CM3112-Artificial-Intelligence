package players;

import snake.GameState;
import snake.Snake;

import java.util.HashMap;
import java.util.Map;

public class NonParanoidPlayer extends BaseMinimaxPlayer {

    private int moveCalculationTimeoutMs;


    public NonParanoidPlayer(GameState state, int index, Snake game, int moveCalculationTimeoutMs) {
        super(state, index, game);
        this.moveCalculationTimeoutMs = moveCalculationTimeoutMs;
    }


    @Override
    public void doMove() {
        int direction = getBestMoveUsingIterativeDeepening();
        state.setOrientation(index, direction);
    }


    private int getBestMoveUsingIterativeDeepening() {
        long startTime = System.currentTimeMillis();
        Map<MinimaxNode, Double> moveValues = new HashMap<>();
        int depth = 1; // Initial depth of 1

        // Initialise moveValues with all possible moves
        for (MinimaxNode node : new MinimaxNode(state, index).GenerateChildNodes())
            moveValues.put(node, 0d);

        // Increase depth iteratively until the timeout reached.
        while (System.currentTimeMillis() - startTime < moveCalculationTimeoutMs) {

            MinimaxNode fallbackBestMove = null;
            Double fallbackBestValue = Double.NEGATIVE_INFINITY;
            boolean livingMoveFoundThisIteration = false; // Used to track if all moves end in death, as there is a different way to decide the move in that case.
            for (MinimaxNode move : moveValues.keySet()) {

                // If a move ends in death, it does not need to be explored at further depths.
                if (moveValues.get(move) == Double.NEGATIVE_INFINITY) continue;

                // Get the value (for this player) of the predicted board state.
                Double value = getMiniMaxValue(move, depth).getValue(index);

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

                // If time is up, break out of the loop inner loop
                // Outer loop will also break at the start of the next iteration
                if ((System.currentTimeMillis() - startTime) >= moveCalculationTimeoutMs)
                    break;
            }
            // If all evaluated moves end in death, terminate the search
            // The default behaviour would value all of these moves equally and return the first in the list.
            // We can potentially pick a better move than the default behaviour.
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
        // After the outer loop has broken and not all moves end in death.
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
        // return the direction of the player for that move
        return bestMove.getGameState().getLastOrientation(index);
    }


    private EvaluatedState getMiniMaxValue(MinimaxNode node, int targetDepth) {
        // Terminate the search if:
        //     we have reached the maximum depth,
        //     and the next iteration would bring us to an even lower depth
        if (!node.isChanceNode() && targetDepth == 1)
            return new LazyEvaluatedState(node.getGameState());
        MinimaxNode[] childNodes = node.GenerateChildNodes();
        if (childNodes.length < 1)
            return new LazyEvaluatedState(node.getGameState());

        // Node is a chance node
        // Sum up and take the average of the values of all the children for each player (all outcomes have equal probability)
        // Do not increment the depth because chance nodes are states with no target. These states would be unfairly undervalued as leaf nodes.
        if (node.isChanceNode()) {
            EvaluatedState[] evaluatedStates = new EvaluatedState[childNodes.length];
            for (int i = 0; i < childNodes.length; i++)
                evaluatedStates[i] = getMiniMaxValue(childNodes[i], targetDepth);
            return new LazyEvaluatedChanceState(evaluatedStates);
        }

        // Choose the next move based on the best predicted outcome for the player who's turn it is.
        EvaluatedState bestEvaluatedState = null;
        int currentPlayer = node.getCurrentPlayer();
        for (MinimaxNode child : childNodes) {
            EvaluatedState evaluatedState = getMiniMaxValue(child, targetDepth - 1);
            if (bestEvaluatedState == null || evaluatedState.getValue(currentPlayer) > bestEvaluatedState.getValue(currentPlayer))
                bestEvaluatedState = evaluatedState;
        }
        return bestEvaluatedState;
    }


    // Used to allow interchangeability between the two classes below
    private interface EvaluatedState {
        Double getValue(int playerIndex);
    }


    // This class represents a collection of the values of a game state for each player
    // It does not actually evaluate the value for a given player until that value is requested
    // This saves time as not all values will necessarily be used
    private class LazyEvaluatedState implements EvaluatedState {
        private GameState state;
        private Double[] values;

        public LazyEvaluatedState(GameState state)
        {
            this.state = state;
            values = new Double[state.getNrPlayers()];
        }

        public Double getValue(int playerIndex) {
            if (values[playerIndex] == null)
                values[playerIndex] = evaluateState(state, playerIndex);
            return values[playerIndex];
        }
    }


    // This class is the equivalent of the LazyEvaluatedState above, but for chance nodes
    // It has multiple game states instead of one, and takes the average from scores from them
    // It uses other EvaluatedStates so it can take advantage of values that have already been calculated from further down the search tree
    private class LazyEvaluatedChanceState implements EvaluatedState {
        private EvaluatedState[] states;
        private Double[] values;

        public LazyEvaluatedChanceState(EvaluatedState[] states)
        {
            this.states = states;
            values = new Double[state.getNrPlayers()];
        }

        public Double getValue(int playerIndex) {
            if (values[playerIndex] == null) {
                values[playerIndex] = 0d;
                for (EvaluatedState evaluatedState : states)
                    values[playerIndex] += evaluatedState.getValue(playerIndex);
                values[playerIndex] /= states.length;
            }
            return values[playerIndex];
        }
    }
}