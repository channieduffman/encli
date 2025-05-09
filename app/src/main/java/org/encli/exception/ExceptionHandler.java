package org.encli.exception;

import picocli.CommandLine;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParameterException;

public class ExceptionHandler implements IExecutionExceptionHandler {
    private static final String ERR = "error: %s: %s";

    private void printErrorMessage(String type, Exception e) {
        System.err.println(String.format(ERR, type, e.getMessage()));
    }

    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, ParseResult parseResult) {
        // TODO: logging
        if (e instanceof UserFileSystemException) {
            printErrorMessage("filesystem", e);
        } else if (e instanceof CryptoException) {
            printErrorMessage("crypto", e);
        } else if (e instanceof UserConfigurationException) {
            printErrorMessage("config", e);
        } else if (e instanceof ParameterException) {
            printErrorMessage("args", e);
        } else { // the exception was unanticipated
            printErrorMessage("unknown", e);
        }
        return commandLine.getCommandSpec().exitCodeOnExecutionException();
    }
}
