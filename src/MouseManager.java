import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

public class MouseManager implements MouseListener{

	public GUI gui;
	public Point mousePos;
	public Point clicked = null;
	
	public MouseManager(GUI gui) {
		this.gui = gui;
	}

	public void tick() {
		int mouse_x=MouseInfo.getPointerInfo().getLocation().x-gui.display.getCanvas().getLocationOnScreen().x;
		int mouse_y=MouseInfo.getPointerInfo().getLocation().y-gui.display.getCanvas().getLocationOnScreen().y;
		mousePos = new Point(mouse_x, mouse_y);
//		System.out.println(mousePos); 
	}
	
	
	public void mouseClicked(MouseEvent e) {
		
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		if(gui.mode == "poly") {
			gui.polygons.get(gui.polygons.size()-1).addPoint((int)mousePos.getX(), (int)mousePos.getY());
		}
		if(gui.mode == "line") {
			if(gui.lines.get(gui.lines.size()-1)[0] == new Point(-1,-1))
				gui.lines.get(gui.lines.size()-1)[0] = mousePos;
			else if(gui.lines.get(gui.lines.size()-1)[1] == new Point(-1,-1))
				gui.lines.get(gui.lines.size()-1)[1] = mousePos;
		}
	}

}
