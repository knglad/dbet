package gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public int width = 750;
    public int height = 900;

    public boolean showMe = true;
	public LogWindow logWindow;

	public MainWindow(LogWindow lw) {
		creation();
		logWindow = lw;
		this.add(logWindow);
	}



	private void creation() {
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
		
		//Set height and width of the panel
		this.setSize(width, height);
        this.setVisible(showMe);
		this.setBackground(Color.gray);
	}

}
