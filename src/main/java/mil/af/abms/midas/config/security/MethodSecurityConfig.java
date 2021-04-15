package mil.af.abms.midas.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration

@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true) //do we need: "securedEnabled = true"?
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

        @Autowired
        private ApplicationContext context;

        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler();
            expressionHandler.setApplicationContext(context);
            return expressionHandler;
        }

    }
