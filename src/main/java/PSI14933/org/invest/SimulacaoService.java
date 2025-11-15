package PSI14933.org.invest;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    public Investimento simular(Investimento investimento) {


        /*BigDecimal valorAcumulado = BigDecimal.valueOf(investimento.valorInicial);
        BigDecimal aporte = BigDecimal.valueOf(investimento.aporteMensal);
        BigDecimal taxaJurosMensal = BigDecimal.valueOf(investimento.taxaJuros).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        for (int i = 0; i < investimento.periodoMeses; i++) {
            valorAcumulado = valorAcumulado.add(aporte);
            valorAcumulado = valorAcumulado.multiply(BigDecimal.ONE.add(taxaJurosMensal));
        }*/

        //investimento.valorInicial = valorAcumulado.setScale(2, RoundingMode.HALF_UP).doubleValue();
        //return investimento;
        return null;
    }

    public List<Investimento> listarTodos() {
        return Investimento.listAll();
    }
}
