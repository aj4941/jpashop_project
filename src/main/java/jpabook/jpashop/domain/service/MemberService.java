package jpabook.jpashop.domain.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional // JPA의 데이터 변경, 조회는 트랜잭션 안에서 실행되어야 함 (이게 있어야 지연로딩 등이 가능)
// @AllArgsConstructor : 모든 필드를 담은 생성자를 만들어줌
@RequiredArgsConstructor // final이 붙은 필드를 담은 생성자를 만들어줌
public class MemberService {

    private final MemberRepository memberRepository; // final 사용은 필수는 아니지만 권장 (생성자 주입)
    // @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 회원 가입
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        // 아직 DB에 들어가기 전 영속성 컨텍스트에 저장되는 것이지만 getId()로 아이디 조회 가능
        return member.getId();
    }

    // 문제 발생시 exception 발생
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 모든 회원 조회
    @Transactional(readOnly = true) // 조회할 때는 성능 최적화 (변경할 때는 사용하면 안됨)
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 단건 회원 조회
    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
