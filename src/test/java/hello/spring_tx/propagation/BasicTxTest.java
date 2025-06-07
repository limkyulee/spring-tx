package hello.spring_tx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

/**
 * ===========================================
 * Project        : hello.spring_tx.propagation
 * File Name      : BasicTxTest
 * Author         : pneum
 * Created Date   : 2025-06-04 10:01 pm
 * Updated Date   : 2025-06-07 08:37 pm
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

    /**
     * 외부에서 시작된 물리적인 트랜잭션의 범위가 내부 트랜잭션까지 넓어진다.
     * 외부 트랜잭션과 내부 트랜잭션이 하나의 물리 트랜잭션으로 묶이게 된다.
     */
    @Test
    void inner_commit(){
        log.info("외부 트랜잭션 시작");
        // 외부 트랜잭션만 물리 트랜잭션을 시작하고 커밋한다.
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionDefinition());
        // LOG | outer is New Transaction true
        log.info("outer is New Transaction {}", outer.isNewTransaction());

        // 외부에서 트랜잭션이 이미 수행 중인데 내부에서 트랜잭션 시작.
        inner();

        log.info("외부 트랜잭션 커밋");
        transactionManager.commit(outer);
    }

    private void inner() {
        log.info("내부 트랜잭션 시작");
        // 해당 시점에서 외부 트랜잭션에 참여한다.
        // 기존 트랜잭션에 참여 == 아무것도 하지 않는다.
        // LOG | Participating in existing transaction
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionDefinition());
        // LOG | inner is New Transaction false
        log.info("inner is New Transaction {}", inner.isNewTransaction());

        log.info("내부 트랜잭션 커밋");
        // 해당 시점에서 내부 트랜잭션에 대한 커밋을 실행하지않는다.
        // 내부 트랜잭션은 물리 트랜잭션을 커밋해서는 안된다. (중복 커밋 불가)
        transactionManager.commit(inner);
    }

    /**
     * 외부 트랜잭셕 롤백, 내부 트랜잭션 커밋 시, 모든 트랜잭션이 롤백 되어야한다.
     */
    @Test
    void outer_rollback(){
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionDefinition());

        log.info("내부 트랜잭션 시작");
        // LOG | Participating in existing transaction
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info("내부 트랜잭션 커밋");
        transactionManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        // LOG | Initiating transaction rollback
        transactionManager.rollback(outer);
        // LOG | Rolling back JDBC transaction on Connection
    }

    /**
     * 외부 트랜잭션 커밋, 내부 트랜잭션 롤백 시, 모든 트랜잭션이 롤백 되어야한다.
     * 내부 트랜잭션을 롤백하면 실제 물리 트랜잭션은 롤백하지않는다. (실제 물리 트랜잭션은 롤백은 외부 트랜잭션만 가능)
     * 대신에 기존 트랜잭션을 롤백 전용으로 표시한다.
     * 결과적으로, 외부 트랜잭션이 커밋을 호출했지만, 전체 트랜잭션이 롤백 전용으로 표시되어있어 물리 트랜잭션을 롤백한다.
     */
    @Test
    void inner_rollback(){
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionDefinition());

        log.info("내부 트랜잭션 시작");
        // LOG | Participating in existing transaction
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info("내부 트랜잭션 롤백");
        // LOG | Participating transaction failed - marking existing transaction as rollback-only
        // 기존 트랜잭션을 롤백 전용으로 표시. (rollbackOnly = true)
        transactionManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        // LOG | Global transaction is marked as rollback-only but transactional code requested commit

        // transactionManager.commit(outer);
        // LOG | Initiating transaction rollback
        // [ERROR] UnexpectedRollbackException | 커밋을 호출했지만 롤백이 호출되었으므로 예외 발생.
        Assertions.assertThatThrownBy(() -> transactionManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
    }
}
