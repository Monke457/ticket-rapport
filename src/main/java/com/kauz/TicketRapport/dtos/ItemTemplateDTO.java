package com.kauz.TicketRapport.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemTemplateDTO {
    private UUID id;
    private String description;
    private boolean checked;
}
