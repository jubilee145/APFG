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
    	for(Fighter f: fighters)
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
		}
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
		//can't separate two immovable objects
		Boolean act1immovable = actor1.immovable;
		Boolean act2immovable = actor2.immovable;
		if(act1immovable && act2immovable)
			return false;

		//First, get the two object deltas
		float overlap = 0;
		float act1delta = actor1.location.x - actor1.last.x;
		float act2delta = actor2.location.x - actor2.last.x;
		
		if(act1delta != act2delta)
		{
			//Check if the X hulls actually overlap
			float act1deltaAbs = (act1delta > 0)?act1delta:-act1delta;
			float act2deltaAbs = (act2delta > 0)?act2delta:-act2delta;
			Rectangle act1rect = actor1.touchBox;
			Rectangle act2rect = actor2.touchBox;
			{
				float maxOverlap = act1deltaAbs + act2deltaAbs + 150;//OVERLAP_BIAS;
				
				//If they did overlap (and can), figure out by how much and flip the corresponding flags
				if(act1rect.getX() + act1rect.getWidth()/2 < act2rect.getX() + act2rect.getWidth()/2)
				{
					overlap = actor1.location.x + act1rect.getWidth() - actor2.location.x;
					if((overlap > maxOverlap))
						overlap = 0;
				}
				else if(act1rect.getX() + act1rect.getWidth()/2 > act2rect.getX() + act2rect.getWidth()/2)
				{
					overlap = actor1.location.x - act2rect.getWidth() - actor2.location.x;
					if((-overlap > maxOverlap))
						overlap = 0;
				}
			}
		}
		
		//Then adjust their positions and velocities accordingly (if there was any overlap)
		if(overlap != 0)
		{
			float act1v = actor1.velocity.x;
			float act2v = actor2.velocity.x;
			
			if(!act1immovable && !act2immovable)
			{
				overlap *= 0.5;
				actor1.location.x = actor1.location.x - overlap;
				actor2.location.x += overlap;

				float act1velocity = (float) (Math.sqrt((act2v * act2v * actor2.mass)/actor1.mass) * ((act2v > 0)?1:-1));
				float act2velocity = (float) (Math.sqrt((act1v * act1v * actor1.mass)/actor2.mass) * ((act1v > 0)?1:-1));
				float average = (act1velocity + act2velocity)*0.5f;
				act1velocity -= average;
				act2velocity -= average;
			}
			else if(!act1immovable)
			{
				actor1.location.x = actor1.location.x - overlap;
				actor1.velocity.x = act2v - act1v*actor1.elasticity;
			}
			else if(!act2immovable)
			{
				actor2.location.x += overlap;
				actor2.velocity.x = act1v - act2v*actor2.elasticity;
			}
			return true;
		}
		else
			return false;
	}

 
    @Override
    public int getID() {
        return Manager.StateIndex.FIGHT.ordinal();
    }
 
}