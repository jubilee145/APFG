package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Player;
import entities.Actor;
import entities.Hitbox;
import entities.State;

public class TestGrabbed implements StatusPacket {

	String grabState;
	private Actor target;
	private Actor parent;
	
	
	public TestGrabbed(String grabState)
	{
		this.grabState = grabState;	
	}

	@Override
	public void update() {
		target.setInputString("");
		float setX = parent.location.getX();
		float setY = parent.location.getY();
		target.location = new Vector2f(setX, setY);
		parent.immovable = true;
		target.immovable = true;
	}

	@Override
	public boolean die() {
		//Trigger when the last hitbox in the state hits the parent
		if(parent.getState().getHitBoxes()[parent.getState().getHitBoxes().length-1].spent)
		{
			if(parent.getState().getName().contentEquals(grabState))
			{
				parent.immovable = false;
				target.immovable = false;
				return true;
			}
		}
		return false;
	}

	public void setParent(Actor f)
	{
		parent = f;
	}
	
	public Actor getParent()
	{
		return parent;
	}
	
	@Override
	public void giveObject(Actor a) {
		target = a;
	}

	@Override
	public void giveObject(Hitbox h) {}

	@Override
	public void giveObject(State s) {}

	@Override
	public void giveObject(Player p) {}

}
