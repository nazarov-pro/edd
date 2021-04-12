package com.shahinnazarov.edd.krm.container.entities;

import com.shahinnazarov.edd.krm.container.base.BaseEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Email Message Entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_email_messages")
public class EmailMessageEntity implements BaseEntity, Serializable {
    private static final long serialVersionUID = -5229172918273032419L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "t_email_message_id_seq_gen"
    )
    @SequenceGenerator(
            name = "t_email_message_id_seq_gen",
            sequenceName = "t_email_message_id_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;
    @Column(unique = true, nullable = false)
    private String eventId;
    @Column(nullable = false)
    private String recipients;
    @Column(nullable = false)
    private String content;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @CreationTimestamp
    private LocalDateTime createdAt;

}
