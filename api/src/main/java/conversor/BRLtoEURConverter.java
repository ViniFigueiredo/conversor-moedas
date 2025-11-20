package conversor;

public class BRLtoEURConverter implements Runnable {
    public double valor_real;
    public BRLtoEURConverter(double r){
        this.valor_real=r;
    }
    @Override
    public void run() {
        System.out.println("inicializado");
        ConversorMoeda conversao= new ConversorMoeda(0.16);
        double resultado=conversao.converter(this.valor_real);
        System.out.println(this.valor_real+"reais equivale a "+resultado+"euros");
    
    }
}
