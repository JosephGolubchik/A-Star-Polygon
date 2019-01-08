import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
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
	public ArrayList<Line> lines;

	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;


	public GUI(int width, int height){
		keyManager = new KeyManager();
		mouseManager = new MouseManager(this);
		this.width = width;
		this.height = height;
		polygons = new ArrayList<Polygon>();
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
//		System.out.println(p.npoints);

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

