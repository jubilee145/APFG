package svb;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

/**
 * The moveFactory reads a characters movelist file, and uses it
 * to build up a network of connected States. 
 * @author Jubilee
 *
 */

public class MoveFactory {

	Player player = new Player();
	StatusPacketFactory packetFactory = new StatusPacketFactory();
	Fighter fighter;
	List<SpriteSheet> sheetList;
	
	public MoveFactory() throws SlickException
	{
		player = new Player(600,0);
		fighter = new Fighter(Manager.haroldSheet.get(0), new Vector2f(350,Manager.WORLD.groundLevel));
		
		fighter.touchBox = new Rectangle(0,0,150,260);
		fighter.touchBoxOffset = new Vector2f(75,96);
		fighter.zoneBox.setLocation(fighter.location);
		fighter.touchBox.setX(fighter.location.getX() + fighter.touchBoxOffset.x);
		
		fighter.health = fighter.maxHealth = 5000;
		player.fighter = fighter;
		sheetList = Manager.haroldSheet;
		populateMoveList("assets/moves/HaroldMovelist");
		Manager.player1 = player;
		Manager.players.add(player);
		Manager.fighters.add(fighter);

		Manager.cameras.get(0).target1 = fighter;
		
		player = new Player(0,0);
		player.setKeys(Input.KEY_NUMPAD8, Input.KEY_NUMPAD2, Input.KEY_NUMPAD4, Input.KEY_NUMPAD6, 
				Input.KEY_O, Input.KEY_P, Input.KEY_K, Input.KEY_L);
		fighter = new Fighter(Manager.haroldSheet.get(0), new Vector2f(125,Manager.WORLD.groundLevel));
		
		fighter.touchBox = new Rectangle(0,0,150,260);
		fighter.touchBoxOffset = new Vector2f(75,96);
		fighter.zoneBox.setLocation(fighter.location);
		fighter.touchBox.setX(fighter.location.getX() + fighter.touchBoxOffset.x);
		
		fighter.health = fighter.maxHealth = 5000;
		player.fighter = fighter;
		sheetList = Manager.haroldSheet;
		populateMoveList("assets/moves/HaroldMovelist");
		Manager.player2 = player;
		Manager.players.add(player);
		Manager.fighters.add(fighter);

		Manager.cameras.get(0).target2 = fighter;
	}

