package com.jisungin.domain.user;

import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.oauth.OauthId;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "oauth_id_unique",
                        columnNames = {
                                "oauth_id",
                                "oauth_type"
                        }
                ),
        }
)
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Embedded
    private OauthId oauthId;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role role;

    @Builder
    public User(OauthId oauthId, String name, String email, String profileImage, Role role) {
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.role = role;
    }

    public boolean isMe(Long userId) {
        return this.id.equals(userId);
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public User update(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;

        return this;
    }

}
