package snake;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author steven
 */
public class GameState {

    int width;
    int height;
    boolean[][] occupied;
    List<Integer>[] playerX;
    List<Integer>[] playerY;
    int[] playerOrientation;
    int[] lastPlayerOrientation;
    public static final int NORTH = 1;
    public static final int SOUTH = 3;
    public static final int EAST = 2;
    public static final int WEST = 4;
    static final int extensionsPerTarget = 3;
    int targetX;
    int targetY;
    static Random rand = new Random();
    boolean[] dead;
    int nrDead;
    int nrPlayers;
    int[] nrExtensionsLeft;
    int stepCount;

    public GameState(GameState st) {
        width = st.width;
        height = st.height;
        targetX = st.targetX;
        targetY = st.targetY;
        nrDead = st.nrDead;
        nrPlayers = st.nrPlayers;
        occupied = new boolean[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                occupied[i][j] = st.occupied[i][j];
            }
        }
        playerX = new List[nrPlayers];
        playerY = new List[nrPlayers];
        playerOrientation = new int[nrPlayers];
        lastPlayerOrientation = new int[nrPlayers];
        dead = new boolean[nrPlayers];
        nrExtensionsLeft = new int[nrPlayers];
        for (int i = 0; i < nrPlayers; i++) {
            playerX[i] = new ArrayList(st.playerX[i]);
            playerY[i] = new ArrayList(st.playerY[i]);
            playerOrientation[i] = st.playerOrientation[i];
            lastPlayerOrientation[i] = st.lastPlayerOrientation[i];
            dead[i] = st.dead[i];
            nrExtensionsLeft[i] = st.nrExtensionsLeft[i];
        }
        stepCount = st.stepCount;
    }

    public GameState(int nrPlayers, int width, int height) throws Exception {
        this.width = width;
        this.height = height;
        occupied = new boolean[width][height];
        playerX = new List[nrPlayers];
        playerY = new List[nrPlayers];
        playerOrientation = new int[nrPlayers];
        lastPlayerOrientation = new int[nrPlayers];
        nrExtensionsLeft = new int[nrPlayers];
        for (int i = 0; i < nrPlayers; i++) {
            playerX[i] = new ArrayList();
            playerY[i] = new ArrayList();
            nrExtensionsLeft[i] = 3;
        }
        if (width <= 10 || height <= 10) {
            throw new Exception("Board size too small");
        }
        if (nrPlayers == 2) {
            playerX[0].add(width / 3);
            playerX[1].add((2 * width) / 3);
            playerY[0].add(height / 2);
            playerY[1].add(height / 2);
            playerOrientation[0] = NORTH;
            playerOrientation[1] = NORTH;
            lastPlayerOrientation[0] = NORTH;
            lastPlayerOrientation[1] = NORTH;
        }
        else if (nrPlayers == 4) {
            playerX[0].add(width / 3);
            playerX[1].add((2 * width) / 3);
            playerX[2].add(width / 3);
            playerX[3].add((2 * width) / 3);
            playerY[0].add(height / 2 - 1);
            playerY[1].add(height / 2 - 1);
            playerY[2].add(height / 2);
            playerY[3].add(height / 2);
            playerOrientation[0] = NORTH;
            playerOrientation[1] = NORTH;
            playerOrientation[2] = SOUTH;
            playerOrientation[3] = SOUTH;
            lastPlayerOrientation[0] = NORTH;
            lastPlayerOrientation[1] = NORTH;
            lastPlayerOrientation[2] = SOUTH;
            lastPlayerOrientation[3] = SOUTH;
        }
        else {
            throw new Exception("Number of players not supported");
        }
        dead = new boolean[nrPlayers];
        for (int i = 0; i < nrPlayers; i++) {
            occupied[playerX[i].get(0)][playerY[i].get(0)] = true;
        }
        nrDead = 0;
        this.nrPlayers = nrPlayers;
        chooseNextTarget();
        stepCount = 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNrPlayers() {
        return nrPlayers;
    }

    public List<Integer> getPlayerX(int i) {
        return playerX[i];
    }

    public List<Integer> getPlayerY(int i) {
        return playerY[i];
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public int getSize(int index){
        return playerX[index].size();
    }
    
    public boolean isOccupied(int x, int y) {
        return occupied[x][y];
    }

    public boolean isGameOver() {
        return nrDead == nrPlayers;
    }

    public int getLastOrientation(int player) {
        return lastPlayerOrientation[player];
    }

    public void setOrientation(int player, int dir) {
        playerOrientation[player] = dir;
    }

    public void chooseNextTarget() {
        targetX = rand.nextInt(width);
        targetY = rand.nextInt(height);
        while (occupied[targetX][targetY]) {
            targetX = rand.nextInt(width);
            targetY = rand.nextInt(height);
        }
    }

    public int nextPlayerX(int player, int move) {
        int headX = playerX[player].get(0);
        if (move == EAST) {
            return headX + 1;
        }
        else if (move == WEST) {
            return headX - 1;
        }
        return headX;
    }

    public int nextPlayerY(int player, int move) {
        int headY = playerY[player].get(0);
        if (move == NORTH) {
            return headY - 1;
        }
        else if (move == SOUTH) {
            return headY + 1;
        }
        else {
            return headY;
        }
    }

    public boolean isDead(int player) {
        return dead[player];
    }

    public boolean isFree(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height && !occupied[x][y]);
    }

    public boolean isLegalMove(int player, int move) {
        int headX = nextPlayerX(player, move);
        int headY = nextPlayerY(player, move);
        return isFree(headX, headY);
    }

    public void updatePlayerPosition(int player) {
        if (!dead[player]) {
            int size = playerX[player].size();
            lastPlayerOrientation[player] = playerOrientation[player];
            if (nrExtensionsLeft[player] == 0) {
                occupied[playerX[player].get(size - 1)][playerY[player].get(size - 1)] = false;
                playerX[player].remove(size - 1);
                playerY[player].remove(size - 1);
            }
            else {
                nrExtensionsLeft[player]--;
            }
            int headX = nextPlayerX(player, playerOrientation[player]);
            int headY = nextPlayerY(player, playerOrientation[player]);

            if (isFree(headX, headY)) {
                playerX[player].add(0, headX);
                playerY[player].add(0, headY);
                occupied[headX][headY] = true;
                if (headX == targetX && headY == targetY) {
                    targetX = -1;
                    targetY = -1;
                    nrExtensionsLeft[player] += extensionsPerTarget;
                }
            }
            else {
                dead[player] = true;
                nrDead++;
                for (int i = 0; i < playerX[player].size(); i++) {
                    occupied[playerX[player].get(i)][playerY[player].get(i)] = false;
                }
                playerX[player] = new ArrayList();
                playerY[player] = new ArrayList();
            }
        }
    }

    public boolean hasTarget() {
        return targetX >= 0 && targetY >= 0;
    }
}
