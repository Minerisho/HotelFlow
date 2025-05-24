package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Cliente;
import entornos.hotelflow.hotel_flow.repositorio.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServicio implements IClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodosLosClientes() {
        return clienteRepositorio.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorId(Integer idCliente) {
        return clienteRepositorio.findById(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorCedula(String cedula) {
        return clienteRepositorio.findByCedula(cedula);
    }

    @Override
    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        if (cliente.getCedula() != null && clienteRepositorio.findByCedula(cliente.getCedula()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con la cédula: " + cliente.getCedula());
        }
        // Aquí puedes añadir más validaciones
        return clienteRepositorio.save(cliente);
    }

    @Override
    @Transactional
    public Cliente actualizarCliente(Integer idCliente, Cliente clienteDetalles) {
        Cliente clienteExistente = clienteRepositorio.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + idCliente));

        // No permitir cambiar cédula si ya existe para otro cliente
        if (clienteDetalles.getCedula() != null && !clienteDetalles.getCedula().equals(clienteExistente.getCedula())) {
            if (clienteRepositorio.findByCedula(clienteDetalles.getCedula()).isPresent()) {
                throw new IllegalArgumentException("La nueva cédula ya está registrada para otro cliente: " + clienteDetalles.getCedula());
            }
            clienteExistente.setCedula(clienteDetalles.getCedula());
        }

        clienteExistente.setNombres(clienteDetalles.getNombres());
        clienteExistente.setApellidos(clienteDetalles.getApellidos());
        clienteExistente.setNacionalidad(clienteDetalles.getNacionalidad());
        clienteExistente.setFechaNacimiento(clienteDetalles.getFechaNacimiento());
        clienteExistente.setGenero(clienteDetalles.getGenero());
        clienteExistente.setProfesion(clienteDetalles.getProfesion());
        clienteExistente.setProcedencia(clienteDetalles.getProcedencia());
        clienteExistente.setDestino(clienteDetalles.getDestino());
        clienteExistente.setFechaLlegada(clienteDetalles.getFechaLlegada()); 
        clienteExistente.setFechaSalida(clienteDetalles.getFechaSalida());  
        clienteExistente.setHabitacion(clienteDetalles.getHabitacion());     
        clienteExistente.setCorreo(clienteDetalles.getCorreo());
        clienteExistente.setTelefonoEmergencia(clienteDetalles.getTelefonoEmergencia());
        clienteExistente.setEps(clienteDetalles.getEps());

        return clienteRepositorio.save(clienteExistente);
    }

    @Override
    @Transactional
    public void eliminarCliente(Integer idCliente) {
        if (!clienteRepositorio.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado con id: " + idCliente);
        }
        // Aquí deberías verificar si el cliente tiene reservas activas, pagos pendientes, etc.
        // antes de permitir la eliminación, o usar borrado lógico.
        clienteRepositorio.deleteById(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorApellidos(String apellidos) {
        return clienteRepositorio.findByApellidosContainingIgnoreCase(apellidos);
    }
}