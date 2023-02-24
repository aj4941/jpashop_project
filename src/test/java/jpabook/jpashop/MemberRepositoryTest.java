package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class) // 스프링과 관련한 테스트를 진행 (이걸 안적으면 너무 많은 기능을 가져와서 무거워짐)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    // @Rollback(false) : rollback이 되지 않도록 함
    // 엔티티 매니저를 통한 모든 데이터 변경은 트랜잭션 안에서 일어나야함
    // 트랜잭션이 테스트 케이스에 있으면 테스트가 끝난 후에 바로 rollback 처리 (기본값)
    public void testMember() {
        // given
        Member member = new Member();
        member.setUsername("memberA");
        memberRepository.save(member);

        // when
        Long savedId = memberRepository.save(member);

        // then
        Member findMember = memberRepository.find(savedId);
        assertThat(member).isEqualTo(findMember); // 저장한 것과 조회한 것은 주소까지 같은 값
                                                  // 같은 영속성 컨텍스트이므로 Id 값이 같으면 같은 엔티티로 판별
    }
}