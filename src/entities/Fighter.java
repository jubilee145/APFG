package entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import status.StatusPacket;
import status.TestStatus;
import svb.Manager;
import svb.State;

public class Fighter extends Actor {

	private String inputString = "5,";
	public String inputBuffer = "";
	public String lastString = "";
	public String heldDirection = "";
	public String heldButton = "";
	
	public int time;
	public int hitStun = 0;
	public int health;
	public int maxHealth;

	public SpriteSheet spriteSheet;

	private State state;
	public List<State> openStates;
	public List<StatusPacket> status;
	private List<StatusPacket> removeStatus;
	
	public boolean canTurn = true;
	public boolean isFacingLeft = false;
	public boolean isTouchingGround = false;
	public boolean hitConfirmed = false;

	public Vector2f touchBoxOffset;

	public Fighter() throws SlickException {
		super();
	}

	public Fighter(SpriteSheet sheet, Vector2f startLocation) throws SlickException
	{
		super(sheet, startLocation);

		spriteSheet = sheet;

		openStates = new ArrayList<State>();
		status = new ArrayList<StatusPacket>();
		removeStatus = new ArrayList<StatusPacket>();
		
		touchBox = new Rectangle(0,0,32,50);
		touchBoxOffset = new Vector2f(31,30);
		zoneBox.setLocation(location);
		touchBox.setX(location.getX() + touchBoxOffset.x);
	}
	
	public void turnAround()
	{
		isFacingLeft = !isFacingLeft;

	}

	public void update(GameContainer container, int delta)
			throws SlickException {
		
		/** 
		 * NB: If animation.update() is handed a number greater than the animation duration, frames WILL be skipped.
		 * This will cause logic problems in states where the frame number is relevant, 
		 * such as IMPULSE states, and may cause strange hitbox behaviour.
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
			this.location.y = Manager.WORLD.groundLevel - zoneBox.getHeight() +(zoneBox.getHeight() - touchBoxOffset.y - touchBox.getHeight());
			isTouchingGround = true;
			if(velocity.y > 0)
				this.velocity.y = 0;
		}else if(this.touchBox.getY() + touchBox.getHeight() < Manager.WORLD.groundLevel)
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
		//Keep players onscreen
		if(this.touchBox.getX() < Manager.cameras.get(0).location.x)
		{
			this.location.x = Manager.cameras.get(0).location.x - touchBoxOffset.x;
		} else if(this.touchBox.getX() + this.touchBox.getWidth() > Manager.cameras.get(0).location.x + Manager.cameras.get(0).screen.getWidth())
		{
			this.location.x = Manager.cameras.get(0).location.x + Manager.cameras.get(0).screen.getWidth() - zoneBox.getWidth() + touchBoxOffset.x;
		}
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		render(container, g, 0,0);
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
	
	private void statusUpdate()
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

	public void setState(State newState)
	{
		this.state = newState;
		animation = newState.getAnimation();
		
		newState.reset();
		newState.doActions(this);
		newState.resetHitboxes(this);
		
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
		/*health -= hitbox.currentFrame.damage;
		
		velocity.x = hitbox.currentFrame.impulseH * hitbox.directionMultiplier;
		velocity.y = hitbox.currentFrame.impulseV;*/
		
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

	public String getInputString() {
		return inputString;
	}

	public void setInputString(String inputString) {
		this.inputString = inputString;
	}
}
