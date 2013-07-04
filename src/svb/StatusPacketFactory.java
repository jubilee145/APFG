package svb;

import java.util.List;

import org.json.simple.JSONObject;

import status.Damage;
import status.Impulse;
import status.SetState;
import status.StatusPacket;
import status.TestGrabbed;
import status.WallBounce;
import entities.Actor;
import entities.State;

/**
 * Gets input from the movelist file, and figures out what status effects need to go where.
 * A more complete explanation of what the status effects do will be in their own comment section.
 * @author Jubilee
 *
 */
public class StatusPacketFactory {

	public StatusPacketFactory(){}
	
	public void BuildPacket(JSONObject packetData, List<StatusPacket> packetList, Actor actor)
	{
		String type = packetData.get("type").toString();
		String params = packetData.get("parameters").toString();
		if(type.contentEquals("GRAB"))
		{
			TestGrabbed testGrabbed = new TestGrabbed(params);
			testGrabbed.setParent(actor);
			packetList.add(testGrabbed);
			return;
		} else if(type.contentEquals("SETSTATE"))
		{
			SetState setState = new SetState(params);
			packetList.add(setState);
			return;
		} else if(type.contentEquals("IMPULSE"))
		{
			String[] splitParams = params.split(",");
			Impulse impulse = new Impulse(Integer.parseInt(splitParams[0]), Integer.parseInt(splitParams[1]));
			impulse.setParent(actor);
			packetList.add(impulse);
			return;
		} else if(type.contentEquals("DAMAGE"))
		{
			Damage damage = new Damage(Integer.parseInt(params));
			packetList.add(damage);
		}
		else if(type.contentEquals("WALLBOUNCE"))
		{
			WallBounce wallBounce = new WallBounce();
			packetList.add(wallBounce);
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
