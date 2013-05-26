package entities;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import status.StatusPacket;
import entities.Hitbox.Frame;
import entities.Hitbox.Hit;
import entities.Hitbox.StatusList;


/**
 * Touchboxes represent the area of the player that is physically active, i.e.
 * that can be collided with. They are used to determine when a player should be
 * hit, when they are touching the ground and when they should be pushed away from 
 * another player.
 * @author Jubilee
 *
 */

@SuppressWarnings("serial")
public class Touchbox extends Rectangle {

	/**
	 * The dimensions and relative location of the current frame.
	 * 'active' determines whether the touchbox will do anything in
	 * the current frame. If not, it is ignored in collisions and 
	 * not displayed.
	 */
	public class Frame {
		public int x;
		public int y;
		public int width;
		public int height;
		public boolean active;
	}
	
	/**
	 * The array of frame data. This cycles at the same speed as the state animation
	 * and is polled to create the correct touchbox at the correct time.
	 */
	public Frame[] frames;
	
	/**
	 * The current, 'active' frame.
	 */
	public Frame currentFrame;
	
	/**
	 * The parent of this touchbox.
	 */
	public Actor parent;
	
	public Touchbox(int numFrames, Actor parent) {
		super(0,0,0,0);
		
		this.parent = parent;
		frames = new Frame[numFrames];
		for(int i = 0; i< numFrames;i++)
		{
			Frame f = new Frame();
			frames[i] = f;
		}
	}
	
	public void update()
	{
		int frame = parent.getState().getCurrentFrame();
		currentFrame = frames[frame];
		
		this.setHeight(currentFrame.height);
		this.setY(parent.location.y + currentFrame.y);
		this.setWidth(currentFrame.width);
		
		if(parent.isFacingLeft)
		{
			//offsetX + zoneWidth/2 - frames[currentFrame].getOffsetX() + (zoneWidth - image.getWidth())
			//this.setX(parent.location.x + parent.zoneBox.getWidth()/2 - currentFrame.x + currentFrame.width);
			this.setX(parent.location.x + parent.zoneBox.getWidth()/2  + (parent.zoneBox.getWidth() - currentFrame.x - currentFrame.width));
		}
		else
		{
			this.setX(parent.location.x - parent.zoneBox.getWidth()/2 + currentFrame.x);
		}
	}

	public Frame[] getFrames() {
		return frames;
	}

	public void setFrames(Frame[] frames) {
		this.frames = frames;
	}
}
