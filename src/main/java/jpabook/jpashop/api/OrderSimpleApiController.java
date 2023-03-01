package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 무한 루프 발생 (join 코드에서 member 호출)
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // 1. Order 안에 Member 존재 -> Member 안에 orders 존재
        // 2. 양방향 매핑되는 곳에 @JsonIgnore을 붙여주면 member가 프록시 객체이므로 Type definition error 발생
        // -> 지연로딩 이므로 Order만 가져오고 Member는 손대지않음 (이 프록시 객체를 조회하려고 하니 오류 발생)
        // -> 하이버네이트에서 지연로딩인 상태에서 member에 null을 넣어둘 수는 없으므로 가짜 프록시 객체를 넣어둠
        // -> 지연로딩인 경우에는 member를 조회하지 않도록 설정할 수 있음 (Hibernate5Module 추가)
        for (Order order : all) {
            order.getMember().getName();
            // order.getMember() 까지는 프록시 객체
            // .getName() 하는 순간 Lazy 강제 초기화
            order.getDelivery().getAddress(); // 마찬가지로 Lazy 강제 초기화
        }
        // 이 방법을 이용하면 Member, Delivery만 얻을 수 있음
        // (Hibernate5Module을 이용하여 원하는 값만 받음)
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
