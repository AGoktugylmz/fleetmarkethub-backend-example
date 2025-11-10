package com.cosmosboard.fmh.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}, name = "uk_users_unique_email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "gsm", length = 25)
    private String gsm;

    @Column(name = "avatar", columnDefinition = "text")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String avatar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_user_roles_user_id"),
            nullable = false
        ),
        inverseJoinColumns = @JoinColumn(
            name = "role_id",
            foreignKey = @ForeignKey(name = "fk_user_roles_role_id"),
            nullable = false
        ),
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "role_id"}, name = "uk_user_roles_unique_user_id_role_id")
        }
    )
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @Transient
    private String emailActivationToken;

    @Transient
    private String gsmActivationToken;

    @Transient
    private String passwordResetToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubscriptionTransaction> subscriptionTransactions = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_favorite_cars",
        joinColumns = @JoinColumn(
            name = "favorite_car_id",
            foreignKey = @ForeignKey(name = "fk_user_favorite_car_car_id"),
            nullable = false
        ),
        inverseJoinColumns = @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_user_favorite_car_car_id"),
            nullable = false
        ),
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "favorite_car_id"}, name = "uk_user_favorite_car_unique_user_id_favorite_car_id")
        }
    )
    @Builder.Default
    private List<Car> favoriteCars = new ArrayList<>();

    @Column(name = "email_activated_at")
    private LocalDateTime emailActivatedAt;

    @Column(name = "gsm_activated_at")
    private LocalDateTime gsmActivatedAt;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private AutoCheckAccount autoCheckAccount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AutoCheckReport> autoCheckReports;

    @Column(name = "has_auto_check", nullable = false)
    @Builder.Default
    @ColumnDefault("false")
    private boolean hasAutoCheck = false;

    public String getFullName() {
        return String.format("%s, %s", lastName, name);
    }
}
