package hardcoders808.bata.bank.backend.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.attributes.JwtAllMatchingClaimsExtractor;
import org.zalando.logbook.json.PrettyPrintingJsonBodyFilter;
import org.zalando.logbook.servlet.LogbookFilter;

import tools.jackson.databind.json.JsonMapper;


/**
 * @author HAMMA FATAKA (mfataka@monetplus.cz)
 * @project bata-bank-backend
 * @date 03.03.2026 15:36
 */
@Configuration
public class LogbookConfig {
    @Bean
    public LogbookFilter logbookFilter(@Qualifier("jsonBodyFieldsFilter") final BodyFilter filter, final JsonMapper jsonMapper) {

        final var logbook = Logbook.builder()
                .bodyFilter(filter)
                .attributeExtractor(
                        new JwtAllMatchingClaimsExtractor(jsonMapper, new ArrayList<>())
                )
                .build();

        return new LogbookFilter(logbook);
    }

    @Bean
    public BodyFilter prettyJsonBodyFilter() {
        return new PrettyPrintingJsonBodyFilter();
    }
}
