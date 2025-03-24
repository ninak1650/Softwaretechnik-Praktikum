package de.uni_leipzig.swtp.borna_lecker.exceptions;

public class NoOrderFoundExcpetion extends RuntimeException {

    // Konstruktor, der di e Nachricht übergibt
    public NoOrderFoundExcpetion(String message) {
        super(message); // übergebe die Nachricht an die RuntimeException
    }
}
