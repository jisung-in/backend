package com.jisungin.domain.user;

import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.oauth.OauthId;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    @Column(name = "user_profile_image")
    private String profileImage;

    @Builder
    public User(OauthId oauthId, String name, String profileImage) {
        this.oauthId = oauthId;
        this.name = name;
        this.profileImage = profileImage;
    }

    public boolean isMe(Long userId) {
        return this.id.equals(userId);
    }

}
