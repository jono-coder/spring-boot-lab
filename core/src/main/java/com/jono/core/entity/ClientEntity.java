package com.jono.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Objects;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@DynamicInsert
@DynamicUpdate
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @Column(length = 10, nullable = false, unique = true)
    @NonNull
    @NotBlank
    private String accountNo;

    @Column(length = 100, nullable = false)
    @NonNull
    @NotBlank
    private String name;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String note;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    @NonNull
    private ClientStatusEntity status;

    protected void id(final Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof final ClientEntity rhs)) {
            return false;
        }

        return Objects.equals(id, rhs.id());
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

}
