package hardcoders808.bata.bank.backend.cards.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import hardcoders808.bata.bank.backend.enums.CardStatus;
import hardcoders808.bata.bank.backend.enums.CardType;
import hardcoders808.bata.bank.backend.jpa.domain.Card;
import lombok.Builder;

@Builder
public record CardResponseDTO(
        Long id,
        Long accountId,
        String cardNumber, // Masked
        String cardHolderName,
        LocalDate expiryDate,
        CardType type,
        CardStatus status,
        BigDecimal dailyLimit,
        BigDecimal monthlyLimit,
        LocalDateTime createdAt
) {
    public static CardResponseDTO fromDomain(Card card) {
        return CardResponseDTO.builder()
                .id(card.getId())
                .accountId(card.getAccount().getId())
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .cardHolderName(card.getCardHolderName())
                .expiryDate(card.getExpiryDate())
                .type(card.getType())
                .status(card.getStatus())
                .dailyLimit(card.getDailyLimit())
                .monthlyLimit(card.getMonthlyLimit())
                .createdAt(card.getCreatedAt())
                .build();
    }

    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
