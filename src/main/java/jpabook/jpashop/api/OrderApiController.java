package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    // 엔티티를 노출하는 방식 -> 선호되지 않음
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // 강제 초기화
            order.getDelivery().getAddress(); // 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems(); // ;;
            orderItems.stream().forEach(o -> o.getItem().getName()); // ;;
        }

        return all;
    }

    // 엔티티를 Dto로 변환하는 방식 -> N + 1 문제 발생
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
    }

    // 페치조인 최적화 (단점 : 페이징 불가능)
    // DB에서 나오는 결과는 4개, distinct로 줄인 개수는 2개 (DB 기준으로 가져올 때 순서가 안맞음 -> 페이징 불가능)
    // 컬렉션 둘 이상에 페치 조인을 사용하지 말 것 (양이 너무 많아지므로)
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // 값 타입 정도는 노출해도 문제없음
//        private List<OrderItem> orderItems; // dto 안에 엔티티가 있는 상황
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); // 단순 반복하여 강제 초기화
//            orderItems = order.getOrderItems(); // 강제 초기화한 후에 받으면 Item 정보도 얻을 수 있음
            orderItems = order.getOrderItems().stream()
                    .map(o -> new OrderItemDto(o))
                    .collect(Collectors.toList());
        }
    }

    // dto 안에 엔티티가 있는 상황을 방지
    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
