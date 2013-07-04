package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Manager;
import svb.Player;
import entities.Actor;
import entities.Hitbox;
import entities.State;

/**
 * Multiplies movement by numbers handed to the constructor. A drag of 1,1 will cause no change,
 * while a drag of 0,0 will halt movement completely. Negative drag is bad, and will cause insanity.
 * Drag > 1 will cause runaway acceleration, use with caution.
 * Use for skids, to control temporary friction effects, etc.
 * Does not remove itself.
 * @author Jubilee
 *
 */
public class Drag implements StatusPacket{

	private Actor target;
	private Actor parent;
	private Vector2f applyDrag;
	
	public Drag(float impX, float impY)
	{
		applyDrag = new Vector2f(impX * Manager.WORLD.conversionConstant, impY * Manager.WORLD.conversionConstant);
	}
	
	@Override
	public void update() {
		Vector2f tempVector = applyDrag.copy();
		
		
		target.velocity.x *= tempVector.x;
		target.velocity.y *= tempVector.y;
	}

	@Override
	public boolean die() {
		// Does not self-destruct.
		return false;
	}
	
	public void setParent(Actor a)
	{
		parent = a;
	}

	@Override
	public void giveObject(Actor a) {
		// TODO Auto-generated method stub
		target = a;
	}

	@Override
	public void giveObject(Hitbox h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void giveObject(State s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void giveObject(Player p) {
		// TODO Auto-generated method stub
		
	}

}
