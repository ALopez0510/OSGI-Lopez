package edu.udla.osgi.saludo.api;

/**
 * Contrato del servicio. Es lo UNICO que comparten el bundle proveedor y el
 * bundle consumidor: ninguno conoce las clases del otro.
 */
public interface ServicioSaludo {

    /** Devuelve un saludo para la persona indicada. */
    String saludar(String nombre);
}
