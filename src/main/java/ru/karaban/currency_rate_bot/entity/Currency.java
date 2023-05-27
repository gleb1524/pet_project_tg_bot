package ru.karaban.currency_rate_bot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Currency {

    @Column(name = "code")
    private String code;
    @Id
    @Column(name = "iso_code")
    private Long isoCode;

    @Column(name = "rate_to_rub")
    private BigDecimal rateToRUB;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
