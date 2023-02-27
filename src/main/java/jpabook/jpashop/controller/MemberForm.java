package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
// Form : dto와 다른 점은 제약을 더 두어서 컨트롤러 까지만 사용해야 한다는 의미를 강조한 것
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수입니다.")
    private String name;

    private String city, street, zipcode;

}
