package 
entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import status.StatusPacket;
import svb.Manager;

/**
 * Hitboxes are added to a State instance when the state needs to do something to the other
 * player. The lists of statusPackets are applied when the hitbox touches the other player.
 * The hitbox can also apply status effects to the 'attacking' player or state. Things like
 * lifedrain, teleportation or stuff like that.
 * Most commonly they have a Damage statusPacket in them and are used for attacking.
 * @author Jubilee
 *
 */

@SuppressWarnings("serial")
public class Hitbox extends Rectangle {

	/**
	 * The dimensions and relative location of the current frame.
	 * 'active' determines whether the hitbox will do anything in
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
	 * and is polled to create the correct hitbox at the correct time.
	 */
	public Frame[] frames;
	
	/**
	 * The current, 'active' frame.
	 */
	public Frame currentFrame;
	
	
	/**
	 * Hit data. Determines where the hitbox will be blockable.
	 * E.g. if a hitbox that hits high connects with a target that
	 * is blocking high, the hitbox will be blocked. 'Confirmed' is
	 * set to true when the hitbox successfully connects with a
	 * target (i.e. is not blocked/whiffed).
	 */
	public class Hit {
		public boolean unblockable = false;
		public boolean high = false;
		public boolean mid = false;
		public boolean low = false;
		public boolean confirmed = false;
	}
	
	/**
	 * Active instance of the hit class.
	 */
	public Hit hit;
	
	/**
	 * lists of status effects to apply to the target, the parent, and (possibly)
	 * itself. 
	 * TODO The self and removeStatus lists may be deprecated. We will know when the
	 * character creator integration is complete.
	 */
	public class StatusList {
		public List<StatusPacket> self;
		public List<StatusPacket> applyTarget;
		public List<StatusPacket> applyParent;
		private List<StatusPacket> removeStatus;
	}
	
	/**
	 * Active instance of the status lists.
	 */
	public StatusList status;
	
	/**
	 * Set to true when the hitbox connects, whether it is blocked
	 * or not. Reset to false at the beginning of a state. Prevents
	 * hitboxes from connecting multiple times.
	 */
	public boolean spent = false;
	
	/**
	 * The parent of this hitbox.
	 */
	public Actor parent;
	
	public Hitbox(int numFrames, Actor parent) {
		super(0,0,0,0);
		this.parent = parent;
		hit = new Hit();
		frames = new Frame[numFrames];
		status = new StatusList();
		status.self = new ArrayList<StatusPacket>();
		status.removeStatus = new ArrayList<StatusPacket>();
		status.applyParent = new ArrayList<StatusPacket>();
		status.applyTarget = new ArrayList<StatusPacket>();
		
		for(int i = 0; i< numFrames;i++)
		{
			Frame f = new Frame();
			frames[i] = f;
		}
	}
	
	public void addNode(int frame, Vector2f location, Vector2f size)
	{
		addNode(frame, location, size, true);
	}
	
	public void addNode(int frame, Vector2f location, Vector2f size, boolean active)
	{
		frames[frame].x = (int) location.x;
		frames[frame].y = (int) location.y;
		frames[frame].width = (int) size.x;
		frames[frame].height = (int) size.y;
		frames[frame].active = active;
		
		//Find last active node, if it exists
		int lastActiveFrame = -1; 
		for (int i = frame - 1; i >= 0; i--)
		{
			if (frames[i].height != -1)
			{
				lastActiveFrame = i;
				break;
			}
		}
		
		//If not the first active node, "tween" between last active node		
		if(lastActiveFrame != -1)
		{
			Frame tweenMultiplier = new Frame();
			int divisor = frame - lastActiveFrame;
			tweenMultiplier.height = (frames[frame].height - frames[lastActiveFrame].height)/divisor;
			tweenMultiplier.width = (frames[frame].width - frames[lastActiveFrame].width)/divisor;
			tweenMultiplier.x = (frames[frame].x - frames[lastActiveFrame].x)/divisor;
			tweenMultiplier.y = (frames[frame].y - frames[lastActiveFrame].y)/divisor;
			tweenMultiplier.active = frames[lastActiveFrame].active;
			
			int count = 1;
			for(int i = lastActiveFrame + 1; i<frame;i++)
			{
				frames[i].height = frames[lastActiveFrame].height + (tweenMultiplier.height * count);
				frames[i].width = frames[lastActiveFrame].width + (tweenMultiplier.width * count);
				frames[i].x = frames[lastActiveFrame].x + (tweenMultiplier.x * count);
				frames[i].y = frames[lastActiveFrame].y + (tweenMultiplier.y * count);
				frames[i].active = tweenMultiplier.active;
				count ++;
			}
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
			this.setX(parent.location.x + parent.zoneBox.getWidth()/2  + (parent.zoneBox.getWidth() - currentFrame.x - currentFrame.width));
		}
		else
		{
			this.setX(parent.location.x - parent.zoneBox.getWidth()/2 + currentFrame.x);
		}
		
		statusUpdate();
	}
	
	private void statusUpdate()
	{
		for (StatusPacket s : status.self)
		{
			s.update();
			if(s.die())
				status.removeStatus.add(s);
		}
		
		for (StatusPacket s : status.removeStatus)
		{
			status.self.remove(s);
		}
		status.removeStatus.clear();
	}
	
	/**
	 * Apply the hitboxes status effects to the target and parent.
	 * 
	 * TODO: This will currently ignore blocking. Put target status effects in
	 * Fighter.hit() instead, once blocking mechanics are sorted out.
	 * 
	 * @param actor
	 */
	public void hit(Actor actor)
	{
		for(StatusPacket s: status.applyParent)
		{
			s.giveObject(parent);
			parent.applyStatus(s);
		}
		hit.confirmed = true;
		spent = true;
	}
	
	
}
