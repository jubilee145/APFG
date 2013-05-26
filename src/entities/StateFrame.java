package entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

/**
 * Contains guard & image data for an individual frame of a state.
 * @author Bubbles
 *
 */
public class StateFrame {
	
	/**
	 * The image to be displayed during this frame.
	 * ONLY USED when preLoadTextures is set to true.
	 */
	private Image image;
	
	/**
	 * The url of the image to be displayed during this frame.
	 * NOT USED when preLoadTextures is set to true.
	 */
	private String url;
	
	/**
	 * The display offsets of the frames image. Image will be drawn at actorCoordinate + offSet;
	 */
	private int offsetX;
	private int offsetY;
	
	/**
	 * All the block types. If HIGH is true, then any high attacks can be blocked in this state, 
	 * etc. Guard will cause the hitbox to activate, but will nullify most harmful effects.
	 * ('destroying' the hitbox in the process) Whiff will cause the hitbox to ignore the collision.
	 */
	private Guard guard;
	private Whiff whiff;
	
	public class Guard
	{
		public boolean high = false;
		public boolean mid = false;
		public boolean low = false;
	}
	public class Whiff
	{
		public boolean high = false;
		public boolean mid = false;
		public boolean low = false;
	}
	
	public StateFrame()
	{
		guard = new Guard();
		whiff = new Whiff();
	}
	
	public Guard getGuard() {
		return guard;
	}

	public void setGuard(Guard guard) {
		this.guard = guard;
	}

	public Whiff getWhiff() {
		return whiff;
	}

	public void setWhiff(Whiff whiff) {
		this.whiff = whiff;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
