package edu.udla.osgi.saludo.consumidor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import edu.udla.osgi.saludo.api.ServicioSaludo;

/**
 * Usa el servicio buscandolo en el registro por su interfaz (el contrato).
 * No sabe que bundle lo implementa. Si el servicio desaparece, el consumidor
 * NO falla: lo informa y sigue trabajando hasta que vuelva a aparecer.
 */
public class Activator implements BundleActivator {

    private ServiceTracker<ServicioSaludo, ServicioSaludo> tracker;
    private Thread trabajo;

    @Override
    public void start(BundleContext context) {
        tracker = new ServiceTracker<>(context, ServicioSaludo.class, null) {
            @Override
            public ServicioSaludo addingService(ServiceReference<ServicioSaludo> ref) {
                System.out.println("[CONSUMIDOR] Evento: ServicioSaludo DISPONIBLE (idioma="
                        + ref.getProperty("idioma") + ").");
                return super.addingService(ref);
            }

            @Override
            public void removedService(ServiceReference<ServicioSaludo> ref, ServicioSaludo servicio) {
                System.out.println("[CONSUMIDOR] Evento: ServicioSaludo RETIRADO.");
                super.removedService(ref, servicio);
            }
        };
        tracker.open();

        trabajo = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Se pide el servicio en cada ciclo: nunca se guarda una referencia fija.
                    ServicioSaludo servicio = tracker.getService();
                    if (servicio != null) {
                        System.out.println("[CONSUMIDOR] " + servicio.saludar("Karaf"));
                    } else {
                        System.out.println("[CONSUMIDOR] Servicio no disponible; sigo funcionando y reintento...");
                    }
                    Thread.sleep(5_000);
                }
            } catch (InterruptedException e) {
                // el bundle se esta deteniendo
            }
        }, "consumidor-trabajo");
        trabajo.setDaemon(true);
        trabajo.start();
        System.out.println("[CONSUMIDOR] Iniciado.");
    }

    @Override
    public void stop(BundleContext context) {
        trabajo.interrupt();
        tracker.close();
        System.out.println("[CONSUMIDOR] Detenido.");
    }
}
