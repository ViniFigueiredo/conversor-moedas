package conversor;


public class App 
{
    public static void main( String[] args )
    {
        Thread USDtoBRL = new Thread(new USDtoBRLConverter());
        Thread BRLtoUSD = new Thread(new BRLtoUSDConverter());
        Thread EURtoBRL = new Thread(new EURtoBRLConverter());
        Thread BRLtoEUR = new Thread(new BRLtoEURConverter());

        USDtoBRL.start();
        BRLtoUSD.start();
        EURtoBRL.start();
        BRLtoEUR.start();
    }
}
