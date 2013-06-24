package svb;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import stage.Stage;

import entities.Actor;
import entities.Fighter;
import entities.Hitbox;

/**
 * Holds a bunch of constants and variables that need to be accessed by many other classes.
 * Also options, controls and such later?
 * 
 * @author Jubilee
 *
 */
public class Manager
{
	public static List<Actor> actors;
	public static List<Fighter> fighters;
	public static List<Player> players;
	public static Player player1, player2;
	public static List<Camera> cameras;
	public static List<SpriteSheet> haroldSheet;
	public static boolean debug;
	public static float timeScale = 100f;
	public static boolean preLoadTextures = false;
	public static boolean allowShaders = false;
	
	public static class WORLD
	{
		public static final int groundLevel = 0;
		public static final float gravity = 0.005f;
		public static final float conversionConstant = 0.01f;
	}
	
	public static enum StateIndex {
		SPLASH,
		MAIN_MENU,
		MAIN_VS,
		MAIN_OPTIONS,
		EYE_CATCH,
		FIGHT,
		SHADER_TEST,
		MEMORY_TEST;
	}
	
	public Manager() throws SlickException
	{
		actors = new ArrayList<Actor>();
		fighters = new ArrayList<Fighter>();
		players = new ArrayList<Player>();
		cameras = new ArrayList<Camera>();	

		debug = false;
		
		
	}
}
