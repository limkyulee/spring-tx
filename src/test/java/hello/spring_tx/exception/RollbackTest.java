package hello.spring_tx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

/**
 * ===========================================
 * Project        : hello.spring_tx.apply
 * File Name      : RollbackTest
 * Author         : pneum
 * Created Date   : 2025-06-03 08:05 pm
 * Updated Date   : 2025-06-03 08:05 pm
 * Description    : runtimeException flow vs checkedException flow
 * ===========================================
 */

@Slf4j
@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService rollbackService;

    @Test
    void runtimeException() {
        Assertions.assertThatThrownBy(() -> rollbackService.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedException() {
        Assertions.assertThatThrownBy(() -> rollbackService.checkedException())
                .isInstanceOf(MyException.class);
    }

    @Test
    void rollbackFor() {
        Assertions.assertThatThrownBy(() -> rollbackService.rollbackFor())
                .isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfiguration {

        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {

        /**
         * runtimeException
         * 예외 발생 시, 롤백
         */
        @Transactional
        public void runtimeException () {
            log.info("RollbackService runtimeException");
            throw new RuntimeException("RollbackService runtimeException");
        }

        /**
         * checkedException
         * 예외 발생 시, 커밋
         * @throws MyException
         */
        @Transactional
        public void checkedException () throws MyException {
            log.info("RollbackService checkedException");
            throw new MyException();
        }

        /**
         * checkedException rollbackFor
         * 체크 예외 rollbackFor 지정 시, 롤백
         * @throws MyException
         */
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("RollbackService rollbackFor");
            throw new MyException();
        }
    }

    static class MyException extends Exception {
    }
}
