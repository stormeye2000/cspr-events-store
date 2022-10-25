package com.stormeye.producer.config;

import java.util.Objects;

/**
 * Definition for a kafka topic configuration
 */
public class Topic {
    private String topic;
    private int partitions;
    private int replicas;
    private String compression;

    public Topic(final String topic, final int partitions, final int replicas, final String compression) {
        this.topic = Objects.requireNonNull(topic, "topic must not be null");
        this.partitions = partitions;
        this.replicas = replicas;
        this.compression = compression;
    }

    @SuppressWarnings("unused")
    public Topic() {
        // Needed for spring configuration
    }

    @SuppressWarnings("unused")
    public void setTopic(String topic) {
        this.topic = topic;
    }

    @SuppressWarnings("unused")
    public void setPartitions(int partitions) {
        this.partitions = partitions;
    }

    @SuppressWarnings("unused")
    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartitions() {
        return partitions;
    }

    public int getReplicas() {
        return replicas;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(final String compression) {
        this.compression = compression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic1 = (Topic) o;
        return partitions == topic1.partitions && replicas == topic1.replicas && topic.equals(topic1.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partitions, replicas);
    }
}
