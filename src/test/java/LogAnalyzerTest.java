import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

class LogAnalyzerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setStdout() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testNotGettingExceptionOnBadLogLine() {
        BufferedReader input = new BufferedReader(new StringReader("it's not valid log line"));
        LogAnalyzer analyzer = new LogAnalyzer(input, 50f, 60f);
        Assertions.assertDoesNotThrow(analyzer::parse);
    }

    @Test
    void testParseLog() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(
                getClass().getResource("test.log").getFile()
        ));

        LogAnalyzer analyzer = new LogAnalyzer(input, 50f, 60f);
        analyzer.parse();
        Assertions.assertEquals(
                "00:00:04\t00:00:04\t50.00\r\n"
                        + "00:00:09\t00:00:09\t55.56\r\n",
                outContent.toString());
    }

    @Test
    void testParseLogWithoutFailureTolerance() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(
                getClass().getResource("test.log").getFile()
        ));

        LogAnalyzer analyzer = new LogAnalyzer(input, 50f, 100f);
        analyzer.parse();
        Assertions.assertEquals(
                "00:00:03\t00:00:09\t55.56\r\n",
                outContent.toString());
    }

    @Test
    void testAllSuccessRsReturnsNothing() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(
                getClass().getResource("test.log").getFile()));

        LogAnalyzer analyzer = new LogAnalyzer(input, 50f, 0f);
        analyzer.parse();
        Assertions.assertEquals(
                "",
                outContent.toString());
    }

    @Test
    void testAllFailedRsReturnsOneSpan() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(
                getClass().getResource("test.log").getFile()
        ));

        LogAnalyzer analyzer = new LogAnalyzer(input, 0f, 100f);
        analyzer.parse();
        Assertions.assertEquals(
                "00:00:01\t00:00:09\t0.00\r\n",
                outContent.toString());
    }

    @AfterEach
    void unsetStdout() {
        System.setOut(originalOut);
    }
}
