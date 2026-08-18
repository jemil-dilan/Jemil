package cm.jemil.agency.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "schedules")
@Getter
@Setter
public class ScheduleJpa {
    @Id
    @Column(name = "c_id")
    private UUID id;

    @Column(name = "c_departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "c_total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "c_available_seats", nullable = false)
    private int availableSeats;
}
