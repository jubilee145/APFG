package status;

import svb.Player;
import entities.Actor;
import entities.Fighter;
import entities.Hitbox;
import entities.State;

public interface StatusPacket {

	/**
	 * A StatusPacket is a class that can be injected into a fighter, hitbox or state, 
	 * and will execute code whenever its target updates. For example, the MOVE statusPacket
	 * is permanently attached to any 'walking' state, and causes the fighter to move forward
	 * or backward. The DAMAGE statusPacket subtracts health from its target, but then removes itself
	 * immediately (the same packet doesn't damage you twice.)
	 */
	
	public void update();
	
	/**
	 * The condition(s) for this packets removal. If this returns TRUE, the packet will be removed
	 * from its target.
	 */
	public boolean die();
	
	/**
	 * Ugh. This is ugly. Some StatusPackets need to be given all of these things, some of them
	 * only need a few. Targets for damage and such are assigned by the giveObject(Fighter) method,
	 * but it doesn't need a hitbox, state or player. Things like that.
	 */
	public void giveObject(Actor a);
	public void giveObject(Hitbox h);
	public void giveObject(State s);
	public void giveObject(Player p);
}
