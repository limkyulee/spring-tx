package hello.spring_tx.apply;

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
 * Description  : 트랜잭션 내부호출 (프록시의 한계)
 * ===========================================
 */

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    CallService callService;

    @Test
    void printProxy() {
        log.info("call service class = {}", callService.getClass());
    }

    /**
     * @Method internalCall
     * @Transactional 존재
     * 트랜잭션 프록시 호출 > 트랜잭션 적용
     */
    @Test
    void internalCall() {
        callService.internal();
    }

    /**
     * @Method externalCall
     * @Transactinal 미존재 > 트랜잭션 없이 실행
     * external 내부에서 internal 호출
     * 기대값 > internal 트랜잭션 실행
     * 결과값 > internal 트랜잭션 미실행
     * 사유 | external 은 프록시를 거치지않고 바로 내부 메서드 internal 을 실행하였기 때문 (this.internal)
     *     | 프록시 적용 불가
     */
    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfiguration {
        @Bean
        CallService callService(){
            return new CallService();
        }
    }

    @Slf4j
    static class CallService {

        public void external() {
            log.info("call external method");
            printTxInfo();
            internal();
            // this.internal();
        }

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
