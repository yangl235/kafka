/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;

import org.apache.kafka.common.config.TopicConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * RepartitionTopicConfig captures the properties required for configuring
 * the repartition topics.
 */
public class RepartitionTopicConfig extends InternalTopicConfig {

    private static final Map<String, String> REPARTITION_TOPIC_DEFAULT_OVERRIDES;
    static {
        final Map<String, String> tempTopicDefaultOverrides = new HashMap<>();
        tempTopicDefaultOverrides.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE);
        tempTopicDefaultOverrides.put(TopicConfig.SEGMENT_INDEX_BYTES_CONFIG, "52428800");     // 50 MB
        tempTopicDefaultOverrides.put(TopicConfig.SEGMENT_BYTES_CONFIG, "52428800");           // 50 MB
        tempTopicDefaultOverrides.put(TopicConfig.SEGMENT_MS_CONFIG, "600000");                // 10 min
        REPARTITION_TOPIC_DEFAULT_OVERRIDES = Collections.unmodifiableMap(tempTopicDefaultOverrides);
    }

    public RepartitionTopicConfig(final String name, final Map<String, String> topicConfigs) {
        super(name, topicConfigs);
    }

    /**
     * Get the configured properties for this topic. If rententionMs is set then
     * we add additionalRetentionMs to work out the desired retention when cleanup.policy=compact,delete
     *
     * @param additionalRetentionMs - added to retention to allow for clock drift etc
     * @return Properties to be used when creating the topic
     */
    public Map<String, String> getProperties(final Map<String, String> defaultProperties, final long additionalRetentionMs) {
        // internal topic config overridden rule: library overrides < global config overrides < per-topic config overrides
        final Map<String, String> topicConfig = new HashMap<>(REPARTITION_TOPIC_DEFAULT_OVERRIDES);

        topicConfig.putAll(defaultProperties);

        topicConfig.putAll(topicConfigs);

        return topicConfig;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RepartitionTopicConfig that = (RepartitionTopicConfig) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(topicConfigs, that.topicConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, topicConfigs);
    }

    @Override
    public String toString() {
        return "RepartitionTopicConfig(" +
                "name=" + name +
                ", topicConfigs=" + topicConfigs +
                ")";
    }
}
