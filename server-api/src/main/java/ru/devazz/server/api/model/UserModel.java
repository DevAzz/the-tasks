package ru.devazz.server.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode
@ToString
@Data
public class UserModel implements Serializable, IEntity{

    /** Идентификатор пользователя */
    private Long suid;

    /** Логин */
    private String login;

    /** Пароль */
    private String password;

    /** Идентификатор роли */
    private Long idRole;

    /** Идентификатор элемнета подчиненности */
    private Long positionSuid;

    /** ФИО пользователя */
    private String name;

    /** Должность пользователя */
    private String position;

    /** Прикрепляемое изображение */
    private byte[] image;

    /** Флаг того, что пользователь находится в сети */
    private Boolean online;

}
