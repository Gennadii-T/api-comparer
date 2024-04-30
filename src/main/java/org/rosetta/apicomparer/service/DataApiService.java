package org.rosetta.apicomparer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosetta.apicomparer.dto.data.block.BlockDTO;
import org.rosetta.apicomparer.utility.ApiResponseComparator;
import org.rosetta.apicomparer.utility.DTOBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataApiService {

  private final ApiResponseComparator apiResponseComparator;
  private final RestTemplate restTemplateTS;
  private final RestTemplate restTemplateJava;
  private final DTOBuilder dtoBuilder;

  @Value("${api.path_to_ignore.data.network.options.version}")
  private String pathToIgnoreNetworkOptionsAPI;

  public void compareNetworkListResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/data/network_list.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /network/list");
    compareApiResponses("/network/list", requestEntity, Collections.emptyList());
  }

  public void compareNetworkStatusResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/data/network_status.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /network/status");
    compareApiResponses("/network/status", requestEntity, Collections.emptyList());
  }

  public void compareNetworkOptionsResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/data/network_options.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /network/options");
    compareApiResponses("/network/options", requestEntity, Collections.singletonList(pathToIgnoreNetworkOptionsAPI));
  }

  public void compareBlockResponses(int startBlock) {
    int index = startBlock;
    while (true) {
      try {
        HttpEntity<BlockDTO> requestEntity = new HttpEntity<>(dtoBuilder.createBlockDTO(index));
        log.info("Block № {}", index);
        ResponseEntity<String> responseJava = restTemplateJava.postForEntity("/block", requestEntity, String.class);
        ResponseEntity<String> responseTS = restTemplateTS.postForEntity("/block", requestEntity, String.class);

        apiResponseComparator.compareBlocks(responseTS.getBody(), responseJava.getBody(), Collections.emptyList());
        index++;
      } catch (InternalServerError e) {
        log.warn("Internal Server Error, block not found at index {}: ", index);
        break;
      }
    }
  }

  public void compareAccountBalanceResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/data/account_balance.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /account/balance");
    compareApiResponses("/account/balance", requestEntity, Collections.emptyList());
  }

  public void compareAccountCoinsResponses() {
    String jsonPayload = readTestDataFromFile("src/main/resources/test_data/data/account_coins.json");
    HttpEntity<String> requestEntity = prepareHttpEntity(jsonPayload);
    log.info("Making an API call to: /account/coins");
    compareApiResponses("/account/coins", requestEntity, Collections.emptyList());
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
