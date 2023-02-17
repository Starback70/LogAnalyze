import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogAnalyzer {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd/MM/yyyy:H:m:s");
    private final BufferedReader reader;
    private final float maxTime;
    private final float availability;

    LogAnalyzer(BufferedReader reader, Float maxTime, Float availability) {
        this.reader = reader;
        this.maxTime = maxTime;
        this.availability = availability;
    }

    public void parse() throws IOException {
        long lineNumber = 0L;
        try {
            String s;
            Stats stats = new Stats(availability);
            while ((s = reader.readLine()) != null) {
                lineNumber++;
                boolean isFault = isFault(s);
                LocalTime time = getTime(s);
                stats.process(time, isFault);
            }
            if (stats.isInSpan()) {
                System.out.println(stats);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Wrong format at line " + lineNumber);
        }
    }

    private LocalTime getTime(String line) throws IllegalArgumentException {
        try {
            String[] s = line.split(" ");
            return LocalDateTime.parse(s[3].replace('[', ' '), formatter).toLocalTime();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isFault(String line) throws IllegalArgumentException {
        try {
            String[] s = line.split(" ");
            int code = Integer.parseInt(s[8]);
            float time = Float.parseFloat(s[10]);
            return code == 500 || time > maxTime;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
}
