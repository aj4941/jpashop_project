package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 엔티티는 필드 값이 바뀔 수가 있는데 이로 인해서 RequestBody 시에 API 오류가 발생할 수 있다.
    // 따라서 dto 객체를 받아서 여러 데이터가 들어와도 해당 dto가 필드를 포함하고 있으면 문제가 되지 않는다.
    // dto는 전송 객체이므로 자유롭게 필드를 추가해도 된다는 장점이 있기 때문에 이런 점에서 API 에러를 방지한다.
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }


}
