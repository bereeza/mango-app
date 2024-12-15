package com.mango.mangocompanyservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company", schema = "mango")
public class Company {

    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "name")
    private String name;

    @Column(value = "description")
    private String description;

    @Column(value = "website")
    private String website;

    @Column(value = "logo")
    private String logo;

    @CreatedDate
    @Column(value = "created_at")
    private LocalDateTime createdAt;

    @Column(value = "ceo_id")
    private long ceoId;
}
