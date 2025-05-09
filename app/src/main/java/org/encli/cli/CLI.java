package org.encli.cli;

import org.encli.exception.CryptoException;
import org.encli.exception.UserConfigurationException;
import org.encli.exception.UserFileSystemException;
import org.encli.service.*;

import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;
import javax.crypto.SecretKey;

import io.github.cdimascio.dotenv.Dotenv;

import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

@Command(name = "encli")
public class CLI implements Callable<Integer> {
    /* picocli Annotations */

    @Spec
    private CommandSpec spec;

    @ArgGroup(exclusive = true, multiplicity = "1")
    private Mode mode;

    private static class Mode {
        @Option(names = "-e", description = "encrypt mode")
        static boolean encrypt;
        @Option(names = "-d", description = "decrypt mode")
        static boolean decrypt;
    }

    @Option(names = "-o", description = "(optional) the output file path")
    private Path output;

    @Option(names = "--env", required = true, description = "path to .env file")
    private Path env;

    @Parameters(arity = "1..*", description = "a list of input file paths (to be encrypted/decrypted)")
    private List<Path> inputs;

    @Override
    public Integer call()
            throws ParameterException, CryptoException, UserConfigurationException, UserFileSystemException {
        try {
            Path parent = env.getParent();
            Dotenv dotenv = Dotenv.configure().directory(parent.toString()).load();

            // Fetch key; create one if needed
            SecretKey key = KeyStoreService.getKey(Paths.get(dotenv.get("KEYSTORE_LOCATION")), dotenv.get("KEY_ALIAS"),
                    dotenv.get("KEYSTORE_PASSWORD").toCharArray());

            // Can only encrypt/decrypt one file at a time if '-o' specified
            if (output != null && inputs.size() > 1)
                throw new ParameterException(spec.commandLine(), "Cannot specify output file name for multiple inputs");

            if (Mode.encrypt) {
                for (Path input : inputs)
                    EncryptionService.encrypt(input, output, key);
            } else {
                for (Path input : inputs)
                    EncryptionService.decrypt(input, output, key);
            }
        } catch (CryptoException e) {
            throw e;
        } catch (UserConfigurationException e) {
            throw e;
        } catch (UserFileSystemException e) {
            throw e;
        }

        return 0;
    }
}
