/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package com.amazon.opendistroforelasticsearch.sql.sql;

import static com.amazon.opendistroforelasticsearch.sql.util.MatcherUtils.rows;
import static com.amazon.opendistroforelasticsearch.sql.util.MatcherUtils.schema;
import static com.amazon.opendistroforelasticsearch.sql.util.MatcherUtils.verifyDataRows;
import static com.amazon.opendistroforelasticsearch.sql.util.MatcherUtils.verifySchema;
import static com.amazon.opendistroforelasticsearch.sql.util.TestUtils.createHiddenIndexByRestClient;
import static com.amazon.opendistroforelasticsearch.sql.util.TestUtils.performRequest;

import com.amazon.opendistroforelasticsearch.sql.legacy.SQLIntegTestCase;
import java.io.IOException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.opensearch.client.Request;

/**
 * Integration tests for identifiers including index and field name symbol.
 */
public class IdentifierIT extends SQLIntegTestCase {

  @Test
  public void testIndexNames() throws IOException {
    createIndexWithOneDoc("logs", "logs_2020_01");
    queryAndAssertTheDoc("SELECT * FROM logs");
    queryAndAssertTheDoc("SELECT * FROM logs_2020_01");
  }

  @Test
  public void testSpecialIndexNames() throws IOException {
    createIndexWithOneDoc(".system", "logs-2020-01");
    queryAndAssertTheDoc("SELECT * FROM .system");
    queryAndAssertTheDoc("SELECT * FROM logs-2020-01");
  }

  @Test
  public void testQuotedIndexNames() throws IOException {
    createIndexWithOneDoc("logs+2020+01", "logs.2020.01");
    queryAndAssertTheDoc("SELECT * FROM `logs+2020+01`");
  }

  @Test
  public void testSpecialFieldName() throws IOException {
    new Index("test")
        .addDoc("{\"@timestamp\": 10, \"dimensions:major_version\": 30}");
    final JSONObject result = new JSONObject(executeQuery("SELECT @timestamp, "
        + "`dimensions:major_version` FROM test", "jdbc"));

    verifySchema(result,
        schema("@timestamp", null, "long"),
        schema("dimensions:major_version", null, "long"));
    verifyDataRows(result, rows(10, 30));
  }

  private void createIndexWithOneDoc(String... indexNames) throws IOException {
    for (String indexName : indexNames) {
      new Index(indexName).addDoc("{\"age\": 30}");
    }
  }

  private void queryAndAssertTheDoc(String sql) {
    final JSONObject result = new JSONObject(executeQuery(sql.replace("\"", "\\\""), "jdbc"));
    verifySchema(result, schema("age", null, "long"));
    verifyDataRows(result, rows(30));
  }

  /**
   * Index abstraction for test code readability.
   */
  private static class Index {

    private final String indexName;

    Index(String indexName) throws IOException {
      this.indexName = indexName;

      if (indexName.startsWith(".")) {
        createHiddenIndexByRestClient(client(), indexName, "");
      } else {
        executeRequest(new Request("PUT", "/" + indexName));
      }
    }

    void addDoc(String doc) {
      Request indexDoc = new Request("POST", String.format("/%s/_doc?refresh=true", indexName));
      indexDoc.setJsonEntity(doc);
      performRequest(client(), indexDoc);
    }
  }

}
