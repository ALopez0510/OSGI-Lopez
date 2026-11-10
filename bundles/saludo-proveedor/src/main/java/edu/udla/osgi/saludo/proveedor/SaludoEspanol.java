package edu.udla.osgi.saludo.proveedor;

import java.util.concurrent.atomic.AtomicInteger;

import edu.udla.osgi.saludo.api.ServicioSaludo;

/** Implementacion privada del contrato. Ningun otro bundle puede ver esta clase. */
class SaludoEspanol implements ServicioSaludo {

    private final AtomicInteger atendidas = new AtomicInteger();

    @Override
    public String saludar(String nombre) {
        int n = atendidas.incrementAndGet();
        return "Hola, " + nombre + "! (saludo #" + n + ")";
    }

    int atendidas() {
        return atendidas.get();
    }
}
