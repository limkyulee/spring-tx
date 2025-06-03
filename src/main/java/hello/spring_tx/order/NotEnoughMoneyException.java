package hello.spring_tx.order;

/**
 * ===========================================
 * Project        : hello.spring_tx.order
 * File Name      : NotEnoughMoneyException
 * Author         : pneum
 * Created Date   : 2025-06-03 08:49 pm
 * Updated Date   : 2025-06-03 08:49 pm
 * Description    : 잔고부족 시, 발생하는 비즈니스 예외
 * ===========================================
 */

public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
