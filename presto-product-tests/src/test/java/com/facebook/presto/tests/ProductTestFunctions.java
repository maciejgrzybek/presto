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
package com.facebook.presto.tests;

import com.teradata.test.ProductTest;
import org.testng.annotations.Test;

import static com.facebook.presto.tests.utils.QueryExecutors.onPresto;
import static com.teradata.test.assertions.QueryAssert.Row.row;
import static com.teradata.test.assertions.QueryAssert.assertThat;

public class ProductTestFunctions
        extends ProductTest
{
    @Test(groups = TestGroups.FUNCTIONS)
    public void testSubstring()
    {
        assertThat(onPresto().executeQuery("SELECT SUBSTRING('ala ma kota' from 2 for 4)")).contains(row("la m"));
        assertThat(onPresto().executeQuery("SELECT SUBSTR('ala ma kota', 2, 4)")).contains(row("la m"));
    }

    @Test(groups = TestGroups.FUNCTIONS)
    public void testPosition()
    {
        assertThat(onPresto().executeQuery("SELECT POSITION('ma' IN 'ala ma kota')")).contains(row(5));
    }
}
