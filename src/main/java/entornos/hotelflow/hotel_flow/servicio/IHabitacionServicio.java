package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Habitacion;
import entornos.hotelflow.hotel_flow.modelos.HabitacionDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IHabitacionServicio {
    // Métodos que el compilador parece esperar (adaptados a DTOs donde es lógico)
    HabitacionDTO obtenerHabitacionPorNumero(int numero); // Error indicaba este, devolviendo DTO es consistente
    Habitacion guardarHabitacion(Habitacion habitacion);  // Error indicaba este, devolviendo la entidad (puede ser para uso interno o si no se usa DTO para creación directa)
    List<HabitacionDTO> buscarPorEstado(Habitacion.EstadoHabitacion estado);
    List<HabitacionDTO> buscarPorTipo(Habitacion.TipoHabitacion tipo);
    List<HabitacionDTO> buscarHabitacionesDisponibles(); // Búsqueda general de disponibles (ej. las que tienen el flag 'disponible' en true)

    // Métodos que hemos estado refinando (asegurar que estén y sean consistentes)
    HabitacionDTO crearHabitacion(Habitacion habitacion); // Puede coexistir o reemplazar guardarHabitacion si la lógica es la misma y devuelve DTO
    List<HabitacionDTO> obtenerTodasLasHabitaciones();
    Optional<HabitacionDTO> obtenerHabitacionDTOPorNumero(int numero); // Versión Optional de obtenerHabitacionPorNumero
    Optional<Habitacion> obtenerHabitacionEntidadPorNumero(int numero); // Para uso interno del servicio
    HabitacionDTO actualizarHabitacion(int numero, Habitacion habitacionDetalles);
    void eliminarHabitacion(int numero);
    List<HabitacionDTO> buscarHabitaciones( // Búsqueda combinada
            Habitacion.TipoHabitacion tipo,
            Habitacion.EstadoHabitacion estado,
            Boolean disponible,
            BigDecimal precioMin,
            BigDecimal precioMax
    );
    List<HabitacionDTO> buscarHabitacionesDisponiblesParaFechas( // Búsqueda por disponibilidad en rango de fechas
            LocalDate fechaLlegada,
            LocalDate fechaSalida,
            Habitacion.TipoHabitacion tipo
    );
    HabitacionDTO actualizarEstadoHabitacion(Integer numeroHabitacion, Habitacion.EstadoHabitacion nuevoEstado);
    HabitacionDTO actualizarPrecioHabitacion(Integer numeroHabitacion, BigDecimal nuevoPrecio);
}