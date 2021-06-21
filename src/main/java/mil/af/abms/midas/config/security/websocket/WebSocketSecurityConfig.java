package mil.af.abms.midas.config.security.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;


@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpTypeMatchers(SimpMessageType.DISCONNECT, SimpMessageType.HEARTBEAT).permitAll()  // allow all DISCONNECT and HEARTBEAT
                .simpTypeMatchers(SimpMessageType.CONNECT).authenticated()
                .simpSubscribeDestMatchers("/topic/**").permitAll()
                .anyMessage().permitAll();                                            // disallow everything else
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
