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
 *
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package com.amazon.opendistroforelasticsearch.sql.opensearch.utils;

import com.amazon.opendistroforelasticsearch.sql.ast.tree.Sort;
import com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType;
import com.amazon.opendistroforelasticsearch.sql.expression.Expression;
import com.amazon.opendistroforelasticsearch.sql.expression.NamedExpression;
import com.amazon.opendistroforelasticsearch.sql.expression.ReferenceExpression;
import com.amazon.opendistroforelasticsearch.sql.expression.aggregation.AvgAggregator;
import com.amazon.opendistroforelasticsearch.sql.expression.aggregation.NamedAggregator;
import com.amazon.opendistroforelasticsearch.sql.opensearch.planner.logical.OpenSearchLogicalIndexAgg;
import com.amazon.opendistroforelasticsearch.sql.opensearch.planner.logical.OpenSearchLogicalIndexScan;
import com.amazon.opendistroforelasticsearch.sql.planner.logical.LogicalPlan;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;

@UtilityClass
public class Utils {

  /**
   * Build ElasticsearchLogicalIndexScan.
   */
  public static LogicalPlan indexScan(String tableName, Expression filter) {
    return OpenSearchLogicalIndexScan.builder().relationName(tableName).filter(filter).build();
  }

  /**
   * Build ElasticsearchLogicalIndexScan.
   */
  public static LogicalPlan indexScan(String tableName,
                                      Pair<Sort.SortOption, Expression>... sorts) {
    return OpenSearchLogicalIndexScan.builder().relationName(tableName)
        .sortList(Arrays.asList(sorts))
        .build();
  }

  /**
   * Build ElasticsearchLogicalIndexScan.
   */
  public static LogicalPlan indexScan(String tableName,
                                      Expression filter,
                                      Pair<Sort.SortOption, Expression>... sorts) {
    return OpenSearchLogicalIndexScan.builder().relationName(tableName)
        .filter(filter)
        .sortList(Arrays.asList(sorts))
        .build();
  }

  /**
   * Build ElasticsearchLogicalIndexScan.
   */
  public static LogicalPlan indexScan(String tableName, Integer offset, Integer limit,
                                      Set<ReferenceExpression> projectList) {
    return OpenSearchLogicalIndexScan.builder().relationName(tableName)
        .offset(offset)
        .limit(limit)
        .projectList(projectList)
        .build();
  }

  /**
   * Build ElasticsearchLogicalIndexScan.
   */
  public static LogicalPlan indexScan(String tableName,
                                      Expression filter,
                                      Integer offset, Integer limit,
                                      Set<ReferenceExpression> projectList) {
    return OpenSearchLogicalIndexScan.builder().relationName(tableName)
        .filter(filter)
        .offset(offset)
        .limit(limit)
        .projectList(projectList)
        .build();
  }

  /**
   * Build ElasticsearchLogicalIndexScan.
   */
  public static LogicalPlan indexScan(String tableName,
                                      Expression filter,
                                      Integer offset, Integer limit,
                                      List<Pair<Sort.SortOption, Expression>> sorts,
                                      Set<ReferenceExpression> projectList) {
    return OpenSearchLogicalIndexScan.builder().relationName(tableName)
        .filter(filter)
        .sortList(sorts)
        .offset(offset)
        .limit(limit)
        .projectList(projectList)
        .build();
  }

  /**
   * Build ElasticsearchLogicalIndexScan.
   */
  public static LogicalPlan indexScan(String tableName,
                                      Set<ReferenceExpression> projects) {
    return OpenSearchLogicalIndexScan.builder()
        .relationName(tableName)
        .projectList(projects)
        .build();
  }

  /**
   * Build ElasticsearchLogicalIndexScan.
   */
  public static LogicalPlan indexScan(String tableName, Expression filter,
                                      Set<ReferenceExpression> projects) {
    return OpenSearchLogicalIndexScan.builder()
        .relationName(tableName)
        .filter(filter)
        .projectList(projects)
        .build();
  }

  /**
   * Build ElasticsearchLogicalIndexAgg.
   */
  public static LogicalPlan indexScanAgg(String tableName, List<NamedAggregator> aggregators,
                                         List<NamedExpression> groupByList) {
    return OpenSearchLogicalIndexAgg.builder().relationName(tableName)
        .aggregatorList(aggregators).groupByList(groupByList).build();
  }

  /**
   * Build ElasticsearchLogicalIndexAgg.
   */
  public static LogicalPlan indexScanAgg(String tableName, List<NamedAggregator> aggregators,
                                         List<NamedExpression> groupByList,
                                         List<Pair<Sort.SortOption, Expression>> sortList) {
    return OpenSearchLogicalIndexAgg.builder().relationName(tableName)
        .aggregatorList(aggregators).groupByList(groupByList).sortList(sortList).build();
  }

  /**
   * Build ElasticsearchLogicalIndexAgg.
   */
  public static LogicalPlan indexScanAgg(String tableName,
                                         Expression filter,
                                         List<NamedAggregator> aggregators,
                                         List<NamedExpression> groupByList) {
    return OpenSearchLogicalIndexAgg.builder().relationName(tableName).filter(filter)
        .aggregatorList(aggregators).groupByList(groupByList).build();
  }

  public static AvgAggregator avg(Expression expr, ExprCoreType type) {
    return new AvgAggregator(Arrays.asList(expr), type);
  }

  public static List<NamedAggregator> agg(NamedAggregator... exprs) {
    return Arrays.asList(exprs);
  }

  public static List<NamedExpression> group(NamedExpression... exprs) {
    return Arrays.asList(exprs);
  }

  public static List<Pair<Sort.SortOption, Expression>> sort(Expression expr1,
                                                             Sort.SortOption option1) {
    return Collections.singletonList(Pair.of(option1, expr1));
  }

  public static List<Pair<Sort.SortOption, Expression>> sort(Expression expr1,
                                                             Sort.SortOption option1,
                                                             Expression expr2,
                                                             Sort.SortOption option2) {
    return Arrays.asList(Pair.of(option1, expr1), Pair.of(option2, expr2));
  }

  public static Set<ReferenceExpression> projects(ReferenceExpression... expressions) {
    return ImmutableSet.copyOf(expressions);
  }

  public static Set<ReferenceExpression> noProjects() {
    return null;
  }
}
