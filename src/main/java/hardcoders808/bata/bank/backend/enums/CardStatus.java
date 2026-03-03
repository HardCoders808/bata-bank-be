package hardcoders808.bata.bank.backend.enums;

/**
 * Represents the status of a bank card.
 */
public enum CardStatus {

    /**
     * The card is active and can be used for transactions.
     */
    ACTIVE,

    /**
     * The card is temporarily frozen by the user or bank.
     */
    FROZEN,

    /**
     * The card is permanently blocked (e.g., lost or stolen).
     */
    BLOCKED,

    /**
     * The card has passed its expiration date.
     */
    EXPIRED,

    /**
     * The card is inactive and has not yet been activated.
     */
    INACTIVE
}
