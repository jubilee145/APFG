package gameState;

import java.util.List;

import menu.MenuButton;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import svb.Manager;

/**
 * @author Jubilee
 */

public class OptionsMenu extends BasicGameState{

	private StateBasedGame game;
	private List<MenuButton> buttons;
	private MenuButton activeButton;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		this.game = game;
		// TODO Read file or create it if no file exists. Set up options & buttons.
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		// TODO Auto-generated method stub
		g.drawString("IMMA OPTIONS MENU :D", 500, 500);
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
	{
		// TODO Assign options to Manager class, overwrite .ini file.
	}
	
	@Override
	public int getID() {
		return Manager.StateIndex.MAIN_OPTIONS.ordinal();
	}
	
}
