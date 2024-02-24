package io.feedpulse.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class ReferralCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String code;

    private LocalDate dateCreated;

    private LocalDate dateExpired;

    private LocalDate dateUsed = null;

    @Nullable
    @OneToOne
    @JoinColumn(name = "used_by")
    private User usedBy = null;

    @Column(nullable = false)
    private boolean isUsed = false;


    @Builder
    public ReferralCode(String code, LocalDate dateExpired) {
        this.code = code;
        this.dateCreated = LocalDate.now();
        this.dateExpired = dateExpired;
    }

    @Builder
    public ReferralCode(String code, LocalDate dateCreated, LocalDate dateExpired, LocalDate dateUsed, @Nullable User usedBy, boolean isUsed) {
        this.code = code;
        this.dateCreated = dateCreated;
        this.dateExpired = dateExpired;
        this.dateUsed = dateUsed;
        this.usedBy = usedBy;
        this.isUsed = isUsed;
    }


}
