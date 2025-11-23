package conversor;

public class ConversorMoeda {
  private double taxaCambio;
    public ConversorMoeda(double taxaCambio) {
        this.taxaCambio = taxaCambio;
    }
    public double converter(double valor) {
        return valor * taxaCambio;
    }
}
