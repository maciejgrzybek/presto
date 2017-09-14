-- database: presto_tpch; groups: tpch
SELECT sum(l.extendedprice * l.discount) AS revenue
FROM
  lineitem l
WHERE
  l.shipdate >= DATE '1994-01-01'
  AND l.shipdate < DATE '1994-01-01' + INTERVAL '1' YEAR
AND l.discount BETWEEN decimal '0.06' - decimal '0.01' AND decimal '0.06' + decimal '0.01'
AND l.quantity < 24
