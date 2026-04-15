package com.houssem.housing_management.Repositories;

import com.houssem.housing_management.Entities.Chambre;
import com.houssem.housing_management.Enum.TypeChambre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre, Long> {
    Chambre findByNumeroChambre(Long numeroChambre);
    List<Chambre> findByBlocNomBloc(String nomBloc);
    long countByTypeCAndBlocIdBloc(TypeChambre type, Long idBloc);
    @Query("SELECT COUNT(c) FROM Chambre c WHERE c.bloc.idBloc = :blocId")
    long countByBlocId(@Param("blocId") Long blocId);

    @Query("SELECT COUNT(c) FROM Chambre c WHERE c.bloc.idBloc = :blocId AND c.typeC = :type")
    long countByBlocIdAndTypeC(@Param("blocId") Long blocId, @Param("type") TypeChambre type);
}
