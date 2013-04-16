package svb;

import java.util.List;

import status.Damage;
import status.Impulse;
import status.SetState;
import status.StatusPacket;
import status.TestGrabbed;
import entities.Actor;

/**
 * Gets input from the movelist file, and figures out what status effects need to go where.
 * A more complete explanation of what the status effects do will be in their own comment section.
 * @author Jubilee
 *
 */
public class StatusPacketFactory {

	public StatusPacketFactory(){}
	
	public void BuildPacket(String packetData, List<StatusPacket> packetList, Actor actor)
	{
		String[] subString = packetData.split(",");
		if(subString[0].contentEquals("GRAB"))
		{
			TestGrabbed testGrabbed = new TestGrabbed(subString[1]);
			testGrabbed.setParent(actor);
			packetList.add(testGrabbed);
			return;
		} else if(subString[0].contentEquals("SETSTATE"))
		{
			SetState setState = new SetState(subString[1]);
			packetList.add(setState);
			return;
		} else if(subString[0].contentEquals("IMPULSE"))
		{
			Impulse impulse = new Impulse(Integer.parseInt(subString[1]), Integer.parseInt(subString[2]));
			impulse.setParent(actor);
			packetList.add(impulse);
			return;
		} else if(subString[0].contentEquals("DAMAGE"))
		{
			Damage damage = new Damage(Integer.parseInt(subString[1]));
			packetList.add(damage);
			return;
		} else if(subString[0].contentEquals("HURT"))
		{
			Damage damage = new Damage(Integer.parseInt(subString[1]));
			packetList.add(damage);
			return;
		}
		
	}
	
	public StatusPacket BuildPacket(String packetData, Actor actor)
	{
		return null;
	}
	
	public StatusPacket BuildPacket(String packetData, State state)
	{
		return null;
	}
	
}
