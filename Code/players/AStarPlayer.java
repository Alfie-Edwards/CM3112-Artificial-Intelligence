package players;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import snake.GameState;
import snake.Snake;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author steven
 */
public class AStarPlayer extends RandomPlayer {

    Map<Position, Integer> positions;

    public AStarPlayer(GameState state, int index, Snake game) {
        super(state, index, game);
    }

    @Override
    public void doMove() {
        Node n = searchTarget(state);
        if (n != null) {
            state.setOrientation(index, n.getFirstMove());
        }
        else {
            System.out.println("astar player doing random move");
            doRandomMove();
        }
    }

    /*
     * We want to select the move which gets us closest to the target
     */
    private Node searchTarget(GameState state) {
        initPositions(state);
        PriorityQueue<Node> frontier = new PriorityQueue();
        Set<Position> closed = new HashSet();
        frontier.add(new Node(state.getPlayerX(index).get(0), state.getPlayerY(index).get(0), state.getTargetX(), state.getTargetY()));
        while (!frontier.isEmpty()) {
            Node n = frontier.poll();
            closed.add(new Position(n.x, n.y));
            if (state.getTargetX() == n.x && state.getTargetY() == n.y) {
                return n;
            }
            else {
                if (!isOccupied(n.x + 1, n.y, n.depth, state) && !closed.contains(new Position(n.x + 1, n.y))) {
                    frontier.add(new Node(n.x + 1, n.y, n));
                }
                if (!isOccupied(n.x - 1, n.y, n.depth, state) && !closed.contains(new Position(n.x - 1, n.y))) {
                    frontier.add(new Node(n.x - 1, n.y, n));
                }
                if (!isOccupied(n.x, n.y + 1, n.depth, state) && !closed.contains(new Position(n.x, n.y + 1))) {
                    frontier.add(new Node(n.x, n.y + 1, n));
                }
                if (!isOccupied(n.x, n.y - 1, n.depth, state) && !closed.contains(new Position(n.x, n.y - 1))) {
                    frontier.add(new Node(n.x, n.y - 1, n));
                }
            }
        }
        return null;
    }

    protected void initPositions(GameState state) {
        positions = new HashMap();
        for (int i = 0; i < state.getNrPlayers(); i++) {
            if (!state.isDead(i)) {
                List<Integer> xList = state.getPlayerX(i);
                List<Integer> yList = state.getPlayerY(i);
                for (int j = 0; j < xList.size(); j++) {
                    positions.put(new Position(xList.get(j), yList.get(j)), xList.size() - j);
                }
            }
        }
    }

    /*
     * When searching for the shortest path, we want to take into account the fact that
     * other the snakes of the different players may block the path. Of course we don't know
     * what the positions of these snakes will be by the time we get at a given position.
     * However, we know that the position that takes e.g. 4 steps to each will be blocked if 
     *      (i) it is currently occupied by a snake
     *      (ii) the segment of the snake that occupies it is not among its 3 last segements
     * If these conditions aren't met, then we assume that the cell will be free.
     */
    protected boolean isOccupied(int x, int y, int step, GameState state) {
        if (x < 0 || x >= state.getWidth() || y < 0 || y >= state.getHeight()) {
            return true;
        }
        else if (!state.isOccupied(x, y)) {
            return false;
        }
        int cutoff = positions.get(new Position(x, y));
        return step <= cutoff;
    }
}
