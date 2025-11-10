package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.response.transport.PriceApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class TransportService {
    private static final String PARTNER_ID = "198942b2-c03d-4df2-a9e5-5c63bd5627c8";

    private static final String AUTH_KEY = "6424f8bedfad4eaab1d049fb1f8a4189";

    private static final String PRICING_URL = "https://api.sycncrm.com/production/price/321";

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final ZoneId ZONE = ZoneId.of("Europe/Istanbul");

    /**
     * Kullanıcının gönderdiği dört alan ile dış pricing API'ye istek atar
     * ve PriceApiResponse döner.
     *
     * @param originPostalCode      origin_postal_code (kullanıcıdan)
     * @param originState           origin_state (kullanıcıdan)
     * @param destinationPostalCode destination_postal_code (kullanıcıdan)
     * @param destinationState      destination_state (kullanıcıdan)
     * @return PriceApiResponse dış servisin body içeriği
     */
    public PriceApiResponse getPriceFromExternalApi(
            String originPostalCode,
            String originState,
            String destinationPostalCode,
            String destinationState
    ) {
        try {
            LocalDate tomorrow = LocalDate.now(ZONE).plusDays(1);
            String firstAvailable = tomorrow.format(DATE_FORMAT);

            JsonNode requestBodyNode = objectMapper.createObjectNode()
                    .put("partner_id", PARTNER_ID)
                    .put("origin_postal_code", originPostalCode)
                    .put("origin_state", originState)
                    .put("destination_postal_code", destinationPostalCode)
                    .put("destination_state", destinationState)
                    .put("vehicle_types", "Car")
                    .put("transport_type", 1)
                    .put("vehicles_inop", 0)
                    .put("first_available_for_release", firstAvailable);

            String bodyJson = objectMapper.writeValueAsString(requestBodyNode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("AuthKey", AUTH_KEY);

            HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(PRICING_URL, HttpMethod.POST, entity, String.class);

            String responseBody = responseEntity.getBody();
            log.info("[TransportService] pricing api status={}, body={}", responseEntity.getStatusCode(), responseBody);

            if (responseBody == null || responseBody.isBlank()) {
                log.warn("[TransportService] empty body from pricing API");
                throw new RuntimeException("Empty response from pricing API");
            }

            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode actualBodyNode = root;
            if (root.has("body")) {
                actualBodyNode = root.get("body");
            }

            return objectMapper.treeToValue(actualBodyNode, PriceApiResponse.class);

        } catch (HttpStatusCodeException ex) {
            log.error("[TransportService] Pricing API returned error status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new RuntimeException("Pricing API error: " + ex.getStatusCode(), ex);
        } catch (Exception ex) {
            log.error("[TransportService] Error calling Pricing API", ex);
            throw new RuntimeException("Error calling Pricing API", ex);
        }
    }
}
