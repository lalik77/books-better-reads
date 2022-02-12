package com.mami.betterreads.exception;

import java.time.format.DateTimeParseException;

public class BetterReadsException extends DateTimeParseException {

  public BetterReadsException(String message, CharSequence parsedData, int errorIndex) {
    super(message, parsedData, errorIndex);
  }
}
