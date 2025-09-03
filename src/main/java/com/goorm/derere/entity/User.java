package com.goorm.derere.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Table(name = "user") // 어느 테이블에 있는 내용인지
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //속성 이름이랑 맞추기
    private Long userid;
    private String username;
    private String email;
    private String location;
    private String usertype;


    // 사용자의 이름이나 이메일을 업데이트하는 메소드 - oauth
    public User update( String name, String email) {
        this.username = name;
        this.email = email;

        return this;
    }
}

