package com.kauz.TicketRapport.models.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemTemplatePojo {
    private UUID id;
    private String description;
    private boolean checked;
}
