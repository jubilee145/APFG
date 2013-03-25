package svb;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;

/**
 * Where everything starts. Resolution, fullscreen mode and framerate are set down the bottom in main(), 
 * and this also sets up the name of the window and instantiates a few important classes.
 * 
 * Later, this will probably be setting up the menu instead.
 * @author Jubilee
 *
 */
public class Main extends BasicGame {

	Camera camera;
	Manager manager;

	public Main() {
		super("Monster Horse Adventures: Pugilatio est Sanctae");
	}

	@Override
	public void init(GameContainer container) throws SlickException {

		manager = new Manager();
		camera = new Camera(container);
		Manager.cameras.add(camera);
		
		StateFactory mf = new StateFactory();
		
		
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
			AppGameContainer app = new AppGameContainer(new Main());
			//CanvasGameContainer app = new CanvasGameContainer(new Main());
			
			app.setDisplayMode(1280, 720, false);
			app.setResizable(true);
			app.setAlwaysRender(true);
			app.setTargetFrameRate(100);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}