package blutooth;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import java.io.*;

public class Server{
	static final String serverUUID = "6871790ebe49453ea14549a7a1456326";
	
	public static void main(String[] args) throws IOException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();

		localDevice.setDiscoverable(DiscoveryAgent.GIAC); // Advertising the service

		StreamConnectionNotifier serverConnection = (StreamConnectionNotifier) Connector.open("btspp://localhost:"
                + serverUUID + ";name=ObexExample");

		StreamConnection connection = serverConnection.acceptAndOpen(); // Wait until client connects
		//=== At this point, two devices should be connected ===//
		DataInputStream dis = connection.openDataInputStream();

		char c;
		while (true) {
		    c = dis.readChar();
		    if (c == 'x')
		        break;
		}

		connection.close();
    }
}
