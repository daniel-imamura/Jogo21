import java.net.*;
import java.util.*;  

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket        pedido;
    private Vector<Jogador> usuarios;
    private Baralho baralho;
    private int descarte;

    public AceitadoraDeConexao (String porta, Vector<Jogador> usuarios, Baralho baralho)throws Exception
    {
        if (porta==null)
            throw new Exception ("Porta ausente");

        if(baralho ==null)
            throw new Exception("Baralho ausente");
        try
        {
            this.pedido = new ServerSocket (Integer.parseInt(porta));
        }
        catch (Exception  erro)
        {
            throw new Exception ("Porta invalida");
        }

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        this.usuarios = usuarios;
        this.baralho = baralho;
    }

    public void run ()
    {
        for(;;)
        {
            Socket conexao=null;
            try
            {
                conexao = this.pedido.accept();
            }
            catch (Exception erro)
            {
                continue;
            }

            SupervisoraDeConexao supervisoraDeConexao = null;
            try
            {
                supervisoraDeConexao = new SupervisoraDeConexao (conexao, usuarios, baralho);
            }
            catch (Exception erro)
            {} // sei que passei parametros corretos para o construtor
            supervisoraDeConexao.start();
        }
    }
}
