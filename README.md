# encli

**encli** is a command-line file encryption tool that implements AES encryption. I wrote it to get practice with Java, explore the [picocli](https://picocli.info) library, and introduce myself to cryptography. It is a work in progress.

---

## Usage

    encli [-o=<output>] (-e | -d) <inputs>...
        <inputs>...     a list of input file paths (to be encrypted/decrypted)
        -d              decrypt mode
        -e              encrypt mode
        -o=<output>     the output file path (can only be used with a singular input)

---

## Examples

    encli -e text.txt -o encrypted.enc

This encrypts the file `text.txt` into the file `encrypted.enc`. The following are equivalent:

---

    encli -e text/*

This encrypts all files in the `text/` directory, automatically naming the output files by appending an extension. If such a file already exists, it is not overwritten. 

---

    encli -d encrypted.enc

This will decrypt the file `encrypted.enc` and name it with an appended extension.

---

