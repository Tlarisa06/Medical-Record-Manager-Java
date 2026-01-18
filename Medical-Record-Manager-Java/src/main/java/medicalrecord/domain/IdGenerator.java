package medicalrecord.domain;

import java.io.*;

public class IdGenerator {
    private static final String FILE_NAME = "id.txt";
    private static int currentId = 100;

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = reader.readLine();
            if (line != null)
                currentId = Integer.parseInt(line);
        } catch (IOException e) {
            currentId = 100; // default
        }
    }

    public static int generateId() {
        currentId++;
        saveId();
        return currentId;
    }

    private static void saveId() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(String.valueOf(currentId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
