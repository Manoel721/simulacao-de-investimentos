package PSI14933.org.invest;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Investimento extends PanacheEntity {
    private String nome;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipo;
    private Float rentabilidade;
    private String risco;

    public Investimento() {
    }

    public Investimento(String nome, String tipo, String risco, Float rentabilidade) {
        this.nome = nome;
        this.tipo = tipo;
        this.risco = risco;
        this.rentabilidade = rentabilidade;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public Float getRentabilidade() {
        return rentabilidade;
    }

    public String getRisco() {
        return risco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
// Métodos para persistência são herdados de PanacheEntity
}
