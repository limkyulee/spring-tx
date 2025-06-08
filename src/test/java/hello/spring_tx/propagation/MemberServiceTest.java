package hello.spring_tx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ===========================================
 * Project        : hello.spring_tx.propagation
 * File Name      : MemberServiceTest
 * Author         : pneum
 * Created Date   : 2025-06-07 11:20 pm
 * Updated Date   : 2025-06-07 11:20 pm
 * Description    : 회원가입 로직 트랜잭션 테스트
 * ===========================================
 */

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService | @Transactional OFF
     * memberRepository | @Transactional ON
     * logRepository | @Transactional ON
     * 트랜잭션이 분리되어 작용
     */
    @Test
    void outerTxOff_success(){
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUserName(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isPresent());
    }

    /**
     * memberService | @Transactional OFF
     * memberRepository | @Transactional ON
     * logRepository | @Transactional ON Exception
     */
    @Test
    void outerTxOff_fail() {
        // given
        String username = "로그예외_outerTxOff_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);
//      memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUserName(username).isPresent());
        // log 는 단독적으로 rollback 되어 저장되지않음.
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }


    /**
     * memberService | @Transactional ON
     * memberRepository | @Transactional OFF
     * logRepository | @Transactional OFF
     * 트랜잭션이 하나로 묶여서 작용
     */
    @Test
    void singleTx(){
        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUserName(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isPresent());
    }

    /**
     * memberService | @Transactional ON
     * memberRepository | @Transactional ON
     * logRepository | @Transactional ON
     * 트랜잭션이 하나로 묶여서 작용
     */
    @Test
    void outerTxOn_success(){
        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUserName(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isPresent());
    }

    /**
     * memberService | @Transactional ON
     * memberRepository | @Transactional ON
     * logRepository | @Transactional ON Exception
     * 하나의 트랜잭션에서 rollback 시, 모든 트랜잭션 rollback
     * 회원과, 회원 이력 로그를 처리하는 부분을 하나의 트랜잭션으로 묶었다.
     * 때문에, 둘 중 하나에 문제가 발생했을 때 회원과 회원 이력 로그 모두 함께 롤백된다.
     * 따라서, 데이터 정합성에 문제가 발생하지 않는다.
     */
    @Test
    void outerTxOn_fail(){
        // given
        String username = "로그예외_outerTxOn_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);
//      memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUserName(username).isEmpty());
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }

    /**
     * memberService | @Transactional ON
     * memberRepository | @Transactional ON
     * logRepository | @Transactional ON Exception
     * 논리 트랜잭션 중 하나라도 롤백되면 전체 트랜잭션은 롤백된다.
     * 내부 트랜잭션이 롤백되었는데, 외부 트랜잭션이 커밋되면 UnexpectedRollbackException 예외를 던진다.
     * 트랜잭션 AOP 도 전달받은 UnexpectedRollbackException 을 클라이언트에 던진다. (rollbackOnly = true)
     */
    @Test
    void recoverException_fail(){
        // given
        String username = "로그예외_recoverException_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // then
        assertTrue(memberRepository.findByUserName(username).isEmpty());
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }

    /**
     * memberService | @Transactional ON
     * memberRepository | @Transactional ON
     * logRepository | @Transactional ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success(){
        // given
        String username = "로그예외_recoverException_success";

        // when
        memberService.joinV2(username);

        // then
        assertTrue(memberRepository.findByUserName(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }
}