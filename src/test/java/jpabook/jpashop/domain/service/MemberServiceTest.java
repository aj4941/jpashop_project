package jpabook.jpashop.domain.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // 스프링과 통합하여 테스트
@SpringBootTest
@Transactional
// @Rollback(false) : rollback이 되지 않도록 함
// 엔티티 매니저를 통한 모든 데이터 변경은 트랜잭션 안에서 일어나야함
// 트랜잭션이 테스트 케이스에 있으면 테스트가 끝난 후에 바로 rollback 처리 (기본값)
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {

        // given
        Member member = new Member();
        member.setName("테스트");

        // when
        Long savedId = memberService.join(member);

        // then : 저장한 것과 조회한 것은 주소까지 같은 값
        assertEquals(member, memberRepository.findOne(savedId));
    }
}