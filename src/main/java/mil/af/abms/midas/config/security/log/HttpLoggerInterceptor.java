package mil.af.abms.midas.config.security.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpLoggerInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        String user = RequestParser.getUser(request);
        if (response.getStatus() == HttpStatus.OK.value()) {
            log.info(
                "{} {}, USER: {}, SOURCE: {}, CONTENT_TYPE: {}, ENCODING: {}, LENGTH: {}",
                request.getMethod(),
                request.getRequestURI(),
                user,
                RequestParser.getRemoteAddress(request),
                request.getContentType(),
                request.getCharacterEncoding(),
                request.getContentLength()
            );
        }

        if (request.getContentLength() > 2048) {
            log.warn(
                "LARGE REQUEST: {} {}, USER: {}, SOURCE: {}, CONTENT_TYPE: {}, ENCODING: {}, LENGTH: {}",
                request.getMethod(),
                request.getRequestURI(),
                user,
                RequestParser.getRemoteAddress(request),
                request.getContentType(),
                request.getCharacterEncoding(),
                request.getContentLength()
            );
        }

        if (response.getStatus() != HttpStatus.OK.value()) {
            log.error(
                "{} {} {}, USER: {}, SOURCE: {}, CONTENT_TYPE: {}, ENCODING: {}, LENGTH: {}",
                response.getStatus(),
                request.getMethod(),
                request.getRequestURI(),
                user,
                RequestParser.getRemoteAddress(request),
                request.getContentType(),
                request.getCharacterEncoding(),
                request.getContentLength()
            );
        }
    }
}
