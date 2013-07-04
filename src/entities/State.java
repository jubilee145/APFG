package entities;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import status.StatusPacket;
import svb.EventHandler;
import svb.Manager;

import entities.Actor;

/**
 * The State class is used to build up a kind of decision tree for the Actor class.
 * Each state has a handful of states it can 'cancel' into, depending on the players input.
 * eg. the "Idle" state cancels into the "Walk Forward" state when the player pushes the
 * forward button. "Idle" can also cancel into jump, most of the attack states and probably
 * a lot of other stuff later.
 * @author Jubilee
 *
 */

public class State {

	/**
	 * The list of possible states that can be assigned to the fighter from this state.
	 */
	private List<State> cancels;
	
	/**
	 * The state that will be assigned to the fighter once this state finishes.
	 */
	private State transition; 
	
	/**
	 * This is the state which will happen if the fighter blocks an attack while in this
	 * state. Usually this will just be "Block" or something, but for things like air-blocking
	 * or counters, different states may be used.
	 */
	private State block;
	
	/**
	 * Used to identify the state.
	 */
	private String name;
	
	/**
	 * A list of the conditions required to enter this state. This is what determines
	 * how one state cancels into another. Check the EventHandler class to see what the
	 * different condition terms mean.
	 */
	private List<List<JSONObject>> conditions;
	
	/**
	 * Actions performed during this state. Adds Status effects.
	 */
	private List<JSONObject> actions;
	
	/**
	 * The animation (basically a list of images) that gets sent to the Actor when this state is assigned.
	 */
	private int currentFrame;
	
	/**
	 * The time, in milliseconds, since the framedata of this state was last updated.
	 */
	private float lastUpdate;
	
	/**
	 * The list of possible hitboxes that can happen during this state.
	 */
	private Hitbox[] hitBoxes;
	
	/**
	 * The list of possible touchboxes that can happen during this state.
	 */
	private Touchbox[] touchBoxes;
	
	/**
	 * Status effects like movement and drag get sent here depending on what's in the action list.
	 * Basically anything that happens every time you're in this state should be in here.
	 */
	public List<StatusPacket> status;
	
	/**
	 * A list of statuses (statii?) that are going to be removed from the status list in the next pass. 
	 */
	private List<StatusPacket> removeStatus;
	
	/**
	 * Whether this state loops, or goes to the transition state at the end of the animation.
	 * NB: If this is TRUE, then at least one condition in the condition list must be true in 
	 * each update pass, or else the transition state will be assigned.
	 */
	private boolean looping;
	
	/**
	 * Whether the fighter will fall during this state.
	 */
	private boolean allowGravity = false;
	
	/**
	 * If this is true, any keys pressed to enter this state won't be wiped from the input buffer.
	 * Useful for things like walking that happen as part of another move.
	 */
	private boolean preserveInput = false;
	
	/**
	 * Whether the fighter can turn around in this state. Should never be true for any state that
	 * makes you go forwards, or you get horrible spinny stuff.
	 */
	private boolean canTurn = false;
	
	/**
	 * The array of frames for this state. Contains guard and image data.
	 */
	private StateFrame[] frames;

	public State(int frameCount, int hitboxCount, int touchBoxCount)
	{
		cancels = new ArrayList<State>();
		conditions = new ArrayList<List<JSONObject>>();
		actions = new ArrayList<JSONObject>();
		hitBoxes = new Hitbox[hitboxCount];
		touchBoxes = new Touchbox[touchBoxCount];
		frames = new StateFrame[frameCount];
		status = new ArrayList<StatusPacket>();
		removeStatus = new ArrayList<StatusPacket>();

	}

	public void update(Actor actor, int delta)
	{
		
		checkConditions(actor);
		
		for (Hitbox h : hitBoxes)
		{
			h.update();
			if(h.hit.confirmed)
				actor.hitConfirmed = true;
		}
		
		for (Touchbox t : touchBoxes)
		{
			t.update();
		}


		statusUpdate(actor);

		//Update frame
		lastUpdate += delta * Manager.timeScale * Manager.WORLD.conversionConstant;	
		if(lastUpdate >= 17)
		{
			currentFrame ++;
			lastUpdate = 0;
		}
		//Reset frame
		if(currentFrame >= frames.length)
		{
			actor.setState(this.transition);
		}

	}
	
