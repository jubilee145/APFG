package svb;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;

public class InputHandler {

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
	

	public int DOWN = 2;
	public int UP = 8;
	public int BACK = 4;
	public int FORWARD = 6;

	public String inputBuffer = "";
	public String heldDirection = "";
	public String heldButton = "";
	
	public boolean resetClock = false;

	public SpriteSheet spriteSheet;

	private boolean[] keysDown, keysPressed, keysReleased;
	private int[] keysIndex;
	
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
		
		if(heldDirection.contentEquals("26"))
			heldDirection = "3";
		else if(heldDirection.contentEquals("24"))
			heldDirection = "1";
		else if(heldDirection.contentEquals("48"))
			heldDirection = "7";
		else if(heldDirection.contentEquals("68"))
			heldDirection = "9";
		
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
