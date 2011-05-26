package weigla.sched.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SchedulerObserver {
    public Map<Process, List<int[]>> map = new HashMap<Process, List<int[]>>();

    public Multimap<Process, Integer> deadlines = HashMultimap.create();
    public Multimap<Process, Integer> activates = HashMultimap.create();

    public void onActivate(Task t, int time) {
	if (t.getDeadline() != 0)
	    deadlines.put(t.getProcess(), t.getDeadline());
	activates.put(t.getProcess(), time);
    }

    public void onTaskDeselect(Task t, int time) {
	System.out.format("SchedulerObserver.onTaskDeselect(%s,%d)%n", t
		.getProcess().getName(), time);
	List<int[]> l = map.get(t.getProcess());
	l.get(l.size() - 1)[1] = time;
	l.get(l.size() - 1)[2] = t.isFinished() ? 1 : 0;
	l.get(l.size() - 1)[3] = t.getDeadline();
    }

    public void onTaskSelect(Task t, int time) {
	System.out.format("SchedulerObserver.onTaskSelect(%s,%d)%n", t
		.getProcess().getName(), time);
	if (!map.containsKey(t.getProcess())) {
	    map.put(t.getProcess(), new ArrayList<int[]>());
	}
	List<int[]> l = map.get(t.getProcess());
	l.add(new int[] { time, time, 0, 0, 0 });
    }

    public int countProcesses() {
	return map.size();
    }

    public int maxTime() {
	int max = 0;
	for (List<int[]> l : map.values()) {
	    for (int[] is : l) {
		max = Math.max(is[1], max);
	    }
	}
	return max;
    }
}
