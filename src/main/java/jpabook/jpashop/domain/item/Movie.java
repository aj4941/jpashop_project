package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M") // 싱글 테이블이므로 저장할 때 구분하기 위해 넣는 값
@Getter @Setter
public class Movie extends Item {
    private String director;
    private String actor;
}
