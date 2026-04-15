package com.houssem.housing_management.Services;
import com.houssem.housing_management.Entities.Bloc;
import com.houssem.housing_management.Enum.TypeChambre;
import com.houssem.housing_management.Repositories.BlocRepository;
import com.houssem.housing_management.Repositories.ChambreRepository;
import com.houssem.housing_management.Repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HousingContextService {

    private final BlocRepository blocRepository;
    private final ChambreRepository chambreRepository;
    private final ReservationRepository reservationRepository;

    public String buildContextForChatbot() {
        StringBuilder context = new StringBuilder();

        // Récupère tous les blocs avec leurs chambres
        var blocs = blocRepository.findAll();

        for (Bloc bloc : blocs) {
            long totalRooms = chambreRepository.countByBlocId(bloc.getIdBloc());
            long reservedRooms = reservationRepository.countByBlocIdAndEstValideTrue(bloc.getIdBloc());

            // Calcule le "score de tranquillité" (plus il y a de chambres simples, plus c'est calme)
            long singleRooms = chambreRepository.countByBlocIdAndTypeC(bloc.getIdBloc(), TypeChambre.SIMPLE);
            long doubleRooms = chambreRepository.countByBlocIdAndTypeC(bloc.getIdBloc(), TypeChambre.DOUBLE);
            long tripleRooms = chambreRepository.countByBlocIdAndTypeC(bloc.getIdBloc(), TypeChambre.TRIPLE);

            int calmScore = (int)(singleRooms * 3 + doubleRooms * 2 + tripleRooms * 1);

            context.append(String.format("""
                - Bloc %s (Capacité: %d places)
                  * Ambiance: %s
                  * Chambres disponibles: %d (sur %d au total)
                  * Détail: %d chambres simples, %d doubles, %d triples
                """,
                    bloc.getNomBloc(),
                    bloc.getCapaciteBloc(),
                    calmScore > 10 ? "très calme" : (calmScore > 5 ? "équilibrée" : "animée"),
                    totalRooms - reservedRooms,
                    totalRooms,
                    singleRooms, doubleRooms, tripleRooms
            ));
        }

        return context.toString();
    }
}



