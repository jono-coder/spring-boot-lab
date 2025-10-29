package com.jono.core.entity;

import com.jono.core.IdDesc;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "ClientStatus")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Cacheable
public class ClientStatusEntity implements IdDesc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Version
    private Integer version;

    @Column(length = 100, nullable = false, unique = true)
    @NotNull
    @NonNull
    @NotBlank
    private String description;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof final ClientStatusEntity rhs)) {
            return false;
        }

        return Objects.equals(description, rhs.description());
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

}
