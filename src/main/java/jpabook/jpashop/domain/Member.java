package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // @Embedded, @Embeddable 둘 중 하나만 있어도 되지만 관례상 둘 다 사용함
    private Address address;

    @JsonIgnore // 무한 루프를 막기 위해 선언 (양방향이 걸리는 곳)
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
