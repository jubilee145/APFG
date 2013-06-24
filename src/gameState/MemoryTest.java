package gameState;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

import svb.Manager;

/**
 * @author Jubilee
 */
public class MemoryTest extends BasicGameState{

	private StateBasedGame game;
	
	List<SpriteSheet> sheetList;
	Texture displayTexture, tempTexture;
	Texture displayTexture2, tempTexture2;
	Image displayImage, displayImage2;
	File imageFolder;
	File[] fileNames;
	int fileCounter = 0;
	int fileCounter2 = 1;

	boolean testActive;
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
	{
		testActive = false;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
	        throws SlickException {
	    this.game = game;
	    imageFolder = new File("assets/memorytest");
	    displayImage = new Image(20,20);
	    displayImage2 = new Image(20,20);
	    fileNames = imageFolder.listFiles();
	    
	    try {
			tempTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(fileNames[fileCounter].getPath()));
			displayTexture = tempTexture;
			displayImage.setTexture(displayTexture);
			
			tempTexture2 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(fileNames[fileCounter2].getPath()));
			displayTexture2 = tempTexture2;
			displayImage2.setTexture(displayTexture2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.drawImage(displayImage, 200, 200);
		g.drawImage(displayImage2, 400, 200);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		if(testActive)
		{
			displayTexture = tempTexture;
			displayImage.destroy();
			displayImage.setTexture(displayTexture);
			
			displayTexture2 = tempTexture2;
			displayImage2.destroy();
			displayImage2.setTexture(displayTexture2);
			try {
				tempTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(fileNames[fileCounter + 1].getPath()));
				tempTexture2 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(fileNames[fileCounter2].getPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileCounter ++;
			if(fileCounter + 1 >= fileNames.length)
			{
				//firstTest = false;
				fileCounter = 0;
			}
			fileCounter2 --;
			if(fileCounter2 <= 1)
			{
				//firstTest = false;
				fileCounter2 = 500;
			}
		}
		
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

        if(key == acceptKey1||key == acceptKey2||key == acceptKey3||key == acceptKey4)
        	testActive = true;
        if(key == abortKey1||key == abortKey2||key == abortKey3||key == abortKey4)
        	game.enterState(Manager.StateIndex.MAIN_MENU.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    }

	@Override
	public int getID() {
		return Manager.StateIndex.MEMORY_TEST.ordinal();
	}

}
