package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor // 마찬가지로 EntityManager도 생성자 주입 가능
public class MemberRepository {

    // @PersistenceContext (@Autowired도 가능)
    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
        // JPA에서 em.persist를 하면 그 순간에 영속성 컨텍스트에서 key 값을 member의 id로 인식
        // 따라서 save(member) 후에 member.getId()를 하더라도 값이 있음이 보장됨
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                    .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
