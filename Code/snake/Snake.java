package snake;

import players.*;

/**
 *
 * @author steven
 */
public class Snake {

    GameState state;
    SnakePlayer[] players;
    GameDisplay display;
    boolean showGUI = true;
    int nrPlayers = 4;
    int maxNrSteps = 500;
    static int availableTime = 100;

    public Snake() throws Exception {
        startGUIGame();
    }

    public void startGUIGame() throws Exception {
        state = new GameState(nrPlayers, 20, 20);
        showGUI = true;
        display = new GameDisplay(state);

        players = new SnakePlayer[nrPlayers];
        players[0] = new ParanoidPlayer(state, 0, this, availableTime);
        players[1] = new NonParanoidPlayer(state, 1, this, availableTime);
        players[2] = new AStarPlayer(state, 2, this);
        players[3] = new RandomPlayer(state, 3, this);

        for (int i = 0; i < nrPlayers; i++) {
            players[i].setDisplay(display);
        }

        doMove(0, 0);
    }

    public void doMove(int playerIndex, int currentStep) throws InterruptedException {

        int nextIndex = (playerIndex + 1) % nrPlayers;
        if (showGUI) {
            display.updateState();
        }
        if (!state.isGameOver() && currentStep < maxNrSteps) {
            while (state.isDead(nextIndex)) {
                nextIndex = (nextIndex + 1) % nrPlayers;
            }
            long startTime = System.currentTimeMillis();
            players[nextIndex].doMove();
            state.updatePlayerPosition(nextIndex);
            if (!state.hasTarget()) {
                state.chooseNextTarget();
            }
            long stopTime = System.currentTimeMillis();
            if (stopTime - startTime <= availableTime) {
                Thread.sleep(availableTime - stopTime + startTime);
            }
            doMove(nextIndex, currentStep + 1);
        }
        else if (showGUI) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            int winner = -1;
            int winningScore = -1;
            for (int i = 0; i < nrPlayers; i++) {
                if (!state.isDead(i)) {
                    int score = state.getSize(i);
                    if (score > winningScore) {
                        winningScore = score;
                        winner = i;
                    }
                }
            }
            System.out.println("The winner is player " + winner + " (score: " + winningScore + ")" );
        }
    }

    public static void main(String[] args) throws Exception {
        new Snake();
    }
}
