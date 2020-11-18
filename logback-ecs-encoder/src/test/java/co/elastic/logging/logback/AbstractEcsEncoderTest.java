/*-
 * #%L
 * Java ECS logging
 * %%
 * Copyright (C) 2019 - 2020 Elastic and contributors
 * %%
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * #L%
 */
package co.elastic.logging.logback;

import ch.qos.logback.classic.Logger;
import co.elastic.logging.AbstractEcsLoggingTest;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractEcsEncoderTest extends AbstractEcsLoggingTest {
    protected Logger logger;

    @Override
    public boolean putMdc(String key, String value) {
        MDC.put(key, value);
        return true;
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(String message, Object... logParams) {
        logger.debug(message, logParams);
    }

    @Test
    void testAdditionalFields() throws Exception {
        debug("test");
        assertThat(getAndValidateLastLogLine().get("foo").textValue()).isEqualTo("bar");
        assertThat(getAndValidateLastLogLine().get("baz").textValue()).isEqualTo("qux");
    }

    @Test
    void testMarker() throws Exception {
        Marker parent = MarkerFactory.getMarker("parent");
        Marker child = MarkerFactory.getMarker("child");
        Marker grandchild = MarkerFactory.getMarker("grandchild");
        child.add(grandchild);
        parent.add(child);
        logger.debug(parent, "test");

        assertThat(getAndValidateLastLogLine().get("tags")).contains(
                TextNode.valueOf("parent"),
                TextNode.valueOf("child"),
                TextNode.valueOf("grandchild"));
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }
}
