package players;

import snake.GameDisplay;
import snake.GameState;
import snake.Snake;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 *
 * @author steven
 */
public class HumanPlayer extends SnakePlayer implements KeyListener {

  
    public HumanPlayer(GameState state, int index, Snake game) {
        super(state, index, game);      
    }

    public void setDisplay(GameDisplay display) {
        super.setDisplay(display);
          display.addKeyListener(this);
    }

    public void keyTyped(KeyEvent e) {
    }

   /*
    * Remeber the last arrow key that was pressed to determine the direction of the next move
    * If this key corresponds to the opposite of the current direction, it is ignored as that 
    * would mean a guaranteed death (it suggests the player pressed the key to fast when trying
    * to make a u-turn).
    */
    public void keyPressed(KeyEvent e) {
        int lastOrientation = state.getLastOrientation(index);
        if (e.getKeyCode() == KeyEvent.VK_UP && lastOrientation!= GameState.SOUTH) {            
            state.setOrientation(index,GameState.NORTH);                   
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN && lastOrientation!= GameState.NORTH) {
            state.setOrientation(index,GameState.SOUTH);                   
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && lastOrientation!= GameState.WEST) {
            state.setOrientation(index,GameState.EAST);                   
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && lastOrientation!= GameState.EAST) {            
            state.setOrientation(index,GameState.WEST);                   
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void doMove() {
    }
}
