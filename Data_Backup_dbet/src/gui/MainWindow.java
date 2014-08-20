package gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JPanel{
	public int width = 0;
	public int height = 0;
	
	
	
	public MainWindow(int x, int y){
		width = x;
		height = y;
		creation();
	}



	private void creation() {
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
		
		//Set height and width of the panel
		this.setSize(width, height);
	}

}
