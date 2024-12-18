package com.mango.mangocompanyservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vacancy", schema = "mango")
public class Vacancy {

    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "user_id")
    private long userId;

    @Column(value = "is_anonymous")
    private boolean isAnonymous;

    @Column(value = "company_id")
    private long companyId;

    @Column(value = "title")
    private String title;

    @Column(value = "description")
    private String description;

    @Column(value = "type")
    private Type type;

    @Column(value = "location")
    private String location;

    @Column(value = "salary")
    private BigDecimal salary;

    @CreatedDate
    @Column(value = "created_at")
    private LocalDateTime createdAt;
}
