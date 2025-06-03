package hello.spring_tx.order;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ===========================================
 * Project        : hello.spring_tx.order
 * File Name      : OrderRepository
 * Author         : pneum
 * Created Date   : 2025-06-03 08:57 pm
 * Updated Date   : 2025-06-03 08:57 pm
 * Description    : 주문 JPA Repository - 등록, 수정, 삭제 기본기능
 * ===========================================
 */

public interface OrderRepository extends JpaRepository<Order, Long> {
}
