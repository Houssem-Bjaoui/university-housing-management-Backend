package com.houssem.housing_management.Repositories;

import com.houssem.housing_management.Entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Reservation findByEtudiantCinAndEstValideTrue(Long cin);
    List<Reservation> findByChambreIdChambreAndEstValideTrue(Long idChambre);
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.chambre.bloc.idBloc = :blocId AND r.estValide = true")
    long countByBlocIdAndEstValideTrue(@Param("blocId") Long blocId);
}