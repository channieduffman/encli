package org.encli.cli;

// local local packages
import org.encli.service.*;

// java pacakages
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;
import javax.crypto.SecretKey;

// dotenv-java
import io.github.cdimascio.dotenv.Dotenv;

// picocli
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

@Command(name = "encli")
public class CLI implements Callable<Integer> {
    /* Constants */
    private static final String PARAMETER_EXCEPTION = "Too many arguments: Only one input when '-o' specified.";

    /* picocli Annotations */

    @Spec
    CommandSpec spec;

    @ArgGroup(exclusive = true, multiplicity = "1")
    Mode mode;

    static class Mode {
        @Option(names = "-e", description = "encrypt mode")
        static boolean encrypt;
        @Option(names = "-d", description = "decrypt mode")
        static boolean decrypt;
    }

    @Option(names = "-o", description = "(optional) the output file path")
    Path output;

    @Parameters(arity = "1..*", description = "a list of input file paths (to be encrypted/decrypted)")
    List<Path> inputs;

    @Override
    public Integer call() throws ParameterException {
        try {
            Dotenv dotenv = Dotenv.load();

            // Fetch key; create one if needed
            SecretKey key = KeyStoreService.getKey(Paths.get(dotenv.get("KEYSTORE_LOCATION")), dotenv.get("KEY_ALIAS"),
                    dotenv.get("KEYSTORE_PASSWORD").toCharArray());

            // Can only encrypt/decrypt one file at a time if '-o' specified
            if (output != null && inputs.size() > 1)
                throw new ParameterException(spec.commandLine(), PARAMETER_EXCEPTION);

            if (Mode.encrypt) {
                for (Path input : inputs)
                    EncryptionService.encrypt(input, output, key);
            } else {
                for (Path input : inputs)
                    EncryptionService.decrypt(input, output, key);
            }
        } catch (FileAlreadyExistsException e) {
            System.err.println(e.getMessage());
        }

        return 0;
    }
}
