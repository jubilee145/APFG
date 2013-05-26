package svb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import status.TestGrab;
import status.TestGrabbed;
import status.TestStatus;

import entities.Fighter;
import entities.Hitbox;
import entities.State;
import entities.StateFrame;
import entities.Touchbox;

/**
 * The moveFactory reads a characters movelist file, and uses it
 * to build up a network of connected States. 
 * @author Jubilee
 *
 */

public class StateFactory {

	Player player;
	StatusPacketFactory packetFactory = new StatusPacketFactory();
	List<State> stateList;
	List<List<JSONObject>> cancelList;
	List<String> transitionList;
	
	public StateFactory() throws SlickException
	{

	}
	
	public void populateJsonMoveList(File characterFile, Fighter fighter)
	{
		JSONParser jParser = new JSONParser();
		stateList = new ArrayList<State>();
		cancelList = new ArrayList<List<JSONObject>>();
		transitionList  = new ArrayList<String>();
		
		try {
			File movesFile = new File(characterFile + "/moves");
			File[] fileNames = movesFile.listFiles();
			//Arrays.sort(fileNames);
			
			for(int i = 0; i < fileNames.length; i++)
			{
				// new State();
				JSONObject jsonObject = new JSONObject();
				jsonObject = (JSONObject) jParser.parse(new FileReader(fileNames[i]));
				
				State s = buildState(jsonObject, characterFile, fighter);
				stateList.add(s);
				if (s.getName().contentEquals("Idle"))
					fighter.setState(s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		int currentState = 0;
		int currentCancel;
		for (State s : stateList)
		{
			currentCancel = 0;
			for (State s2 : stateList)
			{
				if(transitionList.get(currentState).contentEquals(s2.getName()))
				{
					s.setTransition(s2);
				}

				//Setup cancels list.
				for(int i = 0; i < cancelList.get(currentState).size(); i++)
				{
					JSONObject jsonCancel = (JSONObject) cancelList.get(currentState).get(i);
					JSONArray conditions = (JSONArray) jsonCancel.get("conditions");
					
					/**If cancel name = the name of the state being checked,
					 * add both the cancel and the state to the relevant lists
					 * in the currentState.
					 */
					if(s2.getName().contentEquals(jsonCancel.get("childState").toString()))
					{
						s.getCancels().add(s2);
						s.getConditions().add(new ArrayList<JSONObject>());
						for(Object j : conditions)
						{
							s.getConditions().get(currentCancel).add((JSONObject) j);
						}
						currentCancel++;
					}
				}
			
			}
			currentState++;
		}
		
		System.out.println("Move list populated from file: " + characterFile);
	}

	/**
	 * Extract data from jObject and write it into state, value by value.
	 * 
	 * @param state
	 * The state being written to.
	 * 
	 * @param jObject
	 * The JSONObject that contains the state data.
	 */
	private State buildState(JSONObject jsonObject, File characterFile, Fighter fighter)
	{

		String name = (String) jsonObject.get("name");
		String transition = (String)jsonObject.get("transition");
		
		boolean allowGravity = (Boolean) jsonObject.get("allowGravity");
		boolean canTurn = (Boolean) jsonObject.get("canTurn");
		boolean preserveInput = (Boolean) jsonObject.get("preserveInput");
		boolean openState = (Boolean) jsonObject.get("openState");
		boolean looping = (Boolean) jsonObject.get("looping");
		
		JSONArray frames = (JSONArray) jsonObject.get("frames");
		JSONArray hitboxes = (JSONArray) jsonObject.get("hitboxes");
		JSONArray touchboxes = (JSONArray) jsonObject.get("touchboxes");
		JSONArray cancels = (JSONArray) jsonObject.get("cancels");
		JSONArray actions = (JSONArray) jsonObject.get("actions");
		List<JSONObject> stateCancelList = new ArrayList<JSONObject>();
		List<JSONObject> stateActionList = new ArrayList<JSONObject>();
		
		State state = new State(frames.size(), hitboxes.size(), touchboxes.size());
		
		state.setName(name);
		state.setAllowGravity(allowGravity);
		state.setCanTurn(canTurn);
		state.setPreserveInput(preserveInput);
		state.setLooping(looping);
		
		state.setFrames(new StateFrame[frames.size()]);
		
		StateFrame[] stateFrames = state.getFrames();
		
		transitionList.add(transition);
		
		for(int i = 0; i < frames.size(); i++)
		{
			stateFrames[i] = buildFrame((JSONObject) frames.get(i), characterFile);
		}
		
		/**Add cancels to the list so that they can be connected together
		 * when all the states are done.
		 */
		for(int i = 0; i < cancels.size(); i++)
		{
			stateCancelList.add((JSONObject) cancels.get(i));
		}
		cancelList.add(stateCancelList);
		
		/**
		 * Add actions to state.
		 */
		for(int i = 0; i < actions.size(); i++)
		{
			state.getActions().add((JSONObject) actions.get(i));
		}
		
		//TODO put hitbox population in its own function.
		for(int i2 = 0; i2 < hitboxes.size(); i2++)
		{
			state.getHitBoxes()[i2] = new Hitbox(frames.size(), fighter);
			Hitbox stateHitbox = state.getHitBoxes()[i2];

			JSONObject jsonHitbox = (JSONObject) hitboxes.get(i2);
			JSONObject jsonHitboxFrame;
			
			stateHitbox.hit.high = (Boolean) jsonHitbox.get("hitHigh");
			stateHitbox.hit.mid = (Boolean) jsonHitbox.get("hitMedium");
			stateHitbox.hit.low = (Boolean) jsonHitbox.get("hitLow");
			
			//TODO Target and parent actions.
			for(int i = 0; i < frames.size(); i++)
			{
				jsonHitboxFrame = (JSONObject)((JSONArray) jsonHitbox.get("boxFrames")).get(i);
				Long width = (Long) jsonHitboxFrame.get("width");
				Long height = (Long) jsonHitboxFrame.get("height");
				Long x = (Long) jsonHitboxFrame.get("x");
				Long y = (Long) jsonHitboxFrame.get("y");
				
				if(width <= 0)
				{
					stateHitbox.frames[i].active = false;
				}
				else
				{
					stateHitbox.frames[i].active = true;
					stateHitbox.frames[i].width = width.intValue();
					stateHitbox.frames[i].height = height.intValue();
					stateHitbox.frames[i].x = x.intValue();
					stateHitbox.frames[i].y = y.intValue();
				}
				
			}
		}

		//TODO put touchbox population in its own function.
		for(int i2 = 0; i2 < touchboxes.size(); i2++)
		{
			state.getTouchBoxes()[i2] = new Touchbox(frames.size(), fighter);
			Touchbox stateTouchbox = state.getTouchBoxes()[i2];

			JSONObject jsonTouchbox = (JSONObject) touchboxes.get(i2);
			JSONObject jsonTouchboxFrame;
			
			for(int i = 0; i < frames.size(); i++)
			{
				jsonTouchboxFrame = (JSONObject)((JSONArray) jsonTouchbox.get("boxFrames")).get(i);
				Long width = (Long) jsonTouchboxFrame.get("width");
				Long height = (Long) jsonTouchboxFrame.get("height");
				Long x = (Long) jsonTouchboxFrame.get("x");
				Long y = (Long) jsonTouchboxFrame.get("y");
				
				if(width <= 0)
				{
					stateTouchbox.frames[i].active = false;
				}
				else
				{
					stateTouchbox.frames[i].active = true;
					stateTouchbox.frames[i].width = width.intValue();
					stateTouchbox.frames[i].height = height.intValue();
					stateTouchbox.frames[i].x = x.intValue();
					stateTouchbox.frames[i].y = y.intValue();
				}
			}
		}
		
		
		return state;
	}
	
	private StateFrame buildFrame(JSONObject jsonFrame, File characterFile)
	{
		StateFrame frame = new StateFrame();
		
		String url = (String) jsonFrame.get("url");
		url = characterFile + "/" + url;
		
		Long offsetX = (Long) jsonFrame.get("offsetX");
		Long offsetY = (Long) jsonFrame.get("offsetY");
		
		boolean guardHigh = (Boolean) jsonFrame.get("guardHigh");
		boolean guardMedium = (Boolean) jsonFrame.get("guardMedium");
		boolean guardLow = (Boolean) jsonFrame.get("guardLow");
		
		boolean whiffHigh = (Boolean) jsonFrame.get("whiffHigh");
		boolean whiffMedium = (Boolean) jsonFrame.get("whiffMedium");
		boolean whiffLow = (Boolean) jsonFrame.get("whiffLow");
		
		if(Manager.preLoadTextures)
		{
			//Load image
			try {
				frame.setImage(new Image(url));
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
		else
		{
			frame.setUrl(url);
		}
		
		frame.setOffsetX(offsetX.intValue());
		frame.setOffsetY(offsetY.intValue());
		
		frame.getGuard().high = guardHigh;
		frame.getGuard().mid = guardMedium;
		frame.getGuard().low = guardLow;
		
		frame.getWhiff().high = whiffHigh;
		frame.getWhiff().mid = whiffMedium;
		frame.getWhiff().low = whiffLow;
		
		return frame;
	}
	
}
