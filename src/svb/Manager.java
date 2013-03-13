package svb;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import entities.Actor;
import entities.Entity;
import entities.Fighter;
import entities.Hitbox;

/**
 * Holds... Ugh, all kinds of stuff. This should probably be about three or four classes.
 * 
 * TODO: Make this into three or four classes. :P
 * 
 * Manager holds details about the world, deals with collisions, holds spritesheets for the characters,
 * deals with the cameras, updates *everything,* controls the flow of time... Bleh. It does everything.
 * @author Jubilee
 *
 */
public class Manager
{
	public static List<Actor> actors;
	public static List<Fighter> fighters;
	public static List<Player> players;
	public static Player player1, player2;
	public static List<Camera> cameras;
	public static List<SpriteSheet> haroldSheet;
	public static boolean debug;
	public static float timeScale = 100f;
	private static Quadtree quadtree;
	
	public static class WORLD
	{
		public static final int groundLevel = 0;
		public static final float gravity = 0.005f;
		public static final float conversionConstant = 0.01f;
	}
	
	public Manager() throws SlickException
	{
		actors = new ArrayList<Actor>();
		fighters = new ArrayList<Fighter>();
		players = new ArrayList<Player>();
		cameras = new ArrayList<Camera>();	
		haroldSheet = new ArrayList<SpriteSheet>();
		
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 1.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 2.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 3.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 4.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 5.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 6.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 7.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 8.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 9.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 10.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 11.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 12.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 13.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 14.png", 300,360));
		haroldSheet.add(new SpriteSheet("assets\\sprites\\HaroldSheet - 15.png", 300,360));
		
		debug = false;
		quadtree = new Quadtree(0, new Rectangle(0,0,800,600));
		
	}
	
