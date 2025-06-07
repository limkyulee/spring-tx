package hello.spring_tx.propagation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
 * ===========================================
 * Project        : hello.spring_tx.propagation
 * File Name      : Log
 * Author         : pneum
 * Created Date   : 2025-06-07 10:49 pm
 * Updated Date   : 2025-06-07 10:49 pm
 * Description    : db 로그 저장 entity
 * ===========================================
 */
@Entity
@Getter
@Setter
public class Log {

    @Id
    @GeneratedValue
    private Long id;
    private String message;

    public Log() {}

    public Log(String message) {
        this.message = message;
    }
}
