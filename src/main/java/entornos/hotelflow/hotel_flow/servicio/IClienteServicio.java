package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Cliente;
// Considera crear ClienteDTO si necesitas transformar la salida
import java.util.List;
import java.util.Optional;

public interface IClienteServicio {
    List<Cliente> listarTodosLosClientes();
    Optional<Cliente> buscarClientePorId(Integer idCliente);
    Optional<Cliente> buscarClientePorCedula(String cedula);
    Cliente guardarCliente(Cliente cliente); // Podrías tomar un ClienteDTO para creación/actualización
    Cliente actualizarCliente(Integer idCliente, Cliente clienteDetalles);
    void eliminarCliente(Integer idCliente);
    List<Cliente> buscarPorApellidos(String apellidos);
    Optional<Cliente> buscarClientePorHabitacion(Integer numeroHabitacion);
    

}