	public static void update(GameContainer container, int delta) throws SlickException
	{
		for(Fighter f: fighters)
		{
			for (Fighter f2 : fighters)
			{
				if(f != f2)
				{
					if(f.touchBox.intersects(f2.touchBox))
					{
						seperateX(f, f2);
					}
					for(Hitbox h : f2.getState().hitBoxes)
					{
						if(h.intersects(f.touchBox)&&h.spent == false)
						{
							h.hit(f);

							if(f.getState().canBlock())
							{
								//Check if block state conditions are satisfied
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
		
		for(Actor a : actors)
		{
			a.update(container, delta);
		}
		
		for(Player p : players)
		{
			p.update(container, delta);
		}
		
		for(Camera c : cameras)
		{
			c.update(delta);
		}
		
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
	}
	
	public static void render(GameContainer container, Graphics g) throws SlickException
	{
		for(Camera c : cameras)
		{
			c.render(g);
		}
		if(debug)
			g.drawString("Actors: " + actors.size(), 5, 35);
	}
	
	public static void collide(Entity entity1, Entity entity2)
	{
		if (entity1.intersects(entity2))
		{
			
			//seperateX(entity1, entity2);
			//seperateY(entity1, entity2);
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
				float maxOverlap = act1deltaAbs + act2deltaAbs + 25;//OVERLAP_BIAS;
				
				//If they did overlap (and can), figure out by how much and flip the corresponding flags
				if(act1delta > act2delta)
				{
					overlap = actor1.location.x + act1rect.getWidth() - actor2.location.x;
					if((overlap > maxOverlap))// || !(actor1.allowCollisions & RIGHT) || !(actor2.allowCollisions & LEFT))
						overlap = 0;
					/*else
					{
						actor1.touching |= RIGHT;
						actor2.touching |= LEFT;
					}*/
				}
				else if(act1delta < act2delta)
				{
					overlap = actor1.location.x - act2rect.getWidth() - actor2.location.x;
					if((-overlap > maxOverlap))// || !(actor1.allowCollisions & LEFT) || !(actor2.allowCollisions & RIGHT))
						overlap = 0;
					/*else
					{
						actor1.touching |= LEFT;
						actor2.touching |= RIGHT;
					}*/
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
				actor1.velocity.x = average + act1velocity * actor1.elasticity;
				actor2.velocity.x = average + act2velocity * actor2.elasticity;
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
	
	private static boolean seperateY(Actor actor1, Actor actor2)
	{
		//can't separate two immovable objects
		Boolean act1immovable = actor1.immovable;
		Boolean act2immovable = actor2.immovable;
		if(act1immovable && act2immovable)
			return false;
		
		//If one of the objects is a tilemap, just pass it off.
		/*if(actor1 is FlyTilemap)
			return (actor1 as FlyTilemap).overlapsWithCallback(actor2,separatey);
		if(actor2 is FlyTilemap)
			return (actor2 as FlyTilemap).overlapsWithCallback(actor1,separatey,true);*/
		
		//First, get the two object deltas
		float overlap = 0;
		float act1delta = actor1.location.y - actor1.last.y;
		float act2delta = actor2.location.y - actor2.last.y;
		if(act1delta != act2delta)
		{
			//Check if the y hulls actually overlap
			float act1deltaAbs = (act1delta > 0)?act1delta:-act1delta;
			float act2deltaAbs = (act2delta > 0)?act2delta:-act2delta;
			Rectangle act1rect = actor1.zoneBox;//new Rectangle(actor1.location.y-((act1delta > 0)?act1delta:0),actor1.last.y,actor1.rect.Height+((act1delta > 0)?act1delta:-act1delta),actor1.height);
			Rectangle act2rect = actor2.zoneBox;//new Rectangle(actor2.location.y-((act2delta > 0)?act2delta:0),actor2.last.y,actor2.rect.getHeight()+((act2delta > 0)?act2delta:-act2delta),actor2.height);
			//if((act1rect.gety() + act1rect.getHeight() > act2rect.gety()) && (act1rect.gety() < act2rect.gety() + act2rect.Height) && (act1rect.y + act1rect.height > act2rect.y) && (act1rect.y < act2rect.y + act2rect.height))
			{
				float mayOverlap = act1deltaAbs + act2deltaAbs + 1;
				//int mayOverlap = 1;
				
				//If they did overlap (and can), figure out by how much and flip the corresponding flags
				if(act1delta > act2delta)
				{
					overlap = actor1.location.y + act1rect.getHeight() - actor2.location.y;
					if((overlap > mayOverlap))// || !(actor1.allowCollisions & RIGHT) || !(actor2.allowCollisions & LEFT))
						overlap = 0;
					/*else
					{
						actor1.touching |= RIGHT;
						actor2.touching |= LEFT;
					}*/
				}
				else if(act1delta < act2delta)
				{
					overlap = actor1.location.y - act2rect.getHeight() - actor2.location.y;
					if((-overlap > mayOverlap))// || !(actor1.allowCollisions & LEFT) || !(actor2.allowCollisions & RIGHT))
						overlap = 0;
					/*else
					{
						actor1.touching |= LEFT;
						actor2.touching |= RIGHT;
					}*/
				}
			}
		}
		
		//Then adjust their positions and velocities accordingly (if there was any overlap)
		if(overlap != 0)
		{
			float act1v = actor1.velocity.y;
			float act2v = actor2.velocity.y;
			
			if(!act1immovable && !act2immovable)
			{
				overlap *= 0.5;
				actor1.location.y = actor1.location.y - overlap;
				actor2.location.y += overlap;

				float act1velocity = (float) (Math.sqrt((act2v * act2v * actor2.mass)/actor1.mass) * ((act2v > 0)?1:-1));
				float act2velocity = (float) (Math.sqrt((act1v * act1v * actor1.mass)/actor2.mass) * ((act1v > 0)?1:-1));
				float average = (act1velocity + act2velocity)*0.5f;
				act1velocity -= average;
				act2velocity -= average;
				actor1.velocity.y = average + act1velocity * actor1.elasticity;
				actor2.velocity.y = average + act2velocity * actor2.elasticity;
			}
			else if(!act1immovable)
			{
				actor1.location.y = actor1.location.y - overlap;
				actor1.velocity.y = act2v - act1v*actor1.elasticity;
			}
			else if(!act2immovable)
			{
				actor2.location.y += overlap;
				actor2.velocity.y = act1v - act2v*actor2.elasticity;
			}
			return true;
		}
		else
			return false;
	}
}
