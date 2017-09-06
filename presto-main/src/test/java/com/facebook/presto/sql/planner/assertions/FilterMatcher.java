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
package com.facebook.presto.sql.planner.assertions;

import com.facebook.presto.Session;
import com.facebook.presto.cost.PlanNodeCost;
import com.facebook.presto.metadata.Metadata;
import com.facebook.presto.sql.planner.plan.FilterNode;
import com.facebook.presto.sql.planner.plan.PlanNode;
import com.facebook.presto.sql.tree.ComparisonExpression;
import com.facebook.presto.sql.tree.DeferredSymbolReference;
import com.facebook.presto.sql.tree.Expression;
import com.facebook.presto.sql.tree.Node;
import com.facebook.presto.sql.tree.SymbolReference;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

final class FilterMatcher
        implements Matcher
{
    private final Expression predicate;
    private final Map<String, String> dynamicFilters;

    FilterMatcher(Expression predicate)
    {
        this(predicate, ImmutableMap.of());
    }

    FilterMatcher(Expression predicate, Map<String, String> dynamicFilters)
    {
        this.predicate = requireNonNull(predicate, "predicate is null");
        this.dynamicFilters = ImmutableMap.copyOf(dynamicFilters);
    }

    @Override
    public boolean shapeMatches(PlanNode node)
    {
        return node instanceof FilterNode;
    }

    @Override
    public MatchResult detailMatches(PlanNode node, PlanNodeCost cost, Session session, Metadata metadata, SymbolAliases symbolAliases)
    {
        checkState(shapeMatches(node), "Plan testing framework error: shapeMatches returned false in detailMatches in %s", this.getClass().getName());

        FilterNode filterNode = (FilterNode) node;
        ExpressionVerifier verifier = new ExpressionVerifier(symbolAliases);
        return new MatchResult(verifier.process(filterNode.getPredicate(), predicate));
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("predicate", predicate)
                .toString();
    }

    class ExpressionVerifierWithDynamicFilterAliases extends ExpressionVerifier
    {
        private final SymbolAliases.Builder addedAliases = SymbolAliases.builder();
        private final Map<String, String> columnToDynamicFilter;
        private ComparisonExpression comparisonExpression;

        ExpressionVerifierWithDynamicFilterAliases(SymbolAliases symbolAliases, Map<String, String> columnToDynamicFilter)
        {
            super(symbolAliases);
            this.columnToDynamicFilter = ImmutableMap.copyOf(columnToDynamicFilter);
        }

        @Override
        protected Boolean visitNode(Node node, Node context)
        {
            comparisonExpression = null;
            return super.visitNode(node, context);
        }

        @Override
        protected Boolean visitComparisonExpression(ComparisonExpression actual, Node expectedExpression)
        {
            if (expectedExpression instanceof ComparisonExpression) {
                ComparisonExpression expected = (ComparisonExpression) expectedExpression;
                if (actual.getType() == expected.getType()) {
                    comparisonExpression = actual;
                    return process(actual.getLeft(), expected.getLeft()) && process(actual.getRight(), expected.getRight());
                }
            }
            return false;
        }

        @Override
        protected Boolean visitDeferredSymbolReference(DeferredSymbolReference node, Node expected)
        {
            if (!(expected instanceof DeferredSymbolReference)) {
                return false;
            }

            checkState(comparisonExpression != null, "DeferredSymbolReferences are not supported in context different than equality comparison");
            checkState(comparisonExpression.getLeft() instanceof SymbolReference, "Dynamic filters are not supported when left side of comparison is not a simple SymbolReference");

            SymbolReference leftSymbol = (SymbolReference) comparisonExpression.getLeft();

            symbolAliases
            columnToDynamicFilter.get(leftSymbol.toString())
            addedAliases.put(
            return true;
        }
    }
}
