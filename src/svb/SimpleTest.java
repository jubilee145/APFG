package svb;
import org.newdawn.slick.Animation;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.geom.Vector2f;

import entities.Fighter;


public class SimpleTest extends BasicGame {

	Camera camera, camera2;
	Manager manager;
	
	Animation animation;

	int X = 0, Y = 0;
	
	public SimpleTest() {
		super("Monster Horse Adventures: Pugilatio est Sanctae");
	}

	@Override
	public void init(GameContainer container) throws SlickException {

		manager = new Manager();
		
		camera = new Camera(container);
		Manager.cameras.add(camera);
		//Manager.cameras.get(0).target = Manager.fighters.get(0);
		//TODO Delete me
		{
			camera.screenLocation.x = 0;
			camera.screenLocation.y = 0;
			camera.location.x = 0;
			camera.location.y = 0;
			

		}
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {

		Manager.update(container, delta);
		if(container.getInput().isKeyPressed(Input.KEY_TAB))
			Manager.debug = !Manager.debug;
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		Manager.render(container, g);
		
		
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new SimpleTest());
			app.setDisplayMode(1280, 720, false);
			app.setTargetFrameRate(100);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}