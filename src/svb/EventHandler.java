package svb;

import org.json.simple.JSONObject;

import status.Drag;
import status.Impulse;
import status.Move;
import entities.Actor;
import entities.Fighter;

/**
 * Determines both the conditions required to enter a state, and the actions that will occur in that state.
 * 
 * TODO: An explanation of all of the condition and action parameters. :/
 * @author Jubilee
 */

public class EventHandler {

	public EventHandler(){}
	
	public static boolean check(Actor actor, JSONObject condition)
	{

		String type = condition.get("type").toString();
		String parameters = condition.get("parameters").toString();
		boolean inverse = (Boolean) condition.get("inverse");
		
		boolean returnBool = true;
		if(inverse)
			returnBool = false;
		
		if(type.contentEquals("INPUT"))
		{
			if(!actor.getInputString().contains(parameters))
			{	
				return inverse;
			}
		}
		else if(type.contentEquals("HELD"))
		{
			String b = actor.heldButton;
			String d = actor.heldDirection;

			if(!(d.contains(parameters)||b.contains(parameters)))
			{
				return inverse;
			}
		}
		else if(type.contentEquals("FRAME"))
		{
			if(actor.getState().getCurrentFrame() <= Integer.parseInt(parameters))
			{
				return inverse;
			}
		}
		else if(type.contentEquals("TOUCHINGGROUND"))
		{
			boolean shouldTouchGround;
			if(parameters.contentEquals("TRUE"))
				shouldTouchGround = true;
			else
				shouldTouchGround = false;

			if(!actor.isTouchingGround == shouldTouchGround)
			{
				return false;
			}
		}
		else if(type.contentEquals("HITCONFIRM"))
		{
			if(!actor.hitConfirmed)
			{
				return inverse;
			}
		}
		
		return returnBool;
	}
	
	public static void doAction(Actor actor, JSONObject action)
	{

		String type = action.get("type").toString();
		String parameters = action.get("parameters").toString();

		if(type.contentEquals("MOVE"))
		{
			
			String[] splitString = parameters.split(",");
			float impX = Integer.parseInt(splitString[0]);
			float impY = Integer.parseInt(splitString[1]);
			
			Move m = new Move(impX,impY);
			m.setParent(actor);
			m.giveObject(actor);
			
			actor.getState().status.add(m);
		}
		else if(type.contentEquals("DRAG"))
		{
			String[] splitString = parameters.split(",");
			float impX = Integer.parseInt(splitString[0]);
			float impY = Integer.parseInt(splitString[1]);
			
			Drag d = new Drag(impX,impY);
			d.setParent(actor);
			d.giveObject(actor);
			
			actor.getState().status.add(d);
		}
		else if(type.contentEquals("IMPULSE"))
		{
			String[] splitString = parameters.split(",");
			float impX = Integer.parseInt(splitString[0]);
			float impY = Integer.parseInt(splitString[1]);
			
			Impulse i = new Impulse(impX,impY);
			i.setParent(actor);
			i.giveObject(actor);
			
			actor.status.add(i);
		}
		else if(type.contentEquals("SUBACTOR"))
		{
			actor.createSubActor(parameters);
		}
		else if(type.contentEquals("WALLBOUNCE"))
		{
			System.out.println("BOUNCE! :D");
			actor.wallBouncing = true;
		}

	}

}
