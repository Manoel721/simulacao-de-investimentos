package PSI14933.org.invest;

public class SimulacaoResponseDTO {
    private ProdutoValidadoDTO produtoValidado;
    private ResultadoSimulacaoDTO resultadoSimulacao;
    private String dataSimulacao;

    // getters e setters

    public ProdutoValidadoDTO getProdutoValidado() {
        return produtoValidado;
    }

    public ResultadoSimulacaoDTO getResultadoSimulacao() {
        return resultadoSimulacao;
    }

    public String getDataSimulacao() {
        return dataSimulacao;
    }

    public void setProdutoValidado(ProdutoValidadoDTO produtoValidado) {
        this.produtoValidado = produtoValidado;
    }

    public void setResultadoSimulacao(ResultadoSimulacaoDTO resultadoSimulacao) {
        this.resultadoSimulacao = resultadoSimulacao;
    }

    public void setDataSimulacao(String dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }
}
