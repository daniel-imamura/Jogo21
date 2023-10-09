public class Descartar extends Comunicado {
    private byte descarte;
    public Descartar(byte descarte)
    {
        this.descarte = descarte;
    }

    public byte getDescarte() {
        return descarte;
    }
}
