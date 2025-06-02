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
 * File Name    : TxLevelTest.java
 * Author       : limkyulee
 * Created Date : 2025. 6. 2. 오후 10:51
 * Updated Date : 2025. 6. 2. 오후 10:51
 * Description  : 트랜잭션 옵션 우선순위 테스트
 * ===========================================
 */

@SpringBootTest
public class TxLevelTest {

    @Autowired
    LevelService levelService;

    @Test
    void orderTest(){
        levelService.write();
        levelService.read();
    }

    @TestConfiguration
    static class TxLevelTestConfig {
        @Bean
        LevelService levelService(){
            return new LevelService();
        }
    }
    
    @Slf4j
    @Transactional(readOnly = true)
    static class LevelService {

        /**
         * 구체적인 것이 우선순위를 가짐
         * write method transaction > readonly = false (default)
         */
        @Transactional(readOnly = false)
        public void write(){
            log.info("call write");
            printTxInfo();
        }
        
        public void read(){
            log.info("call read");
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
