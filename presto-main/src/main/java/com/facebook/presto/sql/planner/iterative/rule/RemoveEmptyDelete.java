/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.sql.planner.iterative.rule;

import com.facebook.presto.matching.Capture;
import com.facebook.presto.matching.Captures;
import com.facebook.presto.matching.Pattern;
import com.facebook.presto.sql.planner.iterative.PatternBasedRule;
import com.facebook.presto.sql.planner.plan.PlanNode;
import com.facebook.presto.sql.planner.plan.TableFinishNode;
import com.facebook.presto.sql.planner.plan.ValuesNode;
import com.facebook.presto.sql.tree.LongLiteral;
import com.google.common.collect.ImmutableList;

import java.util.Optional;

import static com.facebook.presto.matching.Capture.newCapture;
import static com.facebook.presto.sql.planner.plan.Patterns.delete;
import static com.facebook.presto.sql.planner.plan.Patterns.exchange;
import static com.facebook.presto.sql.planner.plan.Patterns.source;
import static com.facebook.presto.sql.planner.plan.Patterns.tableFinish;
import static com.facebook.presto.sql.planner.plan.Patterns.values;

/**
 * If the predicate for a delete is optimized to false, the target table scan
 * of the delete will be replaced with an empty values node. This type of
 * plan cannot be executed and is meaningless anyway, so we replace the
 * entire thing with a values node.
 * <p>
 * Transforms
 * <pre>
 *  - TableFinish
 *    - Exchange
 *      - Delete
 *        - empty Values
 * </pre>
 * into
 * <pre>
 *  - Values (0)
 * </pre>
 */
// TODO split into multiple rules (https://github.com/prestodb/presto/issues/7292)
public class RemoveEmptyDelete
        implements PatternBasedRule<TableFinishNode>
{
    private static final Capture<ValuesNode> VALUES = newCapture();

    private static final Pattern<TableFinishNode> PATTERN = tableFinish()
            .with(source().matching(exchange()
                    .with(source().matching(delete()
                            .with(source().matching(values().capturedAs(VALUES)))))));

    @Override
    public Pattern<TableFinishNode> getPattern()
    {
        return PATTERN;
    }

    @Override
    public Optional<PlanNode> apply(TableFinishNode node, Captures captures, Context context)
    {
        if (!captures.get(VALUES).getRows().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new ValuesNode(
                        node.getId(),
                        node.getOutputSymbols(),
                        ImmutableList.of(ImmutableList.of(new LongLiteral("0")))));
    }
}
