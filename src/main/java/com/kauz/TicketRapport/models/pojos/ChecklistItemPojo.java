package com.kauz.TicketRapport.models.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItemPojo {
    private String id;
    private String description;
    private boolean completed;
}
