package svb;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import entities.Fighter;
import entities.Hitbox;

public class Player {

	/**
	 * The fighter object associated with this player.
	 */
	public Fighter fighter;
	
	/**
	 * Where input is parsed/comes from.
	 */
	public InputHandler inputHandler;
	
	/**
	 * Used to determine input delays. Clears the input after 1/4 of a second.
	 */
	private int inputTime;
	
	/**
	 * Where to display the debug information on the screen.
	 */
	private Vector2f debugDrawLocation;
	
	/**
	 * Whether the combo tracker information is displayer. TRUE when comboCounter > 1,
	 * FALSE when time since last hit > 250ms
	 */
	private boolean comboVisible = false;
	
	/**
	 * The number of hits dealt to this player in the current combo
	 */
	private int comboCounter = 0;
	
	/**
	 * The total damage dealt to the player in the current combo.
	 */
	private int damageCounter = 0;
	
	/**
	 * The colour of the displayed combo counter information. Gets set to RED when the
	 * player is repeatedly hit during hitstun, gets set to BLUE if they are hit out
	 * of hitstun, but soon enough for it to be considered a combo. (IE The player could
	 * have broken out of the combo, but did not.)
	 */
	private Color comboColour = Color.red;
	
	/**
	 * The time since the player was last hit by an opponents attack. Stops incrementing at
	 * 250 ms.
	 */
	private int comboTimer = 250;
	
	public Player()
	{
		debugDrawLocation = new Vector2f(0,0);
		
		inputHandler = new InputHandler();
	}
	
	public Player(int x, int y)
	{
		debugDrawLocation = new Vector2f(x,y);
		
		inputHandler = new InputHandler();
	}
	
	public void update(GameContainer container, int delta) throws SlickException
	{
		inputTime += delta;
		
		if(comboTimer < 500)
		{
			if(comboCounter > 1)
			{
				comboVisible = true;
				comboTimer += delta;
			}
		}
		else
		{
			damageCounter = 0;
			comboCounter = 0;
			comboVisible = false;
		}
		
		inputHandler.update(container);
		
		fighter.inputBuffer = inputHandler.checkDirections() + inputHandler.checkButtons();
		fighter.heldDirection = inputHandler.heldDirection;
		fighter.heldButton = inputHandler.heldButton;		
		
		if(inputHandler.resetClock)
			inputTime = 0;
		
		if(!fighter.inputBuffer.contentEquals(""))
		{
			if(fighter.getInputString().contains("5,"))
				fighter.setInputString("");
			fighter.setInputString(fighter.getInputString() + fighter.inputBuffer);
		}
		fighter.inputBuffer = "";

		if(inputTime >= 250)
		{
			if(fighter.getInputString() != "5,")
				fighter.lastString = fighter.getInputString();
			fighter.setInputString("5,");
			inputTime = 0;
		}

		inputHandler.reset(container);
		
		fighter.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		
		//Render health bar, on right hand side if player 2
		if(this == Manager.player1)
		{
			g.drawRect(50, 35, 500, 20);
			g.fillRect(50, 35, 500 * fighter.health/fighter.maxHealth, 20);
		}
		else
		{
			g.drawRect(1920 - 50, 35, -500, 20);
			g.fillRect(1920 - 50, 35, -500 * fighter.health/fighter.maxHealth, 20);
		}
		
		if(Manager.debug)
		{
			g.drawString("Input String: " + fighter.getInputString(), debugDrawLocation.x+5, 55);
			g.drawString("Input Buffer: " + fighter.inputBuffer, debugDrawLocation.x+5, 75);
			g.drawString("Time: " + inputTime, debugDrawLocation.x+5, 95);
			g.drawString("State: " + fighter.getState().getName(), debugDrawLocation.x+5, 115);
			g.drawString("Frame: " + fighter.getState().getCurrentFrame(), debugDrawLocation.x+5, 135);
			if(fighter.isFacingLeft)
				g.drawString("Facing: Left", debugDrawLocation.x+5, 155);
			else
				g.drawString("Facing: Right", debugDrawLocation.x+5, 155);
			g.drawString("Held Direction: " + fighter.heldDirection, debugDrawLocation.x+5, 175);
			g.drawString("Held Button: " + fighter.heldButton, debugDrawLocation.x+5, 195);
			g.drawString("On ground: " + fighter.isTouchingGround, debugDrawLocation.x+5, 215);
			g.drawString("Last Buffer: " + fighter.lastString, debugDrawLocation.x+5, 235);
			
			g.setColor(Color.red);
			for(Hitbox h : fighter.getState().getHitBoxes())
			{
				g.draw(h);
			}
			g.setColor(Color.darkGray);
			g.draw(fighter.zoneBox);
			g.setColor(Color.green);
			//g.draw(fighter.touchBox);
			g.setColor(Color.white);
		}
		
		if(comboVisible)
		{
			g.setColor(comboColour);
			g.drawString(getComboCounter() + " " + damageCounter, debugDrawLocation.x, debugDrawLocation.y + 100);
			g.setColor(Color.white);
		}
	}

	public boolean isComboVisible() {
		return comboVisible;
	}

	public void setComboVisible(boolean comboVisible) {
		this.comboVisible = comboVisible;
	}

	public int getDamageCounter() {
		return damageCounter;
	}

	public void setDamageCounter(int damageCounter) {
		this.damageCounter = damageCounter;
	}

	public Color getComboColour() {
		return comboColour;
	}

	public void setComboColour(Color comboColour) {
		this.comboColour = comboColour;
	}

	public int getComboTimer() {
		return comboTimer;
	}

	public void setComboTimer(int comboTimer) {
		this.comboTimer = comboTimer;
	}

	public void setKeys(int up, int down, int left, int right, int A, int B, int C, int D)
	{
		inputHandler.setKeys(up, down, left, right, A, B, C, D);
	}
	
	public void turnAround()
	{
		fighter.turnAround();
		inputHandler.turnAround();
	}

	public int getComboCounter() {
		return comboCounter;
	}

	public void setComboCounter(int comboCounter) {
		this.comboCounter = comboCounter;
	}
}
