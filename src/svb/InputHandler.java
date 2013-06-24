package svb;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;

/**
 * Gets the input from the keyboard (and later, any networked player) and converts it into
 * the eight directional numbers and A B C and D, so that the Fighter and State classes can
 * figure out what they're supposed to do. :)
 * 
 * Directions work like this:
 * 
 * 	789
 * 	4 6
 * 	123
 * 
 * With 6 being forward, 4 being back, 8 is up and so on.
 * There are only four directional buttons, so pressing the 6 and 8 buttons
 * simultaneously will turn the input into a 9.
 * When no directional button is down, this will be a 5 instead.
 * 
 * TODO Make gamepads and so on work with this too.
 * 
 * @author Jubilee
 *
 */

public class InputHandler {

	/**
	 * just an easy way to reference the keys array.
	 */
	class Keys {
		public static final int DOWN = 0;
		public static final int UP = 1;
		public static final int LEFT = 2;
		public static final int RIGHT = 3;

		public static final int A = 4;
		public static final int B = 5;
		public static final int C = 6;
		public static final int D = 7;
	}
	
	/**
	 * The parsed directions of the keys. (e.g. '2' is seen as down in the movelists.)
	 */
	public int DOWN = 2;
	public int UP = 8;
	public int BACK = 4;
	public int FORWARD = 6;

	/**
	 * Buffers holding a string of recent inputs, and currently held inputs.
	 */
	public String inputBuffer = "";
	public String heldDirection = "";
	public String heldButton = "";
	
	public boolean resetClock = false;

	private boolean[] keysDown, keysPressed, keysReleased;
	public int[] keysIndex;
	
	public InputHandler()
	{
		keysDown = new boolean[8];
		keysPressed = new boolean[8];
		keysReleased = new boolean[8];
		keysIndex = new int[8];
		
		setKeys(Input.KEY_UP, Input.KEY_DOWN, Input.KEY_LEFT, Input.KEY_RIGHT, 
				Input.KEY_A, Input.KEY_S, Input.KEY_Z, Input.KEY_X);
	}
	
	public void setKeys(int up, int down, int left, int right, int A, int B, int C, int D)
	{
		keysIndex[Keys.UP] = up;
		keysIndex[Keys.DOWN] = down;
		keysIndex[Keys.LEFT] = left;
		keysIndex[Keys.RIGHT] = right;

		keysIndex[Keys.A] = A;
		keysIndex[Keys.B] = B;
		keysIndex[Keys.C] = C;
		keysIndex[Keys.D] = D;
	}
	
	public void update(GameContainer container)
	{
		resetClock = false;
		checkKeys(container);
	}
	
	private void checkKeys(GameContainer container)
	{
		if(keysDown[Keys.DOWN] && !container.getInput().isKeyDown(keysIndex[Keys.DOWN])){keysReleased[Keys.DOWN] = true; }
		else keysReleased[Keys.DOWN] = false;
		if(keysDown[Keys.UP] && !container.getInput().isKeyDown(keysIndex[Keys.UP])){keysReleased[Keys.UP] = true; }
		else keysReleased[Keys.UP] = false;
		if(keysDown[Keys.RIGHT] && !container.getInput().isKeyDown(keysIndex[Keys.RIGHT])){keysReleased[Keys.RIGHT] = true; }
		else keysReleased[Keys.RIGHT] = false;
		if(keysDown[Keys.LEFT] && !container.getInput().isKeyDown(keysIndex[Keys.LEFT])){keysReleased[Keys.LEFT] = true; }
		else keysReleased[Keys.LEFT] = false;

		if(container.getInput().isKeyDown(keysIndex[Keys.DOWN])){keysDown[Keys.DOWN] = true;}
		else keysDown[Keys.DOWN] = false;
		if(container.getInput().isKeyDown(keysIndex[Keys.UP])){keysDown[Keys.UP] = true;}
		else keysDown[Keys.UP] = false;
		if(container.getInput().isKeyDown(keysIndex[Keys.LEFT])){keysDown[Keys.LEFT] = true;}
		else keysDown[Keys.LEFT] = false;
		if(container.getInput().isKeyDown(keysIndex[Keys.RIGHT])){keysDown[Keys.RIGHT] = true;}
		else keysDown[Keys.RIGHT] = false;
		if(container.getInput().isKeyDown(keysIndex[Keys.A])){keysDown[Keys.A] = true; }
		else keysDown[Keys.A] = false;
		if(container.getInput().isKeyDown(keysIndex[Keys.B])){keysDown[Keys.B] = true; }
		else keysDown[Keys.B] = false;
		if(container.getInput().isKeyDown(keysIndex[Keys.C])){keysDown[Keys.C] = true; }
		else keysDown[Keys.C] = false;
		if(container.getInput().isKeyDown(keysIndex[Keys.D])){keysDown[Keys.D] = true; }
		else keysDown[Keys.D] = false;

		if(container.getInput().isKeyPressed(keysIndex[Keys.UP])){keysPressed[Keys.UP] = true; }
		if(container.getInput().isKeyPressed(keysIndex[Keys.DOWN])){keysPressed[Keys.DOWN] = true; }
		if(container.getInput().isKeyPressed(keysIndex[Keys.LEFT])){keysPressed[Keys.LEFT] = true; }
		if(container.getInput().isKeyPressed(keysIndex[Keys.RIGHT])){keysPressed[Keys.RIGHT] = true; }
		if(container.getInput().isKeyPressed(keysIndex[Keys.A])){keysPressed[Keys.A] = true; }
		if(container.getInput().isKeyPressed(keysIndex[Keys.B])){keysPressed[Keys.B] = true; }
		if(container.getInput().isKeyPressed(keysIndex[Keys.C])){keysPressed[Keys.C] = true; }
		if(container.getInput().isKeyPressed(keysIndex[Keys.D])){keysPressed[Keys.D] = true; }
	}

