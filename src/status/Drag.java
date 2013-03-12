package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Manager;
import svb.Player;
import svb.State;
import entities.Fighter;
import entities.Hitbox;

/**
 * Multiplies movement by numbers handed to the constructor. A drag of 1,1 will cause no change,
 * while a drag of 0,0 will halt movement completely. Negative drag is bad, and will cause insanity.
 * Drag > 1 will cause runaway acceleration, use with caution.
 * Use for skids, to control temporary friction effects, etc.
 * Does not self-destruct.
 * @author Jubilee
 *
 */
public class Drag implements StatusPacket{

	private Fighter target;
	private Fighter parent;
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
	
	public void setParent(Fighter f)
	{
		parent = f;
	}

	@Override
	public void giveObject(Fighter f) {
		// TODO Auto-generated method stub
		target = f;
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
