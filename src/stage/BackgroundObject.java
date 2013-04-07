package stage;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

/**
 * Essentially an image/animation with parallax. No need for collisions
 * or any real logic. Maybe give them a means to follow paths or something
 * later on.
 * 
 * @author Jubilee
 */
public class BackgroundObject {

	public float distance;
	float parallax;
	public Vector2f location;
	public Vector2f baseLocation;
	public Color color;
	Image image;
	
	public BackgroundObject(Image image, float parallax, Vector2f location, float distance)
	{
		this.distance = distance;
		this.parallax = parallax;
		this.baseLocation = location;
		this.location = new Vector2f(0,0);
		this.image = image;
	}
	
	public void render(GameContainer container, Graphics g, float offsetX, float offsetY)
			throws SlickException {
		g.drawImage(image, location.x, location.y, color);
	}
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		render(container, g, 0,0);
	}
	
	public void setLocation(Vector2f location)
	{
		this.location = location.scale(parallax);
		this.location.add(baseLocation);
	}
}
