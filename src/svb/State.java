package svb;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Animation;

import status.StatusPacket;
import status.TestStatus;

import entities.Fighter;
import entities.Hitbox;
import entities.Hitbox.Hit;

public class State {

	private List<State> cancels;
	private State transition;
	private State block;
	private String name;
	private List<String> conditions;
	private List<String> actions;
	private int val;
	private Animation animation;
	public List<Hitbox> hitBoxes;
	public List<StatusPacket> status;
	private List<StatusPacket> removeStatus;
	private boolean looping;
	private boolean allowGravity = false;
	private boolean preserveInput = false;
	private boolean canTurn = false;
	private boolean canBlock = false;
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
			s.checkConditions(fighter);
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
			for (String condition : conditions)
			{
				if(!EventHandler.check(fighter, condition))
				{
					fighter.animation.restart();
					fighter.setState(this.transition);
				}
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
	
	public void checkConditions(Fighter fighter)
	{
		for (String condition : conditions)
		{
			if(EventHandler.check(fighter, condition))
			{
				fighter.setState(this);
				break;
			}
		}
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
