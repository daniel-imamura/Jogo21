
public class Get extends Comunicado {
    private byte valorResultante;
    private boolean rodada;

    public Get (byte valorResultante)
    {
        this.valorResultante = valorResultante;
    }

    public Get (boolean rodada)
    {
        this.rodada = rodada;
    }

    public byte getValorResultante ()
    {
        return this.valorResultante;
    }

    public  boolean getRodada(){ return  this.rodada;}

    public String toString ()
    {
        return (""+this.valorResultante);
    }
}
