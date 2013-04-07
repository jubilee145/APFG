package gameState;

import java.util.ArrayList;
import java.util.List;

import menu.MenuButton;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import svb.Manager;

/**
 * @author Jubilee
 */
public class MainMenu extends BasicGameState{
 
	private StateBasedGame game;
	private List<MenuButton> buttons;
	private MenuButton activeButton;
	 
	MenuButton btnVS;
	MenuButton btnOptions;
	MenuButton btnQuit;
	
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
	        throws SlickException {
	    this.game = game;
	    buttons = new ArrayList<MenuButton>();
	    btnVS = new MenuButton(new Image("assets/images/menu/btnVS.png"),50,400);
	    btnOptions = new MenuButton(new Image("assets/images/menu/btnOptions.png"),60,500);
	    btnQuit = new MenuButton(new Image("assets/images/menu/btnQuit.png"),80,600);
	    buttons.add(btnVS);
	    buttons.add(btnOptions);
	    buttons.add(btnQuit);
	    
	    activeButton = btnVS;
	}
 
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        // TODO Auto-generated method stub
    	
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        for(MenuButton b : buttons)
        {
        	g.setColor(Color.darkGray);
        	if(b == activeButton)
        		g.setColor(Color.white);
        	b.render(container, g);
        	g.setColor(Color.white);
        }
 
    }
    
    public void keyPressed(int key, char c) {
        switch(key) {
        case Input.KEY_UP:
            if(buttons.indexOf(activeButton)!=0)
            	activeButton = buttons.get(buttons.indexOf(activeButton)-1);
            else
            	activeButton = buttons.get(buttons.size()-1);
            break;
        case Input.KEY_DOWN:
            if(buttons.indexOf(activeButton)!=buttons.size()-1)
            	activeButton = buttons.get(buttons.indexOf(activeButton)+1);
            else
            	activeButton = buttons.get(0);
            break;
        default:
            break;
        }
    }
    
    public void keyReleased(int key, char c) {
        int acceptKey1 = Manager.player1.inputHandler.keysIndex[4];
        int acceptKey2 = Manager.player1.inputHandler.keysIndex[6];
        int acceptKey3 = Manager.player2.inputHandler.keysIndex[4];
        int acceptKey4 = Manager.player2.inputHandler.keysIndex[6];
        int abortKey1 = Manager.player1.inputHandler.keysIndex[5];
        int abortKey2 = Manager.player1.inputHandler.keysIndex[7];
        int abortKey3 = Manager.player2.inputHandler.keysIndex[5];
        int abortKey4 = Manager.player2.inputHandler.keysIndex[7];

        boolean accept = false;
        boolean abort = false;
        
        if(key == acceptKey1||key == acceptKey2||key == acceptKey3||key == acceptKey4)
        	accept = true;
        if(key == abortKey1||key == abortKey2||key == abortKey3||key == abortKey4)
        	abort = true;
        
        if(accept)
        {
	    	if(activeButton == btnVS)
	        {
	        	game.enterState(Manager.StateIndex.MAIN_VS.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
	        }
	        else if(activeButton == btnOptions)
	        {
	        	System.out.println("Options doesn't exist yet!");
	        }
	        else if(activeButton == btnQuit)
	        {
	        	System.exit(0);
	        }
        }
        if(abort)
        	game.enterState(Manager.StateIndex.SPLASH.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));

        /*switch(key) {
        case Input.KEY_1:
            game.enterState(2, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
            break;
        case Input.KEY_2:
            // TODO: Implement later
            break;
        case Input.KEY_3:
        	System.exit(0);
            break;
        default:
            break;
        }*/
    }
 
    @Override
    public int getID() {
        // TODO Auto-generated method stub
        return 1;
    }
 
}