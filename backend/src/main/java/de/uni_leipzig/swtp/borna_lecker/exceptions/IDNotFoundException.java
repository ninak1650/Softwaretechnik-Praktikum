package de.uni_leipzig.swtp.borna_lecker.exceptions;

public class IDNotFoundException extends RuntimeException {

    // Konstruktor, der die Nachricht übergibt
    public IDNotFoundException(String message) {
        super(message); // übergebe die Nachricht an die RuntimeException
    }
}
