package hello.spring_tx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

/**
 * ===========================================
 * Project        : hello.spring_tx.propagation
 * File Name      : BasicTxTest
 * Author         : pneum
 * Created Date   : 2025-06-04 10:01 pm
 * Updated Date   : 2025-06-04 10:01 pm
 * Description    : 기본 트랜잭션 전파 테스트
 * ===========================================
 */
@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager transactionManager;

    @TestConfiguration
    static class BasicTxTestConfiguration {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit(){
        log.info("트랜잭션 시작");
        // Transaction manager 에서 트랜잭션을 가져온다.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        log.info("트랜잭션 커밋 시작");
        transactionManager.commit(status);
        log.info("트랜잭션 커밋 완료");

    }

    @Test
    void rollback(){
        log.info("트랜잭션 시작");
        // Transaction manager 에서 트랜잭션을 가져온다.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        log.info("트랜잭션 롤백 시작");
        transactionManager.rollback(status);
        log.info("트랜잭션 롤백 완료");

    }
}
