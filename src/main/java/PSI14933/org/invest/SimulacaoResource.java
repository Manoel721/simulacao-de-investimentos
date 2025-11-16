package PSI14933.org.invest;

import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    EntityManager em;

    @Inject
    SPPDRepository SPPDR;

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
        responseDTO.setDataSimulacao(Instant.now().toString());

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

    @GET
    @Path("/simulacoes/por-produto-dia")
    @Produces(MediaType.APPLICATION_JSON)
    public String listarPorProdutoDia() throws JsonProcessingException {

        List<SimulacaoEntity> listaDatas = SPPDR.listAll();
        Set<String> dattas = new HashSet<>(Set.of());

        String jsonSaida = null;
        for (SimulacaoEntity s1 : listaDatas){
            dattas.add (s1.getDataSimulacao().substring(0, 10));
        }
        List<SimulacaoEntity> simulacoes = SPPDR.listAll();
        for (String data : dattas) {
            // Agrupamento por produto
            Map<String, List<SimulacaoEntity>> agrupado = simulacoes.stream()
                    .collect(Collectors.groupingBy(SimulacaoEntity::getProduto));

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode resultado = mapper.createArrayNode();

            for (Map.Entry<String, List<SimulacaoEntity>> entry : agrupado.entrySet()) {
                String produto = entry.getKey();
                List<SimulacaoEntity> lista = entry.getValue();

                int quantidade = lista.size();
                double mediaValorFinal = lista.stream().mapToDouble(SimulacaoEntity::getValorFinal).average().orElse(0);

                ObjectNode obj = mapper.createObjectNode();
                obj.put("produto", produto);
                obj.put("data", data); // Data fixa conforme exemplo
                obj.put("quantidadeSimulacoes", quantidade);
                obj.put("mediaValorFinal", Math.round(mediaValorFinal * 100.0) / 100.0);

                resultado.add(obj);
            }
            // Saída JSON
            jsonSaida = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultado);
        }
        System.out.println(jsonSaida);

       return jsonSaida;
    }

    @GET
    @Path("/perfil-risco/{clienteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String listarPerfilRisco(@PathParam("clienteId") Long clienteId) throws JsonProcessingException {
        List<SimulacaoEntity> simulacoes = SPPDR.listAll();
        double volumeInvestimentos = 0.0;
        int frequenciaMovimentacoes = 0;
        String preferencia = null;
        double mediaTempoResgate = 0.0;
        for (SimulacaoEntity s1 : simulacoes){
            if (s1.getClienteId().equals(clienteId)){
                volumeInvestimentos += s1.getValorInvestido();
                frequenciaMovimentacoes += 1;
                mediaTempoResgate += s1.getPrazoMeses();
            }
            if (frequenciaMovimentacoes>0) {
                mediaTempoResgate = mediaTempoResgate / frequenciaMovimentacoes;
            }
            if (mediaTempoResgate>=60){preferencia = "liquidez";}
            else if (mediaTempoResgate<13) {preferencia = "rentabilidade";}
            else {preferencia = "equilibrio";}
        }
        MotorRecomendacao perfil1 = new MotorRecomendacao (clienteId, volumeInvestimentos, frequenciaMovimentacoes, preferencia);
        System.out.println(perfil1.toString());
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // Then use this mapper to serialize your object
        String json = mapper.writeValueAsString(perfil1);
        return json;
    }

    private Double calculaDados(Double valor, Integer prazoMeses, Float rentabilidade) {
        return (Double) Math.ceil(valor + (valor * prazoMeses * rentabilidade));
    }
}
