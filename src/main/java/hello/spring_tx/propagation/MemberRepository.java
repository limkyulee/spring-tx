package hello.spring_tx.propagation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ===========================================
 * Project        : hello.spring_tx.propagation
 * File Name       : MemberRepository
 * Author         : pneum
 * Created Date   : 2025-06-07
 * Updated Date   : 2025-06-07
 * Description    :
 * ===========================================
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    @Transactional
    public void save(Member member) {
        log.info("저장");
        em.persist(member);
    }

    public Optional<Member> findByUserName(String userName) {
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", userName)
                .getResultList().stream().findAny();
    }
}
