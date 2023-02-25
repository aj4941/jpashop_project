package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter // 값 타입은 변경되면 안됨 -> Getter는 제공하고 Setter는 제공하지 않음
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() { } // 자식 클래스가 아니고 다른 패키지라면 접근 불가

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
