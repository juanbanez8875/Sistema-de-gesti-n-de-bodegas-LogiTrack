package com.logitracksa.project.config;

import com.logitracksa.project.model.entity.Bodega;
import com.logitracksa.project.model.entity.Inventario;
import com.logitracksa.project.model.entity.Producto;
import com.logitracksa.project.model.entity.Usuario;
import com.logitracksa.project.model.enums.Rol;
import com.logitracksa.project.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Inicializa la base de datos con datos de prueba si está vacía.
 * Se ejecuta al arrancar la aplicación.
 *
 * Usuarios por defecto:
 *   admin    / admin123     (rol ADMIN)
 *   empleado / empleado123  (rol EMPLEADO)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                             BodegaRepository bodegaRepository,
                             ProductoRepository productoRepository,
                             InventarioRepository inventarioRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return; // la BD ya tiene datos
        }

        // ----- Usuarios -----
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@logitrack.com");
        admin.setNombre("Administrador General");
        admin.setRol(Rol.ADMIN);
        admin.setEnabled(true);
        usuarioRepository.save(admin);

        Usuario empleado = new Usuario();
        empleado.setUsername("empleado");
        empleado.setPassword(passwordEncoder.encode("empleado123"));
        empleado.setEmail("empleado@logitrack.com");
        empleado.setNombre("Empleado Bodega Norte");
        empleado.setRol(Rol.EMPLEADO);
        empleado.setEnabled(true);
        usuarioRepository.save(empleado);

        // ----- Bodegas -----
        Bodega central = bodega("Bodega Central", "Av. Industrial 100, Guayaquil", 5000, "Carlos Mendoza");
        Bodega norte = bodega("Bodega Norte", "Km 15 Vía a la Costa, Manta", 3000, "Ana Ruiz");
        Bodega sur = bodega("Bodega Sur", "Panamericana Sur 200, Cuenca", 2000, "Luis Tamayo");
        bodegaRepository.save(central);
        bodegaRepository.save(norte);
        bodegaRepository.save(sur);

        // ----- Productos -----
        Producto arroz = producto("Arroz 5kg", "Alimentos", 150, new BigDecimal("4.50"));
        Producto aceite = producto("Aceite 1L", "Alimentos", 80, new BigDecimal("2.80"));
        Producto detergente = producto("Detergente 1kg", "Limpieza", 8, new BigDecimal("3.20"));
        Producto papel = producto("Papel Higiénico", "Limpieza", 200, new BigDecimal("1.50"));
        Producto atun = producto("Atún Lata", "Enlatados", 5, new BigDecimal("1.80"));
        Producto fideos = producto("Fideos 500g", "Alimentos", 300, new BigDecimal("1.20"));
        Producto azucar = producto("Azúcar 1kg", "Alimentos", 0, new BigDecimal("1.10"));
        productoRepository.save(arroz);
        productoRepository.save(aceite);
        productoRepository.save(detergente);
        productoRepository.save(papel);
        productoRepository.save(atun);
        productoRepository.save(fideos);
        productoRepository.save(azucar);

        // ----- Inventario por bodega -----
        inventario(central, arroz, 100);
        inventario(central, aceite, 50);
        inventario(central, detergente, 8);
        inventario(central, papel, 120);
        inventario(central, atun, 5);
        inventario(norte, arroz, 30);
        inventario(norte, fideos, 150);
        inventario(norte, azucar, 0);
        inventario(sur, arroz, 20);
        inventario(sur, papel, 80);
        inventario(sur, fideos, 150);
    }

    private Bodega bodega(String nombre, String ubicacion, int capacidad, String encargado) {
        Bodega b = new Bodega();
        b.setNombre(nombre);
        b.setUbicacion(ubicacion);
        b.setCapacidad(capacidad);
        b.setEncargado(encargado);
        return b;
    }

    private Producto producto(String nombre, String categoria, int stock, BigDecimal precio) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setStock(stock);
        p.setPrecio(precio);
        return p;
    }

    private void inventario(Bodega bodega, Producto producto, int cantidad) {
        Inventario inv = new Inventario();
        inv.setBodega(bodega);
        inv.setProducto(producto);
        inv.setCantidad(cantidad);
        inventarioRepository.save(inv);
    }
}
