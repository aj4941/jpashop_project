package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") // FK
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // FK
    private Order order;

    private int orderPrice;
    private int count;
}
