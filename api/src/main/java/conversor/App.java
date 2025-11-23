package conversor;

public class App 
{
    public static void main( String[] args )
    {
        Thread USDtoBRL = new Thread(new USDtoBRLConverter(10.00));
        Thread BRLtoUSD = new Thread(new BRLtoUSDConverter(10.00));
        Thread EURtoBRL = new Thread(new EURtoBRLConverter(10.00));
        Thread BRLtoEUR = new Thread(new BRLtoEURConverter(10.00));

        USDtoBRL.start();
        BRLtoUSD.start();
        EURtoBRL.start();
        BRLtoEUR.start();
    }
}
