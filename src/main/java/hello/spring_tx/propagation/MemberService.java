package hello.spring_tx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ===========================================
 * Project        : hello.spring_tx.propagation
 * File Name      : MemberService
 * Author         : pneum
 * Created Date   : 2025-06-07 11:07
 * Updated Date   : 2025-06-07 11:07
 * Description    : 회원 service
 * ===========================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    /**
     * @Method joinV1
     * @Description 회원가입 | 트랜잭션을 각각 실행하는 예제
     * @param username
     */
    @Transactional
    public void joinV1(String username){
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository 호출 시작 ==");
        memberRepository.save(member);
        log.info("== memberRepository 호출 종료 ==");

        log.info("== logRepository 호출 시작 ==");
        logRepository.save(logMessage);
        log.info("== logRepository 호출 종료 ==");
    }

    /**
     * @Method joinV2
     * @Description joinV1 과 같은 흐름의 회원가입
     *            | 로그 에러 발생 시, 정상 흐름으로 실행되도록 한다.
     * @param username
     */
    public void joinV2(String username){
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository 호출 시작 ==");
        memberRepository.save(member);
        log.info("== memberRepository 호출 종료 ==");

        log.info("== logRepository 호출 시작 ==");
        try {
            logRepository.save(logMessage);
        } catch (RuntimeException e) {
            log.info("log 저장에 실패했습니다. logMessage: {}",logMessage.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("== logRepository 호출 종료 ==");
    }
}
