package status;

import svb.Player;
import entities.Actor;
import entities.Fighter;
import entities.Hitbox;
import entities.State;

/**
 * Just what it says on the tin. :) Causes damage once, then removes itself.
 * @author Jubilee
 *
 */
public class Damage implements StatusPacket{

	private Actor target;
	private int damage;
	
	public Damage(int damage)
	{
		this.damage = damage;
	}
	
	@Override
	public void update() {
		target.setHealth(target.getHealth() - damage);
	}

	@Override
	public boolean die() {
		// Only implements once
		return true;
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
