package weigla.sched.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import weigla.sched.model.DrawGantt;
import weigla.sched.model.EarliestDeadlineFirst;
import weigla.sched.model.Process;
import weigla.sched.model.RateMonotonicSchedulers;
import weigla.sched.model.RoundRobinScheduler;
import weigla.sched.model.Scheduler;
import weigla.sched.model.SchedulerObserver;

/**
 * Central frame of the gui.
 * 
 * @author weigla
 * 
 */
public class MainFrame extends JFrame implements ActionListener, ChangeListener {
    private static final long serialVersionUID = 8136951197387929515L;
    private ProcessTableModel ptm = new ProcessTableModel();
    private DrawPanel drawPanel;
    private JComboBox combobox = new JComboBox(new String[] { "RRS", "EDF",
	    "RMS", "RRS-Prority" });
    private JSpinner spinnerTime = new JSpinner();
    private JButton btnRemove;
    private JButton btnAdd;

    private JButton btnOpen = new JButton(new LoadStateAction());
    private JButton btnSave = new JButton(new SaveStateAction());

    private JButton btnUpdate = new JButton("Update View");
    private JSpinner spinnerQuantum = new JSpinner();
    private JTable jtable;

    public MainFrame() {
	init();
    }

    private void update() {
	int time = (Integer) spinnerTime.getValue();
	int quantum = (Integer) spinnerQuantum.getValue();
	Scheduler sched;
	String s = combobox.getSelectedItem().toString();
	if ("EDF".equals(s)) {
	    sched = new EarliestDeadlineFirst();
	} else if ("RMS".equals(s)) {
	    sched = new RateMonotonicSchedulers();
	} else {
	    sched = new RoundRobinScheduler();
	}
	SchedulerObserver o = new SchedulerObserver();
	sched.setObserver(o);
	sched.setQuantum(quantum);
	sched.put(ptm.processes());
	sched.simulate(time);
	final BufferedImage bi = DrawGantt.paint(o);
	drawPanel.setImage(bi);
	try {
	    ImageIO.write(bi, "png", new File("test.png"));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void init() {
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	spinnerTime.setValue(1000);
	spinnerQuantum.setValue(10);
	setLayout(new BorderLayout());
	add(createControlPanel(), BorderLayout.EAST);
	add(drawPanel = createDrawPanel(), BorderLayout.CENTER);
	setSize(1000, 500);
    }

    private DrawPanel createDrawPanel() {
	return new DrawPanel();
    }

    private Component createControlPanel() {
	JPanel p = new JPanel(new MigLayout());
	addInto(p, "Scheduler", combobox, "", btnUpdate);
	addInto(p, "Time:", spinnerTime, "Quantum", spinnerQuantum);

	p.add(new JSeparator(), new CC().spanX());
	jtable = new JTable(ptm);
	p.add(new JScrollPane(jtable), new CC().spanX());
	p.add(new JSeparator(), new CC().spanX(3));
	p.add(btnAdd = new JButton("Add"), new CC().split(2));
	p.add(btnRemove = new JButton("Remove"), "wrap");
	// p.add(btnOpen);
	// p.add(btnSave);
	spinnerQuantum.addChangeListener(this);
	btnUpdate.addActionListener(this);

	btnAdd.addActionListener(new ActionListener() {
	    private int cntP = 0;
	    private Random r = new Random();

	    @Override
	    public void actionPerformed(ActionEvent e) {
		ptm.add(new Process("p" + (cntP++), r.nextInt(10), 0, 0, r
			.nextInt(10), 1));
	    }
	});

	btnRemove.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		ptm.remove(jtable.getSelectedRow());
	    }
	});

