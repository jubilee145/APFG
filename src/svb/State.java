package svb;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Animation;

import status.StatusPacket;
import status.TestStatus;

import entities.Fighter;
import entities.Hitbox;
import entities.Hitbox.Hit;

/**
 * The State class is used to build up a kind of decision tree for the Fighter class.
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
	private List<String> conditions;
	
	/**
	 * Actions performed during this state. Adds Status effects.
	 */
	private List<String> actions;
	
	/**
	 * ...What. <.< >.> I'm not entirely sure what this is.
	 */
	private int val;
	
	/**
	 * The animation (basically a list of images) that gets sent to the Fighter when this state is assigned.
	 */
	private Animation animation;
	
	/**
	 * The list of possible hitboxes that can happen during this state.
	 */
	public List<Hitbox> hitBoxes;
	
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
	 * Whether the player can block... ?
	 * TODO: Can't actually recall exactly how this works, i'll flesh this out later.
	 */
	private boolean canBlock = false;
	
	/**
	 * All the block types. If HIGH is true, then any high attacks can be blocked in this state, 
	 * etc. etc.
	 */
	private Guard guard;
	public class Guard
	{
		boolean high = false;
		boolean mid = false;
		boolean low = false;
	}

	public State()
	{
		cancels = new ArrayList<State>();
		conditions = new ArrayList<String>();
		actions = new ArrayList<String>();
		hitBoxes = new ArrayList<Hitbox>();
		status = new ArrayList<StatusPacket>();
		removeStatus = new ArrayList<StatusPacket>();
		guard = new Guard();
		
	}

	public void update(Fighter fighter)
	{
		for (State s : cancels)
		{
			if(s.checkConditions(fighter))
			{
				break;
			}
		}
		
		for (Hitbox h : hitBoxes)
		{
			h.update(fighter);
			if(h.hit.confirmed)
				fighter.hitConfirmed = true;
		}

		statusUpdate();

		if(fighter.animation.isStopped())
		{
			fighter.animation.restart();
			fighter.setState(this.transition);
		}
		if(looping)
		{
			boolean keepGoing = false;
			for (String condition : conditions)
			{
				if(EventHandler.check(fighter, condition))
				{
					keepGoing = true;
					break;
				}
			}
			if(!keepGoing)
			{
				fighter.animation.restart();
				fighter.setState(this.transition);
			}
		}
	}
	
	private void statusUpdate()
	{
		for (StatusPacket s : status)
		{
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
	
	public void doActions(Fighter fighter)
	{
		for (String action : actions)
		{
			EventHandler.doAction(fighter, action);
		}
	}
	
	public boolean checkConditions(Fighter fighter)
	{
		for (String condition : conditions)
		{
			if(EventHandler.check(fighter, condition))
			{
				fighter.setState(this);
				return true;
			}
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

	public List<String> getConditions() {
		return conditions;
	}

	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
		this.animation.setLooping(true);
	}
	
	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public List<Hitbox> getHitBoxes() {
		return hitBoxes;
	}

	public void setHitBoxes(List<Hitbox> hitBoxes) {
		this.hitBoxes = hitBoxes;
	}

	public boolean isAllowGravity() {
		return allowGravity;
	}

	public void setAcceptGravity(boolean acceptGravity) {
		this.allowGravity = acceptGravity;
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

	public boolean canBlock() {
		return canBlock;
	}

	public void setCanBlock(boolean canBlock) {
		this.canBlock = canBlock;
	}
	
	public Guard getGuard() {
		return guard;
	}

	public void setGuard(Guard guard) {
		this.guard = guard;
	}
	
	public void reset() {
		animation.restart();
		status.clear();
	}

	//return TRUE if the state defends against the hitbox
	public boolean checkGuard(Hitbox h)
	{
		if(h.hit.high && guard.high)
			return true;
		if(h.hit.mid && guard.mid)
			return true;
		if(h.hit.low && guard.low)
			return true;
			
		return false;
	}

	//stops the hitboxes from "flashing" from the previous location
	public void resetHitboxes(Fighter fighter) {
		for (Hitbox h: hitBoxes)
		{
			h.hit.confirmed = false;
			h.spent = false;
			h.update(fighter);
		}
	}
}
