package conversor;

public class USDtoBRLConverter implements Runnable{
    public double valor_dolar;
    public USDtoBRLConverter(double d){
        this.valor_dolar=d;
    }
    @Override
        public void run() {
        System.out.println("inicializado");
        ConversorMoeda conversao= new ConversorMoeda(5.33);
        double resultado=conversao.converter(this.valor_dolar);
        System.out.println(this.valor_dolar+"dolares equivale a "+resultado+"reais");
    
    }
        
}
