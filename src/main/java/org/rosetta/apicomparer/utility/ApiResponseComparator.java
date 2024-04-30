package org.rosetta.apicomparer.utility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rosetta.apicomparer.dto.BalanceInfo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApiResponseComparator {

  private static final String BALANCES = "balances";
  private static final String BLOCK = "block";
  private static final String TRANSACTIONS = "transactions";
  private static final String COINS = "coins";
  private static final String TRANSACTION_IDENTIFIER = "transaction_identifier";
  private static final String COIN_IDENTIFIER = "coin_identifier";
  private static final String HASH = "hash";
  private static final String IDENTIFIER = "identifier";
  private static final String POLICY_ID = "policyId";
  private static final String DECIMALS = "decimals";
  private static final String VALUE = "value";
  private static final String SYMBOL = "symbol";
  private static final String CURRENCY = "currency";
  private static final String METADATA = "metadata";

  public void compareJson(String jsonStr1, String jsonStr2, List<String> pathsToIgnore) {

    JSONObject json1 = new JSONObject(jsonStr1);
    JSONObject json2 = new JSONObject(jsonStr2);

    JSONObject diffReport = new JSONObject();
    compareJsonObjects(json1, json2, diffReport, pathsToIgnore, new StringBuilder());
    if (diffReport.isEmpty()) {
      log.warn("Responses are identical");
    } else {
      log.error("Differences found: \n {}", diffReport.toString(4));
    }
  }

  public void compareBlocks(String block1, String block2, List<String> pathsToIgnore) {

    JSONObject json1 = new JSONObject(block1);
    JSONObject json2 = new JSONObject(block2);

    JSONObject block = json1.getJSONObject(BLOCK);
    JSONArray actualTransactions = block.getJSONArray(TRANSACTIONS);
    actualTransactions = sortTransactions(actualTransactions);

    JSONObject expectedBlock = json2.getJSONObject(BLOCK);
    JSONArray expectedTransactions = expectedBlock.getJSONArray(TRANSACTIONS);
    expectedTransactions = sortTransactions(expectedTransactions);

    block.put(TRANSACTIONS, actualTransactions);
    expectedBlock.put(TRANSACTIONS, expectedTransactions);

    json1.put(BLOCK, block);
    json2.put(BLOCK, expectedBlock);

    JSONObject diffReport = new JSONObject();
    compareJsonObjects(json1, json2, diffReport, pathsToIgnore, new StringBuilder());

    log.error("Differences found: {} \n", diffReport.toString(4));
  }

  private void compareJsonObjects(JSONObject expected, JSONObject actual, JSONObject diffReport, List<String> pathsToIgnore,
      StringBuilder pathBuilder) {

    Set<String> allKeys = new HashSet<>(expected.keySet());
    allKeys.addAll(actual.keySet());

    for (String key : allKeys) {
      processKey(key, expected, actual, diffReport, pathsToIgnore, pathBuilder);
    }
  }

  private void compareJsonArrays(JSONArray expected, JSONArray actual, JSONArray diffReport, List<String> pathsToIgnore,
      StringBuilder pathBuilder) {
    int maxLength = Math.max(expected.length(), actual.length());

    for (int i = 0; i < maxLength; i++) {
      handleElementAtIndex(i, expected, actual, diffReport, pathsToIgnore, pathBuilder);
    }
  }

  private void processKey(String key, JSONObject expected, JSONObject actual, JSONObject diffReport, List<String> pathsToIgnore,
      StringBuilder pathBuilder) {

    int initialLength = pathBuilder.length();
    if (initialLength > 0) {
      pathBuilder.append(".");
    }
    pathBuilder.append(key);
    if (pathsToIgnore.contains(pathBuilder.toString())) {
      pathBuilder.setLength(initialLength);
      return;
    }

    if (!expected.has(key)) {
      diffReport.put("Extra key in Java JSON", createReport(key, actual.get(key)));
      return;
    }

    if (!actual.has(key)) {
      diffReport.put("Missing key in Java JSON", createReport(key, expected.get(key)));
      return;
    }

    handleSpecialKeysOrObjects(key, expected, actual, diffReport, pathsToIgnore, pathBuilder);
    pathBuilder.setLength(initialLength);
  }

  private void handleSpecialKeysOrObjects(String key, JSONObject expected, JSONObject actual, JSONObject diffReport,
      List<String> pathsToIgnore, StringBuilder pathBuilder) {
    Object expectedElement = expected.get(key);
    Object actualElement = actual.get(key);

    if (key.equals(BALANCES)) {
      compareBalances(expected.getJSONArray(key), actual.getJSONArray(key), diffReport);
      return;
    }

    if (key.equals(COINS)) {
      compareCoins(expected.getJSONArray(key), actual.getJSONArray(key), diffReport);
      return;
    }

    if (expectedElement instanceof JSONObject expectedObject && actualElement instanceof JSONObject actualObject) {
      JSONObject subReport = new JSONObject();
      compareJsonObjects(expectedObject, actualObject, subReport, pathsToIgnore, pathBuilder);
      if (!subReport.isEmpty()) {
        diffReport.put(key, subReport);
      }
      return;
    }

    if (expectedElement instanceof JSONArray expectedArray && actualElement instanceof JSONArray actualArray) {
      JSONArray subReport = new JSONArray();
      compareJsonArrays(expectedArray, actualArray, subReport, pathsToIgnore, pathBuilder);
      if (!subReport.isEmpty()) {
        diffReport.put(key, subReport);
      }
      return;
    }

    if (!expectedElement.toString().equals(actualElement.toString())) {
      diffReport.put(key, "TS JSON: " + expectedElement + ", Java JSON: " + actualElement);
    }
  }

  private void handleElementAtIndex(int i, JSONArray expected, JSONArray actual, JSONArray diffReport, List<String> pathsToIgnore,
      StringBuilder pathBuilder) {

    int initialLength = pathBuilder.length();
    if (initialLength > 0) {
      pathBuilder.append(".");
    }
    pathBuilder.append(i);

    if (i >= expected.length()) {
      diffReport.put(createReport("Extra element in Java array", actual.get(i)));
      return;
    }
    if (i >= actual.length()) {
      diffReport.put(createReport("Missing element in Java array", expected.get(i)));
      return;
    }

    compareElementsAtSameIndex(i, expected, actual, diffReport, pathsToIgnore, pathBuilder);
    pathBuilder.setLength(initialLength);
  }

  private void compareElementsAtSameIndex(int index, JSONArray expected, JSONArray actual, JSONArray diffReport,
      List<String> pathsToIgnore, StringBuilder pathBuilder) {

    Object expectedElement = expected.get(index);
    Object actualElement = actual.get(index);

    if (expectedElement instanceof JSONObject expectedObject && actualElement instanceof JSONObject actualObject) {
      JSONObject subReport = new JSONObject();
      compareJsonObjects(expectedObject, actualObject, subReport, pathsToIgnore, pathBuilder);
      if (!subReport.isEmpty()) {
        diffReport.put(subReport);
      }
      return;
    }

    if (expectedElement instanceof JSONArray expectedArray && actualElement instanceof JSONArray actualArray) {
      JSONArray subReport = new JSONArray();
      compareJsonArrays(expectedArray, actualArray, subReport, pathsToIgnore, pathBuilder);
      if (!subReport.isEmpty()) {
        diffReport.put(subReport);
      }
      return;
    }

    comparePrimitiveElementsAtSameIndex(index, expectedElement, actualElement, diffReport);
  }

  private void comparePrimitiveElementsAtSameIndex(int index, Object expectedElement, Object actualElement,
      JSONArray diffReport) {
    if (!expectedElement.toString().equals(actualElement.toString())) {
      diffReport.put("Difference at index " + index + ": TS JSON: " + expectedElement + ", Java JSON: " + actualElement);
    }
  }

  private JSONObject createReport(String key, Object element) {
    JSONObject report = new JSONObject();
    report.put(key, element);
    return report;
  }

  private JSONArray sortTransactions(JSONArray transactions) {
    List<JSONObject> jsonValues = jsonArrayToList(transactions);
    jsonValues.sort((a, b) -> {
      String valA = a.getJSONObject(TRANSACTION_IDENTIFIER).getString(HASH);
      String valB = b.getJSONObject(TRANSACTION_IDENTIFIER).getString(HASH);
      return valA.compareTo(valB);
    });

    return new JSONArray(jsonValues);
  }

  private void compareBalances(JSONArray expected, JSONArray actual, JSONObject diffReport) {
    Map<String, BalanceInfo> balanceInfo1 = processBalances(expected);
    Map<String, BalanceInfo> balanceInfo2 = processBalances(actual);

    Set<String> balanceSet1 = balanceInfo1.keySet();
    Set<String> balanceSet2 = balanceInfo2.keySet();

    Set<String> missingInActual = new HashSet<>(balanceSet1);
    missingInActual.removeAll(balanceSet2);

    Set<String> extraInActual = new HashSet<>(balanceSet2);
    extraInActual.removeAll(balanceSet1);

    Set<String> commonKeys = new HashSet<>(balanceSet1);
    commonKeys.retainAll(balanceSet2);

    JSONObject balancesDiff = new JSONObject();
    addDifferences(balancesDiff, missingInActual, balanceInfo1, "Missing in Java JSON");
    addDifferences(balancesDiff, extraInActual, balanceInfo2, "Extra in Java JSON");
    addDuplicateDifferences(balancesDiff, commonKeys, balanceInfo1, balanceInfo2);

    if (!balancesDiff.isEmpty()) {
      diffReport.put(BALANCES, balancesDiff);
    }
  }

  private void addDifferences(JSONObject diff, Set<String> keys, Map<String, BalanceInfo> balanceInfo, String label) {
    if (!keys.isEmpty()) {
      JSONArray array = new JSONArray(keys.stream().map(k -> balanceInfo.get(k).toJSONObject()).toList());
      diff.put(label, array);
    }
  }

  private void addDuplicateDifferences(JSONObject diff, Set<String> commonKeys, Map<String, BalanceInfo> balanceInfo1,
      Map<String, BalanceInfo> balanceInfo2) {
    JSONArray duplicatesArray = new JSONArray();
    for (String key : commonKeys) {
      BalanceInfo info1 = balanceInfo1.get(key);
      BalanceInfo info2 = balanceInfo2.get(key);
      if (info1.getCount() != info2.getCount() || !info1.getValue().equals(info2.getValue()) || !info1.getPolicyId()
          .equals(info2.getPolicyId())) {
        JSONObject duplicateObj = new JSONObject();
        duplicateObj.put("balance", info1.toJSONObject());
        duplicateObj.put("Amount in Java JSON", info1.getCount());
        duplicateObj.put("Amount in TS JSON", info2.getCount());
        duplicatesArray.put(duplicateObj);
      }
    }
    if (!duplicatesArray.isEmpty()) {
      diff.put("Differences in Duplicates", duplicatesArray);
    }
  }

  private Map<String, BalanceInfo> processBalances(JSONArray balances) {
    Map<String, BalanceInfo> balanceMap = new HashMap<>();
    for (int i = 0; i < balances.length(); i++) {
      JSONObject balance = balances.getJSONObject(i);
      JSONObject currency = balance.getJSONObject(CURRENCY);
      JSONObject metadata = currency.optJSONObject(METADATA);
      BalanceInfo balanceInfo = BalanceInfo.builder()
          .symbol(currency.getString(SYMBOL))
          .decimals(currency.getInt(DECIMALS))
          .value(balance.getString(VALUE))
          .policyId(metadata != null ? metadata.getString(POLICY_ID) : "")
          .count(0)
          .build();

      String key = balanceInfo.toString();
      BalanceInfo info = balanceMap.getOrDefault(key, balanceInfo);
      info.incrementCount();
      balanceMap.put(key, info);
    }
    return balanceMap;
  }

  private void compareCoins(JSONArray expectedCoins, JSONArray actualCoins, JSONObject diffReport) {
    JSONArray sortedExpectedCoins = sortCoins(expectedCoins);
    JSONArray sortedActualCoins = sortCoins(actualCoins);

    for (int i = 0; i < sortedExpectedCoins.length(); i++) {
      JSONObject expectedCoin = sortedExpectedCoins.getJSONObject(i);
      JSONObject actualCoin = sortedActualCoins.getJSONObject(i);

      String expectedIdentifier = expectedCoin.getJSONObject(COIN_IDENTIFIER).getString(IDENTIFIER);
      String actualIdentifier = actualCoin.getJSONObject(COIN_IDENTIFIER).getString(IDENTIFIER);

      if (!expectedIdentifier.equals(actualIdentifier)) {
        diffReport.put("Coins array mismatch", "TS JSON " + expectedIdentifier + ", Java JSON " + actualIdentifier);
        return;
      }

      JSONObject expectedAmount = expectedCoin.getJSONObject("amount");
      JSONObject actualAmount = actualCoin.getJSONObject("amount");

      if (!expectedAmount.toString().equals(actualAmount.toString())) {
        diffReport.put("Coins array mismatch", "TS JSON " + expectedIdentifier + expectedAmount + ", Java JSON " + actualAmount);
        return;
      }
    }
  }

  private JSONArray sortCoins(JSONArray coins) {
    List<JSONObject> coinList = jsonArrayToList(coins);
    coinList.sort(Comparator.comparing(obj -> obj.getJSONObject(COIN_IDENTIFIER).getString(IDENTIFIER)));
    return new JSONArray(coinList);
  }

  private List<JSONObject> jsonArrayToList(JSONArray array) {
    return IntStream.range(0, array.length())
        .mapToObj(array::getJSONObject)
        .collect(Collectors.toList());
  }
}
