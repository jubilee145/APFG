package svb;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import entities.Actor;
import entities.Entity;
import entities.Fighter;


public class Camera {

	public Rectangle screen;
	public Rectangle bounds;
	public Vector2f location;
	public Vector2f screenLocation;
	public Vector2f speed;
	
	public Actor target;
	public boolean crop = false;

	private GameContainer container;
	
	public int tempInt = 0;

	public Camera(GameContainer container) {
		this.container = container;
		
		location = new Vector2f();
		screenLocation = new Vector2f(50,125);
		screen = new Rectangle(0, 0, container.getWidth(), container.getHeight());
	}

	public void update(int delta)
	{
		if(target!=null)
		{
			location.x = target.location.x + target.zoneBox.getWidth()/2 - screen.getWidth()/2;
			location.y = target.location.y + target.zoneBox.getHeight()/2 - screen.getHeight()/2;
		}
		screen.setLocation(screenLocation);
	}
	
	public void render(Graphics g)
			throws SlickException {
		
		Vector2f offset = new Vector2f();
		
		for (Fighter f : Manager.fighters) {
			
			offset.x = 0;
			offset.y = 0;

			f.zoneBox.setLocation(f.location.add(location.negate().add(screenLocation)));

			if (screen.intersects(f.zoneBox)) {
				f.render(container, g);
			}
			f.zoneBox.setLocation(f.location.add(location).add(screenLocation.negate()));
			
			if(Manager.debug)
			{
				g.draw(screen);
			}
		}
		for (Player p : Manager.players) 
		{
			p.render(container, g);
		}
		
	}
}