	public void render(GameContainer container, Graphics g, float offsetX, float offsetY, Actor actor)
	{
		
		float zoneWidth = actor.zoneBox.getWidth();
		Image image = frames[currentFrame].getImage();
		
		offsetY = offsetY+ frames[currentFrame].getOffsetY();
		// TODO Image decompression.
		// image.draw(x,y,scale);
		if(actor.isFacingLeft)
			g.drawImage(image.getFlippedCopy(true, false), offsetX + zoneWidth/2 - frames[currentFrame].getOffsetX() + (zoneWidth - image.getWidth()), offsetY);
		else
			g.drawImage(image,offsetX - zoneWidth/2 + frames[currentFrame].getOffsetX(), offsetY);
		
	}
	
	private void statusUpdate(Actor actor)
	{

		for (StatusPacket s : status)
		{
			if(Math.abs(actor.velocity.y) > 0.5f)
				System.out.println(s.getClass().toString());
			s.update();
			if(s.die())
				removeStatus.add(s);
			
		}
		
		for (StatusPacket s : removeStatus)
		{
			status.remove(s);
		}
		removeStatus.clear();
		


	}
	
	public void doActions(Actor actor)
	{
		for (JSONObject action : actions)
		{
			EventHandler.doAction(actor, action);
		}
	}
	
	public boolean checkConditions(Actor actor)
	{
		int currentCancel = 0;
		for(State s : cancels)
		{
			for (JSONObject condition : conditions.get(currentCancel))
			{
				if(EventHandler.check(actor, condition))
				{
					actor.setState(s);
					return true;
				}
			}
			currentCancel++;
		}
		return false;
	}
	
	public List<State> getCancels() {
		return cancels;
	}

	public void setCancels(List<State> cancels) {
		this.cancels = cancels;
	}

	public State getTransition() {
		return transition;
	}

	public void setTransition(State transition) {
		this.transition = transition;
	}

	public State getBlock() {
		return block;
	}

	public void setBlock(State block) {
		this.block = block;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<List<JSONObject>> getConditions() {
		return conditions;
	}

	public void setConditions(List<List<JSONObject>> conditions) {
		this.conditions = conditions;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}
	
	public List<JSONObject> getActions() {
		return actions;
	}

	public void setActions(List<JSONObject> actions) {
		this.actions = actions;
	}

	public boolean isAllowGravity() {
		return allowGravity;
	}

	public void setAllowGravity(boolean allowGravity) {
		this.allowGravity = allowGravity;
	}

	public boolean isPreserveInput() {
		return preserveInput;
	}

	public void setPreserveInput(boolean preserveInput) {
		this.preserveInput = preserveInput;
	}
	
	public boolean canTurn() {
		return canTurn;
	}

	public void setCanTurn(boolean canTurn) {
		this.canTurn = canTurn;
	}
	
	public void reset() {
		currentFrame = 0;
		status.clear();
	}

	//TODO return TRUE if the state defends against the hitbox
	public boolean checkGuard(Hitbox h)
	{	
		return false;
	}

	public StateFrame[] getFrames() {
		return frames;
	}

	public void setFrames(StateFrame[] frames) {
		this.frames = frames;
	}

	//stops the hitboxes from "flashing" from the previous location on state reset.
	public void resetHitboxes(Actor actor) {
		for (Hitbox h: hitBoxes)
		{
			h.hit.confirmed = false;
			h.spent = false;
			h.update();
		}
	}
	
	public void resetTouchboxes(Actor actor) {
		for (Touchbox t: touchBoxes)
		{
			t.update();
		}
	}

	public Hitbox[] getHitBoxes() {
		return hitBoxes;
	}

	public void setHitBoxes(Hitbox[] hitBoxes) {
		this.hitBoxes = hitBoxes;
	}

	public Touchbox[] getTouchBoxes() {
		return touchBoxes;
	}

	public void setTouchBoxes(Touchbox[] touchBoxes) {
		this.touchBoxes = touchBoxes;
	}

	
}
