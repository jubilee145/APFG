package status;

import org.newdawn.slick.geom.Vector2f;

import svb.Player;
import svb.State;
import entities.Fighter;
import entities.Hitbox;

public class TestGrabbed implements StatusPacket {

	public TestGrabbed(){}
	
	private Fighter target;
	private Fighter parent;
	
	@Override
	public void update() {
		target.inputString = "";
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
			if(parent.getState().getName().contains("Grabbing"))
			{
				/*Vector2f applyVel = new Vector2f();
				applyVel.x = parent.state.hitBoxes.get(parent.state.hitBoxes.size()-1).currentFrame.impulseH;
				applyVel.y = parent.state.hitBoxes.get(parent.state.hitBoxes.size()-1).currentFrame.impulseV;
				target.velocity = applyVel;
				target.hitStun = 0;*/
				parent.immovable = false;
				target.immovable = false;
				return true;
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
