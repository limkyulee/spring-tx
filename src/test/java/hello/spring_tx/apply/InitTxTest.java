package hello.spring_tx.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * ===========================================
 * Project        : hello.spring_tx.apply
 * File Name      : InitTxTest
 * Author         : kyuleelim
 * Created Date   : 2025-06-03 07:41 pm
 * Updated Date   : 2025-06-03 07:41 pm
 * Description    : 트랜잭션 초기화
 * ===========================================
 */

@Slf4j
@SpringBootTest
public class InitTxTest {

    @Autowired
    Hello hello;

    @Test
    void go(){
        // 초기화 코드는 스프링이 초기화 시점에 호출 (직접 호출 시, 트랜잭션 적용됨)
    }

    @TestConfiguration
    static class InitTxTestConfiguration {
        @Bean
        Hello hello(){
            return new Hello();
        }
    }

    static class Hello {

        /**
         * @PostConstruct 와 @Transactional 을 함께 숑ㅇ하면 트랜잭션이 적용되지 않는다.
         * 설명 | 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP 가 적용되지 때문이다.
         *     | 초기화 시점에 해당 메서드에서 트랜잭션을 획득할 수 없다.
         * 대안 | 컨테이너가 완전히 생성되고 난 후에 트랜잭션 호출.
         */
        @PostConstruct
        @Transactional
        public void initV1() {
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("actual transaction active @PostConstruct : {}", actualTransactionActive);
        }

        /**
         * @EventListener(ApplicationReadyEvent.class)
         * 설명 | 트랜잭션 AOP 를 포함한 스프링이 컨테이너가 완전히 생성되고 난 다음에 해당 이벤트가 붙은 메서드를 호출한다.
         *     | 트랜잭션 적용 가능 대안.
         */
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2() {
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("actual transaction active Application Ready : {}", actualTransactionActive);
        }
    }
}
