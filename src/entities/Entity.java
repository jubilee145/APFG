package entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import svb.Manager;


public class Entity extends Rectangle {

	public Vector2f location;
	
	public Entity()
	{
		super(4,4,4,4);
		init();
	}
	
	private void init()
	{
		location = new Vector2f();
	}
	public void update(GameContainer container, int delta) throws SlickException
	{

	}
	
	public void render(GameContainer container, Graphics g) throws SlickException
	{

	}

}
