package gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public int width = 750;
    public int height = 900;

    public boolean showMe = true;

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
        this.setVisible(showMe);
        this.setBackground(Color.black);
    }

}
