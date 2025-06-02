package hello.spring_tx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * ===========================================
 * Project      : spring-tx
 * File Name    : InternalCallV1Test.java
 * Author       : limkyulee
 * Created Date : 2025. 6. 2. 오후 11:08
 * Updated Date : 2025. 6. 2. 오후 11:08
 * Description  : 트랜잭션 내부호출 (프록시의 한계 해결)
 * ===========================================
 */

@Slf4j
@SpringBootTest
public class InternalCallV2Test {

    @Autowired
    CallService callService;

    @Test
    void printProxy() {
        log.info("call service class = {}", callService.getClass());
    }

    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfiguration {

        @Bean
        CallService callService(){
            return new CallService(innerService());
        }

        @Bean
        InternalService innerService(){
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {

        private final InternalService internalService;

        public void external() {
            log.info("call external method");
            printTxInfo();
            // internal 외부호출
            internalService.internal();
        }

        private void printTxInfo(){
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("actual transaction active: {}", actualTransactionActive);
            boolean currentTransactionReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("current transaction readonly: {}", currentTransactionReadOnly);
        }
    }

    static class InternalService {

        @Transactional
        public void internal() {
            log.info("call internal method");
            printTxInfo();
        }

        private void printTxInfo(){
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("actual transaction active: {}", actualTransactionActive);
            boolean currentTransactionReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("current transaction readonly: {}", currentTransactionReadOnly);
        }
    }
}
