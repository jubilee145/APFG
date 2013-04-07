package gameState;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import svb.Manager;

/**
 * @author Jubilee
 */
public class SplashScreen extends BasicGameState{

	private StateBasedGame game;
	private Image backdrop;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
	        throws SlickException {
	    this.game = game;
		backdrop = new Image("assets/images/menu/splash.png");
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		backdrop.draw();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Maybe animations and stuff?
		
	}
	
	public void keyPressed(int key, char c) 
	{
		game.enterState(Manager.StateIndex.MAIN_MENU.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
	}

	@Override
	public int getID() {
		return Manager.StateIndex.SPLASH.ordinal();
	}

}
