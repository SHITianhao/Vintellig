package ssh;

public class ActionController {
	private AlumerLED led = null;
	
	public ActionController(){
		led = new AlumerLED();
	}
	
	public void controllerON_OFF(){
		try {
			led.alumer();
			Thread.sleep(5000);
			led.eteindre();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
