package io.ten1010.coaster.auth.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;
import java.util.Optional;

@MappedSuperclass
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public abstract class DomainModel {

    public static final String COL_NAME_ID = "id";

    /**
     * _ prefixed because JpaEntityInformation insists on using property method even though AccessType.FIELD
     */
    @Id
    @Column(name = COL_NAME_ID)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    protected Long _id;
    @Version
    protected Integer version;
    protected Instant creationTimestamp;

    @PrePersist
    public void prePersist() {
        this.creationTimestamp = Instant.now();
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(this._id);
    }

    public Optional<Integer> getVersion() {
        return Optional.ofNullable(this.version);
    }

    public Optional<Instant> getCreationTimestamp() {
        return Optional.ofNullable(this.creationTimestamp);
    }

}
