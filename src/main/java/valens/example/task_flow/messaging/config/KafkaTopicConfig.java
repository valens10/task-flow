package valens.example.task_flow.messaging.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic taskCreatedTopic() {
        return TopicBuilder.name("task-flow.task.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskUpdatedTopic() {
        return TopicBuilder.name("task-flow.task.updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskDeletedTopic() {
        return TopicBuilder.name("task-flow.task.deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name("task-flow.user.registered")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userLoginTopic() {
        return TopicBuilder.name("task-flow.user.login")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Dead Letter Queue topics
    @Bean
    public NewTopic taskCreatedDlqTopic() {
        return TopicBuilder.name("task-flow.task.created.dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskUpdatedDlqTopic() {
        return TopicBuilder.name("task-flow.task.updated.dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskDeletedDlqTopic() {
        return TopicBuilder.name("task-flow.task.deleted.dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userRegisteredDlqTopic() {
        return TopicBuilder.name("task-flow.user.registered.dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userLoginDlqTopic() {
        return TopicBuilder.name("task-flow.user.login.dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

