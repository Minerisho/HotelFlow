package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.ReservaDTO;
import entornos.hotelflow.hotel_flow.modelos.ReservaRequestDTO;
import entornos.hotelflow.hotel_flow.servicio.IReservaServicio;
import jakarta.validation.Valid; // Para validar el RequestBody
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ReservaControlador {

    @Autowired
    private IReservaServicio reservaServicio;

    // Crear una nueva reserva
    @PostMapping
    public ResponseEntity<ReservaDTO> crearReserva(@Valid @RequestBody ReservaRequestDTO reservaRequest) {
         ReservaDTO nuevaReserva = reservaServicio.crearReserva(reservaRequest);
         return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }

    // Obtener una reserva por ID
    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaDTO> obtenerReservaPorId(@PathVariable Integer idReserva) {
        ReservaDTO reserva = reservaServicio.buscarReservaPorId(idReserva);
        return ResponseEntity.ok(reserva);
    }

    // Listar todas las reservas
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarTodas() {
        List<ReservaDTO> reservas = reservaServicio.listarTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }


    // Listar reservas por ID de usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaDTO>> listarReservasPorUsuario(@PathVariable Long idUsuario) {
        List<ReservaDTO> reservas = reservaServicio.listarReservasPorUsuario(idUsuario);
        return ResponseEntity.ok(reservas);
    }

    // Cancelar una reserva
    @PatchMapping("/{idReserva}/cancelar")
    public ResponseEntity<ReservaDTO> cancelarReserva(@PathVariable Integer idReserva) {
        ReservaDTO reservaCancelada = reservaServicio.cancelarReserva(idReserva);
        return ResponseEntity.ok(reservaCancelada);
    }

    @PatchMapping("/{idReserva}/confirmar")
    public ResponseEntity<ReservaDTO> confirmarReserva(@PathVariable Integer idReserva) {
        ReservaDTO reservaConfirmada= reservaServicio.confirmarReserva(idReserva);
        return ResponseEntity.ok(reservaConfirmada);
    }

}