package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.CheckLogDTO; 
import entornos.hotelflow.hotel_flow.modelos.CheckInRequestDTO; 
import entornos.hotelflow.hotel_flow.servicio.ICheckServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/check") 
public class CheckControlador {

    @Autowired
    private ICheckServicio checkServicio;


    @PostMapping("/in")
    public ResponseEntity<CheckLogDTO> realizarCheckIn(@RequestBody CheckInRequestDTO checkInRequest) { 
        CheckLogDTO checkLogDTO = checkServicio.registrarCheckIn(checkInRequest);
        return new ResponseEntity<>(checkLogDTO, HttpStatus.CREATED);
    }

 
    @PostMapping("/out/{idCheckLog}") 
    public ResponseEntity<CheckLogDTO> realizarCheckOut(@PathVariable Long idCheckLog) { 
        CheckLogDTO checkLogDTO = checkServicio.registrarCheckOut(idCheckLog);
        return ResponseEntity.ok(checkLogDTO);
    }

 
     @GetMapping("/{idCheckLog}") 
     public ResponseEntity<CheckLogDTO> obtenerCheckLog(@PathVariable Long idCheckLog) { 
         CheckLogDTO checkLogDTO = checkServicio.buscarCheckLogPorId(idCheckLog);
         if (checkLogDTO != null) {
             return ResponseEntity.ok(checkLogDTO);
         } else {
             return ResponseEntity.notFound().build();
         }
     }
}