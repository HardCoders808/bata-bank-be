package hardcoders808.bata.bank.backend.enums;

/**
 * Represents the type of a financial transaction.
 */
public enum TransactionType {

    /**
     * A transfer of funds between accounts.
     */
    TRANSFER,

    /**
     * A withdrawal of cash from an account.
     */
    WITHDRAWAL,

    /**
     * A deposit of funds into an account.
     */
    DEPOSIT,

    /**
     * A payment made using a bank card.
     */
    CARD_PAYMENT,

    /**
     * A bank fee or service charge.
     */
    FEE
}
