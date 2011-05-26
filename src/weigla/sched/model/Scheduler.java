package weigla.sched.model;

import java.util.List;

public interface Scheduler {
    public void setObserver(SchedulerObserver s);
    public void simulate(int t);
    public void put(List<Process> list);
    public void setQuantum(int quantum);
}
