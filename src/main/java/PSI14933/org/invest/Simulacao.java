package PSI14933.org.invest;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Simulacao extends PanacheEntity {
    private Long clienteId;
    private Double valor;
    private Integer prazoMeses;
    private String tipoProduto;

    public Long getClienteId() {
        return clienteId;
    }

    public Double getValor() {
        return valor;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }
}
