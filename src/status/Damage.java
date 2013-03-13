package status;

import svb.Player;
import svb.State;
import entities.Fighter;
import entities.Hitbox;

/**
 * Just what it says on the tin. :) Causes damage once, then self-destructs.
 * @author Jubilee
 *
 */
public class Damage implements StatusPacket{

	private Fighter target;
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