	btnAdd.addActionListener(this);
	btnRemove.addActionListener(this);
	spinnerTime.addChangeListener(this);
	combobox.addActionListener(this);
	return p;
    }

    private void addInto(JPanel p, String s1, JComponent c1, String s2,
	    JComponent c2) {
	JLabel l = new JLabel(s1);
	p.add(l);
	l.setLabelFor(c1);
	p.add(c1);

	l = new JLabel(s2);
	p.add(l);
	l.setLabelFor(c2);
	p.add(c2, "wrap");
    }

    // private void addInto(JPanel p, String string, JComponent comp) {
    // JLabel l = new JLabel(string);
    // p.add(l);
    // l.setLabelFor(comp);
    // p.add(comp, "wrap");
    // }

    public MainFrameState saveState() {
	MainFrameState state = new MainFrameState();
	state.setCurrentSched(combobox.getSelectedItem().toString());
	state.setQuantum((Integer) spinnerQuantum.getValue());
	state.setTime((Integer) spinnerTime.getValue());
	state.setProcesses(ptm.listProcess);
	return state;
    }

    public void loadState(MainFrameState state) {
	spinnerQuantum.setValue(state.getQuantum());
	spinnerTime.setValue(state.getTime());
	ptm.listProcess = state.getProcesses();
	combobox.setSelectedItem(state.getCurrentSched());
	update();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	MainFrame.this.update();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
	MainFrame.this.update();
    }

    class DrawPanel extends JPanel {
	private static final long serialVersionUID = -3621732502581537678L;
	private JLabel lbl = new JLabel();
	private JScrollPane content = new JScrollPane(lbl);

	public DrawPanel() {
	    setLayout(new BorderLayout());
	    setBackground(Color.WHITE);
	    add(content);

	}

	public void setImage(BufferedImage bi) {
	    ImageIcon ii = new ImageIcon(bi);
	    lbl.setIcon(ii);
	}

    }

    JFileChooser fileChooser = new JFileChooser();

    class SaveStateAction extends AbstractAction {
	private static final long serialVersionUID = -6726654921435931275L;

	public SaveStateAction() {
	    putValue(Action.NAME, "Save");
	    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl s"));
	}

	public void actionPerformed(ActionEvent e) {
	    int c = fileChooser.showOpenDialog(MainFrame.this);
	    if (c == JFileChooser.APPROVE_OPTION) {
		try {
		    XMLEncoder enc = new XMLEncoder(new FileOutputStream(
			    fileChooser.getSelectedFile()));
		    enc.writeObject(saveState());
		} catch (FileNotFoundException e1) {
		    JOptionPane.showMessageDialog(MainFrame.this,
			    e1.getMessage(), "I/O Fehler",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }
	};
    }

    class LoadStateAction extends AbstractAction {
	private static final long serialVersionUID = 463566875678210729L;

	public LoadStateAction() {
	    putValue(Action.NAME, "Open");
	    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl O"));
	}

	public void actionPerformed(ActionEvent e) {
	    int c = fileChooser.showOpenDialog(MainFrame.this);
	    if (c == JFileChooser.APPROVE_OPTION) {
		try {
		    XMLDecoder dec = new XMLDecoder(new FileInputStream(
			    fileChooser.getSelectedFile()));
		    MainFrameState s = (MainFrameState) dec.readObject();
		    loadState(s);
		} catch (FileNotFoundException e1) {
		    JOptionPane.showMessageDialog(MainFrame.this,
			    e1.getMessage(), "I/O Fehler",
			    JOptionPane.ERROR_MESSAGE);
		}
	    }
	};
    }

    class ProcessTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private List<Process> listProcess = new ArrayList<Process>();

	public ProcessTableModel() {
	    super();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
	    return true;
	}

	public int toInt(Object a) {
	    try {
		return Integer.parseInt(a.toString());
	    } catch (Exception e) {
		e.printStackTrace();
		return 0;
	    }
	}

	@Override
	public void setValueAt(Object a, int rowIndex, int columnIndex) {
	    Process p = listProcess.get(rowIndex);
	    switch (columnIndex) {
	    case 0:
		p.setName(a.toString());
		break;
	    case 1:
		p.setDuration(toInt(a));
		break;
	    case 2:
		p.setPeriod(toInt(a));
		break;
	    case 3:
		p.setDelay(toInt(a));
		break;
	    case 4:
		p.setDeadline(toInt(a));
		break;
	    case 5:
		p.setPriority(toInt(a));
		break;
	    }
	    MainFrame.this.update();
	}

	public List<Process> processes() {
	    return listProcess;
	}

	public void remove(int selectedRow) {
	    listProcess.remove(selectedRow);
	    fireTableDataChanged();
	}

	public void add(Process process) {
	    listProcess.add(process);
	    fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
	    return listProcess.size();
	}

	@Override
	public int getColumnCount() {
	    return 6;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
	    Process p = listProcess.get(rowIndex);

	    switch (columnIndex) {
	    case 0:
		return p.getName();
	    case 1:
		return p.getDuration();
	    case 2:
		return p.getPeriod();
	    case 3:
		return p.getDelay();
	    case 4:
		return p.getDeadline();
	    case 5:
		return p.getPriority();
	    }
	    return null;
	}

	@Override
	public String getColumnName(int column) {
	    final String[] s = { "Name", "Duration", "Period", "Delay",
		    "Deadline", "Priorität" };
	    return s[column];
	}
    }

    public static void main(String[] args) {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (UnsupportedLookAndFeelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		new MainFrame().setVisible(true);
	    }
	});
    }
}