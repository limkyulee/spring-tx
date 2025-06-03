package hello.spring_tx.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * ===========================================
 * Project        : hello.spring_tx.order
 * File Name      : Order
 * Author         : pneum
 * Created Date   : 2025-06-03
 * Updated Date   : 2025-06-03
 * Description    : 주문 클래스 (JPA)
 * ===========================================
 */

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    private Long id;
    private String username; // 정상, 예외, 잔고부족
    private String payStatus; // 대기, 완료
}
