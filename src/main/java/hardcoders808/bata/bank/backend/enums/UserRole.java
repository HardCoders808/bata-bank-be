package hardcoders808.bata.bank.backend.enums;

/**
 * Represents the role and permissions level of a user.
 */
public enum UserRole {

    /**
     * System administrator with full access.
     */
    SYS_ADMIN,

    /**
     * Bank employee with administrative access to accounts.
     */
    BANKER,

    /**
     * Senior account holder with full account control.
     */
    SENIOR_ACCOUNT_HOLDER,

    /**
     * Junior account holder with restricted account access.
     */
    JUNIOR_ACCOUNT_HOLDER,

    /**
     * Standard account holder.
     */
    ACCOUNT_HOLDER
}
