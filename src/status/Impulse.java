package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Manager;
import svb.Player;
import svb.State;
import entities.Actor;
import entities.Hitbox;

/**
 * Applies a force on the target object, with direction modified by the parent object,
 * once, then removes itself.
 * @author Jubilee
 *
 */
public class Impulse implements StatusPacket{

	private Actor target;
	private Actor parent;
	private Vector2f applyImpulse;
	
	public Impulse(float impX, float impY)
	{
		applyImpulse = new Vector2f(impX * Manager.WORLD.conversionConstant, impY * Manager.WORLD.conversionConstant);
	}
	
	@Override
	public void update() {
		//Apply impulse to target. Flip horizontal impulse depending on
		//parent direction.
		Vector2f tempVector = applyImpulse.copy();
		if(parent.isFacingLeft)
			tempVector.x *= -1;
		
		target.velocity.x = tempVector.x;
		target.velocity.y = tempVector.y;
	}

	@Override
	public boolean die() {
		// Only implements once
		return true;
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
