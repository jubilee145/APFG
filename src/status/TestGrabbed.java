package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Player;
import svb.State;
import entities.Fighter;
import entities.Hitbox;

public class TestGrabbed implements StatusPacket {

	String grabState;
	private Fighter target;
	private Fighter parent;
	
	
	public TestGrabbed(String grabState)
	{
		this.grabState = grabState;	
	}

	@Override
	public void update() {
		target.setInputString("");
		float setX = parent.getState().hitBoxes.get(0).getX();
		float setY = parent.getState().hitBoxes.get(0).getY();
		target.location = new Vector2f(setX, setY);
		parent.immovable = true;
		target.immovable = true;
	}

	@Override
	public boolean die() {
		//Trigger when the last hitbox in the state hits the parent
		if(parent.getState().hitBoxes.get(parent.getState().hitBoxes.size()-1).spent)
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

	public void setParent(Fighter f)
	{
		parent = f;
	}
	
	public Fighter getParent()
	{
		return parent;
	}
	
	@Override
	public void giveObject(Fighter f) {
		target = f;
	}

	@Override
	public void giveObject(Hitbox h) {}

	@Override
	public void giveObject(State s) {}

	@Override
	public void giveObject(Player p) {}

}
