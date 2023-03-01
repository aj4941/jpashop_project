package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    private final EntityManager em;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 무한 루프 발생 (join 코드에서 member 호출)
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // 1. Order 안에 Member 존재 -> Member 안에 orders 존재
        // 2. 양방향 매핑되는 곳에 @JsonIgnore을 붙여주면 member가 프록시 객체이므로 Type definition error 발생
        // -> 지연로딩 이므로 Order만 가져오고 Member는 손대지않음 (이 프록시 객체를 조회하려고 하니 오류 발생)
        // -> 하이버네이트에서 지연로딩인 상태에서 member에 null을 넣어둘 수는 없으므로 가짜 프록시 객체를 넣어둠
        // -> Module를 통해 프록시 객체인 member를 null로 출력하도록 설정할 수 있음 (Hibernate5Module 추가)
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

        // ORDER 2개 (1번 쿼리) : Order 조회 1번에 결과수 N(=2) 발생
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        // LAZY 로딩 2번씩 (2 * 2번 쿼리) -> N + 1 문제 발생
        // order -> member : N번
        // order -> delivery : N번
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    public class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }

    // 페치 조인을 사용하면 쿼리가 1번 실행 (N + 1 문제 해결)
    // ORDER 와 MEMBER를 조인 + 다시 DELIVERY 조인 -> 모두 SELECT 절에 필드들을 출력
    // 1번의 쿼리로 모든 연관관계를 처리 (MEMBER 객체와 DELIVERY 객체가 ORDER 테이블에 묶여서 나옴)
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // JPA에서 DTO로 바로 조회
    // select 절에서 내가 원하는 것만 호출하는 형태
    // API 스펙이 (원하는 dto 내용) repository에 들어가는 문제점이 존재
    // -> dto 내용이 바뀌면 repository 수정이 불가피해짐
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
}
