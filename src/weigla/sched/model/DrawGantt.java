package weigla.sched.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

/**
 * Functions for drawing the Gantt diagram.
 * @author weigla
 */

public class DrawGantt {
    private static final int ROW_DELTA = 50;
    private static final int MARGIN = 30;
    private static final int STARTX = 100;
    private static final int STARTY = 50;
    private static final int TICKS = 10;
    private static final int BOX_HEIGHT = 10;

    /**
     * paint the gantt diagram
     * @param so
     * @return
     */
    public static BufferedImage paint(SchedulerObserver so) {
	BufferedImage bi = new BufferedImage(so.maxTime() * TICKS + STARTX
		+ MARGIN, so.countProcesses() * ROW_DELTA + STARTY + MARGIN,
		BufferedImage.TYPE_INT_ARGB);
	Graphics2D g = bi.createGraphics();

	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);

	Map<Process, List<int[]>> map = so.map;
	Multimap<Process, Integer> deadlines = so.deadlines;
	Multimap<Process, Integer> activates = so.activates;
	List<Process> pl = new LinkedList<Process>(map.keySet());

	Collections.sort(pl, new Comparator<Process>() {
	    @Override
	    public int compare(Process o1, Process o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});

	g.setColor(Color.WHITE);
	g.fillRect(0, 0, bi.getWidth(), bi.getHeight());

	g.setColor(Color.BLACK);

	// x - ticks
	for (int x = STARTX; x < bi.getWidth(); x += TICKS) {
	    g.setColor(Color.LIGHT_GRAY);
	    g.drawLine(x, STARTY, x, bi.getHeight());
	    g.setColor(Color.BLACK);
	    g.drawLine(x, STARTY - 5, x, STARTY);
	}

	g.drawLine(STARTX, STARTY, STARTX, bi.getHeight());
	g.drawLine(STARTX, STARTY, bi.getWidth(), STARTY);

	int y = STARTY + ROW_DELTA / 2;
	int x = 5;
	for (Process p : pl) {
	    g.drawString(p.getName(), x, y);
	    final int ry = y - 5;

	    // times
	    for (int[] l : map.get(p)) {
		int s = l[0]; // start time
		int e = l[1] - l[0]; // duration

		final int rx = STARTX + TICKS * s;
		final int width = e * TICKS;

		g.drawRect(rx, ry, width, BOX_HEIGHT);
		if (l[3] != 0) { //deadline not taken
		    final int dx = STARTX + TICKS * l[3];
		    g.drawRect(dx, y - 5, (rx + width) - dx, BOX_HEIGHT);
		    schaffrur(g, dx, y - 5, (rx + width) - dx, BOX_HEIGHT,
			    Math.toRadians(45), 5, new BasicStroke(0.3f));
		}

		if (l[2] == 1) { // task ended
		    g.fillOval(rx + width - 3, y - 7, 8, 8);
		}

	    }

	    g.setColor(Color.green);
	    for (Integer i : activates.get(p)) {
		g.fillRect(STARTX - 1 + TICKS * i, ry, 3, BOX_HEIGHT);
	    }

	    g.setColor(Color.red);
	    for (Integer i : deadlines.get(p)) {
		g.fillRect(STARTX - 1 + TICKS * i, ry, 3, BOX_HEIGHT);
	    }
	    g.setColor(Color.black);
	    y += ROW_DELTA;
	}
	return bi;
    }

    /**
     * 
     * @param g
     * @param x
     * @param y
     * @param width
     * @param height
     * @param angle
     * @param whitespace
     * @param s
     */
    public static void schaffrur(Graphics2D g, double x, double y,
	    double width, double height, double angle, double whitespace,
	    Stroke s) {
	g = (Graphics2D) g.create((int) x, (int) y, (int) width, (int) height);
	g.setColor(Color.RED);
	g.setStroke(s);
	double xmove = height * Math.cos(angle);
	for (double xc = -whitespace; xc <= width + whitespace; xc += whitespace) {
	    g.draw(new Line2D.Double(xc - xmove, 0, xc, height));
	}
    }
}
