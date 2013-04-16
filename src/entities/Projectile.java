package entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import svb.State;
import svb.StatusPacketFactory;

/**
 * @author Jubilee
 */
public class Projectile extends Actor {

	public Projectile(SpriteSheet sheet, Vector2f startLocation)
			throws SlickException {
		super(sheet, startLocation);

		// TODO Auto-generated constructor stub
		state = new State();
		state.setAnimation(new Animation());
		touchBox = new Rectangle(0,0,100,100);
		
		state.setName("Whoosh!");
		state.getAnimation().addFrame(sheet.getSprite(0, 0), 17);
		
		Hitbox hitBox = new Hitbox(1, this);
		hitBox.addNode(0,new Vector2f(0,0),new Vector2f(100,100));
		hitBox.hit.mid = true;
		
		StatusPacketFactory packetFactory = new StatusPacketFactory();
		packetFactory.BuildPacket("DAMAGE,800", hitBox.status.applyTarget, this);
		//packetFactory.BuildPacket("MOVE,35,0", this.status, this);
		state.getActions().add("M=\"35,0\"");
		state.setTransition(state);
		
		this.setState(state);
	}
	
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {	
		super.update(container, delta);
	}
}
