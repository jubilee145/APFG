package entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import status.StatusPacket;
import svb.Manager;
import svb.Player;

/**
 * Actor is basically the class used for anything that is on the screen and active.
 * It has behaviour for states and stage interaction, it holds information for collisions,
 * stuff like that.
 * 
 * @author Jubilee
 *
 */
public class Actor {

	public Vector2f acceleration; 
	public Vector2f drag; 
	public Vector2f friction;
	public Vector2f lastLocation;
	public Vector2f parallax;
	public Vector2f touchBoxOffset;
	public Vector2f velocity;
	
	public Color[] lights;
	
	public float maxVelocity;
	public float mass = 1;
	public float elasticity = 0;
	public float rotation = 0;
	
	public Player player;
	
	public Vector2f location;
	
	public Rectangle zoneBox;
	
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
		init(null);
	}
	
	public Actor(Vector2f startLocation) throws SlickException
	{
		init(startLocation);
	}
	
	protected void init(Vector2f startLocation) throws SlickException
	{
		openStates = new ArrayList<State>();
		status = new ArrayList<StatusPacket>();
		removeStatus = new ArrayList<StatusPacket>();
		
		//touchBox = new Rectangle(0,0,0,0);
		//touchBoxOffset = new Vector2f(0,0);
		
		acceleration = new Vector2f();
		drag = new Vector2f(0.2f,0.95f);
		
		parallax = new Vector2f(1,1);
		velocity = new Vector2f();
		maxVelocity = 100;
		
		location = startLocation;
		lastLocation = startLocation.copy();
		//if there is no startlocation, make an empty one.
		if(location==null)
			location = new Vector2f(0,0);
		
		/**
		 * TODO The width of the zonebox is used to determine when characters should turn around.
		 * This number is not exact, but it may be good enough. If characters jump and shift when
		 * they turn around, this is probably why.
		 */
		zoneBox = new Rectangle(0, 0, 300, 495);
	}
	
	public void update(GameContainer container, int delta)
			throws SlickException {
		
		//Don't update if the actor is not active.
		if(!active)
			return;
		
		statusUpdate();
		state.update(this, delta);
		
		
		
		location.add(velocity.copy().scale((float) (delta * Manager.timeScale * Manager.WORLD.conversionConstant)));
		
		if(state.isAllowGravity())
		{
			this.velocity.y += Manager.WORLD.gravity*delta * Manager.WORLD.conversionConstant * Manager.timeScale;
		}
		/**Set location of each touchbox (relative to fighter base position)
		 * Check if any touchboxes are in contact with the ground. If so,
		 * call touchGround, if not call inAir.
		 */
		int verticalOffset = state.getFrames()[state.getCurrentFrame()].getOffsetY();
		boolean inAir = true;
		for (Touchbox t : state.getTouchBoxes())
		{	
			
			/**
			 * If any part of the actor is colliding with the edge of the visible world, 
			 * call offCamera, which may do different things for different Actor types, 
			 * or depending on the situation. Usually will just shunt them back on screen
			 * (for fighters) or destroy them (for projectiles and co.)
			 */
			if(t.getX() < Manager.cameras.get(0).location.x)
			{
				offCamera(false);
				
			} else if(t.getX() + t.getWidth() > Manager.cameras.get(0).location.x + Manager.cameras.get(0).screen.getWidth())
			{
				offCamera(true);
			}
		}
		/**
		 * Trigger actors touchGround function if zonebox is below ground level.
		 * Otherwise, trigger the actors inAir function.
		 */
		if(zoneBox.getY() + zoneBox.getHeight() > Manager.WORLD.groundLevel)
		{
			touchGround();
		}
		else
		{
			inAir();
		}
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {

		if(!visible)
			return;
		state.render(container, g, location.x, location.y, this);
		
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
		state = newState;

		newState.reset();
		newState.doActions(this);
		newState.resetHitboxes(this);
		newState.resetTouchboxes(this);
		
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
		if(!hitbox.spent)
		{
			//Apply hitbox effects if character is not blocking.
			if(state.getBlock() != null && heldDirection.contains("4") && isValidBlock(hitbox, state))
			{
				setState(state.getBlock());
			}
			else
			{
				for(StatusPacket s: hitbox.status.applyTarget)
				{
					s.giveObject(this);
					this.applyStatus(s);
				}
			}
		}

		hitbox.spent = true;
	}
	
	private boolean isValidBlock(Hitbox h, State s)
	{
		StateFrame f = s.getBlock().getFrames()[0];

		if(h.hit.high && (f.getGuard().high || f.getWhiff().high))
		{
			return true;
		}
		if(h.hit.mid && (f.getGuard().mid || f.getWhiff().mid))
		{
			return true;
		}
		if(h.hit.low && (f.getGuard().low || f.getWhiff().low))
		{
			return true;
		}
		return false;
	}
	
	public void createSubActor(String params)
	{
		//Empty by default
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
