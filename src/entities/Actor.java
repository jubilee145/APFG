package entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import status.StatusPacket;
import svb.Manager;
import svb.State;

/**
 * Actor is basically the class used for anything that is on the screen and active.
 * It has behaviour for animation and movement, it holds information for collisions,
 * stuff like that.
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
	public Vector2f touchBoxOffset;
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
	public boolean active = true;
	
	private String inputString = "5,";
	public String inputBuffer = "";
	public String lastString = "";
	public String heldDirection = "";
	public String heldButton = "";
	
	public int health;
	public int maxHealth;
	
	/**
	 * A list of states that can be accessed at all times/don't need to be cancelled into.
	 * "Hurt" is one such state, as is "Idle."
	 * This is more for convenience than anything else, since making every state cancel into
	 * idle when it was done would make the idle part of each characters movelist about
	 * ten pages long. 
	 */
	public List<State> openStates;
	
	/**
	 * Status effect that will be applied to the fighter. Used for any status that will last
	 * longer than a single State, such as poison. Some characters may have permanent status 
	 * effects built into them. (Things like ammunition counters, or super bars that behave
	 * differently to other characters.)
	 */
	public List<StatusPacket> status;
	protected List<StatusPacket> removeStatus;
	
	protected State state;
	
	public boolean canTurn = true;
	public boolean isFacingLeft = false;
	public boolean isTouchingGround = false;
	
	/**
	 * True when any hitbox of the current state has successfully connected with another player.
	 * Used to cancel into states that require hit confirmation, like grabs and counters.
	 */
	public boolean hitConfirmed = false;

	
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
		openStates = new ArrayList<State>();
		status = new ArrayList<StatusPacket>();
		removeStatus = new ArrayList<StatusPacket>();
		
		touchBox = new Rectangle(0,0,0,0);
		touchBoxOffset = new Vector2f(0,0);
		
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
		
		if(!active)
			return;
		
		/** 
		 * NB: If animation.update() is handed a number greater than the animation duration,
		 * frames WILL be skipped. This will cause logic problems in states where the frame 
		 * number is relevant,such as IMPULSE states, and may cause strange hitbox behaviour.
		*/
		animation.update((long)(delta * Manager.WORLD.conversionConstant * Manager.timeScale));
		statusUpdate();
		state.update(this);
		
		last = location.copy();
		location.add(velocity.copy().scale((float) (delta * Manager.timeScale * Manager.WORLD.conversionConstant)));
		
		if(isFacingLeft)
		{
			touchBox.setX(location.getX() + zoneBox.getWidth() - touchBox.getWidth() - touchBoxOffset.x);
		} 
		else
		{
			touchBox.setX(location.getX() + touchBoxOffset.x);
		}
		touchBox.setY(location.getY() + touchBoxOffset.y);
		
		
		
		if(state.isAllowGravity())
		{
			
			this.velocity.y += Manager.WORLD.gravity*delta * Manager.WORLD.conversionConstant * Manager.timeScale;
		}
		if(this.touchBox.getY() + touchBox.getHeight() >= Manager.WORLD.groundLevel)
		{
			touchGround();

		}else if(this.touchBox.getY() + touchBox.getHeight() < Manager.WORLD.groundLevel)
		{
			inAir();
		}
		
		//TODO make into offCamera function, and 
		//put the rest in Fighter where it belongs.
		//Keep players onscreen
		if(this.touchBox.getX() < Manager.cameras.get(0).location.x)
		{
			offCamera(false);
			
		} else if(this.touchBox.getX() + this.touchBox.getWidth() > Manager.cameras.get(0).location.x + Manager.cameras.get(0).screen.getWidth())
		{
			offCamera(true);
		}

	}

	public void render(GameContainer container, Graphics g, float offsetX, float offsetY)
			throws SlickException {

		if(!visible)
			return;

		if(isFacingLeft)
			g.drawImage(animation.getCurrentFrame().getFlippedCopy(true, false), location.x + offsetX, location.y + offsetY, color);
		else
			g.drawImage(animation.getCurrentFrame(), location.x + offsetX, location.y + offsetY, color);

	}
	
	
	
	protected void touchGround()
	{
		//Empty by default.
	}
	
	protected void inAir()
	{
		//Empty by default.
	}
	
	protected void offCamera(boolean isRightOfCamera)
	{
		//Empty by default.
	}
	
	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
	protected void statusUpdate()
	{
		for (StatusPacket s : status)
		{
			s.update();
			if(s.die())
				removeStatus.add(s);
		}
		
		for (StatusPacket s : removeStatus)
		{
			status.remove(s);
		}
		removeStatus.clear();
	}

	/**
	 * Resets and assigns a state to the fighter, along with the states animation.
	 */
	public void setState(State newState)
	{
		this.state = newState;
		animation = newState.getAnimation();
		
		newState.reset();
		newState.doActions(this);
		newState.resetHitboxes(this);
		
		//Clear input stream.
		if(!newState.isPreserveInput())
		{
			lastString = inputString;
			inputString = "5,";
		}
		
		hitConfirmed = false;
		canTurn = newState.canTurn();
	}
	
	public State getState() {
		return state;
	}

	public void hit(Hitbox hitbox)
	{	

		for(StatusPacket s: hitbox.status.applyTarget)
		{
			s.giveObject(this);
			this.applyStatus(s);
		}
	}
	
	public void applyStatus(StatusPacket s)
	{
		status.add(s);
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		
		if(this.health < 0)
			this.health = 0;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	
	public boolean intersects(Polygon shape)
	{
		return intersects(shape);
	}
	
	public boolean intersects(Actor actor)
	{
		return intersects(actor);
	}
	
	public String getInputString() {
		return inputString;
	}

	public void setInputString(String inputString) {
		this.inputString = inputString;
	}
	
}
