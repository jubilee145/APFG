package entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import svb.Camera;
import svb.Manager;

/**
 * Actor is basically the class used for anything that is on the screen and active.
 * It has behaviour for animation and movement, it holds information for collisions,
 * stuff like that.
 * 
 * Right now, this could probably be be merged with Fighter, since it's the only class that extends
 * it, and it overrides a lot of the important things. Later on though, actor will be used for
 * projectiles, animated background objects, maybe menu items and stuff too.
 * 
 * @author Jubilee
 *
 */
public class Actor {

	public Vector2f acceleration; 
	public Vector2f drag; 
	public Vector2f friction;
	public Vector2f last;
	public Vector2f parallax;
	public Vector2f velocity;
	
	public Color[] lights;
	
	public float maxVelocity;
	public float mass = 1;
	public float elasticity = 0;
	public float rotation = 0;
	
	public Animation animation;
	
	public Vector2f location;
	
	public Rectangle zoneBox;
	public Rectangle touchBox;
	
	public Color color;
	
	public boolean immovable = false;
	public boolean visible = true;
	
	public Actor() throws SlickException
	{
		init(null, null);
	}
	
	public Actor(SpriteSheet sheet, Vector2f startLocation) throws SlickException
	{
		init(sheet, startLocation);
	}
	
	protected void init(SpriteSheet sheet, Vector2f startLocation) throws SlickException
	{
		acceleration = new Vector2f();
		drag = new Vector2f(0.2f,0.95f);
		
		parallax = new Vector2f(1,1);
		velocity = new Vector2f();
		maxVelocity = 100;
		
		//if there is no image, make an empty one.
		if(sheet == null)
			this.animation = new Animation();
		else
			this.animation = new Animation(sheet, 100);
		
		location = startLocation;
		//if there is no startlocation, make an empty one.
		if(location==null)
			location = new Vector2f(0,0);
		
		zoneBox = new Rectangle(0, 0, animation.getWidth(), animation.getHeight());
	}
	
	public void update(GameContainer container, int delta)
			throws SlickException {
		last = location.copy();
		
		location.add(velocity.copy().scale((float) (delta * Manager.timeScale)));
		zoneBox.setLocation(location);
		
		if(acceleration.x != 0)
			velocity.x += (acceleration.x);
		else
			velocity.x *= (drag.x);
		
		if(acceleration.y != 0)
			velocity.y += (acceleration.y);
		else
			velocity.y *= drag.y;
		
		//TODO Make work for negative velocity
		if(velocity.x > maxVelocity)
		{
			velocity.x = maxVelocity;
		}
		if(velocity.y > maxVelocity)
		{
			velocity.y = maxVelocity;
		}

	}
	
	private void draw(GameContainer container, Graphics g, float offsetX, float offsetY)
	{
		if(!visible)
			return;

		animation.draw(location.x + offsetX, location.y + offsetY, color);
		

	}
	
	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		draw(container, g, 0, 0);
	}
	
	public void render(GameContainer container, Graphics g, float offsetX, float offsetY)
			throws SlickException {
		draw(container, g, offsetX, offsetY);
	}
	
	public boolean intersects(Polygon shape)
	{
		return intersects(shape);
	}
	
	public boolean intersects(Actor actor)
	{
		return intersects(actor);
	}
	
}
