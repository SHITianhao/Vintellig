package hostonline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Host {
    // StringBuffer result;
    // Process process;
    // String intoString;
    // String whoString;
    // String username;
    long lastTimeFileSize = 0;

    public Host() {
        // result = new StringBuffer();
        // intoString = "cd /var/log";
        // whoString = "who";
        // username = "";
    }

    @SuppressWarnings("resource")
    public void analyse(File logFile) throws IOException {
        // process = Runtime.getRuntime().exec(intoString);
        // process.waitFor();
        // process = Runtime.getRuntime().exec(whoString);
        // process.waitFor();
        final RandomAccessFile randomAccessFile = new RandomAccessFile(logFile,
                "r");
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    randomAccessFile.seek(lastTimeFileSize);
                    String tmp = "";
                    while ((tmp = randomAccessFile.readLine()) != null) {
                        System.out.println(new String(tmp.getBytes("ISO8859-1")));
                    }
                    lastTimeFileSize = randomAccessFile.length();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    // public String getUsername(String line) {
    // int count = 0;
    // String[] parts = line.split("(");
    // String name = "";
    // if (parts.length == 1) {
    // name = "user" + count;
    // count++;
    // } else {
    // name = parts[1].split(")")[0];
    // }
    // return name;
    // }

    public File getHostFile() {
        // File outfile = new File("Desktop/hostnames.txt");
        // try {
        // String line = "";
        // BufferedReader reader = new BufferedReader(new InputStreamReader(
        // process.getInputStream()));
        // while ((line = reader.readLine()) != null) {
        // result.append(getUsername(line) + "\n");
        // }
        //
        // if (!outfile.exists()) {
        // outfile.createNewFile();
        // }
        // BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
        // out.write(result.toString());
        // out.flush();
        // out.close();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // return outfile;
        return new File("/var/log/authd.log");
    }
}
