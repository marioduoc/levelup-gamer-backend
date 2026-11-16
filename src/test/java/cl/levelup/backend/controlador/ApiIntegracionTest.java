package cl.levelup.backend.controlador;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

// levanta la app completa con H2 y prueba los endpoints con seguridad incluida
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegracionTest {

    private static final String PRODUCTO_NUEVO = """
            {"codigo":"TS001","nombre":"Producto test","precio":1000,"oferta":0,
             "stock":5,"stockCritico":1,"categoriaId":1}""";

    private static final String COMPRA = """
            {"nombre":"Camila","apellidos":"Muñoz","correo":"cliente@gmail.com","calle":"Calle 3",
             "region":"Valparaíso","comuna":"Viña del Mar","tarjeta":"%s",
             "items":[{"codigo":"%s","cantidad":%d}]}""";

    @Autowired
    private MockMvc mvc;

    // ---------- autenticación ----------

    @Test
    void loginCorrectoEntregaTokensYDatosDelUsuario() throws Exception {
        mvc.perform(json(post("/api/v1/auth/login"), credenciales("admin@duoc.cl", "Admin123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.usuario.rol").value("ADMIN"))
                .andExpect(jsonPath("$.usuario.password").doesNotExist());
    }

    @Test
    void loginConClaveIncorrectaDa401() throws Exception {
        mvc.perform(json(post("/api/v1/auth/login"), credenciales("admin@duoc.cl", "otra")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos."));
    }

    @Test
    void refreshTokenRenuevaYAccessTokenNoSirveComoRefresh() throws Exception {
        String respuesta = mvc.perform(json(post("/api/v1/auth/login"), credenciales("cliente@gmail.com", "Cliente123!")))
                .andReturn().getResponse().getContentAsString();
        String access = JsonPath.read(respuesta, "$.accessToken");
        String refresh = JsonPath.read(respuesta, "$.refreshToken");

        mvc.perform(json(post("/api/v1/auth/refresh"), "{\"refreshToken\":\"" + refresh + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

        mvc.perform(json(post("/api/v1/auth/refresh"), "{\"refreshToken\":\"" + access + "\"}"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/api/v1/usuarios/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + refresh))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenAlteradoDa401() throws Exception {
        String token = token("cliente@gmail.com", "Cliente123!");
        mvc.perform(get("/api/v1/usuarios/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token + "x"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("El token no es válido."));
    }

    @Test
    void registroValidaLosCamposYCreaCliente() throws Exception {
        mvc.perform(json(post("/api/v1/auth/registro"), """
                {"run":"123","nombre":"","apellidos":"X","correo":"x@hotmail.com","password":"1",
                 "fechaNacimiento":"2020-01-01","region":"R","comuna":"C","direccion":"D"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.run").exists())
                .andExpect(jsonPath("$.campos.correo").exists())
                .andExpect(jsonPath("$.campos.password").exists())
                .andExpect(jsonPath("$.campos.fechaNacimiento").exists());

        mvc.perform(json(post("/api/v1/auth/registro"), """
                {"run":"123456785","nombre":"Pedro","apellidos":"Pérez","correo":"pedro@gmail.com","password":"clave1",
                 "fechaNacimiento":"2000-01-01","region":"Valparaíso","comuna":"Quilpué","direccion":"Calle 9"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        mvc.perform(json(post("/api/v1/auth/login"), credenciales("pedro@gmail.com", "clave1")))
                .andExpect(status().isOk());
    }

    @Test
    void registroConCorreoRepetidoDa409() throws Exception {
        mvc.perform(json(post("/api/v1/auth/registro"), """
                {"run":"123456785","nombre":"Pedro","apellidos":"Pérez","correo":"cliente@gmail.com","password":"clave1",
                 "fechaNacimiento":"2000-01-01","region":"Valparaíso","comuna":"Quilpué","direccion":"Calle 9"}"""))
                .andExpect(status().isConflict());
    }

    // ---------- productos (CRUD + roles) ----------

    @Test
    void catalogoEsPublico() throws Exception {
        mvc.perform(get("/api/v1/productos")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
        mvc.perform(get("/api/v1/productos?ofertas=true")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/v1/productos/JM002"))
                .andExpect(jsonPath("$.precioFinal").value(19992));
        mvc.perform(get("/api/v1/productos/XX999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/categorias")).andExpect(status().isOk());
    }

    @Test
    void crearProductoSinTokenDa401YConClienteDa403() throws Exception {
        mvc.perform(json(post("/api/v1/productos"), PRODUCTO_NUEVO)).andExpect(status().isUnauthorized());
        mvc.perform(json(conToken(post("/api/v1/productos"), token("cliente@gmail.com", "Cliente123!")), PRODUCTO_NUEVO))
                .andExpect(status().isForbidden());
        mvc.perform(json(conToken(post("/api/v1/productos"), token("vendedor@duoc.cl", "Vendedor123!")), PRODUCTO_NUEVO))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminHaceCrudCompletoDeProductos() throws Exception {
        String admin = token("admin@duoc.cl", "Admin123!");

        mvc.perform(json(conToken(post("/api/v1/productos"), admin), PRODUCTO_NUEVO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoria").value("Juegos de Mesa"));

        mvc.perform(json(conToken(post("/api/v1/productos"), admin), PRODUCTO_NUEVO))
                .andExpect(status().isConflict());

        mvc.perform(json(conToken(put("/api/v1/productos/TS001"), admin), PRODUCTO_NUEVO.replace("1000", "2000")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(2000));

        mvc.perform(conToken(delete("/api/v1/productos/TS001"), admin)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/productos/TS001")).andExpect(status().isNotFound());
    }

    @Test
    void productoConVentasNoSeElimina() throws Exception {
        mvc.perform(conToken(delete("/api/v1/productos/JM001"), token("admin@duoc.cl", "Admin123!")))
                .andExpect(status().isConflict());
    }

    @Test
    void stockCriticoSoloAdmin() throws Exception {
        mvc.perform(conToken(get("/api/v1/productos/stock-critico"), token("admin@duoc.cl", "Admin123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("CO001"));
        mvc.perform(conToken(get("/api/v1/productos/stock-critico"), token("vendedor@duoc.cl", "Vendedor123!")))
                .andExpect(status().isForbidden());
    }

    // ---------- boletas ----------

    @Test
    void clienteCompraYSeDescuentaElStock() throws Exception {
        String cliente = token("cliente@gmail.com", "Cliente123!");

        mvc.perform(json(conToken(post("/api/v1/boletas"), cliente), COMPRA.formatted("4111111111111111", "JM002", 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("APROBADA"))
                .andExpect(jsonPath("$.total").value(19992 * 2));

        mvc.perform(get("/api/v1/productos/JM002")).andExpect(jsonPath("$.stock").value(16));
        mvc.perform(conToken(get("/api/v1/boletas/mias"), cliente)).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void compraSinStockDa409() throws Exception {
        mvc.perform(json(conToken(post("/api/v1/boletas"), token("cliente@gmail.com", "Cliente123!")),
                COMPRA.formatted("4111111111111111", "CO001", 3)))
                .andExpect(status().isConflict());
    }

    @Test
    void vendedorVeOrdenesPeroNoCompraNiAnula() throws Exception {
        String vendedor = token("vendedor@duoc.cl", "Vendedor123!");

        mvc.perform(conToken(get("/api/v1/boletas"), vendedor)).andExpect(status().isOk());
        mvc.perform(conToken(get("/api/v1/boletas/1"), vendedor)).andExpect(status().isOk());
        mvc.perform(json(conToken(put("/api/v1/boletas/1/estado"), vendedor), "{\"estado\":\"ANULADA\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(json(conToken(post("/api/v1/boletas"), vendedor), COMPRA.formatted("4111111111111111", "JM001", 1)))
                .andExpect(status().isForbidden());
        mvc.perform(conToken(get("/api/v1/usuarios"), vendedor)).andExpect(status().isForbidden());
    }

    @Test
    void clienteNoVeTodasLasOrdenesNiLasDeOtros() throws Exception {
        String admin = token("admin@duoc.cl", "Admin123!");
        String compraAdmin = mvc.perform(json(conToken(post("/api/v1/boletas"), admin),
                COMPRA.formatted("4111111111111111", "JM001", 1))).andReturn().getResponse().getContentAsString();
        Integer idBoletaAdmin = JsonPath.read(compraAdmin, "$.id");

        String cliente = token("cliente@gmail.com", "Cliente123!");
        mvc.perform(conToken(get("/api/v1/boletas"), cliente)).andExpect(status().isForbidden());
        mvc.perform(conToken(get("/api/v1/boletas/1"), cliente)).andExpect(status().isOk());
        mvc.perform(conToken(get("/api/v1/boletas/" + idBoletaAdmin), cliente)).andExpect(status().isForbidden());
    }

    @Test
    void adminAnulaYDespuesPuedeEliminar() throws Exception {
        String admin = token("admin@duoc.cl", "Admin123!");

        mvc.perform(conToken(delete("/api/v1/boletas/1"), admin)).andExpect(status().isConflict());
        mvc.perform(json(conToken(put("/api/v1/boletas/1/estado"), admin), "{\"estado\":\"ANULADA\"}"))
                .andExpect(jsonPath("$.estado").value("ANULADA"));
        mvc.perform(get("/api/v1/productos/JM001")).andExpect(jsonPath("$.stock").value(26));
        mvc.perform(conToken(delete("/api/v1/boletas/1"), admin)).andExpect(status().isNoContent());
    }

    // ---------- usuarios ----------

    @Test
    void usuarioVeYEditaSuPerfil() throws Exception {
        String cliente = token("cliente@gmail.com", "Cliente123!");
        mvc.perform(conToken(get("/api/v1/usuarios/me"), cliente))
                .andExpect(jsonPath("$.correo").value("cliente@gmail.com"));
        mvc.perform(json(conToken(put("/api/v1/usuarios/me"), cliente), """
                {"nombre":"Cami","apellidos":"Muñoz","region":"Valparaíso","comuna":"Quilpué","direccion":"Nueva 1"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comuna").value("Quilpué"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void adminNoPuedeEliminarseNiQuitarseElRol() throws Exception {
        String admin = token("admin@duoc.cl", "Admin123!");
        mvc.perform(conToken(delete("/api/v1/usuarios/1"), admin)).andExpect(status().isConflict());
        mvc.perform(json(conToken(put("/api/v1/usuarios/1"), admin), """
                {"run":"111111111","nombre":"Ana","apellidos":"Rojas","correo":"admin@duoc.cl","rol":"CLIENTE"}"""))
                .andExpect(status().isConflict());
    }

    @Test
    void cuentaDesactivadaNoPuedeEntrar() throws Exception {
        String admin = token("admin@duoc.cl", "Admin123!");
        mvc.perform(json(conToken(put("/api/v1/usuarios/2"), admin), """
                {"run":"222222222","nombre":"Bruno","apellidos":"Soto","correo":"vendedor@duoc.cl",
                 "rol":"VENDEDOR","activo":false}"""))
                .andExpect(status().isOk());
        mvc.perform(json(post("/api/v1/auth/login"), credenciales("vendedor@duoc.cl", "Vendedor123!")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("La cuenta está desactivada."));
    }

    @Test
    void swaggerEstaDisponible() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Level-Up Gamer API"));
    }

    private String token(String correo, String password) throws Exception {
        String respuesta = mvc.perform(json(post("/api/v1/auth/login"), credenciales(correo, password)))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.accessToken");
    }

    private static String credenciales(String correo, String password) {
        return "{\"correo\":\"" + correo + "\",\"password\":\"" + password + "\"}";
    }

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder req, String cuerpo) {
        return req.contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(cuerpo);
    }

    private static MockHttpServletRequestBuilder conToken(MockHttpServletRequestBuilder req, String token) {
        return req.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
