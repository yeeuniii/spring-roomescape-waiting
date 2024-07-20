package roomescape.reservation.application;

import org.springframework.stereotype.Service;
import roomescape.reservation.ui.dto.ReservationRequest;
import roomescape.reservation.ui.dto.ReservationResponse;
import roomescape.reservation.domain.entity.Reservation;
import roomescape.exception.NotFoundException;
import roomescape.reservation.domain.ReservationRepository;

import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationValidator reservationValidator) {
        this.reservationRepository = reservationRepository;
        this.reservationValidator = reservationValidator;
    }

    public List<ReservationResponse> findAll() {
        List<Reservation> reservations = reservationRepository.findAll();
        return ReservationResponse.fromReservations(reservations);
    }

    public ReservationResponse findOne(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("id와 일치하는 예약이 없습니다."));
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse make(ReservationRequest request) {
        reservationValidator.validateRequest(request);
        long reservationId = reservationRepository.save(
                request.memberId(),
                request.date(),
                request.timeId(),
                request.themeId()
        );
        return findOne(reservationId);
    }

    public void cancel(Long id) {
        long deleteCount = reservationRepository.deleteById(id);

        if (deleteCount == 0) {
            throw NotFoundException.of("id와 일치하는 예약이 없습니다.");
        }
    }
}
