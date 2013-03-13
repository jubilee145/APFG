package svb;

import status.Drag;
import status.Impulse;
import status.StandardMove;
import entities.Fighter;

/**
 * Determines both the conditions required to enter a state, and the actions that will occur in that state.
 * TODO: An explanation of all of the condition and action flags. :/
 * @author Jubilee
 *
 */

public class EventHandler {

	public EventHandler(){}
	
	public static boolean check(Fighter fighter, String condition)
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
				if(!fighter.getInputString().contains(subString))
				{	
					return false;
				}
			}
			else if(flag =='H')
			{
				String b = fighter.heldButton;
				String d = fighter.heldDirection;
				
				if(!(d.contains(subString)||b.contains(subString)))
				{
					return false;
				}
			}
			else if(flag =='F')
			{
				if(fighter.animation.getFrame() <= Integer.parseInt(subString))
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

				if(!fighter.isTouchingGround == shouldTouchGround)
				{
					return false;
				}
			}
			else if(flag =='S')
			{
				if(!fighter.hitConfirmed)
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
	
	public static void doAction(Fighter fighter, String action)
	{
		String subString = null;
		int index;
		char flag;
		int facingAdjustment = 1;
		if(fighter.isFacingLeft)
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
				
				StandardMove m = new StandardMove(impX,impY);
				m.setParent(fighter);
				m.giveObject(fighter);
				
				fighter.getState().status.add(m);
			}
			else if(flag == 'D')
			{
				String[] splitString = subString.split(",");
				float impX = Integer.parseInt(splitString[0]);
				float impY = Integer.parseInt(splitString[1]);
				
				Drag d = new Drag(impX,impY);
				d.setParent(fighter);
				d.giveObject(fighter);
				
				fighter.getState().status.add(d);
			}
			else if(flag == 'I')
			{
				String[] splitString = subString.split(",");
				float impX = Integer.parseInt(splitString[0]);
				float impY = Integer.parseInt(splitString[1]);
				
				Impulse i = new Impulse(impX,impY);
				i.setParent(fighter);
				i.giveObject(fighter);
				
				fighter.status.add(i);
				
			}
			
			action = action.substring(index + 1);
			action.trim();
			
			if(action.length() == 0)
				action = "DONE";
		}
	}

}
