package weigla.sched.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Test {
    public static void main(String[] args) throws IOException {
	SchedulerObserver o = new SchedulerObserver();
	RoundRobinScheduler rrs = new RoundRobinScheduler();
	rrs.setObserver(o);

	Process p1 = new Process("p1", 10, 0, 0, 0, 1);
	Process p2 = new Process("p2", 10, 0, 0, 0, 1);
	Process p3 = new Process("p3", 10, 0, 0, 0, 1);

	rrs.put(p1, p2, p3);
	rrs.simulate(30);
	BufferedImage bi = DrawGantt.paint(o);
	ImageIO.write(bi, "png", new File("test.png"));
    }
}
