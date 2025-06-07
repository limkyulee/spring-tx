package hello.spring_tx.propagation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * ===========================================
 * Project        : hello.spring_tx.propagation
 * File Name      : Member
 * Author         : pneum
 * Created Date   : 2025-06-07 10:44 pm
 * Updated Date   : 2025-06-07 10:44 pm
 * Description    : 회원 entity
 * ===========================================
 */
@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    public Member() {
    }

    public Member(String username) {
        this.username = username;
    }
}
