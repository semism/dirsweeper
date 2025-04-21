import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BroomParser {

    public static List<BroomCommand> parseFile(String path) throws IOException {
        List<BroomCommand> commandList = new ArrayList<>();
        List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(path));

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = line.split("\\s+");
            if (parts.length == 0) continue;

            String cmd = parts[0];
            boolean isRegex = false;
            String[] args;

            if (parts.length > 1 && (parts[1].equals("-r") || parts[1].equals("--regex"))) {
                isRegex = true;
                args = Arrays.copyOfRange(parts, 2, parts.length);
            } else {
                args = Arrays.copyOfRange(parts, 1, parts.length);
            }

            switch (cmd) {
                case "new":
                case "n":
                    if (args.length >= 1) {
                        commandList.add(new BroomCommand(CommandType.CREATE, args[0], null, false));
                    }
                    break;
                case "rename":
                case "rn":
                    if (args.length >= 2) {
                        commandList.add(new BroomCommand(CommandType.RENAME, args[0], args[1], isRegex));
                    }
                    break;
                case "move":
                case "m":
                    if (args.length >= 2) {
                        commandList.add(new BroomCommand(CommandType.MOVE, args[0], args[1], isRegex));
                    }
                    break;
                case "delete":
                case "d":
                    if (args.length >= 1) {
                        commandList.add(new BroomCommand(CommandType.DELETE, args[0], null, isRegex));
                    }
                    break;
            }
        }

        return commandList;
    }
}
