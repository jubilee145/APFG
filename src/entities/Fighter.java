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
		if(container.getInput().isKeyDown(Input.KEY_C))
		{

		}
	}
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		super.render(container, g);
		
		for(Actor a : subActors)
		{
			a.render(container, g);
		}
	}
	
	@Override
	protected void touchGround()
	{

		/**
		 * Raise the actor until it is not colliding with the ground.
		 */
		this.location.y = Manager.WORLD.groundLevel - this.zoneBox.getHeight();

		if(!isTouchingGround)
		{
			for(State s : openStates)
			{
				if(s.getName().contentEquals("Land"))
					this.setState(s);
			}
			isTouchingGround = true;
		}
		if(velocity.y > 0)
			this.velocity.y = 0;
		
	}
	
	@Override
	protected void inAir()
	{
		/*if(this.state.getName().contentEquals("Idle"))
		{
			for(State s : openStates)
			{
				if(s.getName().contentEquals("Fall"))
					this.setState(s);
			}
		}*/
		isTouchingGround = false;
	}
	
	protected void createSubactor()
	{
		//Empty by default.
	}
	
	@Override
	protected void offCamera(boolean isRightOfCamera)
	{
		/**
		 * If characters are off camera, bring them back on, provided they aren't already moving
		 * in the right direction. If they ARE moving in the right direction, leave them alone, 
		 * other wise you can get characters 'sticking' to the walls. 
		 * It still shouldn't allow
		 * characters offscreen, hopefully. You'd need to get off-camera, by moving towards the
		 * center of the camera.
		 */
		float xDelta = velocity.x;
		if(isRightOfCamera)
		{
			if(xDelta >= 0)
				this.location.x = Manager.cameras.get(0).location.x + Manager.cameras.get(0).screen.getWidth() - zoneBox.getWidth() + state.getFrames()[state.getCurrentFrame()].getOffsetX()/4 - 1;
		} 
		else
		{
			if(xDelta <= 0)
				this.location.x = Manager.cameras.get(0).location.x - state.getFrames()[state.getCurrentFrame()].getOffsetX()/4 + 1;
		}
	}
	
	@Override
	public void createSubActor(String params)
	{
		Vector2f locCopy = location.copy();
		locCopy.y +=200;
		Projectile proj;
		try {
			proj = new Projectile(locCopy, isFacingLeft);
			proj.player = player;
			subActors.add(proj);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
