package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Manager;
import svb.Player;
import entities.Actor;
import entities.Hitbox;
import entities.State;

/**
 * Assigns a state from the targets list of open states.
 * @author Jubilee
 *
 */
public class SetState implements StatusPacket{

	private Actor target;
	private String stateName;
	
	public SetState(String stateName)
	{
		this.stateName = stateName;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		for(State s : target.openStates)
		{
			if(s.getName().contentEquals(stateName))
			{
				target.setState(s);
			}
		}
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
