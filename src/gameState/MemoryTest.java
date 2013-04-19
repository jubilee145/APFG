package gameState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

import entities.Fighter;

import svb.Manager;
import svb.Player;
import svb.StateFactory;

/**
 * @author Jubilee
 */
public class MemoryTest extends BasicGameState{

	private StateBasedGame game;
	
	List<SpriteSheet> sheetList;
	Texture displayTexture, tempTexture;
	Image displayImage;
	File imageFolder;
	File[] fileNames;
	int fileCounter = 0;

	boolean firstTest, secondTest, thirdTest;
	
	long totalMem;
	long maxMem;
	long freeMem;
	double megs;
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
	{
		firstTest = secondTest = thirdTest = false;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
	        throws SlickException {
	    this.game = game;
	    imageFolder = new File("assets/memorytest");
	    displayImage = new Image(20,20);
	    fileNames = imageFolder.listFiles();
	    
	    try {
			tempTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(fileNames[fileCounter].getPath()));
			displayTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(fileNames[fileCounter].getPath()));
			displayImage.setTexture(displayTexture);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
        Runtime rt = Runtime.getRuntime();
        totalMem = rt.totalMemory();
        maxMem = rt.maxMemory();
        freeMem = rt.freeMemory();
        megs = 1048576.0;
		
        g.drawString ("Total Memory: " + totalMem + " (" + (totalMem/megs) + " MiB)", 20, 40);
        g.drawString ("Max Memory:   " + maxMem + " (" + (maxMem/megs) + " 20)", 20, 60);
        g.drawString ("Free Memory:  " + freeMem + " (" + (freeMem/megs) + " 20)", 20, 80);
        
		g.drawImage(displayImage, 200, 200);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		//System.gc();
		if(firstTest)
		{
			displayTexture = tempTexture;
			displayImage.destroy();
			displayImage.setTexture(displayTexture);
			try {
				tempTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(fileNames[fileCounter + 1].getPath()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fileCounter ++;
			if(fileCounter + 1 >= fileNames.length)
			{
				//firstTest = false;
				fileCounter = 0;
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
        	firstTest = true;
        if(key == abortKey1||key == abortKey2||key == abortKey3||key == abortKey4)
        	game.enterState(Manager.StateIndex.MAIN_MENU.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    }

	@Override
	public int getID() {
		return Manager.StateIndex.MEMORY_TEST.ordinal();
	}

}
