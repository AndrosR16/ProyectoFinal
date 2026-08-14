package com.ufide.Farmacia.config;

import java.util.List;
import java.util.Map;

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

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Map<String, String> IMAGENES_MEDICAMENTOS = Map.ofEntries(
            Map.entry("Acetaminofén 500mg", "/img/productos/acetaminofen.jpg"),
            Map.entry("Ibuprofeno 400mg", "/img/productos/ibuprofeno.jpg"),
            Map.entry("Amoxicilina 500mg", "/img/productos/amoxicilina.jpg"),
            Map.entry("Azitromicina 500mg", "https://loremflickr.com/600/400/pills,antibiotic?lock=14"),
            Map.entry("Loratadina 10mg", "/img/productos/loratadina.jpg"),
            Map.entry("Cetirizina 10mg", "/img/productos/cetirizina.jpg"),
            Map.entry("Omeprazol 20mg", "/img/productos/omeprazol.jpg"),
            Map.entry("Diclofenac 50mg", "/img/productos/diclofenac.jpg"),
            Map.entry("Metformina 850mg", "/img/productos/metformina.jpg"),
            Map.entry("Losartán 50mg", "/img/productos/losartan.jpg"),
            Map.entry("Salbutamol Inhalador 100mcg", "/img/productos/salbutamol.jpg"),
            Map.entry("Ácido Acetilsalicílico 100mg", "/img/productos/acido-acetilsalicilico.jpg"),
            Map.entry("Dextrometorfano Jarabe", "/img/productos/dextrometorfano.jpg"),
            Map.entry("Complejo B", "/img/productos/complejo-b.jpg"),
            Map.entry("Clotrimazol Crema 1%", "/img/productos/clotrimazol.jpg"));

    private static final Map<String, String> DESCRIPCIONES_MEDICAMENTOS = Map.ofEntries(
            Map.entry(
                    "Acetaminofén 500mg",
                    "Analgésico y antipirético de uso común para el alivio del dolor leve a moderado y la fiebre."),
            Map.entry(
                    "Ibuprofeno 400mg",
                    "Antiinflamatorio no esteroideo utilizado para aliviar el dolor, la inflamación y la fiebre."),
            Map.entry(
                    "Amoxicilina 500mg",
                    "Antibiótico de amplio espectro empleado en el tratamiento de infecciones bacterianas."),
            Map.entry(
                    "Azitromicina 500mg",
                    "Antibiótico macrólido indicado para tratar diversas infecciones bacterianas respiratorias y de la piel."),
            Map.entry(
                    "Loratadina 10mg",
                    "Antihistamínico no sedante utilizado para aliviar los síntomas de alergias como estornudos y picazón."),
            Map.entry(
                    "Cetirizina 10mg",
                    "Antihistamínico utilizado para el alivio de síntomas alérgicos como rinitis y urticaria."),
            Map.entry(
                    "Omeprazol 20mg",
                    "Inhibidor de la bomba de protones que reduce la acidez estomacal en afecciones como gastritis y reflujo."),
            Map.entry(
                    "Diclofenac 50mg",
                    "Antiinflamatorio no esteroideo indicado para el alivio del dolor e inflamación musculoesquelética."),
            Map.entry(
                    "Metformina 850mg",
                    "Medicamento de uso habitual en el control de los niveles de glucosa en sangre. Consulte a su farmacéutico."),
            Map.entry(
                    "Losartán 50mg",
                    "Antihipertensivo utilizado para el control de la presión arterial. Consulte a su farmacéutico."),
            Map.entry(
                    "Salbutamol Inhalador 100mcg",
                    "Broncodilatador en aerosol que ayuda a aliviar la dificultad respiratoria asociada al asma."),
            Map.entry(
                    "Ácido Acetilsalicílico 100mg",
                    "Analgésico y antiagregante plaquetario de uso frecuente. Consulte a su farmacéutico."),
            Map.entry(
                    "Dextrometorfano Jarabe",
                    "Jarabe antitusivo utilizado para aliviar la tos seca irritativa."),
            Map.entry(
                    "Complejo B",
                    "Suplemento vitamínico que aporta vitaminas del grupo B para apoyar el metabolismo energético."),
            Map.entry(
                    "Clotrimazol Crema 1%",
                    "Antimicótico de uso tópico indicado para el tratamiento de infecciones cutáneas por hongos."));

    private final MedicamentoRepository medicamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            MedicamentoRepository medicamentoRepository,
            ClienteRepository clienteRepository,
            ProveedorRepository proveedorRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.medicamentoRepository = medicamentoRepository;
        this.clienteRepository = clienteRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        sembrarProveedores();
        sembrarMedicamentos();
        sembrarClientes();
        sembrarUsuarios();
    }

    private void sembrarMedicamentos() {

        if (medicamentoRepository.count() > 0) {
            return;
        }

        List<Medicamento> medicamentos = List.of(
                medicamento("Acetaminofén 500mg", 1200.0, 150, true),
                medicamento("Ibuprofeno 400mg", 1500.0, 200),
                medicamento("Amoxicilina 500mg", 3200.0, 80, true),
                medicamento("Azitromicina 500mg", 4500.0, 4),
                medicamento("Loratadina 10mg", 1800.0, 120),
                medicamento("Cetirizina 10mg", 2100.0, 90),
                medicamento("Omeprazol 20mg", 2500.0, 100),
                medicamento("Diclofenac 50mg", 1700.0, 60),
                medicamento("Metformina 850mg", 2800.0, 3),
                medicamento("Losartán 50mg", 3100.0, 75),
                medicamento("Salbutamol Inhalador 100mcg", 6500.0, 40, true),
                medicamento("Ácido Acetilsalicílico 100mg", 1400.0, 130),
                medicamento("Dextrometorfano Jarabe", 3800.0, 55),
                medicamento("Complejo B", 2200.0, 70),
                medicamento("Clotrimazol Crema 1%", 2900.0, 45));

        medicamentos.forEach(medicamento -> {

            String imagen =
                    IMAGENES_MEDICAMENTOS.get(
                            medicamento.getNombre()
                    );

            if (imagen != null) {
                medicamento.setImagenUrl(imagen);
            }

            medicamento.setDescripcion(
                    DESCRIPCIONES_MEDICAMENTOS.get(
                            medicamento.getNombre()
                    )
            );
        });

        List<Proveedor> proveedores =
                proveedorRepository.findAll();

        if (!proveedores.isEmpty()) {

            for (int i = 0; i < medicamentos.size(); i++) {

                medicamentos
                        .get(i)
                        .setProveedor(
                                proveedores.get(
                                        i % proveedores.size()
                                )
                        );
            }
        }

        medicamentoRepository.saveAll(
                medicamentos
        );
    }

    private void sembrarClientes() {

        if (clienteRepository.count() > 0) {
            return;
        }

        clienteRepository.saveAll(
                List.of(
                        cliente(
                                "María José Rodríguez Solano",
                                "8712-3456",
                                "maria.rodriguez@gmail.com"
                        ),
                        cliente(
                                "Carlos Andrés Jiménez Vargas",
                                "8834-1298",
                                "carlos.jimenez@hotmail.com"
                        ),
                        cliente(
                                "Ana Lucía Fernández Castro",
                                "7012-4567",
                                "ana.fernandez@yahoo.com"
                        ),
                        cliente(
                                "Luis Fernando Mora Araya",
                                "8901-2345",
                                "luis.mora@gmail.com"
                        ),
                        cliente(
                                "Gabriela Vanessa Chacón Rojas",
                                "8456-7890",
                                "gabriela.chacon@outlook.com"
                        ),
                        cliente(
                                "José Manuel Alvarado Solís",
                                "6123-4589",
                                "jose.alvarado@gmail.com"
                        ),
                        cliente(
                                "Karla Patricia Salas Méndez",
                                "8567-1234",
                                "karla.salas@gmail.com"
                        ),
                        cliente(
                                "Diego Alonso Vargas Quesada",
                                "7089-3456",
                                "diego.vargas@hotmail.com"
                        )
                )
        );
    }

    private void sembrarProveedores() {

        List.of(
                proveedor(
                        "Distribuidora Farmacéutica del Valle S.A.",
                        "22334455",
                        "ventas@delvalle.co.cr"
                ),
                proveedor(
                        "Corporación Médica Costarricense S.A.",
                        "22456789",
                        "contacto@medicorp.co.cr"
                ),
                proveedor(
                        "Droguería Central de Costa Rica S.A.",
                        "22987654",
                        "info@droguecentral.co.cr"
                ),
                proveedor(
                        "Suministros Farmacéuticos Ticos S.A.",
                        "22765432",
                        "pedidos@sufarti.co.cr"
                ),
                proveedor(
                        "Importadora Farmacéutica San José S.A.",
                        "22543210",
                        "servicioalcliente@ifsj.co.cr"
                )
        ).forEach(proveedor -> {

            if (!proveedorRepository.existsByNombre(
                    proveedor.getNombre())) {

                proveedorRepository.save(
                        proveedor
                );
            }
        });
    }

    private void sembrarUsuarios() {

        crearUsuarioSiNoExiste(
                "admin",
                "Administrador del Sistema",
                "admin@farmacia.co.cr",
                "Admin123!",
                "ADMIN"
        );

        crearUsuarioSiNoExiste(
                "empleado",
                "Empleado de Farmacia",
                "empleado@farmacia.co.cr",
                "Empleado123!",
                "EMPLEADO"
        );
    }

    private void crearUsuarioSiNoExiste(
            String username,
            String nombre,
            String correo,
            String password,
            String rol) {

        if (usuarioRepository.existsByUsername(
                username)) {

            return;
        }

        Usuario usuario = new Usuario();

        usuario.setUsername(username);

        usuario.setNombre(nombre);

        usuario.setCorreo(correo);

        usuario.setPassword(
                passwordEncoder.encode(
                        password
                )
        );

        usuario.setRol(rol);

        usuarioRepository.save(
                usuario
        );
    }

    private Medicamento medicamento(
            String nombre,
            Double precio,
            Integer stock) {

        Medicamento medicamento =
                new Medicamento();

        medicamento.setNombre(nombre);
        medicamento.setPrecio(precio);
        medicamento.setStock(stock);

        return medicamento;
    }

    private Medicamento medicamento(
            String nombre,
            Double precio,
            Integer stock,
            boolean destacado) {

        Medicamento medicamento =
                medicamento(
                        nombre,
                        precio,
                        stock
                );

        medicamento.setDestacado(
                destacado
        );

        return medicamento;
    }

    private Cliente cliente(
            String nombre,
            String telefono,
            String correo) {

        Cliente cliente =
                new Cliente();

        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setCorreo(correo);

        return cliente;
    }

    private Proveedor proveedor(
            String nombre,
            String telefono,
            String correo) {

        Proveedor proveedor =
                new Proveedor();

        proveedor.setNombre(nombre);
        proveedor.setTelefono(telefono);
        proveedor.setCorreo(correo);

        return proveedor;
    }
}