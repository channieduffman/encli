package org.encli.exception;

import picocli.CommandLine;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.IExecutionExceptionHandler;

public class ExceptionHandler implements IExecutionExceptionHandler {
    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, ParseResult parseResult) {
        if (e instanceof UserFileSystemException) {
            System.err.println("FILESYSTEM ERROR: " + e.getMessage());
        } else if (e instanceof CryptoException) {
            System.err.println("CRYPTOGRAPHIC ERROR: " + e.getMessage());
        } else if (e instanceof UserConfigurationException) {
            System.err.println("CONFIGURATION ERROR: " + e.getMessage());
        } else { // the exception was unanticipated
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
        }
        return commandLine.getCommandSpec().exitCodeOnExecutionException();
    }
}
