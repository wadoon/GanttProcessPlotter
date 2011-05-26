package weigla.sched.model;

/**
 * A Process is a program. it can triggered multiple times, for periodic {@link Task} 
 * @author weigla
 *
 */
public class Process {
    private int duration;
    private int period;
    private int deadline;
    private int delay;
    private int priority;
    private String name;

    public Process(String name, int duration, int period, int deadline,
	    int delay, int priority) {
	this.duration = duration;
	this.period = period;
	this.deadline = deadline;
	this.delay = delay;
	this.name = name;
	this.priority = priority;
    }

    public int getPriority() {
	return priority;
    }

    public void setPriority(int priority) {
	this.priority = priority;
    }

    public int getDuration() {
	return duration;
    }

    public void setDuration(int duration) {
	this.duration = duration;
    }

    public int getPeriod() {
	return period;
    }

    public void setPeriod(int period) {
	this.period = period;
    }

    public int getDeadline() {
	return deadline;
    }

    public void setDeadline(int deadline) {
	this.deadline = deadline;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getDelay() {
	return delay;
    }

    public void setDelay(int delay) {
	this.delay = delay;
    }

    /**
     * create a task at time
     * @param time - current time
     * @return
     */
    public Task getTask(int time) {
	Task t = new Task(this, time);
	return t;
    }
}
