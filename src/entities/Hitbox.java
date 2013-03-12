package 
entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import status.StatusPacket;
import svb.Manager;

public class Hitbox extends Rectangle {

	public class Frame {
		public int x;
		public int y;
		public int width;
		public int height;
		public boolean active;
	}
	
	public class Hit {
		public boolean unblockable = false;
		public boolean high = false;
		public boolean mid = false;
		public boolean low = false;
		public boolean confirmed = false;
	}
	
	public class StatusList {
		public List<StatusPacket> self;
		public List<StatusPacket> applyTarget;
		public List<StatusPacket> applyParent;
		private List<StatusPacket> removeStatus;
	}
	
	public boolean spent = false;
	public Frame[] frames;
	public Frame currentFrame;
	public Hit hit;
	public StatusList status;
	public int directionMultiplier;
	public Fighter parent;
	
	public Hitbox(int numFrames, Fighter parent) {
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
			f.active = false;
			f.height = -1;
			f.width = 0;
			f.x = 0;
			f.y = 0;
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
	
	public void update(Fighter fighter)
	{
		int frame = fighter.animation.getFrame();
		currentFrame = frames[frame];
		
		this.setHeight(currentFrame.height);
		this.setY(currentFrame.y + fighter.location.y);
		this.setWidth(currentFrame.width);
		
		if(fighter.isFacingLeft)
		{
			directionMultiplier = -1;
			this.setX(-currentFrame.x -currentFrame.width + fighter.location.x + fighter.zoneBox.getWidth());
		}
		else
		{
			directionMultiplier = 1;
			this.setX(currentFrame.x + fighter.location.x);
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
	
	public void hit(Fighter fighter)
	{
		for(StatusPacket s: status.applyTarget)
		{
			s.giveObject(fighter);
			fighter.applyStatus(s);
		}
		for(StatusPacket s: status.applyParent)
		{
			s.giveObject(parent);
			parent.applyStatus(s);
		}
		hit.confirmed = true;
		spent = true;
	}
	
	
}
