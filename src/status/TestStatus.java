package status;

import svb.Player;
import svb.State;
import entities.Fighter;
import entities.Hitbox;


public class TestStatus implements StatusPacket {

	private String testData = "EMPTY";
	
	public TestStatus(){}
	
	public TestStatus(String i)
	{
		testData = i;
	}
	
	@Override
	public void update() {
		System.out.println("STATUS UPDATE: " + testData);
	}

	@Override
	public boolean die() {
		// TestStatus only triggers once.
		return true;
	}

	@Override
	public void giveObject(Fighter f) {
		// TODO Auto-generated method stub
		
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
