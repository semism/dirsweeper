# dirsweeper

**dirsweeper** is a command-line tool for managing and transforming directory structures using `.broom` instruction files. It provides a simple, declarative syntax for creating, renaming, moving, and deleting files or directories — including support for regular expressions.

---

## Installation

### Build from source

```
git clone https://github.com/yourusername/dirsweeper.git
cd dirsweeper
./gradlew jar
```

### Or download the release

Unzip the release archive and add the executable script to your system path.

```
unzip dirsweeper-cli.zip
chmod +x dirsweeper
```

---

## Usage

```
dirsweeper init [filename]
dirsweeper apply file.broom [-y] [-keep]
```

### Flags

- `-y` : Apply changes (default is preview only)
- `-keep` : Keep the `.broom` file after applying

---

## .broom Syntax

Each instruction is written on a new line. You can use either long or short command names:

| Command    | Alias | Description                  |
|------------|-------|------------------------------|
| `new`      | `n`   | Create a directory           |
| `rename`   | `rn`  | Rename files or directories  |
| `move`     | `m`   | Move files or directories    |
| `delete`   | `d`   | Delete files or directories  |

Use `-r` for regex-based commands.

---

### Example

```
# Create folders
new src/
new dist/

# Rename files with a pattern
rename -r ^old_(.*)$ new_$1

# Move all logs to archive
move -r logs/(.*\.log)$ archive/$1

# Delete all contents of the temp folder
delete -r ^temp/.*
```

---

## Preview

By default, `apply` previews the changes it would make:

```
dirsweeper apply structure.broom
```

To actually make the changes:

```
dirsweeper apply structure.broom -y
```

To apply and keep the `.broom` file:

```
dirsweeper apply structure.broom -y -keep
```

---

## Requirements

- Java 8+

---

## License

MIT
