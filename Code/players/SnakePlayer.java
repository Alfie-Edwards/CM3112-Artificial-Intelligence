package players;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import snake.GameDisplay;
import snake.GameState;
import snake.Snake;

/**
 *
 * @author steven
 */
public abstract class SnakePlayer {
    protected GameState state;
    protected GameDisplay display;
    protected Snake game;
    protected int index;
    
    public SnakePlayer(GameState state, int index, Snake game){
        this.state=state;                             
        this.index = index;
        this.game=game;
    }     
 
    public void setState(GameState state){
        this.state=state;
    }
    
    public int getIndex(){
        return index;
    }
    
   public void setDisplay(GameDisplay display) {
        this.display = display;        
    }
    
    public abstract void doMove();
       
    
}
