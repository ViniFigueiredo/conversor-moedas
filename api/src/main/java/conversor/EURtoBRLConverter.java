package conversor;

public class EURtoBRLConverter implements Runnable {
    public double valor_euro;
    public EURtoBRLConverter(double e){
        this.valor_euro=e;
    }
    @Override
    public void run() {
        System.out.println("inicializado");
        ConversorMoeda conversao= new ConversorMoeda(6.14);
        double resultado=conversao.converter(this.valor_euro);
        System.out.println(this.valor_euro+" euros equivale a "+resultado+"reais");
    
    }
}
