package hello.spring_tx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}