package com.kauz.TicketRapport.models.helpers;

import com.kauz.TicketRapport.models.Ticket;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TicketConverter implements Converter<Ticket, TicketFormData> {

    @Override
    public TicketFormData convert(Ticket source) {
        return new TicketFormData(source.getId(),
                source.getTitle(),
                source.getDescription(),
                source.getProtocol(),
                source.getSolution(),
                getHours(source.getWorktime()),
                getMinutes(source.getWorktime()),
                source.getAssignedUser(),
                source.getClient(),
                source.getStatus());
    }

    public Ticket convert(TicketFormData source) {
        Ticket result = new Ticket(source.getTitle(),
                source.getDescription(),
                source.getProtocol(),
                source.getSolution(),
                getTime(source.getHours(), source.getMinutes()),
                source.getAssignedUser(),
                source.getClient(),
                source.getStatus());
        result.setId(source.getId());
        return result;
    }

    private int getHours(String worktime) {
        if (worktime == null || worktime.isBlank()) return 0;
        return Integer.parseInt(worktime.substring(0, worktime.indexOf(':')));
    }

    private int getMinutes(String worktime) {
        if (worktime == null || worktime.isBlank()) return 0;
        return Integer.parseInt(worktime.substring(worktime.indexOf(':')+1));
    }

    private String getTime(int hours, int minutes) {
        hours = validate(hours, 99);
        minutes = validate(minutes, 59);

        StringBuilder sb = new StringBuilder();
        sb.append(hours < 10 ? "0" + hours : hours).append(":");
        sb.append(minutes < 10 ? "0" + minutes : minutes);
        return sb.toString();
    }

    private int validate(int value, int max) {
        if (value > max) return max;
        return Math.max(value, 0);
    }
}
