package gameState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import svb.Manager;
import svb.Player;

/**
 * @author Jubilee
 */
public class Roster extends BasicGameState{

	private class Character {
		public Image portrait;
		public File charRef;
		public int x;
		public int y;
	}
	
	class Folder {
		   boolean isFile;
		   String folderName;
		   List<File> subFolders = new ArrayList<File>();
		}
	
	private Character selectedP1, selectedP2;
	private Character[][] roster;
	private int rosterWidth = 2;
	private int numChars = 6;
	private int imageSize = 100;
	private int imageMargin = 7;
	private StateBasedGame game;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		//For each character, get portrait, assign to member of 2d array.
		//when that member is selected, that character is assigned to that
		//player. When both players have selected, the stateFactory is called
		//and the fight can begin.
		roster = new Character[9][9];
		this.game = game;
		
		File fname = new File("assets/chars");
		Folder obj = new Folder();
		if (fname.isDirectory()) {
		   File[] fileNames;
		   fileNames = fname.listFiles();
		   int folderNum = 0;
		   for (int i = 0; i < fileNames.length; i++) 
		   {  
				int X = folderNum%rosterWidth;
				int Y = folderNum/rosterWidth;
				roster[X][Y] = new Character();
				roster[X][Y].charRef = fileNames[i];
				roster[X][Y].portrait = new Image(roster[X][Y].charRef.getPath() + "/portrait.png");
				roster[X][Y].x = X;
				roster[X][Y].y = Y;
		  		
				if(Y==0)
		  		{
		  			if(X==0)
		  			{
		  				selectedP1 = roster[X][Y];
		  			}
		  			else if(X==rosterWidth-1)
		  			{
		 				selectedP2 = roster[X][Y]; 	
		  			}
		  		}
		  		folderNum++;
			   obj.subFolders.add(fileNames[i]);
		   }
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
        g.setColor(Color.white);
        
        int i = 0;
        int height = 0;
        Color color = Color.white;
        while(i < numChars)
        {
        	for(int width = 0; width < rosterWidth; width++)
        	{
        		color = Color.white;
        		if(roster[width][height]!=null)
        		{
        			//TODO Display swirlies rather than just highlight the portrait
        			//Maybe make portraits darker by default and lighten them when
        			//'hovering.'
        			if(roster[width][height]==selectedP1)
        			{
        				color = Color.blue;
        			}
        			if(roster[width][height]==selectedP2)
    				{
    					color = Color.red;
        				if(roster[width][height]==selectedP1)
        				{
        					color = Color.magenta;
        				}
    				}
        			g.drawImage(roster[width][height].portrait, 550+imageSize*width+imageMargin*width, 150+imageSize*height+imageMargin*height, color);
        		}
        		else
        			g.drawString("CHAR " + i, 550+imageSize*width+imageMargin*width, 150+imageSize*height+imageMargin*height);
            	i++;
        	}
        	height ++;
        }
        
        g.drawString("Press enter to confirm selection.", 500, 500);
	}
	
    public void keyPressed(int key, char c) {
    	
    	int acceptKey1 = Manager.player1.inputHandler.keysIndex[4];
        int acceptKey2 = Manager.player1.inputHandler.keysIndex[6];
        int acceptKey3 = Manager.player2.inputHandler.keysIndex[4];
        int acceptKey4 = Manager.player2.inputHandler.keysIndex[6];
        int abortKey1 = Manager.player1.inputHandler.keysIndex[5];
        int abortKey2 = Manager.player1.inputHandler.keysIndex[7];
        int abortKey3 = Manager.player2.inputHandler.keysIndex[5];
        int abortKey4 = Manager.player2.inputHandler.keysIndex[7];

        boolean accept = false;
        boolean abort = false;
        
        if(key == acceptKey1||key == acceptKey2||key == acceptKey3||key == acceptKey4)
        	accept = true;
        if(key == abortKey1||key == abortKey2||key == abortKey3||key == abortKey4)
        	abort = true;
    	
    	selectedP1 = moveSelection(selectedP1, key, Manager.player1);
    	selectedP2 = moveSelection(selectedP2, key, Manager.player2);

    	if(accept || key == Input.KEY_ENTER)
    	{
    		((EyeCatch)game.getState(Manager.StateIndex.EYE_CATCH.ordinal())).setFighters(selectedP1.charRef, selectedP2.charRef);
    		game.enterState(Manager.StateIndex.EYE_CATCH.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    	}
        else if(abort)
        	game.enterState(Manager.StateIndex.MAIN_MENU.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));

    }

    private Character moveSelection(Character character, int key, Player player)
    {
    	
    	int down = player.inputHandler.keysIndex[0];
    	int up = player.inputHandler.keysIndex[1];
    	int left = player.inputHandler.keysIndex[2];
    	int right = player.inputHandler.keysIndex[3];
    	
    	if(key==up)
    	{
            if(character.y == 0)
            	return roster[character.x][numChars/rosterWidth - 1];
            else
            	return roster[character.x][character.y-1];
    	}
    	else if(key==down)
    	{
            if(character.y == numChars/rosterWidth - 1)
            	return roster[character.x][0];
            else
            	return roster[character.x][character.y+1];
    	}
    	else if(key==left)
    	{
            if(character.x == 0)
            	return roster[rosterWidth - 1][character.y];
            else
            	return roster[character.x-1][character.y];
    	}
    	else if(key==right)
    	{
            if(character.x == rosterWidth - 1)
            	return roster[0][character.y];
            else
            	return roster[character.x+1][character.y];
    	}
    	return character;
    }
    
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		return Manager.StateIndex.MAIN_VS.ordinal();
	}

}
