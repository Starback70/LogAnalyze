import java.time.LocalTime;
import java.util.Locale;

public class Stats {
    private boolean inSpan;
    private LocalTime start;
    private LocalTime end;

    private long successes;
    private long fails;
    private final float minAvail;

    Stats(float minAvail) {
        this.minAvail = minAvail;
    }

    public void process(LocalTime time, boolean isFailure) {
        if (isFailure) {
            fails++;
        } else {
            if (inSpan && predictAvail() >= minAvail) {
                inSpan = false;
                System.out.println(this);
            }
            successes++;
        }

        if (inSpan) {
            end = time;
        }
        if (calcAvail() < minAvail && !inSpan) {
            inSpan = true;
            start = end = time;
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s\t%s\t%.2f", start, end, calcAvail());
    }

    public boolean isInSpan() {
        return inSpan;
    }

    private float predictAvail() {
        return 100 * (float) (successes + 1) / (successes + fails + 1);
    }

    private float calcAvail() {
        return 100 * (float) successes / (successes + fails);
    }
}