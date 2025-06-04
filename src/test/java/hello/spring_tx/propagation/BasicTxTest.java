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

    /**
     * 커낵션 풀 사용에 의해 두 트랜잭션은 같은 0번 커넥션을 사용한다.
     * 하지만, 트랜잭션1이 반남을 완료한 커넥션을 트랜잭션2가 획득한 것이기 때문에
     * 둘은 완전히 다른 커넥션으로 인지하는 것이 맞다.
     * 구분법 | 히카리 커넥션 풀에서 커넥션 획득 시,
     *       | 실제 커넥션 반환이 아닌 '내부 관리를 위한 히카리 커넥션 객체를 생성하여 반환'한다.
     *       | 내부에 실제 물리 커넥션이 포함되어있긴함.
     *       | 히카리 커넥션풀이 반환해주는 커넥션이 다루는 프록시 객체 주소를 확인하면 구분이 가능하다. (다름)
     */
    @Test
    void double_commit(){
        log.info("트랜잭션1 시작");
        TransactionStatus status1 = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info("트랜잭션1 커밋");
        transactionManager.commit(status1);

        log.info("트랜잭션2 시작");
        TransactionStatus status2 = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info("트랜잭션2 커밋");
        transactionManager.commit(status2);
    }

    /**
     * 서로 다른 커넥션을 사용하기 때문에 각각의 커밋과 롤백에 영향을 주지않는다.
     */
    @Test
    void double_commit_rollback(){
        log.info("트랜잭션1 시작");
        TransactionStatus status1 = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info("트랜잭션1 커밋");
        transactionManager.commit(status1);

        log.info("트랜잭션2 시작");
        TransactionStatus status2 = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info("트랜잭션2 롤백");
        transactionManager.rollback(status2);
    }
}
