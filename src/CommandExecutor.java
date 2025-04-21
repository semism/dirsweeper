import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class CommandExecutor {

    public static void execute(List<BroomCommand> commands) {
        for (BroomCommand command : commands) {
            try {
                switch (command.getType()) {
                    case CREATE:
                        createDirectory(command);
                        break;
                    case RENAME:
                        rename(command);
                        break;
                    case MOVE:
                        move(command);
                        break;
                    case DELETE:
                        delete(command);
                        break;
                }
            } catch (IOException e) {
                System.err.println("Error executing command: " + command);
                e.printStackTrace();
            }
        }
    }

    private static void createDirectory(BroomCommand command) throws IOException {
        Path path = Paths.get(command.getSource());
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            System.out.println("Created: " + path);
        } else {
            System.out.println("Already exists: " + path);
        }
    }

    private static void rename(BroomCommand command) throws IOException {
        if (command.isRegex()) {
            regexRenameOrMove(command, false);
        } else {
            Path source = Paths.get(command.getSource());
            Path dest = Paths.get(command.getDestination());
            if (Files.exists(source)) {
                Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Renamed: " + source + " → " + dest);
            } else {
                System.err.println("Source not found: " + source);
            }
        }
    }

    private static void move(BroomCommand command) throws IOException {
        if (command.isRegex()) {
            regexRenameOrMove(command, true);
        } else {
            Path source = Paths.get(command.getSource());
            Path dest = Paths.get(command.getDestination());
            if (Files.exists(source)) {
                Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved: " + source + " → " + dest);
            } else {
                System.err.println("Source not found: " + source);
            }
        }
    }

    private static void regexRenameOrMove(BroomCommand command, boolean isMove) throws IOException {
        Path root = Paths.get(".");
        String pattern = command.getSource();
        String replacement = command.getDestination();

        try (Stream<Path> stream = Files.walk(root)) {
            stream
                    .sorted(Comparator.comparingInt(p -> p.getNameCount())) // ensure top-down
                    .forEach(path -> {
                        String relPath = root.relativize(path).toString().replace("\\", "/");
                        if (relPath.matches(pattern)) {
                            String newRelPath = relPath.replaceAll(pattern, replacement);
                            Path newPath = root.resolve(newRelPath);
                            try {
                                Files.createDirectories(newPath.getParent());
                                Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
                                System.out.println((isMove ? "Moved (regex): " : "Renamed (regex): ") + relPath + " → " + newRelPath);
                            } catch (IOException e) {
                                System.err.println("Failed to move/rename: " + relPath + " → " + newRelPath);
                            }
                        }
                    });
        }
    }

    private static void delete(BroomCommand command) throws IOException {
        if (command.isRegex()) {
            Path root = Paths.get(".");
            String pattern = command.getSource();

            try (Stream<Path> stream = Files.walk(root)) {
                stream
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            String relPath = root.relativize(path).toString().replace("\\", "/");
                            if (relPath.matches(pattern)) {
                                try {
                                    Files.deleteIfExists(path);
                                    System.out.println("Deleted (regex): " + relPath);
                                } catch (IOException e) {
                                    System.err.println("Failed to delete: " + relPath);
                                }
                            }
                        });
            }
        } else {
            Path path = Paths.get(command.getSource());
            if (Files.exists(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Deleted: " + path);
            } else {
                System.err.println("Not found: " + path);
            }
        }
    }
}
