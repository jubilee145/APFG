package svb;

import status.Drag;
import status.Impulse;
import status.Move;
import entities.Actor;
import entities.Fighter;

/**
 * Determines both the conditions required to enter a state, and the actions that will occur in that state.
 * For instance, a condition of I="6,A" will return true if the players input equals "6,A".
 * The action M="50,0" will add a MOVE statusPacket to the state the fighter is in, telling it to move
 * forwards at a speed of fifty.
 * TODO: An explanation of all of the condition and action flags. :/
 * @author Jubilee
 *
 */

public class EventHandler {

	public EventHandler(){}
	
	public static boolean check(Actor actor, String condition)
	{
		String subString = null;
		int index;
		char flag;
		
		while(condition != "DONE")
		{
			flag = condition.charAt(0);
			index = condition.indexOf('"', condition.indexOf('"')+1);
			subString = condition.substring(condition.indexOf('"')+1, index);

			if(flag == 'I')
			{
				if(!actor.getInputString().contains(subString))
				{	
					return false;
				}
			}
			else if(flag =='H')
			{
				String b = actor.heldButton;
				String d = actor.heldDirection;
				
				if(!(d.contains(subString)||b.contains(subString)))
				{
					return false;
				}
			}
			else if(flag =='F')
			{
				if(actor.animation.getFrame() <= Integer.parseInt(subString))
				{
					return false;
				}
			}
			else if(flag =='G')
			{
				boolean shouldTouchGround;
				if(subString.contentEquals("TRUE"))
					shouldTouchGround = true;
				else
					shouldTouchGround = false;

				if(!actor.isTouchingGround == shouldTouchGround)
				{
					return false;
				}
			}
			else if(flag =='C')
			{
				if(!actor.hitConfirmed)
				{
					return false;
				}
			}
			
			condition = condition.substring(index + 1);
			condition = condition.trim();
			
			if(condition.length() == 0)
				condition = "DONE";
		}

		
		return true;
	}
	
	public static void doAction(Actor actor, String action)
	{
		String subString = null;
		int index;
		char flag;
		int facingAdjustment = 1;
		if(actor.isFacingLeft)
			facingAdjustment = -1;
		
		while(action != "DONE")
		{
			flag = action.charAt(0);
			index = action.indexOf('"', action.indexOf('"')+1);
			subString = action.substring(action.indexOf('"')+1, index);
			if(flag == 'M')
			{
				String[] splitString = subString.split(",");
				float impX = Integer.parseInt(splitString[0]);
				float impY = Integer.parseInt(splitString[1]);
				
				Move m = new Move(impX,impY);
				m.setParent(actor);
				m.giveObject(actor);
				
				actor.getState().status.add(m);
			}
			else if(flag == 'D')
			{
				String[] splitString = subString.split(",");
				float impX = Integer.parseInt(splitString[0]);
				float impY = Integer.parseInt(splitString[1]);
				
				Drag d = new Drag(impX,impY);
				d.setParent(actor);
				d.giveObject(actor);
				
				actor.getState().status.add(d);
			}
			else if(flag == 'I')
			{
				String[] splitString = subString.split(",");
				float impX = Integer.parseInt(splitString[0]);
				float impY = Integer.parseInt(splitString[1]);
				
				Impulse i = new Impulse(impX,impY);
				i.setParent(actor);
				i.giveObject(actor);
				
				actor.status.add(i);
				
			}
			
			action = action.substring(index + 1);
			action.trim();
			
			if(action.length() == 0)
				action = "DONE";
		}
	}

}
