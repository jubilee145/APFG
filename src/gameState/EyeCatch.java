package gameState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import entities.Fighter;

import svb.Manager;
import svb.Player;
import svb.StateFactory;

/**
 * @author Jubilee
 */
public class EyeCatch extends BasicGameState{

	private StateBasedGame game;
	
	File fighter1, fighter2;
	Fighter currentFighter;
	StateFactory stateF;
	List<SpriteSheet> sheetList;
	File fileName;
	File[] fileNames;
	int fileCounter = 0;
	boolean firstLoad;
	boolean loadComplete;
	boolean loadingSecondCharacter;
	private Fight fight;
	
	public void setFighters(File fighter1, File fighter2)
	{
		this.fighter1 = fighter1;
		this.fighter2 = fighter2;
		fileName = fighter1;
		fight = ((Fight)game.getState(Manager.StateIndex.FIGHT.ordinal()));
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
	{
		firstLoad = true;
		loadComplete = false;
		loadingSecondCharacter = false;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
	        throws SlickException {
	    this.game = game;
	    stateF = new StateFactory();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		// TODO Auto-generated method stub
		g.drawString("This is a loading screen...", 500, 500);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		if(!loadComplete)
		{
			if(makeFighter(fileName, currentFighter))
			{
				if(loadingSecondCharacter)
				{
					loadComplete = true;
					System.out.println("LOAD COMPLETE");
				}
				else
				{
					fileName = fighter2;
					loadingSecondCharacter = true;
				}
			}
		}
		if(loadComplete)
		{
			//if(flashyThingDone || skipButtonPressed)
			game.enterState(Manager.StateIndex.FIGHT.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
		}
	}
	
	public boolean makeFighter(File file, Fighter fighter) throws SlickException
	{
		if(firstLoad)
		{
			System.out.println("Load begun, character: " + file);
			sheetList = new ArrayList<SpriteSheet>();
			fileName = new File(file + "/sprites");
			fileNames = fileName.listFiles();
			firstLoad = false;
		}
		if (fileCounter < fileNames.length) 
		{
			System.out.print("Loading file " + fileNames[fileCounter] + " ...");
			sheetList.add(new SpriteSheet(fileNames[fileCounter].toString(), 300,360));
			System.out.println("Done.");
			fileCounter++;
		}
		else
		{
			fighter = initFighter();
			
			if(loadingSecondCharacter)
				Manager.cameras.get(0).target1 = fighter;
			else
				Manager.cameras.get(0).target2 = fighter;

			String movelist = file.getParentFile() + "/movelist";
			
			stateF.populateMoveList(movelist, sheetList, fighter);
			fight.fighters.add(fighter);
			fileCounter = 0;
			firstLoad = true;
			return true;
		}
		
		return false;
	}
	
	private Fighter initFighter() throws SlickException
	{
		
		Player player = Manager.player1;
		Rectangle touchBox = new Rectangle(0,0,150,260);
		Vector2f touchBoxOffset = new Vector2f(75,96);
		Vector2f startVector = new Vector2f(-500,Manager.WORLD.groundLevel);
		if(loadingSecondCharacter)
		{
			player = Manager.player2;
			startVector.x = 500;
		}
		Fighter fighter = new Fighter(sheetList.get(0), startVector);
		fighter.touchBox = touchBox;
		fighter.touchBoxOffset = touchBoxOffset;
		fighter.zoneBox.setLocation(fighter.location);
		fighter.touchBox.setX(fighter.location.getX() + fighter.touchBoxOffset.x);
		
		fighter.health = fighter.maxHealth = 5000;
		player.fighter = fighter;
		
		return fighter;
	}
	
    public void keyPressed(int key, char c) {
    	if(key==Input.KEY_ESCAPE)
    	{
    		game.enterState(Manager.StateIndex.MAIN_VS.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    	}
    }

	@Override
	public int getID() {
		return Manager.StateIndex.EYE_CATCH.ordinal();
	}

}
