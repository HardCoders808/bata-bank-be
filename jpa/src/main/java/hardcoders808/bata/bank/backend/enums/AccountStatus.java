package hardcoders808.bata.bank.backend.enums;

/**
 * Represents the status of a bank account.
 */
public enum AccountStatus {

    /**
     * The account is active and can perform all operations.
     */
    ACTIVE,

    /**
     * The account is frozen, restricting certain operations.
     */
    FROZEN,

    /**
     * The account is closed and cannot be used.
     */
    CLOSED,

    /**
     * The account is pending approval or activation.
     */
    PENDING
}
