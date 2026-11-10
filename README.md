# OSGi y Apache Karaf: dos bundles desacoplados

Práctica de arquitectura de software: dos bundles OSGi funcionales, con bajo acoplamiento pero inter-relacionados, que se inician y se detienen a demanda en Apache Karaf sin afectar la ejecución del sistema.

## Contenido

| Ruta | Qué es |
|---|---|
| `INFORME_APA7.docx` | Informe: investigación sobre OSGi, implementación y evidencias |
| `bundles/` | Código fuente (proyecto Maven con tres módulos) |
| `dist/` | Los tres JAR ya compilados |
| `evidencias/capturas/` | Capturas de la consola de Karaf |
| `evidencias/sesion-karaf.txt` | Sesión completa de consola de una ejecución de prueba |

## Diseño

| Bundle | Rol |
|---|---|
| `saludo-api` | Contrato: solo la interfaz `ServicioSaludo`. Es el único paquete exportado. |
| `saludo-proveedor` | Implementa el servicio y lo publica en el registro de servicios OSGi. |
| `saludo-consumidor` | Busca el servicio por su interfaz con un `ServiceTracker` y lo usa cada 5 segundos. |

Proveedor y consumidor no se conocen: ninguno exporta paquetes ni depende del otro. Se encuentran en tiempo de ejecución a través del registro de servicios.

## Cómo ejecutarlo

Requisitos: JDK 11 o superior (probado con JDK 21) y Apache Karaf 4.4.8. Karaf no está incluido en el repositorio; se descarga de <https://karaf.apache.org/download.html> y se descomprime dentro de esta carpeta.

1. Opcional, para recompilar los bundles (requiere Maven):

   ```
   cd bundles
   mvn clean package
   ```

2. Arrancar Karaf:

   ```
   apache-karaf-4.4.8\bin\karaf.bat
   ```

3. Instalar e iniciar los bundles desde la consola de Karaf:

   ```
   bundle:install -s file:../dist/saludo-api-1.0.0.jar
   bundle:install -s file:../dist/saludo-proveedor-1.0.0.jar
   bundle:install -s file:../dist/saludo-consumidor-1.0.0.jar
   ```

4. Probar el inicio y la parada en caliente. Los identificadores se consultan con `bundle:list`:

   ```
   bundle:list
   service:list edu.udla.osgi.saludo.api.ServicioSaludo
   bundle:stop <id del proveedor>
   bundle:start <id del proveedor>
   bundle:stop <id del consumidor>
   bundle:start <id del consumidor>
   ```

5. Salir con `shutdown -f`.

## Resultado esperado

- Al detener el proveedor, el consumidor sigue activo e imprime «Servicio no disponible; sigo funcionando y reintento...». Al iniciarlo de nuevo, se reconecta solo.
- Al detener el consumidor, el proveedor sigue activo y conserva su contador de saludos.
