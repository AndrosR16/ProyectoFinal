package com.ufide.Farmacia.config;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ufide.Farmacia.entity.Cliente;
import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.entity.Proveedor;
import com.ufide.Farmacia.entity.Usuario;
import com.ufide.Farmacia.repository.ClienteRepository;
import com.ufide.Farmacia.repository.MedicamentoRepository;
import com.ufide.Farmacia.repository.ProveedorRepository;
import com.ufide.Farmacia.repository.UsuarioRepository;

/**
 * Siembra datos de prueba para el entorno de desarrollo.
 *
 * Es idempotente: cada bloque revisa si ya existen datos antes de insertar
 * (por tabla o, en el caso de proveedores, por ítem), por lo que puede
 * ejecutarse en cada reinicio de la aplicación (por ejemplo con devtools)
 * sin duplicar información.
 *
 * Las contraseñas de los usuarios admin/empleado sembrados se pueden
 * definir con las propiedades app.seed.admin-password /
 * app.seed.empleado-password (variables de entorno SEED_ADMIN_PASSWORD /
 * SEED_EMPLEADO_PASSWORD en producción). Si no se definen, se genera una
 * contraseña temporal aleatoria en el arranque y se registra en el log para
 * que quien despliegue el entorno pueda tomarla y cambiarla.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final MedicamentoRepository medicamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String empleadoPassword;
    private final SecureRandom secureRandom = new SecureRandom();

    public DataSeeder(
            MedicamentoRepository medicamentoRepository,
            ClienteRepository clienteRepository,
            ProveedorRepository proveedorRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin-password:}") String adminPassword,
            @Value("${app.seed.empleado-password:}") String empleadoPassword) {

        this.medicamentoRepository = medicamentoRepository;
        this.clienteRepository = clienteRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.empleadoPassword = empleadoPassword;
    }

    @Override
    public void run(String... args) {
        sembrarMedicamentos();
        sembrarClientes();
        sembrarProveedores();
        sembrarUsuarios();
    }

    private void sembrarMedicamentos() {
        if (medicamentoRepository.count() > 0) {
            return;
        }

        medicamentoRepository.saveAll(List.of(
                medicamento("Acetaminofén 500mg", 1200.0, 150),
                medicamento("Ibuprofeno 400mg", 1500.0, 200),
                medicamento("Amoxicilina 500mg", 3200.0, 80),
                medicamento("Azitromicina 500mg", 4500.0, 4),
                medicamento("Loratadina 10mg", 1800.0, 120),
                medicamento("Cetirizina 10mg", 2100.0, 90),
                medicamento("Omeprazol 20mg", 2500.0, 100),
                medicamento("Diclofenac 50mg", 1700.0, 60),
                medicamento("Metformina 850mg", 2800.0, 3),
                medicamento("Losartán 50mg", 3100.0, 75),
                medicamento("Salbutamol Inhalador 100mcg", 6500.0, 40),
                medicamento("Ácido Acetilsalicílico 100mg", 1400.0, 130),
                medicamento("Dextrometorfano Jarabe", 3800.0, 55),
                medicamento("Complejo B", 2200.0, 70),
                medicamento("Clotrimazol Crema 1%", 2900.0, 45)));
    }

    private void sembrarClientes() {
        if (clienteRepository.count() > 0) {
            return;
        }

        clienteRepository.saveAll(List.of(
                cliente("María José Rodríguez Solano", "8712-3456", "maria.rodriguez@gmail.com"),
                cliente("Carlos Andrés Jiménez Vargas", "8834-1298", "carlos.jimenez@hotmail.com"),
                cliente("Ana Lucía Fernández Castro", "7012-4567", "ana.fernandez@yahoo.com"),
                cliente("Luis Fernando Mora Araya", "8901-2345", "luis.mora@gmail.com"),
                cliente("Gabriela Vanessa Chacón Rojas", "8456-7890", "gabriela.chacon@outlook.com"),
                cliente("José Manuel Alvarado Solís", "6123-4589", "jose.alvarado@gmail.com"),
                cliente("Karla Patricia Salas Méndez", "8567-1234", "karla.salas@gmail.com"),
                cliente("Diego Alonso Vargas Quesada", "7089-3456", "diego.vargas@hotmail.com")));
    }

    private void sembrarProveedores() {
        List.of(
                proveedor("Distribuidora Farmacéutica del Valle S.A.", "22334455", "ventas@delvalle.co.cr"),
                proveedor("Corporación Médica Costarricense S.A.", "22456789", "contacto@medicorp.co.cr"),
                proveedor("Droguería Central de Costa Rica S.A.", "22987654", "info@droguecentral.co.cr"),
                proveedor("Suministros Farmacéuticos Ticos S.A.", "22765432", "pedidos@sufarti.co.cr"),
                proveedor("Importadora Farmacéutica San José S.A.", "22543210", "servicioalcliente@ifsj.co.cr"))
                .forEach(proveedor -> {
                    if (!proveedorRepository.existsByNombre(proveedor.getNombre())) {
                        proveedorRepository.save(proveedor);
                    }
                });
    }

    private void sembrarUsuarios() {
        // Idempotencia: si el username ya existe, crearUsuarioSiNoExiste no lo toca,
        // así que las contraseñas de las variables de entorno solo surten efecto en
        // la primera siembra (base vacía); cambiarlas después no actualiza al usuario ya creado.
        // resolverPassword se llama de forma perezosa dentro de crearUsuarioSiNoExiste,
        // solo cuando el usuario en verdad se va a crear.
        crearUsuarioSiNoExiste(
                "admin",
                "Administrador del Sistema",
                "admin@farmacia.co.cr",
                adminPassword,
                "SEED_ADMIN_PASSWORD",
                "ADMIN");

        crearUsuarioSiNoExiste(
                "empleado",
                "Empleado de Farmacia",
                "empleado@farmacia.co.cr",
                empleadoPassword,
                "SEED_EMPLEADO_PASSWORD",
                "USER");
    }

    private String resolverPassword(String valorConfigurado, String username, String nombreVariableEntorno) {
        if (valorConfigurado == null || valorConfigurado.trim().isEmpty()) {
            String passwordGenerada = generarPasswordTemporal();
            log.warn(
                    "No se definio {}; contraseña temporal generada para el usuario '{}': {}. "
                            + "Cambiela desde /perfil o defina {}.",
                    nombreVariableEntorno, username, passwordGenerada, nombreVariableEntorno);
            return passwordGenerada;
        }
        return valorConfigurado;
    }

    private String generarPasswordTemporal() {
        byte[] bytesAleatorios = new byte[12];
        secureRandom.nextBytes(bytesAleatorios);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytesAleatorios);
    }

    private void crearUsuarioSiNoExiste(
            String username,
            String nombre,
            String correo,
            String passwordConfigurada,
            String nombreVariableEntorno,
            String rol) {

        if (usuarioRepository.existsByUsername(username)) {
            return;
        }

        String password = resolverPassword(passwordConfigurada, username, nombreVariableEntorno);

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);

        usuarioRepository.save(usuario);
    }

    private Medicamento medicamento(String nombre, Double precio, Integer stock) {
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(nombre);
        medicamento.setPrecio(precio);
        medicamento.setStock(stock);
        return medicamento;
    }

    private Cliente cliente(String nombre, String telefono, String correo) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setCorreo(correo);
        return cliente;
    }

    private Proveedor proveedor(String nombre, String telefono, String correo) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(nombre);
        proveedor.setTelefono(telefono);
        proveedor.setCorreo(correo);
        return proveedor;
    }
}
