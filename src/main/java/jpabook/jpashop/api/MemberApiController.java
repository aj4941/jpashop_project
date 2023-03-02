package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 엔티티 값을 그대로 전달
    // 회원과 관련된 API는 여러가지일 것이고 어느 API는 orders가 필요없고 어디서는 address가 필요없고...
    // -> 엔티티를 전달하면 이런 부분에서 혼란이 옴
    // 만약 name 필드가 username으로 바뀌면 API 스펙이 바뀌어버림
    // 엔티티가 화면을 뿌리는 로직에 들어옴 -> 엔티티에 프레젠테이션 계층이 추가된 것임 -> 수정할 때 어려움을 겪음
    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
    }

    // 엔티티 대신 dto를 만들어서 전송하면 원하는 값만 선택적으로 전송할 수 있음
    // List<Member> -> List<MemberDto> -> Result<List<MemberDto>>
    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream() // Stream<Member>
                .map(m -> new MemberDto(m.getName())) // MemberDto
                .collect(Collectors.toList());// List<MemberDto>

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

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

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberRespose updateMemberV2
            (@PathVariable("id") Long id,
             @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id); // update와 find를 분리 (update에서 Member 반환하지 않음)

        return new UpdateMemberRespose(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberRespose {
        private Long id;
        private String name;
    }
}
