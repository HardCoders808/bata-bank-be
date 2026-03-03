package hardcoders808.bata.bank.backend.enums;

/**
 * Represents the current status of a transaction.
 */
public enum TransactionStatus {

    /**
     * The transaction is waiting to be processed.
     */
    PENDING,

    /**
     * The transaction completed successfully.
     */
    SUCCESS,

    /**
     * The transaction failed to complete.
     */
    FAILED,

    /**
     * The transaction was reversed.
     */
    REVERSED
}
