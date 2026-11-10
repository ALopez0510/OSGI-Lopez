package edu.udla.osgi.saludo.proveedor;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import edu.udla.osgi.saludo.api.ServicioSaludo;

/**
 * Al iniciar el bundle publica el servicio en el registro de OSGi;
 * al detenerlo lo retira. Ademas imprime un "latido" propio para demostrar
 * que sigue vivo aunque el consumidor este detenido.
 */
public class Activator implements BundleActivator {

    private ServiceRegistration<ServicioSaludo> registro;
    private Thread latido;

    @Override
    public void start(BundleContext context) {
        SaludoEspanol servicio = new SaludoEspanol();

        Hashtable<String, Object> props = new Hashtable<>();
        props.put("idioma", "es");
        registro = context.registerService(ServicioSaludo.class, servicio, props);
        System.out.println("[PROVEEDOR] Iniciado: servicio ServicioSaludo publicado.");

        latido = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(10_000);
                    System.out.println("[PROVEEDOR] Sigo activo. Saludos atendidos: " + servicio.atendidas());
                }
            } catch (InterruptedException e) {
                // el bundle se esta deteniendo
            }
        }, "proveedor-latido");
        latido.setDaemon(true);
        latido.start();
    }

    @Override
    public void stop(BundleContext context) {
        latido.interrupt();
        registro.unregister();
        System.out.println("[PROVEEDOR] Detenido: servicio retirado del registro.");
    }
}
