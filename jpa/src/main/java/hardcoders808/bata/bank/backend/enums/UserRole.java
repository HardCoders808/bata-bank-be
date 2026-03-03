package hardcoders808.bata.bank.backend.enums;

import java.util.EnumSet;


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
    ACCOUNT_HOLDER,
    UNKNOWN;


    private static final EnumSet<UserRole> CACHED_VALUES = EnumSet.allOf(UserRole.class);

    public boolean isAdmin() {
        return is(SYS_ADMIN);
    }

    public boolean isBanker() {
        return is(BANKER);
    }

    public boolean isSenior() {
        return is(SENIOR_ACCOUNT_HOLDER);
    }

    public boolean isJunior() {
        return is(JUNIOR_ACCOUNT_HOLDER);
    }

    public boolean isAccountHolder() {
        return is(ACCOUNT_HOLDER);
    }

    public boolean is(final UserRole role) {
        return this == role;
    }

    public static UserRole fromString(final String name) {
        return CACHED_VALUES.stream()
                .filter(userRole -> userRole.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
