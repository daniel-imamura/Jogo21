import java.io.*;
import java.net.*;
import java.util.*; 

public class SupervisoraDeConexao extends Thread
{
    private double              valor=0;
    private Jogador             jogador;
    private Socket              conexao;
    private Vector<Jogador> usuarios;
    private Baralho               baralho;
    private byte                   turno=1;
    //private int                descarte;

    public SupervisoraDeConexao(Socket conexao, Vector<Jogador> usuarios, Baralho baralho)throws Exception
    {
        if (conexao==null)
            throw new Exception ("Conexao ausente");

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        if(baralho==null)
            throw new Exception("Baralho ausente");

        this.conexao  = conexao;
        this.usuarios = usuarios;
        this.baralho = baralho;
    }

    public void run ()
    {
        ObjectOutputStream transmissor;
        try
        {
            transmissor =
            new ObjectOutputStream(
            this.conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            return;
        }
        
        ObjectInputStream receptor=null;
        try
        {
            receptor=
            new ObjectInputStream(
            this.conexao.getInputStream());
        }
        catch (Exception err0)
        {
            try
            {
                transmissor.close();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread
            
            return;
        }
        
        try
        {
            this.jogador = new Jogador (this.conexao, receptor, transmissor);
        }
        catch (Exception erro)
        {} // sei que passei os parametros corretos

		boolean vez = true;
        try
        {
			if(usuarios.size() < 3)
			{				
				synchronized(usuarios)
				{			
					this.usuarios.add(this.jogador);					
				}
			}
            

            for(;;)
            {				
                Comunicado comunicado = this.jogador.envie ();

                if (comunicado==null)
                {    
					return;                                
				}				
                else if (comunicado instanceof PedidoParaSair)
                {
                    this.usuarios.remove(this.jogador);
                    this.jogador.adeus();
                }
                else if (comunicado instanceof EntreiNoJogo)
                {
					if(usuarios.size() == 3)
					{
						for(Jogador usuario:usuarios)
						{
							try
							{
								usuario.receba(new ComunicadoDeProntidao());
							}
							catch(Exception erro)
							{}
						}
					}                
				}

                else if(comunicado instanceof ReiniciarPartida)
                {
                    this.baralho = this.jogador.reiniciarBaralho(this.baralho);
                }
                else if(comunicado instanceof Comprar)
                {
                    this.jogador.comprar(baralho);
                }
                else if(comunicado instanceof ComprarDescarte)
                {
                    this.jogador.comprarDescarte(baralho.getDescarte());
                }
                else if(comunicado instanceof  Descartar)
                {
                    Descartar descartar = (Descartar) comunicado;
                    this.jogador.descartar(descartar.getDescarte());
                    baralho.set(descartar.getDescarte());
                }
                else if(comunicado instanceof EntrarNaPartida)
                {
                    this.jogador.entraNaPartida(baralho);
                }
                else if(comunicado instanceof GetCarta1)
                {
                    this.jogador.receba(new Get(this.jogador.getCarta1()));
                }
                else if(comunicado instanceof GetCarta2)
                {
                    this.jogador.receba(new Get(this.jogador.getCarta2()));
                }
                else if(comunicado instanceof GetCarta3)
                {
                    this.jogador.receba(new Get(this.jogador.getCarta3()));
                }
                else if(comunicado instanceof GetCarta4)
                {
                    this.jogador.receba(new Get(this.jogador.getCarta4()));
                }
                else if(comunicado instanceof GetPontuacao)
                {
                    this.jogador.receba(new Get(this.jogador.getPontuacao()));
                }
                else if(comunicado instanceof Nomear)
                {
                    this.jogador.nomearJogador((byte)usuarios.size());
                    this.jogador.receba(new Get(this.jogador.getIdJogador()));
                }
                else if(comunicado instanceof InicioDeTurno)
                {
                    this.jogador.receba(new PodeJogar());
                    turno = 1;
                }
                else if(comunicado instanceof SegundoTurno)
                {
                    this.jogador.receba(new PodeJogar());
                    turno = 2;
                }
                else if(comunicado instanceof TerceiroTurno)
                {
                    this.jogador.receba(new PodeJogar());
                    turno = 3;
                }
                else if(comunicado instanceof JogadorDoisJoga)
                {
                    usuarios.get(1).receba(new PodeJogar());
                }
                else if(comunicado instanceof PodeJogar)
                {
                    System.out.println("id Jogador : " + this.jogador.getIdJogador());
                    System.out.println("turno : " + turno);

                    if(turno == 1  && this.jogador.getIdJogador() == 1 || this.jogador.getIdJogador() == 3)
                    {
                        turno = 2;
                        usuarios.get(0).receba(new PodeJogar());
                    }
                    else if(turno == 2 && this.jogador.getIdJogador() == 1)
                    {
                        turno = 3;
                        usuarios.get(1).receba(new PodeJogar());

                    }
                    else if(this.jogador.getIdJogador() == 2)
                    {
                        turno = 1;
                        usuarios.get(2).receba(new PodeJogar());
                    }
                }

            }
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close ();
                receptor   .close ();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
        }
    }
}
