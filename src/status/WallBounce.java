package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Player;
import entities.Actor;
import entities.Hitbox;
import entities.State;

/**
 * Changes the 'wallBouncing' variable to true. That is all. :P For information on what that
 * actually does, look at Fighter -> offCamera().
 * Essentially tells the fighter that it should bounce of walls instead of stopping on them. 
 * @author Jubilee
 *
 */

public class WallBounce implements StatusPacket{
	
	private Actor target;

	@Override
	public void update() {
		target.wallBouncing = true;
	}

	@Override
	public boolean die() {
		// Only occurs once
		return true;
	}
	
	public void setParent(Actor a){}

	@Override
	public void giveObject(Actor a) {
		target = a;
	}

	@Override
	public void giveObject(Hitbox h){}

	@Override
	public void giveObject(State s) {}

	@Override
	public void giveObject(Player p) {}
}
