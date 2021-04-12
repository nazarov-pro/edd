package com.shahinnazarov.edd.common.container.models;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageEvent implements Serializable {
    private static final long serialVersionUID = -7838809462655420004L;

    private String id;
}
