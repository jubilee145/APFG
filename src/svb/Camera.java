package svb;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import stage.BackgroundObject;
import svb.Manager.WORLD;

import entities.Actor;
import entities.Fighter;
import entities.Hitbox;
import entities.Touchbox;
import gameState.EyeCatch;
import gameState.Fight;

/**
 * The camera class allows the action to move around, while still being visible. It basically shifts
 * whatever object its rendering to be visible onscreen, then sends it back, every render cycle.
 * May have useless stuff from a different project on it. <.<
 * @author Jubilee
 *
 */

public class Camera {

	public Rectangle screen;
	public Rectangle bounds;
	public Vector2f location;
	public Vector2f screenLocation;
	public Vector2f speed;
	
	public Actor target1, target2;
	public boolean crop = false;

	private GameContainer container;
	
	public int tempInt = 0;
	private StateBasedGame game;
	private Fight fight;

	public Camera(GameContainer container, StateBasedGame game) {
		this.container = container;
		
		location = new Vector2f();
		screenLocation = new Vector2f(0,0);
		screen = new Rectangle(0, 0, 1920, 1080);
		this.game = game;
		fight = ((Fight)game.getState(Manager.StateIndex.FIGHT.ordinal()));
	}

	public void update(int delta)
	{
		/**
		 * Needs to be measured from the right-hand side of the right-most target.
		 */
		if(target1 != null && target2 != null)
		{
			//location.x = (target1.location.x + target1.zoneBox.getWidth()/2 + target2.location.x + target2.zoneBox.getWidth()/2)/2 - screen.getWidth()/2;
			location.y = (target1.location.y + target1.zoneBox.getHeight()/2 + target2.location.y + target2.zoneBox.getHeight()/2)/2 - screen.getHeight()/2;
		}
		screen.setLocation(screenLocation);
	}
	
	public void render(Graphics g)
			throws SlickException {
		
		Vector2f offset = new Vector2f();
		g.drawString("Sub-Actors: " + (Manager.player1.fighter.subActors.size() + Manager.player2.fighter.subActors.size()), 50, 200);
		
		/**TODO Delete me. Displays ground level.
		 *-30 so that it looks like they're actually standing on the ground, instead of an impossibly
		 *thin wire.
		 */
		g.drawLine(0, WORLD.groundLevel - location.y + screen.getY() - 30, container.getScreenWidth(), WORLD.groundLevel + screen.getY() - location.y - 30);

		for (BackgroundObject b : fight.stage.background)
		{
			b.setLocation(b.location.add(location.negate().add(screenLocation)));
			b.render(container, g);
			b.setLocation(b.location.add(location).add(screenLocation.negate()));
		}
		
		for (Fighter f : fight.fighters) {

			offset.x = 0;
			offset.y = 0;

			f.zoneBox.setLocation(f.location.add(location.negate().add(screenLocation)));
			if (screen.intersects(f.zoneBox)) {
				f.render(container, g);
			}
			f.zoneBox.setLocation(f.location.add(location).add(screenLocation.negate()));
			
			for (Actor a : f.subActors)
			{
				a.zoneBox.setLocation(a.location.add(location.negate().add(screenLocation)));
				if (screen.intersects(a.zoneBox)) {
					a.render(container, g);
				}
				a.zoneBox.setLocation(a.location.add(location).add(screenLocation.negate()));
				
			}
		}

		for (Player p : Manager.players)
		{
			//TODO Delete me. Shows hitboxes. Replace with effect class when done.
			for(Hitbox h : p.fighter.getState().getHitBoxes())
			{
				g.setColor(Color.red);
				h.setX(h.getX() - location.x + screen.getX());
				h.setY(h.getY() - location.y + screen.getY());
				g.draw(h);
				h.setX(h.getX() + location.x - screen.getX());
				h.setY(h.getY() + location.y - screen.getY());
			}
			for(Touchbox t : p.fighter.getState().getTouchBoxes())
			{
				g.setColor(Color.green);
				t.setX(t.getX() - location.x + screen.getX());
				t.setY(t.getY() - location.y + screen.getY());
				g.draw(t);
				t.setX(t.getX() + location.x - screen.getX());
				t.setY(t.getY() + location.y - screen.getY());
			}
			g.setColor(Color.white);
			////
			p.render(container, g);
		}
	}
}
