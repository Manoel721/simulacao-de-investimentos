package PSI14933.org.invest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.transaction.Transactional;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    EntityManager em;

    @GET
    public List<Investimento> listarInvestimentos() {
        return simulacaoService.listarTodos();
    }

    @POST
    @Path("/simular-investimento")
    @Transactional
    public Response simularEGravar(Simulacao simulacao) {
        // Buscar o produto pelo tipo informado
        List<Investimento> resultados = em.createQuery(
                        "FROM Investimento i WHERE i.tipo = :tipo", Investimento.class)
                .setParameter("tipo", simulacao.getTipoProduto())
                .setMaxResults(1)
                .getResultList();

        if (resultados.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Produto não encontrado para o tipo: " + simulacao.getTipoProduto())
                    .build();
        }

        Investimento produto = resultados.get(0);

        // Montar DTO do produto validado
        ProdutoValidadoDTO produtoDTO = new ProdutoValidadoDTO();
        produtoDTO.setId(produto.getId());
        produtoDTO.setNome(produto.getNome());
        produtoDTO.setTipo(produto.getTipo());
        produtoDTO.setRentabilidade(produto.getRentabilidade());
        produtoDTO.setRisco(produto.getRisco());

        // Calcular resultado da simulação
        ResultadoSimulacaoDTO resultadoDTO = new ResultadoSimulacaoDTO();
        resultadoDTO.setValorFinal(calculaDados(
                simulacao.getValor(),
                simulacao.getPrazoMeses(),
                produto.getRentabilidade()
        ));
        resultadoDTO.setRentabilidadeEfetiva(produto.getRentabilidade() * simulacao.getPrazoMeses());
        resultadoDTO.setPrazoMeses(simulacao.getPrazoMeses());

        // Montar responseDTO
        SimulacaoResponseDTO responseDTO = new SimulacaoResponseDTO();
        responseDTO.setProdutoValidado(produtoDTO);
        responseDTO.setResultadoSimulacao(resultadoDTO);
        responseDTO.setDataSimulacao(java.time.Instant.now().toString());

        // Persistir entidade no banco
        SimulacaoEntity simulacaoEntity = new SimulacaoEntity();
        simulacaoEntity.setClienteId(simulacao.getClienteId());
        simulacaoEntity.setProduto(produto.getNome());
        simulacaoEntity.setValorInvestido(simulacao.getValor());
        simulacaoEntity.setValorFinal(resultadoDTO.getValorFinal());
        simulacaoEntity.setPrazoMeses(resultadoDTO.getPrazoMeses());
        simulacaoEntity.setDataSimulacao(responseDTO.getDataSimulacao());

        em.persist(simulacaoEntity);

        // Retornar DTO para o cliente
        return Response.status(Response.Status.CREATED)
                .entity(responseDTO)
                .build();
    }

    @GET
    @Path("/simulacoes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SimulacaoEntity> listarSimulacoes() {
        return em.createQuery("FROM SimulacaoEntity", SimulacaoEntity.class)
                .getResultList();
    }

    private Double calculaDados(Double valor, Integer prazoMeses, Float rentabilidade) {
        return Math.ceil(valor + (valor * prazoMeses * rentabilidade));
    }
}
