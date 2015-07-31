package hostonline;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Host host = new Host();
        final File file = new File("/var/log/authd.log");
        host.analyse(file);
    }

}
