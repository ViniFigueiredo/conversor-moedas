package conversor;

public class BRLtoUSDConverter implements Runnable {
    public double valor_real;
    public BRLtoUSDConverter(double r){
        this.valor_real=r;
    }
    @Override
    public void run() {
        System.out.println("inicializado");
        ConversorMoeda conversao= new ConversorMoeda(0.19);
        double resultado=conversao.converter(this.valor_real);
        System.out.println(this.valor_real+"reais equivale a "+resultado+"dolares");
    
    }
}
