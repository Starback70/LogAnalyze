import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        float maxTime;
        float availability;

        Options options = new Options();
        options.addOption(Option.builder("t").hasArg().argName("ms")
                .desc("max response time").required().build());
        options.addOption(Option.builder("u").hasArg().argName("N")
                .desc("percentage of succesful responses").required().build());

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            maxTime = Float.parseFloat(cmd.getOptionValue('t'));
            availability = Float.parseFloat(cmd.getOptionValue('u'));
            if (maxTime <= 0 || availability < 0 || availability > 100) {
                throw new IllegalArgumentException();
            }

            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            LogAnalyzer analyzer = new LogAnalyzer(stdin, maxTime, availability);
            analyzer.parse();
        } catch (ParseException | IllegalArgumentException e) {
            System.out.println("Wrong args.");
            new HelpFormatter().printHelp("java -jar farpost.jar", options);
            System.exit(2);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}