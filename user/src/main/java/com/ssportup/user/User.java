package com.ssportup.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Модель пользователя. Записывается в БД в таблицу с имененм usr.
 * @version 0.001
 * @author habatoo
 *
 * @param "userId" - primary key таблицы usr.
 * @param "userName" - имя пользователя - предпоалагается строковоя переменная Имя + Фамилия.
 * @param "userPassword" - пароль, в БД хранится в виде хешированном виде.
 * @param "userEmail" - email пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usr")
public class User {

    @Id
    @SequenceGenerator(
            name = "user_id_sequence",
            sequenceName = "user_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_id_sequence"
    )
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPassword;

}
