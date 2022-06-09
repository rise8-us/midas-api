package mil.af.abms.midas.config.security.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String SOCKET_ROOT = "/midas-websocket";  // path on which socket is opened
    public static final String MESSAGE_TOPIC = "/topic";    // used to send messages to all clients
    public static final String SPECIFIC_USER = "/user";
    public static final String QUEUE = "/queue";
    public static final String MESSAGE_APP = "/app";             // used to filter incoming messages from an app instance

    @Value("${custom.allowedOrigin}")
    private String origin;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(SOCKET_ROOT).setAllowedOriginPatterns(origin).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(MESSAGE_APP);
        registry.enableSimpleBroker(MESSAGE_TOPIC, QUEUE, SPECIFIC_USER)
                .setTaskScheduler(taskScheduler())
                .setHeartbeatValue(new long[] {10000L, 10000L});
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }


}
