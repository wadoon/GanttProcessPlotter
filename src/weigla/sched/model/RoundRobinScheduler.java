package weigla.sched.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * 
 * @author weigla
 * 
 */
public class RoundRobinScheduler implements Scheduler {
    protected SchedulerObserver observer;
    protected Queue<Task> readyQueue = new ArrayDeque<Task>();
    protected ArrayList<Process> process = new ArrayList<Process>();
    protected Task active;
    protected int quantum = 20;

    public void simulate(int t) {
	if (process.size() == 0)
	    return;
	int curQuantum = quantum;
	for (int i = 0; i < t; i++) {
	    activateTask(i);
	    if (active == null)
		select(i);

	    if (active != null)
		active.run(1);

	    curQuantum--;

	    if ((active != null && active.isFinished()) || curQuantum == 0) {
		curQuantum = quantum;
		select(i);
	    }
	}
    }

    public void activateTask(int t) {
	for (Process p : process) {
	    if (p.getDelay() == t) {
		activate(p, t);
	    } else {
		// periodical and it's time and after delay
		if (p.getPeriod() != 0
			&& (t - p.getDelay()) % p.getPeriod() == 0
			&& p.getDelay() < t) {
		    activate(p, t);
		}
	    }
	}
    }

    public void select(int t) {
	if (active != null) {
	    if (!active.isFinished()) {
		readyQueue.add(active);
	    }
	    observer.onTaskDeselect(active, t);
	}
	Task oldact = active;
	active = next(t);
	if (oldact != active && active != null)
	    observer.onTaskSelect(active, t);
    }

    private Task next(int t) {
	if (!readyQueue.isEmpty()) {
	    return readyQueue.poll();
	}
	return null;
    }

    public void activate(Task t, int time) {
	observer.onActivate(t, time);
	readyQueue.add(t);
    }

    public void activate(Process p, int time) {
	activate(p.getTask(time), time);
    }

    public void put(List<Process> p) {
	process.addAll(p);
    }

    public void put(Process... processes) {
	put(Arrays.asList(processes));
    }

    @Override
    public void setObserver(SchedulerObserver s) {
	observer = s;
    }

    @Override
    public void setQuantum(int quantum) {
	this.quantum = quantum;
    }
}
