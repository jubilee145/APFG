package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Manager;
import svb.Player;
import svb.State;
import entities.Fighter;
import entities.Hitbox;

/**
 * Moves the target at a constant direction and speed, modified by parents direction.
 * Used for basic movement, walking etc. Possibly also for stranger movement things, like wind
 * or something.
 * Overwrites whatever movement the target had previously. (Change this?)
 * Does not self-destruct.
 * NB: When using for basic movement, parent and target should be the same.
 * @author Jubilee
 *
 */
public class StandardMove implements StatusPacket{

	private Fighter target;
	private Fighter parent;
	private Vector2f applyMovement;
	
	public StandardMove(float impX, float impY)
	{
		applyMovement = new Vector2f(impX * Manager.WORLD.conversionConstant, impY * Manager.WORLD.conversionConstant);
	}
	
	@Override
	public void update() {
		Vector2f tempVector = applyMovement.copy();
		if(parent.isFacingLeft)
			tempVector.x *= -1;
		
		target.velocity.x = tempVector.x;
		target.velocity.y = tempVector.y;
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
