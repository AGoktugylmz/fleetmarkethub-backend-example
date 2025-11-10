package com.cosmosboard.fmh.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "version")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Version extends BaseEntity{

    private String version;
}