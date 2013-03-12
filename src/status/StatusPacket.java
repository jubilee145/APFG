package status;

import svb.Player;
import svb.State;
import entities.Fighter;
import entities.Hitbox;

public interface StatusPacket {

	public void update();
	public boolean die();
	
	public void giveObject(Fighter f);
	public void giveObject(Hitbox h);
	public void giveObject(State s);
	public void giveObject(Player p);
}
