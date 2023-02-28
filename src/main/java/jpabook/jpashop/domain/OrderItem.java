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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // 생성 메서드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item); // 매핑
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count); // 더티 체킹 적용
        return orderItem;
    }

    // 주문 취소 로직
    public void cancel() {
        item.addStock(count); // 재고 수량을 원복 (더티 체킹 적용)
    }

    // 전체 가격 반환
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
