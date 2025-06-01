package entornos.hotelflow.hotel_flow.controlador;

import org.springframework.web.bind.annotation.RestController;

import entornos.hotelflow.hotel_flow.modelos.LoginDTO;
import entornos.hotelflow.hotel_flow.modelos.Usuario;
import entornos.hotelflow.hotel_flow.servicio.IUsuarioServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;


@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private IUsuarioServicio usuarioServicio;

    // Endpoint que devuelve la cantidad de coincidencias (loginClient)
    @PostMapping("/loginclient")
    public ResponseEntity<Integer> loginClient(@RequestBody LoginDTO loginDto) {
        int resultado = usuarioServicio.login(loginDto.getUsername(), loginDto.getPassword());
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    // Endpoint que retorna el objeto Usuario (ingresar)
    @PostMapping("/ingresar")
    public ResponseEntity<?> ingresar(@RequestBody LoginDTO loginDto) {
        
        Usuario usuario = usuarioServicio.ingresar(loginDto);
        if (usuario == null) {
            return new ResponseEntity<>("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }
        // En un escenario real, aquí se puede generar un token o información adicional.
        
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }
}