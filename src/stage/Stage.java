package stage;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

/**
 * The environment, background objects, floor, music, worldbounds.
 * Associated with a particular character, or just random?
 * Experiment with environmental hazards, effects? (Wind, fire, traps etc.)
 * Maybe certain ares of the stage can react to the players? E.G. windows can smash,
 * books get knocked off library shelves, dust clouds get raised, the buttons in the
 * temple of the Sapphire Statue cause traps to go off in the background.
 * 
 * @author Jubilee
 */


public class Stage {

	private Music music;
	public List<BackgroundObject> background;
	
	public Stage() throws SlickException
	{
		//Test background objects
		background = new ArrayList<BackgroundObject>();
		Image test = new Image("assets/stage/images/bgTestObject.png");
		Image i;
		Vector2f location;
		float parallax;
		BackgroundObject b;
		{
			i = test.copy();
			location = new Vector2f(700,0);
			parallax = 0.1f;
			b = new BackgroundObject(i,parallax, location, 1);
			b.color = new Color(50,50,50);
			background.add(b);
			
			i = test.copy();
			location = new Vector2f(800,0);
			parallax = 0.1f;
			b = new BackgroundObject(i,parallax, location, 1);
			b.color = new Color(50,50,50);
			background.add(b);
			
			i = test.copy();
			location = new Vector2f(900,0);
			parallax = 0.1f;
			b = new BackgroundObject(i,parallax, location, 1);
			b.color = new Color(50,50,50);
			background.add(b);
			
			i = test.copy();
			location = new Vector2f(280,0);
			parallax = 0.25f;
			b = new BackgroundObject(i,parallax, location, 1);
			b.color = new Color(150,150,150);
			background.add(b);
			
			i = test.copy();
			location = new Vector2f(380,0);
			parallax = 0.25f;
			b = new BackgroundObject(i,parallax, location, 1);
			b.color = new Color(150,150,150);
			background.add(b);
			
			i = test.copy();
			location = new Vector2f(140,0);
			parallax = 0.5f;
			b = new BackgroundObject(i,parallax, location, 1);
			b.color = new Color(250,250,250);
			background.add(b);
			
			i = test.copy();
			location = new Vector2f(240,0);
			parallax = 0.5f;
			b = new BackgroundObject(i,parallax, location, 1);
			b.color = new Color(250,250,250);
			background.add(b);
			
			i = test.copy();
			location = new Vector2f(340,0);
			parallax = 0.5f;
			b = new BackgroundObject(i,parallax, location, 1);
			b.color = new Color(250,250,250);
			background.add(b);
		}
		//Set up music.
		/*try {
			music = new Music("assets/sounds/FF7BossBattlePiano_byTannerHelland.ogg");
			music.loop();
			music.play();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure in Stage constructor: " + e.getMessage());
			System.out.println(System.getProperty("user.dir"));
			e.printStackTrace();
		}*/
	}
}
