package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A") // 싱글 테이블이므로 저장할 때 구분하기 위해 넣는 값
@Getter @Setter
public class Album extends Item {
    private String artist;
    private String etc;
}
