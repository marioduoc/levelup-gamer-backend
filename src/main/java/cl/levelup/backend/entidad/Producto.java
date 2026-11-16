package cl.levelup.backend.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @Column(length = 10)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Integer precio;

    // porcentaje de descuento, 0 si no está en oferta
    @Column(nullable = false)
    private Integer oferta = 0;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "stock_critico", nullable = false)
    private Integer stockCritico = 0;

    @Column(length = 255)
    private String imagen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public int precioFinal() {
        if (oferta == null || oferta == 0) {
            return precio;
        }
        return Math.round(precio * (100 - oferta) / 100f);
    }

    public boolean enStockCritico() {
        return stock <= stockCritico;
    }
}
