package fr.soft_labs.eventbyte.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StrapiDeletedEventMessage(String id) {
}
