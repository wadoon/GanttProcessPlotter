package weigla.sched.model;

public class Task {
    private Process process;
    private int time;
    private int deadline;
    private int priority;

    public Task(Process p, int curTime) {
	process = p;
	this.time = p.getDuration();
	if (p.getDeadline() != 0)
	    deadline = curTime + p.getDeadline();
    }

    public int getPriority() {
	return priority;
    }

    public int getTime() {
	return time;
    }

    public int getDeadline() {
	return deadline;
    }

    public void setProcess(Process process) {
	this.process = process;
    }

    public Process getProcess() {
	return process;
    }

    public int run(int quantum) {
	if (time >= quantum) {
	    time -= quantum;
	    return quantum;
	}
	time = 0;
	return time;
    }

    public boolean isFinished() {
	return time <= 0;
    }
}
