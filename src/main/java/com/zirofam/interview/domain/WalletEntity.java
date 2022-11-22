package com.zirofam.interview.domain;

import com.zirofam.interview.config.AppConstants;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", doNotUseGetters = true, callSuper = false)
@Entity
@Table(name = AppConstants.TABLE_PREFIX + "wallet")
public class WalletEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, length = 50)
    private String id;

    @Column(name = "user", unique = true)
    private String user;

    @Column(precision = 10, scale = 2, name = "balance")
    private BigDecimal balance;

    private String creationDate;
}
