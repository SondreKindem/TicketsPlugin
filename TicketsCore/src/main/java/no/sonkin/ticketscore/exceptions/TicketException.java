package no.sonkin.ticketscore.exceptions;

/**
 * General exception for when something goes wrong during ticket handling, and we want to send a message back to the user
 * instead of shutting the plugin down.
 */
public class TicketException extends Exception {
    public TicketException(String errorMessage) {
        super(errorMessage);
    }
}
