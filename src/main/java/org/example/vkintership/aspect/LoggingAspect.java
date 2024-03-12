package org.example.vkintership.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.vkintership.entity.LogEntity;
import org.example.vkintership.entity.User;
import org.example.vkintership.repository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.Date;

@Aspect
@Component
public class LoggingAspect {
    @Autowired
    LogsRepository logsRepository;

    @AfterReturning(pointcut = "execution(* org.example.vkintership.controller..*(..))", returning = "reqRes")
    public void logSuccessRequest(JoinPoint joinPoint, Mono<ResponseEntity<String>> reqRes) {

        reqRes.subscribe(response -> {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Date currentDate = new Date();

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest req = attributes.getRequest();
                String requestUri = String.valueOf(req.getRequestURI());

                Long reqParam = null;
                try {
                    String[] reqPaths = requestUri.split("/");
                    reqParam = Long.parseLong(reqPaths[reqPaths.length - 1]);
                    int lastIdx = requestUri.lastIndexOf('/');
                    requestUri = requestUri.substring(0, lastIdx);
                }
                catch (NumberFormatException ignored) {};

                LogEntity logEntity = LogEntity.builder()
                        .userId(user.getId())
                        .httpStatus(response.getStatusCode().value())
                        .requestType(req.getMethod())
                        .requestTime(new Timestamp(currentDate.getTime()))
                        .endpoint(requestUri)
                        .param(reqParam)
                        .build();

                logsRepository.save(logEntity);
            }
        });
    }

    @AfterReturning(pointcut = "execution(* org.example.vkintership.exceptions.GlobalExceptionHandler.*(..))",
            returning = "reqRes")
    public void logErrorRequest(JoinPoint joinPoint, ResponseEntity<String> reqRes) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Date currentDate = new Date();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest req = attributes.getRequest();
            String requestUri = req.getRequestURI();

            Long reqParam = null;
            try {
                String[] reqPaths = requestUri.split("/");
                reqParam = Long.parseLong(reqPaths[reqPaths.length - 1]);
                int lastIdx = requestUri.lastIndexOf('/');
                requestUri = requestUri.substring(0, lastIdx);
            }
            catch (NumberFormatException ignored) {};

            LogEntity logEntity = LogEntity.builder()
                    .userId(user.getId())
                    .httpStatus(reqRes.getStatusCode().value())
                    .requestType(req.getMethod())
                    .requestTime(new Timestamp(currentDate.getTime()))
                    .endpoint(requestUri)
                    .param(reqParam)
                    .build();

            logsRepository.save(logEntity);
        }
    }
}