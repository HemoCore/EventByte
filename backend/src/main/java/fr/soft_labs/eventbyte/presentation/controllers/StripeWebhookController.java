package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException;
import fr.soft_labs.eventbyte.domain.usecases.booking.HandlePaymentResult;
import fr.soft_labs.eventbyte.infrastructure.payment.InvalidStripeWebhookException;
import fr.soft_labs.eventbyte.infrastructure.payment.StripeWebhookParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * US-17: Stripe calls this route with the result of a payment.
 * No JWT (Stripe has none): the Stripe signature is checked instead.
 * Stripe sends the event again until it gets a 2xx, so:
 * 200 = handled or ignored, 400 = not from Stripe, 503 = try again later (refund failed).
 */
@RestController
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final StripeWebhookParser parser;
    private final HandlePaymentResult handlePaymentResult;

    public StripeWebhookController(StripeWebhookParser parser, HandlePaymentResult handlePaymentResult) {
        this.parser = parser;
        this.handlePaymentResult = handlePaymentResult;
    }

    // the raw body (String) is needed: the signature is computed on the exact bytes Stripe sent
    @PostMapping("/webhooks/stripe")
    public ResponseEntity<Void> receive(@RequestBody String payload,
                                        @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
        try {
            parser.parse(payload, signature).ifPresent(event -> {
                HandlePaymentResult.Result result = handlePaymentResult.execute(event.paymentId(), event.outcome());
                log.info("Stripe payment {} {} -> {}", event.paymentId(), event.outcome(), result);
            });
            return ResponseEntity.ok().build();
        } catch (InvalidStripeWebhookException e) {
            log.warn("Stripe webhook refused: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (PaymentFailedException e) {
            return ResponseEntity.status(503).build();
        }
    }
}
