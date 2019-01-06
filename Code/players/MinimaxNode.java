package players;

import snake.GameState;

public class MinimaxNode {

    private GameState state;
    private int currentPlayer;
    private static final int RANDOM_SAMPLE_COUNT = 5;
    private static final int[] ALL_POSSIBLE_MOVES = {GameState.NORTH, GameState.EAST, GameState.SOUTH, GameState.WEST};

    public MinimaxNode(GameState state, int currentPlayer) {
        this.state = state;
        this.currentPlayer = currentPlayer;
    }


    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public GameState getGameState() { return state; }
    public boolean isChanceNode() { return !state.hasTarget(); }


    public MinimaxNode[] GenerateChildNodes() {

        MinimaxNode[] childNodes;

        // Generates a set number of sample possibilities as children.
        // Current player is not updated on the new nodes because no player action is taken.
        if (isChanceNode()) {

            childNodes = new MinimaxNode[RANDOM_SAMPLE_COUNT];

            for (int i = 0; i < RANDOM_SAMPLE_COUNT; i++) {
                GameState newState = new GameState(state);
                newState.chooseNextTarget();
                childNodes[i] = new MinimaxNode(newState, currentPlayer);
            }

        // Generates children for all legal moves for the current player.
        // The current player is updated on each new node in-case the next player is dead on some nodes.
        } else {
            childNodes = new MinimaxNode[ALL_POSSIBLE_MOVES.length];

            for (int i = 0; i < ALL_POSSIBLE_MOVES.length; i++) {
                GameState newState = new GameState(state);
                newState.setOrientation(currentPlayer, ALL_POSSIBLE_MOVES[i]);
                newState.updatePlayerPosition(currentPlayer);

                // To identify the next player to move: iterate through players until a living player is found.
                int nextPlayer = currentPlayer;

                if (!newState.isGameOver()){
                    do nextPlayer = (nextPlayer + 1) % state.getNrPlayers();
                    while (newState.isDead(nextPlayer));
                }

                childNodes[i] = new MinimaxNode(newState, nextPlayer);
            }
        }

        return childNodes;
    }
}
