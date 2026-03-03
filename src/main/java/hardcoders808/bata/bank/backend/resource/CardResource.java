package hardcoders808.bata.bank.backend.resource;

import hardcoders808.bata.bank.backend.service.CardService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

@Resource
@RequestMapping("/cards")
public class CardResource {

    private static final Logger log = LoggerFactory.getLogger(CardResource.class);

    private final CardService cardService;

    public CardResource(CardService cardService) {
        this.cardService = cardService;
    }

}
