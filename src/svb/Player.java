package svb;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import entities.Fighter;
import entities.Hitbox;

public class Player {

	public Fighter fighter;
	public InputHandler inputHandler;
	
	private int time;
	
	//Where to display the debug information on the screen.
	private Vector2f debugDrawLocation;
	
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
		time += delta;
		
		inputHandler.update(container);
		
		fighter.inputBuffer = inputHandler.checkDirections() + inputHandler.checkButtons();
		fighter.heldDirection = inputHandler.heldDirection;
		fighter.heldButton = inputHandler.heldButton;
		
		
		if(inputHandler.resetClock)
			time = 0;
		
		if(!fighter.inputBuffer.contentEquals(""))
		{
			if(fighter.getInputString().contains("5,"))
				fighter.setInputString("");
			fighter.setInputString(fighter.getInputString() + fighter.inputBuffer);
		}
		fighter.inputBuffer = "";

		if(time >= 250)
		{
			if(fighter.getInputString() != "5,")
				fighter.lastString = fighter.getInputString();
			fighter.setInputString("5,");
			time = 0;
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
			g.drawString("Time: " + time, debugDrawLocation.x+5, 95);
			g.drawString("State: " + fighter.getState().getName(), debugDrawLocation.x+5, 115);
			g.drawString("Frame: " + fighter.animation.getFrame(), debugDrawLocation.x+5, 135);
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
			g.draw(fighter.touchBox);
			g.setColor(Color.white);
		}
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
}
