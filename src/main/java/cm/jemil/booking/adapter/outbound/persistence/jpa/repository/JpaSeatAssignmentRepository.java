package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.SeatAssignmentJpa;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper.SeatAssignmentJpaMapper;
import cm.jemil.booking.domain.booking.SeatAssignment;
import cm.jemil.booking.domain.booking.SeatAssignmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaSeatAssignmentRepository implements SeatAssignmentRepository {

    private final SeatAssignmentSpringRepository seatAssignmentSpringRepository;
    private final SeatAssignmentJpaMapper jpaMapper;

    @Override
    public List<SeatAssignment> saveAll(List<SeatAssignment> seatAssignments) {
        List<SeatAssignmentJpa> jpas =
                seatAssignments.stream().map(jpaMapper::toJpa).toList();
        List<SeatAssignmentJpa> saved = seatAssignmentSpringRepository.saveAll(jpas);
        return saved.stream().map(jpaMapper::toDomain).toList();
    }
}
