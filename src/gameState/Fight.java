package gameState;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import entities.Actor;
import entities.Fighter;
import entities.Hitbox;
import entities.Touchbox;

import stage.Stage;
import svb.Camera;
import svb.EventHandler;
import svb.Manager;
import svb.Player;
 
/**
 * @author Jubilee
 */
public class Fight extends BasicGameState{
 
	private StateBasedGame game;
	public List<Fighter> fighters;
	public Stage stage;
	@Override
	public void init(GameContainer container, StateBasedGame game)
	        throws SlickException {
	    this.game = game;
	    stage = new Stage();
	    fighters = new ArrayList<Fighter>();
	}
 
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        g.setColor(Color.white);
        
		for(Camera c : Manager.cameras)
		{
			c.render(g);
		}
    }
 
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        //Reduce delta in low framerate situations.
    	//THIS SHOULD NEVER HAPPEN
    	if(delta > 17)
    		delta = 17;
    	
    	//Update Fighters
    	/*for(Fighter f: fighters)
		{
			for (Fighter f2 : fighters)
			{
				if(f != f2)
				{
					//If fighters are touching, seperate them.
					if(f.touchBox.intersects(f2.touchBox))
					{
						seperateX(f, f2);
					}
					//If hitboxes from one player touch the other, collide them
					for(Hitbox h : f2.getState().hitBoxes)
					{
						if(h.intersects(f.touchBox)&&h.spent == false)
						{
							
							h.hit(f);
							if(f.getState().canBlock())
							{
								//Check if block State conditions are satisfied
								boolean blockCondition = false;
								if(f.getState().getBlock().getConditions().size() == 0)
									blockCondition = true;
								else
								{
									for(String s : f.getState().getBlock().getConditions())
									{
										if(EventHandler.check(f, s))
										{
											blockCondition = true;
											break;
										}
									}
								}
								
								if(blockCondition)
								{
									f.setState(f.getState().getBlock());
									if(!f.getState().checkGuard(h))
										f.hit(h);
								}
								else
									f.hit(h);
							}
							else
								f.hit(h);
						}
					}
				}
			}
		}*/
    	//Turn the players around if they're behind one another.
    	Player player1 = Manager.player1;
    	Player player2 = Manager.player2;
		float player1Loc = player1.fighter.location.x + player1.fighter.zoneBox.getWidth()/2;
		float player2Loc = player2.fighter.location.x + player2.fighter.zoneBox.getWidth()/2;
		boolean player1Left = player1.fighter.isFacingLeft;
		boolean player2Left = player2.fighter.isFacingLeft;
		boolean player1Turn = player1.fighter.canTurn;
		boolean player2Turn = player2.fighter.canTurn;
		if(player1Loc > player2Loc)
		{
			if(!player1Left && player1Turn)
				player1.turnAround();

			if(player2Left && player2Turn)
				player2.turnAround();
		}
		else if(player1Loc < player2Loc)
		{
			if(player1Left && player1Turn)
				player1.turnAround();

			if(!player2Left && player2Turn)
				player2.turnAround();
		}

		for (Player p : Manager.players)
		{
			p.update(container, delta);
		}
		
		for(Camera c : Manager.cameras)
		{
			c.update(delta);
		}
		
    }
    
    private static boolean seperateX(Actor actor1, Actor actor2)
	{
		return true;
	}

 
    @Override
    public int getID() {
        return Manager.StateIndex.FIGHT.ordinal();
    }
 
}