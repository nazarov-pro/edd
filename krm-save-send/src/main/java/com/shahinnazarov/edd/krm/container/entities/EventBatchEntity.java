package com.shahinnazarov.edd.krm.container.entities;

import com.shahinnazarov.edd.krm.container.base.BaseEntity;
import com.shahinnazarov.edd.krm.container.enums.EventBatchStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Email Message Event Entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_event_batches")
@IdClass(EventBatchPrimaryKey.class)
public class EventBatchEntity implements BaseEntity, Serializable {
    private static final long serialVersionUID = -4866895809628580007L;
    @Id
    private EventDataEntity eventDataEntity;
    @Id
    private EventTypeEntity eventTypeEntity;
    @Enumerated(EnumType.ORDINAL)
    private EventBatchStatus status;
    private String lockedBy;
    private LocalDateTime lockingExpirationDate;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @CreationTimestamp
    private LocalDateTime createdAt;

}
