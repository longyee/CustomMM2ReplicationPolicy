package com.vpsee.kafka;

import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.connect.mirror.DefaultReplicationPolicy;

public class CustomMM2ReplicationPolicy extends DefaultReplicationPolicy {
    private static final Logger logger = LoggerFactory.getLogger(CustomMM2ReplicationPolicy.class);
    private String sourceClusterAlias;
    public static final String REPLICATION_POLICY_TOPICS_RENAME = "replication.policy.topics.rename";
    private Map<String, String> topicsMap;

    @Override
    public void configure(Map<String, ?> props) {
        super.configure(props);

        try {
            if (props.containsKey(REPLICATION_POLICY_TOPICS_RENAME)) {
                topicsMap = new HashMap<>();
                String rename = (String) props.get(REPLICATION_POLICY_TOPICS_RENAME);

                String[] items = rename.split(";");
                for (String item : items) {
                    String[] fromTo = item.split(",");
                    topicsMap.put(fromTo[0], fromTo[1]);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("[CustomMM2ReplicationPolicy]: Failed to parse '%s'", REPLICATION_POLICY_TOPICS_RENAME), e);
        }
    }

    @Override
    public String formatRemoteTopic(String sourceClusterAlias, String topic) {
        if (topicsMap != null && topicsMap.containsKey(topic)) {
            return topicsMap.get(topic);
        } else {
            return super.formatRemoteTopic(sourceClusterAlias, topic);
        }
    }

    @Override
    public String topicSource(String topic) {
        return topic == null? null: sourceClusterAlias;
    }

    @Override
    public String upstreamTopic(String topic) {
        return null;
    }
}
