package weigla.sched.model;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Implementation for an Rate Monotonic Schedulers. 
 * Consider this would not work right for Process that are not periodic
 * @author weigla
 *
 */
public class RateMonotonicSchedulers extends RoundRobinScheduler {
    public RateMonotonicSchedulers() {
	readyQueue = new PriorityQueue<Task>(10, new RMSComparator());
    }

}

class RMSComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
	return o1.getProcess().getPeriod() - o2.getProcess().getPeriod();
    }
}
