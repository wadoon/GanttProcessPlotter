package weigla.sched.model;

import java.util.Comparator;
import java.util.PriorityQueue;

public class RoundRobinPriority extends RoundRobinScheduler {
    public RoundRobinPriority() {
	readyQueue = new PriorityQueue<Task>(10, new PrioComparator());
    }

}

class PrioComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
	return o1.getProcess().getPriority() - o2.getProcess().getPriority();
    }
}