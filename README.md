# encli

**encli** is a command-line file encryption tool that implements AES encryption. I wrote it to practice with Java, explore the [picocli](https://picocli.info) library, and introduce myself to cryptography.

---

## Usage

    encli --env=<file> [-o=<output>] (-e | -d) <inputs>...
        <inputs>...     a list of input file paths (to be encrypted/decrypted)
        -d              decrypt mode
        -e              encrypt mode
        --env=<file>    path to configuration (.env) file
        -o=<output>     the output file path (can only be used with a singular input)

While picocli handles argument parsing, invoking the app in this way is achieved locally with a little script magic. For instance, `/usr/local/bin`:

    #!/bin/bash
    java -jar /path/to/executable/app.jar --env=/path/to/config/.env "$@"

---

## Examples

    encli -e text.txt -o encrypted.enc

This encrypts the file `text.txt` into the file `encrypted.enc`. 

---

    encli -e text/*

This encrypts all files in the `text/` directory, automatically naming the output files by appending an extension. If such a file already exists, it is not overwritten. 

---

    encli -d encrypted.enc

This will decrypt the file `encrypted.enc` and name it with an appended extension.

---

    find . -type f -name "*.java" | xargs -I{} encli -e "{}"

This recursively encrypts all Java source files in or below the current directory.

---