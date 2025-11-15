package PSI14933.org.invest;

public class ProdutoValidadoDTO {
    private Long id;
    private String nome;
    private String tipo;
    private Float rentabilidade;
    private String risco;

    // getters e setters

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setRentabilidade(Float rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }
}
