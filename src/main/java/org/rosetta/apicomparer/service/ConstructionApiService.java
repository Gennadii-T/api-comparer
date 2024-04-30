package org.rosetta.apicomparer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosetta.apicomparer.utility.ApiResponseComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConstructionApiService {

  private final ApiResponseComparator apiResponseComparator;
  private final RestTemplate restTemplateTS;
  private final RestTemplate restTemplateJava;

  @Value("${api.path_to_ignore.construction.combine}")
  private String pathToIgnoreCombineAPI;

  @Value("${api.path_to_ignore.construction.metadata}")
  private String pathToIgnoreMetadataAPI;

  public void compareHashResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/hash.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/hash");
    compareApiResponses("/construction/hash", requestEntity, Collections.emptyList());
  }

  public void compareMetadataResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/metadata.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/metadata");
    compareApiResponses("/construction/metadata", requestEntity, Collections.singletonList(pathToIgnoreMetadataAPI));
  }

  public void compareDeriveResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/derive.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/derive");
    compareApiResponses("/construction/derive", requestEntity, Collections.emptyList());
  }

  public void compareSimplePreprocessResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/preprocess_simple.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/preprocess - Simple");
    compareApiResponses("/construction/preprocess", requestEntity, Collections.emptyList());
  }

  public void compareWithdrawalPreprocessResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/preprocess-withdrawal.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/preprocess - two withdrawals and key registration");
    compareApiResponses("/construction/preprocess", requestEntity, Collections.emptyList());
  }

  public void comparePoolRegistrationPreprocessResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/preprocess_pool_registration.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/preprocess - pool registration");
    compareApiResponses("/construction/preprocess", requestEntity, Collections.emptyList());
  }

  public void compareSimplePayloadResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/payloads_simple.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/payloads - Simple");
    compareApiResponses("/construction/payloads", requestEntity, Collections.emptyList());
  }

  public void comparePayloadWithMultipleAssetsResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/payload_multiple_assets.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/payloads - Multiple Assets");
    compareApiResponses("/construction/payloads", requestEntity, Collections.emptyList());
  }

  public void compareCombineResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/combine.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/combine");
    compareApiResponses("/construction/combine", requestEntity, Collections.singletonList(pathToIgnoreCombineAPI));
  }

  public void compareParseDepositResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/parse_deposit.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/parse - Deposit");
    compareApiResponses("/construction/parse", requestEntity, Collections.emptyList());
  }

  public void compareParseRefundResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/parse_refund.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/parse - Refund");
    compareApiResponses("/construction/parse", requestEntity, Collections.emptyList());
  }

  public void compareParseSignedMultiassetResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/parse_signed_multiasset.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/parse - Signed Multiasset");
    compareApiResponses("/construction/parse", requestEntity, Collections.emptyList());
  }

  public void comparePSubmitResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/construction/submit.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /construction/submit");
    compareApiResponses("/construction/submit", requestEntity, Collections.emptyList());
  }

  private <T> void compareApiResponses(String apiUrl, HttpEntity<T> requestEntity, List<String> pathsToIgnore) {
    ResponseEntity<String> responseJava = restTemplateJava.postForEntity(apiUrl, requestEntity, String.class);
    ResponseEntity<String> responseTS = restTemplateTS.postForEntity(apiUrl, requestEntity, String.class);

    apiResponseComparator.compareJson(responseTS.getBody(), responseJava.getBody(), pathsToIgnore);
  }

  private String readTestDataFromFile(String pathToFile) {
    try {
      return new String(Files.readAllBytes(Paths.get(pathToFile)));
    } catch (IOException e) {
      log.error("Error reading test data from file", e);
      throw new RuntimeException("Failed to read file " + pathToFile, e);
    }
  }

  private HttpEntity<String> prepareHttpEntity(String jsonPayload) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(jsonPayload, headers);
  }
}
