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

	public Fighter(Vector2f startLocation) throws SlickException
	{
		super(startLocation);
		subActors = new ArrayList<Actor>();
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
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		super.render(container, g, this.location.x, this.location.y);
		
		for(Actor a : subActors)
		{
			//a.render(container, g, offsetX, offsetY);
		}
	}
	
	@Override
	protected void touchGround()
	{
		
		/**
		 * If part of any touchbox is colliding with the ground, 
		 * raise the actor until it is not.
		 */
		int verticalOffset = state.getFrames()[state.getCurrentFrame()].getOffsetY();
		float height = 0;
		for (Touchbox t : state.getTouchBoxes())
		{
			if(t.getY() + t.getHeight() > height)
			{
				height = t.getY() + t.getHeight() + verticalOffset;
			}
			
		}
		this.location.y = Manager.WORLD.groundLevel - height;
		
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
		if(isRightOfCamera)
		{
			this.location.x = Manager.cameras.get(0).location.x + Manager.cameras.get(0).screen.getWidth() - zoneBox.getWidth() + touchBoxOffset.x;
		} else
		{
			this.location.x = Manager.cameras.get(0).location.x - touchBoxOffset.x;
		}
	}


}
