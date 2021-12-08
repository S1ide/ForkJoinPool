import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class Helper {
    public static void write(String filename, Set<String> strings) throws IOException {
        FileWriter fileOutputStream = new FileWriter(filename, false);
        strings.forEach(s -> {
            try {
                fileOutputStream.write("\t".repeat(getCount(s, '/') - 3) + s + "\n");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        fileOutputStream.close();
    }

    public static int getCount(String s, char ch) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return s.lastIndexOf(ch) + 1 == s.length() ? count : count + 1;
    }
}