	private void populateMoveList(String movelist)
	{
		State state = new State();
		List<State> moveList;
		moveList = new ArrayList<State>();
		
		List<String> transitionStates;
		List<String> blockStates;
		
		transitionStates = new ArrayList<String>();
		blockStates = new ArrayList<String>();

		
		State idleState = null;
		
		try
		{
			//TODO Make movelists for seperate characters here.
			FileInputStream fstream = new FileInputStream(movelist);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			Animation anim = null;

			while ((strLine = br.readLine()) != null)   
			{
				//Ignore comment lines
				if(strLine.contains("//"))
				{
					continue;
				}
				
				int index = strLine.indexOf('"');
				if(strLine.contains("[MOVE]"))
				{
					state = new State();
					anim = null;
					Animation tempAnim = new Animation();
					state.setAnimation(tempAnim);
					tempAnim.setLooping(false);
					tempAnim.setAutoUpdate(false);
					state.setAnimation(tempAnim);
					
					moveList.add(state);
					continue;
				}

				if(strLine.contains("name="))
				{
					state.setName(strLine.substring(index + 1, strLine.indexOf('"', index+1)));

					if(state.getName().contentEquals("Idle"))
					{
						//state.setTransition(state);
						idleState = state;
					}
					continue;
				}

				if(strLine.contains("animation="))
				{
					String animString = strLine.substring(index + 1, strLine.indexOf('"', index+1));
					String[] splitString = animString.split(",");
					
					//if(anim == null)
					anim = new Animation(sheetList.get(Integer.parseInt(splitString[0]) - 1),20);
					
					//TODO add frames backwards
					Image[] frames = new Image[Integer.parseInt(splitString[2])-Integer.parseInt(splitString[1]) + 1];
					int[] durations = new int[frames.length];
					//Approximately 60 FPS
					Arrays.fill(durations, 17);
					for (int i = 0; i < frames.length; i++)
					{
						state.getAnimation().addFrame(anim.getImage(Integer.parseInt(splitString[1]) + i), 17);
					}
					continue;
				}
				
				if(strLine.contains("condition="))
				{				
					state.getConditions().add(strLine.substring(strLine.indexOf('=')+1));
					continue;
				}
				
				if(strLine.contains("action="))
				{				
					state.getActions().add(strLine.substring(strLine.indexOf('=')+1));
					continue;
				}
				
				if(strLine.contains("looping"))
				{				
					state.setLooping(true);
					continue;
				}
				
				if(strLine.contains("open"))
				{				
					fighter.openStates.add(state);
					continue;
				}
				
				if(strLine.contains("hitbox="))
				{
					String subString = strLine;
					char flag;
					strLine = strLine.substring(7);
					Hitbox hitBox = new Hitbox(state.getAnimation().getFrameCount(), fighter);
					
					while(strLine != "DONE")
					{
						flag = strLine.charAt(0);
						
						index = strLine.indexOf('"', strLine.indexOf('"')+1);
						subString = strLine.substring(strLine.indexOf('"')+1, index);
						if(flag == 'A')
						{
							String[] splitString = subString.split(",");

							int[] hBC = new int[splitString.length]; //hitbox constructor
							for (int i = 0; i < splitString.length; i++)
								hBC[i] = Integer.parseInt(splitString[i]); 

							
							int startFrame = hBC[0];
							Vector2f location = new Vector2f(hBC[1],hBC[2]);
							Vector2f size = new Vector2f(hBC[3],hBC[4]);

							hitBox.addNode(startFrame,location,size);

							state.getHitBoxes().add(hitBox);
						} else if(flag == 'G')
						{
							if(subString.contains("HIGH"))
								hitBox.hit.high = true;
							if(subString.contains("MID"))
								hitBox.hit.mid = true;
							if(subString.contains("LOW"))
								hitBox.hit.low = true;
						} else if(flag == 'T')
						{
							packetFactory.BuildPacket(subString, hitBox.status.applyTarget, fighter);
						} else if(flag == 'P')
						{
							packetFactory.BuildPacket(subString, hitBox.status.applyParent, fighter);
						}

						strLine = strLine.substring(index +1);
						strLine = strLine.trim();

						if(strLine.length() == 0)
							strLine = "DONE";
					}
					continue;
				}

				if(strLine.contains("preserveInput"))
				{
					state.setPreserveInput(true);
					continue;
				}
				
				if(strLine.contains("allowGravity"))
				{
					
					state.setAcceptGravity(true);
					continue;
				}
				
				if(strLine.contains("transition"))
				{
					index = strLine.indexOf('"', strLine.indexOf('"')+1);
					String subString = strLine.substring(strLine.indexOf('"')+1, index);
					transitionStates.add(state.getName() + "," + subString);
					continue;
				}
				
				if(strLine.contains("block"))
				{
					state.setCanBlock(true);
					index = strLine.indexOf('"', strLine.indexOf('"')+1);
					String subString = strLine.substring(strLine.indexOf('"')+1, index);
					blockStates.add(state.getName() + "," + subString);
					continue;
				}
				
				if(strLine.contains("guard"))
				{
					state.setCanBlock(true);
					index = strLine.indexOf('"', strLine.indexOf('"')+1);
					String subString = strLine.substring(strLine.indexOf('"')+1, index);
					//blockStates.add(state.getName() + "," + subString);
					if(subString.contains("HIGH"))
						state.getGuard().high = true;
					if(subString.contains("MID"))
						state.getGuard().mid = true;
					if(subString.contains("LOW"))
						state.getGuard().low = true;
					continue;
				}
				
				if(strLine.contains("canTurn"))
				{
					state.setCanTurn(true);
					continue;
				}
				
				if(!strLine.contentEquals(strLine))
					System.out.println (strLine);
			}
			in.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}

		int transitionIndex = 0;
		int blockIndex = 0;
		
		for(State s :moveList)
		{
			//Set transition state to idle, unless another is specified.
			s.setTransition(idleState);
			if(transitionIndex < transitionStates.size())
			{
				String[] subString = new String[2];
				subString = transitionStates.get(transitionIndex).split(",");
				if(subString[0].contentEquals(s.getName()))
				{
					for (State s2 : moveList)
					{
						if(subString[1].contentEquals(s2.getName()))
						{
							s.setTransition(s2);
							transitionIndex++;
						}
					}
				}
			}
			
			//Assign block state.
			if(blockIndex < blockStates.size())
			{
				String[] subString = new String[2];
				subString = blockStates.get(blockIndex).split(",");
				if(subString[0].contentEquals(s.getName()))
				{
					for (State s2 : moveList)
					{
						if(subString[1].contentEquals(s2.getName()))
						{
							s.setBlock(s2);
							blockIndex++;
						}
					}
				}
			}
			//Set default state.
			if(s.getName().contentEquals("Idle"))
			{
				fighter.setState(s);
			}
			//Connect moves into tree.
			for(String str : s.getConditions())
			{
				if(str.contains("M="))
				{
					String subString = str.substring(str.indexOf("M=")+3, str.indexOf('"', str.indexOf("M=")+3));
					for(State s2 :moveList)
					{
						if(s2.getName().contentEquals(subString))
							s2.getCancels().add(s);
					}
				}
				
			}	
		}
		System.out.println ("MOVEFACTORY: " + moveList.size());
	}
}
