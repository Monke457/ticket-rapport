package com.kauz.TicketRapport.models.mappers;

import com.kauz.TicketRapport.models.pojos.ChecklistItemPojo;
import com.kauz.TicketRapport.models.pojos.ChecklistPojo;

public class ChecklistMapper {

    public ChecklistPojo mapPojo(String[] ids, String[] descriptions, String[] checkboxes) {
        return addItems(new ChecklistPojo(), ids, descriptions, checkboxes);
    }

    public ChecklistPojo mapPojo(boolean save, String name, String[] ids, String[] descriptions, String[] checkboxes) {
        ChecklistPojo result = new ChecklistPojo(save, name);
        return addItems(result, ids, descriptions, checkboxes);
    }

    private ChecklistPojo addItems(ChecklistPojo pojo, String[] ids, String[] descriptions, String[] checkboxes) {
        for (int i = 0; i < ids.length; i++) {
            if (descriptions[i].trim().isBlank()) continue;
            ChecklistItemPojo item = new ChecklistItemPojo(ids[i], descriptions[i], checkboxes[i].equals("true"));
            pojo.getItems().add(item);
        }
        return pojo;
    }
}
