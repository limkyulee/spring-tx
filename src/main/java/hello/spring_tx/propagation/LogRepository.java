package hello.spring_tx.propagation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * ===========================================
 * Project        : hello.spring_tx.propagation
 * File Name      : LogRepository
 * Author         : pneum
 * Created Date   : 2025-06-07 10:52 pm
 * Updated Date   : 2025-06-07 10:52 pm
 * Description    :
 * ===========================================
 */

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    @Transactional
    public void save(Log logMessage) {
        log.info("log 저장");
        em.persist(log);

        if(logMessage.getMessage().contains("로그예외")) {
            log.info("log. 저장 시 예외 발생");
            throw  new RuntimeException("예외 발생");
        }
    }
}
