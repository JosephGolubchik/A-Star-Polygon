import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class GUI implements Runnable {

	public Display display;
	private int width, height;

	private boolean running = false;
	private Thread thread;

	private BufferStrategy bs;
	private Graphics g;
	
	public ArrayList<Polygon> polygons;
	public ArrayList<Point[]> lines;
	
	public String mode = null;

	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;


	public GUI(int width, int height){
		keyManager = new KeyManager();
		mouseManager = new MouseManager(this);
		this.width = width;
		this.height = height;
		polygons = new ArrayList<Polygon>();
		lines = new ArrayList<Point[]>();
	}

	private void init(){

		display = new Display("A*", width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getCanvas().addMouseListener(mouseManager);

		JMenuBar menuBar = new JMenuBar();

		JButton polygonBtn = new JButton("Polygon");
		menuBar.add(polygonBtn);

		polygonBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				polygons.add(new Polygon());
				mode = "poly";
			}         
		}); 
		
		JButton lineBtn = new JButton("Line");
		menuBar.add(lineBtn);

		lineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Point[] line = new Point[2];
				line[0] = new Point(-1,-1);
				line[1] = new Point(-1,-1);
				lines.add(line);
				mode = "line";
			}         
		}); 
		
		JButton runBtn = new JButton("Run");
		menuBar.add(runBtn);

		runBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}         
		}); 

		display.getFrame().setJMenuBar(menuBar);





	}

	public void setWidth(int width) {
		this.width = width;}

	public void setHeight(int height) {
		this.height = height;}

	private void tick(){
		keyManager.tick();
		mouseManager.tick();
	}

	private void move() {
		if(keyManager.down) {}
		if(keyManager.right) {}

		if(keyManager.up) {}

		if(keyManager.left) {}

	}

	private void render(){
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null){
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		//Clear Screen
		g.clearRect(0, 0, width, height);
		//Draw Here!

		g.fillRect(0, 0, width, height);

		g.setColor(Color.white);

		drawPolygons(g);
		drawLines(g);

		//End Drawing!
		bs.show();
		g.dispose();
	}

	private void drawPolygons(Graphics g) {
		Iterator<Polygon> it = polygons.iterator();
		while(it.hasNext()) {
			Polygon p = it.next();
			g.setColor(new Color(100,100,100));
			g.fillPolygon(p);
			g.setColor(Color.white);
			g.drawPolygon(p);
			int pointSize = 8;
			for (int i = 0; i < p.npoints; i++) {
				g.fillOval(p.xpoints[i] - pointSize/2, p.ypoints[i] - pointSize/2, pointSize, pointSize);
			}
		}
	}
	
	private void drawLines(Graphics g) {
		Iterator<Point[]> it = lines.iterator();
		while(it.hasNext()) {
			Point[] p = it.next();
			g.setColor(Color.white);
			if((int)p[1].getX() != -1) {
				g.drawLine((int)p[0].getX(), (int)p[0].getY(), (int)p[1].getX(), (int)p[1].getY());
				int pointSize = 8;
				g.fillOval((int)p[0].getX() - pointSize/2, (int)p[0].getY() - pointSize/2, pointSize, pointSize);
				g.fillOval((int)p[1].getX() - pointSize/2, (int)p[1].getY() - pointSize/2, pointSize, pointSize);
			}
			else {
				int pointSize = 8;
				g.fillOval((int)p[0].getX() - pointSize/2, (int)p[0].getY() - pointSize/2, pointSize, pointSize);
			}
		}
	}
	
	private boolean lineIntersectsPoly(Polygon pol, Point p0, Point p1) {
		ArrayList<Point> points = divideLineToPoints(p0, p1, 20);
		Iterator<Point> it = points.iterator();
		while(it.hasNext()) {
			Point curr = it.next();
			if(pol.contains(curr)) return true;
		}
		return false;
	}
	
	private ArrayList<Point> divideLineToPoints(Point p0, Point p1, int seg_len) {
		ArrayList<Point> points = new ArrayList<Point>();
		if(p0.getX() > p1.getX()) {
			Point temp = p0;
			p0 = p1;
			p1 = temp;
		}
		double a = (p1.getY() - p0.getY())/(p1.getX() - p0.getX());
		double dx = Math.abs(p1.getX()-p0.getX());
		int numOfPoints = (int) Math.round((dx * Math.sqrt(1 + a*a))/seg_len);
		points.add(p0);
		for (int x = (int) p0.getX(); x < p1.getX(); x += dx/numOfPoints) {
			points.add(new Point(x, (int) lineValueAtX(p0, p1, x)));
		}
		if(points.get(points.size()-1) != p1) {
			points.add(p1);
		}
		return points;
		
	}
	
	private double lineValueAtX(Point p0, Point p1, double x) {
		double a = (p1.getY() - p0.getY())/(p1.getX() - p0.getX());
		double b = p0.getY() - a * p0.getX();
		return a * x + b;
	}
		
	private void createLines() {
		
	}
	
	public void run(){

		init();

		int fps = 60;
		double timePerTick = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0;

		while(running){
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;

			if(delta >= 1){
				tick();
				render();
				ticks++;
				delta--;
			}

			if(timer >= 1000000000){
				//				System.out.println("FPS: " + ticks);
				ticks = 0;
				timer = 0;
			}
		}

		stop();

	}


	public KeyManager getKeyManager() {
		return keyManager;
	}
	
	public MouseManager getMouseManager() {
		return mouseManager;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public synchronized void start(){
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop(){
		if(!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void drawStringCentered(String s, Color color, int x, int y, int fontSize) {
		g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, fontSize)); 
		g.setColor(color);

		int text_width = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		int text_height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();

		g.drawString(s, x - text_width/2, y - text_height/2);
	}



}

