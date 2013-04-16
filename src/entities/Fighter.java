package entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import svb.Manager;
import svb.State;

/**
 * The fighter class is basically what the player is controlling/seeing right now.
 * It holds & updates the current active State, animation and collision data, holds the players 
 * input data for the State to look at & keeps track of things that need to last longer than 
 * any single state, such as hitstun. (When it's done. :D)
 * 
 * @author Jubilee
 *
 */
public class Fighter extends Actor {

	public int time;
	public int hitStun = 0;
	
	public List<Actor> subActors;

	public Fighter(SpriteSheet sheet, Vector2f startLocation) throws SlickException
	{
		super(sheet, startLocation);
		subActors = new ArrayList<Actor>();
		
		//TODO State-based touchboxes
		touchBox = new Rectangle(0,0,32,50);
		touchBoxOffset = new Vector2f(31,30);
		
		zoneBox.setLocation(location);
		touchBox.setX(location.getX() + touchBoxOffset.x);
	}
	
	public void turnAround()
	{
		isFacingLeft = !isFacingLeft;

	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {	
		super.update(container, delta);
		for(Actor a : subActors)
		{
			a.update(container, delta);
		}
		if(container.getInput().isKeyPressed(Input.KEY_C))
		{
			SpriteSheet s = new SpriteSheet("assets/sprites/projectileTest.png", 100, 100);
			subActors.add(new Projectile(s, this.location.copy()));
		}
	}
	
	@Override
	public void render(GameContainer container, Graphics g, float offsetX, float offsetY)
			throws SlickException {
		super.render(container, g, offsetX, offsetY);
		for(Actor a : subActors)
		{
			//a.render(container, g, offsetX, offsetY);
		}
	}
	
	@Override
	protected void touchGround()
	{
		this.location.y = Manager.WORLD.groundLevel - zoneBox.getHeight() +(zoneBox.getHeight() - touchBoxOffset.y - touchBox.getHeight());
		if(!isTouchingGround)
			for(State s : openStates)
			{
				if(s.getName().contentEquals("Land"))
					this.setState(s);
			}
		isTouchingGround = true;
		if(velocity.y > 0)
			this.velocity.y = 0;
	}
	
	@Override
	protected void inAir()
	{
		if(this.state.getName().contentEquals("Idle"))
		{
			for(State s : openStates)
			{
				if(s.getName().contentEquals("Fall"))
					this.setState(s);
			}
		}
		isTouchingGround = false;
	}
	
	@Override
	protected void offCamera(boolean isRightOfCamera)
	{
		//Empty by default.
		if(isRightOfCamera)
		{
			this.location.x = Manager.cameras.get(0).location.x + Manager.cameras.get(0).screen.getWidth() - zoneBox.getWidth() + touchBoxOffset.x;
		} else
		{
			this.location.x = Manager.cameras.get(0).location.x - touchBoxOffset.x;
		}
	}


}