	public String checkDirections()
	{
		boolean dirty = false;
		heldDirection ="";
		inputBuffer = "";
		if(keysDown[Keys.DOWN])
		{
			if(keysPressed[Keys.DOWN] || keysPressed[Keys.RIGHT] || keysReleased[Keys.RIGHT] || keysPressed[Keys.LEFT] || keysReleased[Keys.LEFT])
			{inputBuffer += DOWN; dirty = true;}
			heldDirection += DOWN;
			resetClock = true;
		}

		if(keysDown[Keys.RIGHT])
		{
			if(keysPressed[Keys.RIGHT] || keysPressed[Keys.DOWN] || keysReleased[Keys.DOWN]  || keysReleased[Keys.DOWN] || keysPressed[Keys.UP] || keysReleased[Keys.UP])
			{inputBuffer += FORWARD; dirty = true;}
			heldDirection += FORWARD;
			resetClock = true;
		}

		if(keysDown[Keys.LEFT])
		{
			if(keysPressed[Keys.LEFT] || keysPressed[Keys.DOWN] || keysReleased[Keys.DOWN] || keysPressed[Keys.UP] || keysReleased[Keys.UP])
			{inputBuffer += BACK; dirty = true;}
			heldDirection += BACK;
			resetClock = true;
		}

		if(keysDown[Keys.UP])
		{
			if(keysPressed[Keys.UP] || keysPressed[Keys.RIGHT] || keysReleased[Keys.RIGHT] || keysPressed[Keys.LEFT] || keysReleased[Keys.LEFT])
			{inputBuffer += UP; dirty = true;}
			heldDirection += UP;
			resetClock = true;
		}
		
		/*if(heldDirection.contentEquals("26"))
			heldDirection = "3";
		else if(heldDirection.contentEquals("24"))
			heldDirection = "1";
		else if(heldDirection.contentEquals("48"))
			heldDirection = "7";
		else if(heldDirection.contentEquals("68"))
			heldDirection = "9";*/
		
		if(dirty)
		{
			if(inputBuffer.contentEquals("26"))
				inputBuffer = "3";
			else if(inputBuffer.contentEquals("24"))
				inputBuffer = "1";
			else if(inputBuffer.contentEquals("48"))
				inputBuffer = "7";
			else if(inputBuffer.contentEquals("68"))
				inputBuffer = "9";
			inputBuffer += ",";
		}
			
		return inputBuffer;
	}
	
	public String checkButtons()
	{
		boolean dirty = false;
		heldButton = "";
		inputBuffer = "";
		if(keysDown[Keys.A])
		{
			if(keysPressed[Keys.A] || keysPressed[Keys.B] || keysPressed[Keys.C] || keysPressed[Keys.D]
					|| keysReleased[Keys.B] || keysReleased[Keys.C] || keysReleased[Keys.D])
			{inputBuffer += "A"; resetClock = true; dirty = true;}
			heldButton += "A";
		}

		if(keysDown[Keys.B])
		{
			if(keysPressed[Keys.A] || keysPressed[Keys.B] || keysPressed[Keys.C] || keysPressed[Keys.D]
					|| keysReleased[Keys.A] || keysReleased[Keys.C] || keysReleased[Keys.D])
			{inputBuffer += "B"; resetClock = true; dirty = true;}
			heldButton += "B";
		}

		if(keysDown[Keys.C])
		{
			if(keysPressed[Keys.A] || keysPressed[Keys.B] || keysPressed[Keys.C] || keysPressed[Keys.D]
					|| keysReleased[Keys.A] || keysReleased[Keys.B] || keysReleased[Keys.D])
			{inputBuffer += "C"; resetClock = true; dirty = true;}
			heldButton += "C";
		}

		if(keysDown[Keys.D])
		{
			if(keysPressed[Keys.A] || keysPressed[Keys.B] || keysPressed[Keys.C] || keysPressed[Keys.D]
					|| keysReleased[Keys.A] || keysReleased[Keys.C] || keysReleased[Keys.C])
			{inputBuffer += "D"; resetClock = true; dirty = true;}
			heldButton += "D";
		}
		if(dirty)
		{
			inputBuffer += ",";
		}
		
		return inputBuffer;
	}
	
