
===========
Limitations
===========

.. rubric:: Table of contents

.. contents::
   :local:
   :depth: 2


Introduction
============

In this doc, the restrictions and limitations of SQL plugin is covered as follows.

Limitations on Identifiers
==========================

Using OpenSearch cluster name as catalog name to qualify an index name, such as ``my_cluster.my_index``, is not supported for now.

Limitations on Fields
=====================

We are not supporting use `alias field type <https://www.elastic.co/guide/en/elasticsearch/reference/current/alias.html>`_ as identifier. It will throw exception ``can't resolve Symbol``.


Limitations on Aggregations
===========================

Aggregation over expression is not supported for now. You can only apply aggregation on fields, aggregations can't accept an expression as a parameter. For example, `avg(log(age))` is not supported.

Here's a link to the Github issue - [Issue #288](https://github.com/opendistro-for-elasticsearch/sql/issues/288).


Limitations on Subqueries
=========================

Subqueries in the FROM clause
-----------------------------

Subquery in the `FROM` clause in this format: `SELECT outer FROM (SELECT inner)` is supported only when the query is merged into one query. For example, the following query is supported::

    SELECT t.f, t.d
    FROM (
        SELECT FlightNum as f, DestCountry as d
        FROM opensearch_dashboards_sample_data_flights
        WHERE OriginCountry = 'US') t

But, if the outer query has `GROUP BY` or `ORDER BY`, then it's not supported.


Limitations on JOINs
====================

JOIN does not support aggregations on the joined result. The `join` query does not support aggregations on the joined result.
For example, e.g. `SELECT depo.name, avg(empo.age) FROM empo JOIN depo WHERE empo.id == depo.id GROUP BY depo.name` is not supported.

Here's a link to the Github issue - `Issue 110 <https://github.com/opendistro-for-elasticsearch/sql/issues/110>`_.


Limitations on Window Functions
===============================

For now, only the field defined in index is allowed, all the other calculated fields (calculated by scalar or aggregated functions) is not allowed. For example, either ``avg_flight_time`` or ``AVG(FlightTimeMin)`` is not accessible to the rank window definition as follows::

    SELECT OriginCountry, AVG(FlightTimeMin) AS avg_flight_time,
           RANK() OVER (ORDER BY avg_flight_time) AS rnk
    FROM opensearch_dashboards_sample_data_flights
    GROUP BY OriginCountry

Another limitation is that currently window function cannot be nested in another expression, for example, ``CASE WHEN RANK() OVER(...) THEN ...``.

Workaround for both limitations mentioned above is using a sub-query in FROM clause::

    SELECT
      SUM(t.avg_flight_time) OVER(...)
    FROM (
        SELECT OriginCountry, AVG(FlightTimeMin) AS avg_flight_time,
        FROM opensearch_dashboards_sample_data_flights
        GROUP BY OriginCountry
    ) AS t

Limitations on Pagination
=========================

Pagination only supports basic queries for now. The pagination query enables you to get back paginated responses.
Currently, the pagination only supports basic queries. For example, the following query returns the data with cursor id::

    POST _opensearch/_sql/
    {
      "fetch_size" : 5,
      "query" : "SELECT OriginCountry, DestCountry FROM opensearch_dashboards_sample_data_flights ORDER BY OriginCountry ASC"
    }

The response in JDBC format with cursor id::

    {
      "schema": [
        {
          "name": "OriginCountry",
          "type": "keyword"
        },
        {
          "name": "DestCountry",
          "type": "keyword"
        }
      ],
      "cursor": "d:eyJhIjp7fSwicyI6IkRYRjFaWEo1UVc1a1JtVjBZMmdCQUFBQUFBQUFCSllXVTJKVU4yeExiWEJSUkhsNFVrdDVXVEZSYkVKSmR3PT0iLCJjIjpbeyJuYW1lIjoiT3JpZ2luQ291bnRyeSIsInR5cGUiOiJrZXl3b3JkIn0seyJuYW1lIjoiRGVzdENvdW50cnkiLCJ0eXBlIjoia2V5d29yZCJ9XSwiZiI6MSwiaSI6ImtpYmFuYV9zYW1wbGVfZGF0YV9mbGlnaHRzIiwibCI6MTMwNTh9",
      "total": 13059,
      "datarows": [[
        "AE",
        "CN"
      ]],
      "size": 1,
      "status": 200
    }

The query with `aggregation` and `join` does not support pagination for now.
