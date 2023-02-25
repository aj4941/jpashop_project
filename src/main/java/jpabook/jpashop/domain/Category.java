package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item", // 연관관계의 주인을 category로 잡음
            joinColumns = @JoinColumn(name = "category_id"), // category 쪽으로 들어가는 컬럼
            inverseJoinColumns = @JoinColumn(name = "item_id")) // item 쪽으로 들어가는 컬럼
    // 중간 테이블 매핑 (일대다, 다대일로 풀어내기 위함)
    // 실무에서는 매핑으로 끝나는 것이 아니라 추가 정보를 중간 테이블에 넣는 경우가 있어서 선호하지 않는 방식이다.
    private List<Item> items = new ArrayList<>();

    @ManyToOne // 부모는 1개이므로 ManyToOne으로 설정
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent") // 부모는 One
    private List<Category> child = new ArrayList<>();

    // 연관관계 편의 메서드 (셀프 연관관계 매핑)
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
