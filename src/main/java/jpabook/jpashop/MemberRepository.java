package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em; // 스프링 컨테이너에서 엔티티 매니저 주입

    public Long save(Member member) {
        em.persist(member);
        return member.getId(); // 저장을 하고나면 Id 정도만 반환하도록 설계
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
