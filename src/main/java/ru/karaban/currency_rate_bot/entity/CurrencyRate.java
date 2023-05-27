package ru.karaban.currency_rate_bot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_rate")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate implements Serializable{

    @Id
    @Column(name = "id")
    private String id;

    @OneToOne
    @JoinColumn(name = "target_currency_iso_code")
    private Currency targetCurrency;

    @OneToOne
    @JoinColumn(name = "source_currency_iso_code")
    private Currency sourceCurrency;

    @Column(name = "rate")
    private BigDecimal rate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
