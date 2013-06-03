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
import svb.Quadtree;
 
/**
 * @author Jubilee
 */
public class Fight extends BasicGameState{
 
	private StateBasedGame game;
	public List<Fighter> fighters;
	public Stage stage;
	private Quadtree quadtree;
	private List<Rectangle> boxes;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
	        throws SlickException {
	    this.game = game;
	    stage = new Stage();
	    fighters = new ArrayList<Fighter>();
	    boxes = new ArrayList<Rectangle>();
	    quadtree = new Quadtree(3, new Rectangle(1920,6000, 0, -4000));
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
    	//if(delta > 18)
    	//	delta = 18;
    	
    	List<Rectangle> returnObjects = new ArrayList<Rectangle>();
    	int numBoxes = 0;
    	
    	quadtree.clear();
    	boxes.clear();
		for(Fighter f : fighters)
		{
			int fFrame = f.getState().getCurrentFrame();
			for(Actor a : f.subActors)
			{
				int aFrame = a.getState().getCurrentFrame();
				for(Rectangle r : a.getState().getTouchBoxes())
				{
					quadtree.insert(r);
					boxes.add(r);
					numBoxes++;
				}
				for(Rectangle r : a.getState().getHitBoxes())
				{
					quadtree.insert(r);
					boxes.add(r);
					numBoxes++;
				}
			}
			for(Rectangle r : f.getState().getTouchBoxes())
			{
				quadtree.insert(r);
				boxes.add(r);
				numBoxes++;
			}
			for(Rectangle r : f.getState().getHitBoxes())
			{
				quadtree.insert(r);
				boxes.add(r);
				numBoxes++;
			}
		}
		
		for (int i = 0; i < numBoxes; i++) {
		  returnObjects.clear();
		  quadtree.retrieve(returnObjects, boxes.get(i));
		 
		  for (int x = 0; x < returnObjects.size(); x++) {
			  // Run collision detection algorithm between objects
			  detectCollision(boxes.get(i), returnObjects.get(x));
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
    
    private void detectCollision(Rectangle rect1, Rectangle rect2)
    {
    	if(rect1.intersects(rect2))
    	{
	    	Actor parent1, parent2;	
	    	
	    	if(rect1.getClass() == Hitbox.class)
	    		parent1 = ((Hitbox)rect1).parent;
	    	else
	    		parent1 = ((Touchbox)rect1).parent;
	    	
	    	if(rect2.getClass() == Hitbox.class)
	    		parent2 = ((Hitbox)rect2).parent;
	    	else
	    		parent2 = ((Touchbox)rect2).parent;
	    	
	    	//Prevent self-collisions
	    	if(parent1 != parent2)
	    	{
	    		/* Prevent same-team collisions. This may need to change at a later date,
	    		 * as some characters may have healing/blocking/whatever stuff that
	    		 * they or their minions/projectiles/whatever will need to collide with.
	    		 */
	    		if(parent1.player != parent2.player)
	    		{
			    	if(rect1.getClass() == Hitbox.class)
			    	{
				    	if(rect2.getClass() == Hitbox.class)
				    	{
				    		collide((Hitbox)rect1, (Hitbox)rect2);
				    	}
				    	else
				    	{
				    		collide((Hitbox)rect1, (Touchbox)rect2);
				    	}
			    	}
			    	else
			    	{
			    		
			    		if(rect2.getClass() == Hitbox.class)
				    	{
			    			
				    		collide((Hitbox)rect2, (Touchbox)rect1);
				    	}
				    	else
				    	{
				    		collide((Touchbox)rect1, (Touchbox)rect2);
				    	}
			    	}
		    	}
	    	}

    	}
    }
    
    private void collide(Touchbox t1, Touchbox t2)
    {
    	//Seperate the two actors.
    	seperateX(t1, t2);
    }
    
    private void collide(Hitbox h1, Hitbox h2)
    {
    	//Perform 'clash'
    }
    
    private void collide(Hitbox h, Touchbox t)
    {
    	//Hit the parent of t with h.
    	t.parent.hit(h);
    }
    
    private static boolean seperateX(Touchbox t1, Touchbox t2)
	{
    	Actor actor1, actor2;
    	actor1 = t1.parent;
    	actor2 = t2.parent;
		//can't separate two immovable objects
		Boolean act1immovable = actor1.immovable;
		Boolean act2immovable = actor2.immovable;
		if(act1immovable && act2immovable)
			return false;

		//First, get the two object deltas
		float overlap = 0;
		float act1delta = actor1.location.x - actor1.lastLocation.x;
		float act2delta = actor2.location.x - actor2.lastLocation.x;
		
		if(act1delta != act2delta)
		{
			//Check if the X hulls actually overlap
			float act1deltaAbs = (act1delta > 0)?act1delta:-act1delta;
			float act2deltaAbs = (act2delta > 0)?act2delta:-act2delta;
			Touchbox act1rect = t1;
			Touchbox act2rect = t2;
			{
				float maxOverlap = act1deltaAbs + act2deltaAbs + 150;//OVERLAP_BIAS;
				
				//If they did overlap (and can), figure out by how much and flip the corresponding flags
				if(act1rect.getX() + act1rect.getWidth()/2 < act2rect.getX() + act2rect.getWidth()/2)
				{
					
					overlap = act1rect.getMinX() + act1rect.getWidth() - act2rect.getMinX();
					if((overlap > maxOverlap))
						overlap = 0;
				}
				else if(act1rect.getX() + act1rect.getWidth()/2 > act2rect.getX() + act2rect.getWidth()/2)
				{
					overlap = act1rect.getMinX() - act2rect.getWidth() - act2rect.getMinX();
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