package hello.spring_tx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===========================================
 * Project      : spring-tx
 * File Name    : TxBasicTest.java
 * Author       : limkyulee
 * Created Date : 2025. 6. 1. 오후 9:34
 * Updated Date : 2025. 6. 1. 오후 9:34
 * Description  : transction basic test
 * ===========================================
 */

@Slf4j
@SpringBootTest
public class TxBasicTest {

    @Autowired
    BasicService basicService;

    /**
     * @Method proxyCheck
     * @Description 프록시가 적용되어있는지 체크
     */
    @Test
    void proxyCheck(){
        log.info("app class = {}", basicService.getClass());
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @Test
    void tsTest(){
        // proxy 의 함수 호출 > 트랜잭션 적용 대상 여부 확인
        basicService.tx();
        basicService.nonTx();
    }

    @TestConfiguration
    static class TxBasicTestConfiguration {
        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {

        /**
         * @Method tx
         * @Description 트랜잭션 적용 테스트
         */
        @Transactional
        public void tx(){
            log.info("call tx");
            // PLUS : isActualTransactionActive | 형재 쓰레드에 트랜잭션이 적용되어 있는지 확인 가능
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("actual transaction active: {}", actualTransactionActive);
        }

        /**
         * @Method nonTx
         * @Description 트랜잭션 미적용 테스트
         */
        public void nonTx() {
            log.info("call non tx");
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("actual transaction active: {}", actualTransactionActive);
        }
    }
}
