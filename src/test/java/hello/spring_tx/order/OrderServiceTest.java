package hello.spring_tx.order;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

/**
 * ===========================================
 * Project        : hello.spring_tx.order
 * File Name      : OrderServiceTest
 * Author         : pneum
 * Created Date   : 2025-06-03 09:07 pm
 * Updated Date   : 2025-06-03 09:07 pm
 * Description    : 주문 로직 트랜잭션 적용 테스트
 * ===========================================
 */

@Slf4j
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {
        // given
        Order order = new Order();
        order.setUsername("정상");
        // when
        orderService.order(order);
        // then
        Order findOrder = orderRepository.findById(order.getId()).get();
        Assertions.assertThat(findOrder.getPayStatus()).isEqualTo("완료");
    }

    @Test
    void runtimeException() {
        // given
        Order order = new Order();
        order.setUsername("예외");
        // when
        // JPA 는 롤백 시, insert 로직을 날리지 않는다.
        Assertions.assertThatThrownBy(() -> orderService.order(order))
                .isInstanceOf(RuntimeException.class);
        // then
        // 정상로직 | 롤백되어 DB 에 반영되지않아 저장된 것이 없어야한다.
        Optional<Order> orderOptional = orderRepository.findById(order.getId());
        Assertions.assertThat(orderOptional.isEmpty()).isTrue();
    }

    @Test
    void bizException() {
        // given
        Order order = new Order();
        order.setUsername("잔고부족");

        // when
        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e){
            log.info("고객에게 잔고 부족을 알라고 별도의 계좌로 입금하도록 안내");
            log.error(e.getMessage());
        }

        // then
        Order findOrder = orderRepository.findById(order.getId()).get();
        Assertions.assertThat(findOrder.getPayStatus()).isEqualTo("대기");
    }
}
