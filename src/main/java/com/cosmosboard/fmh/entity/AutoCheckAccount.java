package com.cosmosboard.fmh.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "auto_check_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoCheckAccount extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_auto_check_account_user_id")
    )
    private User user;

    @Column(name = "cid", nullable = false, length = 50)
    private String cid;

    @Column(name = "pwd", nullable = false, length = 50)
    private String pwd;

    @Column(name = "sid", nullable = false, length = 50)
    private String sid;

    @Column(name = "lang", length = 2)
    private String lang;
}