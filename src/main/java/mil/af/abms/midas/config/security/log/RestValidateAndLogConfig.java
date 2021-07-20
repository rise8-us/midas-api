package mil.af.abms.midas.config.security.log;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.exception.IllegalRequestBodyException;
import mil.af.abms.midas.exception.IllegalRequestHeadersException;
import mil.af.abms.midas.exception.InvalidInputParameterException;
import mil.af.abms.midas.exception.InvalidUserInputException;
import mil.af.abms.midas.exception.UnexpectedEncodingException;

@Slf4j
@Configuration
public class RestValidateAndLogConfig {

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new LoggableDispatcherServlet();
    }

    @MultipartConfig(location = "/tmp",
            fileSizeThreshold = 0,
            maxFileSize = 5242880,       // 5 MB
            maxRequestSize = 20971520)   // 20 MB
    private static class LoggableDispatcherServlet extends DispatcherServlet {

        private static final long serialVersionUID = 4844475275751945172L;

        @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
        public StandardServletMultipartResolver multipartResolver() {
            return new StandardServletMultipartResolver();
        }

        @Override
        protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (!(request instanceof ContentCachingRequestWrapper)) {
                request = new ContentCachingRequestWrapper(request);
            }
            if (!(response instanceof ContentCachingResponseWrapper)) {
                response = new ContentCachingResponseWrapper(response);
            }
            HandlerExecutionChain handler = getHandler(request);
            try {
                validate(request);
                super.doDispatch(request, response);
            } finally {
                requestLog(request, response, handler);
                updateResponse(response);
            }
        }

        @Override
        protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
            log.warn("No mapping for {} {}", request.getMethod(), getRequestUri(request));
            super.noHandlerFound(request, response);
        }

        private void requestLog(HttpServletRequest requestToCache, HttpServletResponse responseToCache, HandlerExecutionChain handler) {
            log.debug("status {}, uri {}, method {}, client {}, javaMethod {}, response {}",
                    responseToCache.getStatus(), requestToCache.getRequestURI(), requestToCache.getMethod(), requestToCache.getRemoteAddr(),
                    handler.toString(), log.isTraceEnabled() ? getResponsePayload(responseToCache) : "(enable trace logging)");
        }

        private String getResponsePayload(HttpServletResponse response) {
            ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    int length = Math.min(buf.length, 5120);
                    try {
                        return new String(buf, 0, length, wrapper.getCharacterEncoding());
                    } catch (UnsupportedEncodingException ex) {
                        // NOOP
                    }
                }
            }
            return "[unknown]";
        }

        private void updateResponse(HttpServletResponse response) throws IOException {
            ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
            assert responseWrapper != null : "Found null native response from: " + response;
            responseWrapper.copyBodyToResponse();
        }

        private static String getRequestUri(HttpServletRequest request) {
            String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
            if (uri == null) {
                uri = request.getRequestURI();
            }
            return uri;
        }

        private static void validate(HttpServletRequest request) {
            validateEncoding(request);
            validateInputParameters(request);
            validateHeaders(request);
            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod()))
            {
                validateBody(request);
            }
        }

        private static void validateBody(HttpServletRequest request) {
            //5e6 = 5 mb
            if (request.getContentLengthLong() > 5e6) {
                log.error(String.format("Invalid excessively large body content length: %s bytes", request.getContentLengthLong()));
                 throw new IllegalRequestBodyException(String.format("Invalid excessively large body content length: %s bytes", request.getContentLengthLong()));
            }

        }

        private static void validateHeaders(HttpServletRequest request) {
            AtomicInteger numHeaders = new AtomicInteger();
            forEachRemaining(request.getHeaderNames(), (header -> {
                numHeaders.incrementAndGet();
                forEachRemaining(request.getHeaders(header), (LoggableDispatcherServlet::validate));
            }));

            if (numHeaders.get() > 128) {
                log.error(String.format("Invalid excessively large number of headers: %s", numHeaders.get()));
                throw new IllegalRequestHeadersException(String.format("Invalid excessively large number of headers: %s", numHeaders.get()));
            }
        }

        private static void validateInputParameters(HttpServletRequest request) {
                forEachRemaining(request.getParameterNames(), inputParameterName -> {
                for (String paramValue : request.getParameterValues(inputParameterName)) {
                    validateInputParameter(paramValue);
                    validate(paramValue);
                }
            });
        }

        private static void validateEncoding(HttpServletRequest request) {
            if (!request.getCharacterEncoding().equals(StandardCharsets.UTF_8.name())) {
                log.warn("Unexpected encoding detected: {}", request.getCharacterEncoding());
                throw new UnexpectedEncodingException(String.format("Unexpected encoding detected: %s", request.getCharacterEncoding()));
            }
        }

        private static void validateInputParameter(String inputParameter) {
            if (inputParameter.contains("%")) {
                log.warn("Invalid input parameter, contains %: {}", inputParameter);
                log.warn("Invalid input parameter, contains %: {}", inputParameter);
                throw new InvalidInputParameterException("Invalid input parameter, contains %: " + inputParameter);
            }
        }

        // helper for enumeration class
        public static <T> void forEachRemaining(Enumeration<T> e, Consumer<? super T> c) {
            while (e.hasMoreElements()) c.accept(e.nextElement());
        }

        private static final Pattern NEWLINE_PATTERN = Pattern.compile("[\r\n\t]+");

        private static final Pattern VALID_UTF8_PATTERN = Pattern.compile("[" +
                "\\x09\\x0A\\x0D\\x20-\\x7E" + //ASCII
                "|[\\xC2-\\xDF][\\x80-\\xBF]" + //non-overlong 2-byte
                "|\\xE0[\\xA0-\\xBF][\\x80-\\xBF]" + //excluding overlongs
                "|[\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}" + //straight 3-byte
                "|\\xED[\\x80-\\x9F][\\x80-\\xBF]" + //excluding surrogates
                "|\\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}" + //planes 1-3
                "|[\\xF1-\\xF3][\\x80-\\xBF]{3}" + // planes 4-15
                "|\\xF4[\\x80-\\x8F][\\x80-\\xBF]{2}" + //plane 16
                "]+");

        /**
         * General validation of an input string. Looks for and logs suspicious characters including the null byte, newlines, etc.
         * Note that this doesn't cover the request body, since the request body isn't read until the controller layer is reached,
         * and it is a spring best practice to read request body only ONCE.
         *
         */
        private static void validate(String input) {
            // Log Security Events CIE3 more info @  https://www.owasp.org/index.php/AppSensor_DetectionPoints#CIE3:_Null_Byte_Character_in_File_Request
            // Refuse Null Character
            if (input.contains("\0")) {
                log.error(String.format("Invalid null character found: %s", input));
               throw new InvalidUserInputException(String.format("Invalid null character found: %s", input));
            }

            if (NEWLINE_PATTERN.matcher(input).matches()) {
                log.warn("Newline detected: {}", input);
                throw new InvalidUserInputException(String.format("Newline detected: %s", input));
            }

            if (!VALID_UTF8_PATTERN.matcher(input).matches()) {
                log.error(String.format("Invalid non-printable characters found: %s", input));
                throw new InvalidUserInputException(String.format("Invalid non-printable characters found: %s", input));
            }
        }

    }
}
