package ssh;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AlumerLED {
	static final String GPIO_CH = "14";

	static final String GPIO_OUT = "out";
	static final String GPIO_ON = "1";
	static final String GPIO_OFF = "0";

	public AlumerLED()
	{
		configuration();
	}
	
	private void configuration(){
		try {

			/*** Init GPIO port for output ***/

			// Open file handles to GPIO port unexport and export controls
			FileWriter unexportFile = new FileWriter("/sys/class/gpio/unexport");
			FileWriter exportFile = new FileWriter("/sys/class/gpio/export");

			// Reset the port,if needed

			File exportFileCheck = new File("/sys/class/gpio/gpio" + GPIO_CH);

			
			if (exportFileCheck.exists()) {
				
				unexportFile.write(GPIO_CH);
				unexportFile.flush();
				System.out.println("file exits");
			}

			// Set the port for use
			exportFile.write(GPIO_CH);
			exportFile.flush();

			
			// Open file handle to port input/output control
			FileWriter directionFileWriter = new FileWriter("/sys/class/gpio/gpio"+ GPIO_CH + "/direction");

			// Set port for output
			directionFileWriter.write(GPIO_OUT);
			directionFileWriter.flush();

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	/*** Send commands to GPIO port ***/
	public void alumer(){
		try {
			// Create GPIO Writer
			FileWriter command = new FileWriter("/sys/class/gpio/gpio"+ GPIO_CH + "/value");
			command.write(GPIO_ON);
			command.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void eteindre(){
		try {
			FileWriter command = new FileWriter("/sys/class/gpio/gpio"	+ GPIO_CH + "/value");
			command.write(GPIO_OFF);
			command.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