	public void turnAround()
	{

		int temp = keysIndex[Keys.RIGHT];
		keysIndex[Keys.RIGHT] = keysIndex[Keys.LEFT];
		keysIndex[Keys.LEFT] = temp;
		
	}
	
	public void reset(GameContainer container)
	{
		/**/
		resetKeys(container);
	}

	public void resetKeys(GameContainer container)
	{
		for (int i = 0; i < keysPressed.length; i++)
			keysPressed[i] = false;
	}
	
}


/*/////////////////////////////////////////////////////////////////////////////////////////////////////
 * InputManager class programmed by Azura (James Addison).
 * 
 * Incomplete, however mostly finished.
 * 
 *////////////////////////////////////////////////////////////////////////////////////////////////////
/*
package Client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

public class InputHandler 
{
 private static enum KeyStates
 {
  NONE,
  DOWN,
  PRESSED,
  RELEASED;
 }
 
 private static enum Keys
 {
  UP,
  DOWN,
  LEFT,
  RIGHT;
  
  private static String getName(int id)
  {
    switch (id) 
    {
             case 0:
              return "UP";
             case 1:
              return "DOWN";
             case 2:
              return "LEFT";
             case 3:
              return "RIGHT";
             default:
              return "";
    }
  }
 }
 
 private class KeyData 
 {
  private int id;
  private int state = KeyStates.NONE.ordinal();
  
  public KeyData(int id) 
  {
   this.id = id;
  }
 }
 
 private final String KEYBINDINGS = "src/Client/Keybindings.ini";
 private static final int keySize = Keys.values().length;
 private KeyData[] keyData;
 
 public InputHandler()
 {

 }
 
 public void init()
 {
  try 
  {
   readKeys();
  } 
  catch (FileNotFoundException e) 
  {
   e.printStackTrace();
  }
  
 }
 
 public void readKeys() throws FileNotFoundException
 {
  String actionName;
  int id;
  keyData = new KeyData[keySize];
  String inLine;
  String[] splits;
     Scanner scanner = new Scanner(new File(KEYBINDINGS));
      try 
      {
       while (scanner.hasNextLine())
      {
       inLine = scanner.nextLine();
       inLine = inLine.replaceAll(" ", "");
       inLine = inLine.replaceAll("'", "");
       splits = inLine.split("=");
       actionName = splits[0];
       id = Integer.parseInt(splits[1]);
       keyData[Keys.valueOf(actionName).ordinal()] = new KeyData(id);
       System.out.println(actionName + " : " + id);
      }
      }
      finally
      {
        scanner.close();
      }
 }
 
 public void writeKeys()
 {
  Writer writer = null;
  
  try 
  {
      writer = new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(KEYBINDINGS)));
      for(int i = 0; i < keySize-1; i++)
      {
       writer.write(Keys.getName(i) + " = '" + keyData[i].id + "'" + System.getProperty("line.separator"));
      }
      writer.write(Keys.getName(keySize-1) + " = '" + keyData[keySize-1].id + "'");
  } 
  catch (IOException ex)
  {
  }
  finally 
  {
     try 
     {
      writer.close();
     } 
     catch (Exception ex) 
     {
      
     }
  }
 }
 
 public void setKey(KeyData keyData, int id)
 {
  keyData.id = id;
  keyData.state = KeyStates.NONE.ordinal();
 }
 
 public void resetKeys()
 {
  for (int i = 0; i < keySize; i++)
  {
   keyData[i].state = KeyStates.NONE.ordinal();
  }
 }
 
 public void resetDefaults()
 {
  keyData[0].id = Input.KEY_W;
  keyData[0].state = KeyStates.NONE.ordinal();
  keyData[1].id = Input.KEY_S;
  keyData[1].state = KeyStates.NONE.ordinal();
  keyData[2].id = Input.KEY_A;
  keyData[2].state = KeyStates.NONE.ordinal();
  keyData[3].id = Input.KEY_D;
  keyData[3].state = KeyStates.NONE.ordinal();
 }
 
 public void checkKeys(GameContainer container)
 {
  Input input = container.getInput();
  
  for(int i = 0; i < keySize; i++)
  { 
   //Checks whether the previous key state was DOWN and the current key is not DOWN if so
   //sets the current key state to RELEASED.
   if(keyData[i].state == KeyStates.DOWN.ordinal() && input.isKeyDown(keyData[i].id) == false)
   {
    keyData[i].state = KeyStates.RELEASED.ordinal();
   }
   //Checks whether the key is PRESSED if so sets the key State to PRESSED.
   else if(input.isKeyPressed(keyData[i].id))
   {
    keyData[i].state = KeyStates.PRESSED.ordinal();
   }
   //Checks whether the key is DOWN if so sets the key state to DOWN.
   else if(input.isKeyDown(keyData[i].id))
   {
    keyData[i].state = KeyStates.DOWN.ordinal();
   }
   //If the key isn't currently active it sets the key state to NONE.
   else
   {
    keyData[i].state = KeyStates.NONE.ordinal();
   }
  }
 }
}*/