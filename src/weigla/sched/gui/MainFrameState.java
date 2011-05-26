package weigla.sched.gui;

import java.io.Serializable;
import java.util.List;

import weigla.sched.model.Process;

/**
 * State from {@link MainFrame} for an easy loading and saving
 * @author weigla
 *
 */
public class MainFrameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Process> processes;
    private int quantum;
    private int time;
    private String currentSched;

    public List<Process> getProcesses() {
	return processes;
    }

    public void setProcesses(List<Process> processes) {
	this.processes = processes;
    }

    public int getQuantum() {
	return quantum;
    }

    public void setQuantum(int quantum) {
	this.quantum = quantum;
    }

    public int getTime() {
	return time;
    }

    public void setTime(int time) {
	this.time = time;
    }

    public String getCurrentSched() {
	return currentSched;
    }

    public void setCurrentSched(String currentSched) {
	this.currentSched = currentSched;
    }
}
