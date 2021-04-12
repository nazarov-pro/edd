package com.shahinnazarov.edd.krm.container.entities;

import java.io.Serializable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
public class EventBatchPrimaryKey implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_data_id", referencedColumnName = "ID")
    private EventDataEntity eventDataEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", referencedColumnName = "ID")
    private EventTypeEntity eventTypeEntity;

}
