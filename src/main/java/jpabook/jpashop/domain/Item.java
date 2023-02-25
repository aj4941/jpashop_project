package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.LifecycleState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany
    @JoinTable(name = "category_item", // 연관관계의 주인을 category로 잡음
            joinColumns = @JoinColumn(name = "category"), // category 쪽으로 들어가는 컬럼
            inverseJoinColumns = @JoinColumn(name = "item_id")) // item 쪽으로 들어가는 컬럼
    // 중간 테이블 매핑 (일대다, 다대일로 풀어내기 위함)
    // 실무에서는 매핑으로 끝나는 것이 아니라 추가 정보를 중간 테이블에 넣는 경우가 있어서 선호하지 않는 방식이다.
    private List<Category> categories = new ArrayList<>();

}
