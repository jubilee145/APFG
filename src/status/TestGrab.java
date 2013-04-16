package status;

import svb.Player;
import svb.State;
import entities.Actor;
import entities.Fighter;
import entities.Hitbox;

public class TestGrab implements StatusPacket {

	public TestGrab(){}
	
	private TestGrabbed friend;
	private Actor parent;
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean die() {
		// TODO trigger at the end of grab
		return true;
	}

	public TestGrabbed getFriend() {
		return friend;
	}

	public void setFriend(TestGrabbed friend) {
		this.friend = friend;
	}

	public Actor getParent() {
		return parent;
	}

	@Override
	public void giveObject(Actor a) {
		parent = a;
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
