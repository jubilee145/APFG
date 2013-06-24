package entities;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import svb.StatusPacketFactory;

/**
 * @author Jubilee
 * This is a test of the subActor system. It will be deleted once the character creator
 * has the power to spawn subActors.
 */
public class Projectile extends Actor {

	public Projectile(Vector2f startLocation, boolean isFacingLeft)
			throws SlickException {
		super(startLocation);	

		this.isFacingLeft = isFacingLeft;
		
		state = new State(1,1,0);
		Image i = new Image("assets/sprites/projectileTest.png");
		state.setFrames(new StateFrame[1]);
		
		state.getFrames()[0] = new StateFrame();
		state.getFrames()[0].setOffsetX(0);
		state.getFrames()[0].setOffsetY(0);
		state.getFrames()[0].setImage(i);
		
		state.setName("Whoosh!");
		
		Hitbox hitBox = new Hitbox(1, this);
		hitBox.addNode(0,new Vector2f(0,0),new Vector2f(100,100));
		hitBox.hit.high = false;
		hitBox.hit.mid = true;
		hitBox.hit.low = false;
		hitBox.hit.confirmed = false;
		
		state.getHitBoxes()[0] = hitBox;
		
		StatusPacketFactory packetFactory = new StatusPacketFactory();
		packetFactory.BuildPacket("DAMAGE,10", hitBox.status.applyTarget, this);

		try {
			JSONObject json = (JSONObject)new JSONParser().parse("{\"name\":\"Action: 1\", \"type\":\"MOVE\", \"parameters\":\"70,0\"}");
			state.getActions().add(json);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		state.setTransition(state);
		this.setState(state);
	}
	
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {	
		super.update(container, delta);
	}
}
