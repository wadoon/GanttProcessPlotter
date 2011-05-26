package weigla.sched.model;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Implementation of an EDF
 * @author weigla
 */
public class EarliestDeadlineFirst extends RoundRobinScheduler {
    public EarliestDeadlineFirst() {
	readyQueue = new PriorityQueue<Task>(10, new EDFComparator());
    }
}

class EDFComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
	if (o1.getDeadline() == 0)
	    return 1;
	if (o2.getDeadline() == 0)
	    return -1;
	return o1.getDeadline() - o2.getDeadline();
    }
}
