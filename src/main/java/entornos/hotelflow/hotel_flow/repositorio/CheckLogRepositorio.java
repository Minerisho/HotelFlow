package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.CheckLog; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckLogRepositorio extends JpaRepository<CheckLog, Long> {

    Optional<CheckLog> findByReservaIdReservaAndCheckOutIsNull(Integer idReserva);

}