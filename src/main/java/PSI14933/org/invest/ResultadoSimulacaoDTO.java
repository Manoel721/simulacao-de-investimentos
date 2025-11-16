package PSI14933.org.invest;


public class ResultadoSimulacaoDTO {
    private Double valorFinal;
    private Float rentabilidadeEfetiva;
    private Integer prazoMeses;

    // getters e setters

    public Double getValorFinal() {
        return valorFinal;
    }

    public Float getRentabilidadeEfetiva() {
        return rentabilidadeEfetiva;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public void setValorFinal(Double valorFinal) {
        this.valorFinal = valorFinal;
    }

    public void setRentabilidadeEfetiva(float rentabilidadeEfetiva) {
        this.rentabilidadeEfetiva = (Float) rentabilidadeEfetiva;
    }

    public void setPrazoMeses(Integer prazoMeses) {
        this.prazoMeses = prazoMeses;
    }
}