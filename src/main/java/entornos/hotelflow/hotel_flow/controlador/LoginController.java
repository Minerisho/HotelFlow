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
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private IUsuarioServicio usuarioServicio;

    @PostMapping("/loginclient")
    public ResponseEntity<Integer> loginClient(@RequestBody LoginDTO loginDto) {
        // Usar getUsername() de LoginDTO y pasar getContrasena() como el 'password' esperado por el servicio
        int resultado = usuarioServicio.login(loginDto.getUsername(), loginDto.getContrasena());
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @PostMapping("/ingresar")
    public ResponseEntity<?> ingresar(@RequestBody LoginDTO loginDto) {
        // El servicio 'ingresar' ya toma LoginDTO y manejará el getUsername() internamente.
        Usuario usuario = usuarioServicio.ingresar(loginDto);
        if (usuario == null) {
            return new ResponseEntity<>("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }
}