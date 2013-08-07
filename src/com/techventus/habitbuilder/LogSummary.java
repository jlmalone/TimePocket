package com.techventus.habitbuilder;

public class LogSummary {
	
	
	private int goal_id;
	private String goal;
	private int totalSeconds;
	private long lastPerformedDateMillis;
	private int count;
	
	
	
	
	public LogSummary(int goal_id, String goal, int totalSeconds,
			long lastPerformedDateMillis, int count) {
		super();
		this.goal_id = goal_id;
		this.goal = goal;
		this.totalSeconds = totalSeconds;
		this.lastPerformedDateMillis = lastPerformedDateMillis;
		this.count = count;
	}
	public int getGoal_id() {
		return goal_id;
	}
	public void setGoal_id(int goal_id) {
		this.goal_id = goal_id;
	}
	public String getGoal() {
		return goal;
	}
	public void setGoal(String goal) {
		this.goal = goal;
	}
	public int getTotalSeconds() {
		return totalSeconds;
	}
	public void setTotalSeconds(int totalSeconds) {
		this.totalSeconds = totalSeconds;
	}
	public long getLastPerformedDateMillis() {
		return lastPerformedDateMillis;
	}
	public void setLastPerformedDateMillis(long lastPerformedDateMillis) {
		this.lastPerformedDateMillis = lastPerformedDateMillis;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
	

}
