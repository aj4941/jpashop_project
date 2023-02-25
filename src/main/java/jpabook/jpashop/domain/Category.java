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

    @ManyToMany(mappedBy = "categories")
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne // 부모는 1개이므로 ManyToOne으로 설정
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent") // 부모는 One
    private List<Category> child = new ArrayList<>();
}
