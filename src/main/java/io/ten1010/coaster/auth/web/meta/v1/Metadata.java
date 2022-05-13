package io.ten1010.coaster.auth.web.meta.v1;

import lombok.*;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Metadata {

    @Nullable
    private Long id;
    @Nullable
    private Integer version;
    @Nullable
    private ZonedDateTime creationTimestamp;

}
