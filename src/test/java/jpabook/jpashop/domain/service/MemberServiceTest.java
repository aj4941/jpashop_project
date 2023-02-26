package jpabook.jpashop.domain.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // 스프링과 통합하여 테스트
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    // @Rollback(false) : 롤백을 안하고 실제 DB에 반영
    public void JoinTest() throws Exception {
        // given
        Member member = new Member();
        member.setName("테스트");

        // when
        Long savedId = memberService.join(member);

        // then
        // em.flush(); : DB에 반영
        assertEquals(member, memberRepository.findOne(savedId));

        // JPA에서는 같은 트랜잭션 안에서 Id 값 (PK)이 똑같으면 같은 영속성 컨텍스트에서 똑같은 것이 관리됨
        // 따라서 두 객체가 동일하게 나옴
    }

    @Test(expected = IllegalStateException.class) // 테스트에서 해당 예외가 나오면 정상 통과
    public void DuplicateTest() throws Exception {

        // given
        Member member1 = new Member();
        member1.setName("KIM");

        Member member2 = new Member();
        member2.setName("KIM");

        // when
        memberService.join(member1);
//        try {
            memberService.join(member2);
//        } catch (IllegalStateException e) {
//            return;
//        }

        // then : 여기까지 코드가 오면 실패로 간주 (fail)하고 실패 메시지 출력
        fail("예외가 발생해야 한다.");
    }
}