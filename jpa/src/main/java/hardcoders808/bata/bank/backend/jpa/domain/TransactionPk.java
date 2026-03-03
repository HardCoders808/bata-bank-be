package hardcoders808.bata.bank.backend.jpa.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.*;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransactionPk implements Serializable {

    @Column(name = "terminal_dttm", nullable = false)
    private LocalDateTime terminalDttm;

    @Column(name = "trx_uuid", nullable = false, length = 36)
    private String trxUuid;
}
