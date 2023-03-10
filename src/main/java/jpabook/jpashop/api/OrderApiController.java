package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // 엔티티를 노출하는 방식 -> 선호되지 않음
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // 1 + n
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
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

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); // ToOne 관계는 페치 조인으로 가져온다.
        // default_batch_fetch_size: 100 : orders와 관련된 데이터를 100개만큼 당겨옴
        // 1. 페치 조인 쿼리 실행
        // 2. new OrderDto(o)에서 orderItems 호출 시에 where in (?, ?) 이 나오면서 한 번에 처리 (?의 개수가 size 개수)
        // 3. item을 호출 시에 item_id in (?, ?, ?, ?) 이 나오면서 한 번에 처리
        // -> where in 으로 size 만큼 엔티티의 id를 당겨오면서 orderItems와 item을 1개씩 얻어오는 것이 아니라 size 개수만큼 한 번에 얻을 수 있음
        // if) size = 1이면 orderItem은 where in (?)로 쿼리 2번, item은 4번이 나가게 됨
        // 쿼리 호출 횟수가 1 + N + M -> 1 + 1 + 1로 변화
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
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
