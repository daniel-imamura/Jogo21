import java.io.*;
import java.net.*;
import java.util.*; 


public class Reativo
{
	public static String PORTA_PADRAO = "12345";
    
    public static void main (String[] args) throws Exception
    {
		 	Baralho baralho = new Baralho();	

			String porta = Reativo.PORTA_PADRAO;
			
            Vector<Jogador> usuarios = new Vector<Jogador> ();

            AceitadoraDeConexao aceitadora = null;
            
            try
            {
				aceitadora = new AceitadoraDeConexao(porta, usuarios, baralho);
				aceitadora.start();
			}
			catch(Exception erro)
			{
				System.err.println ("Escolha uma porta apropriada e liberada para uso!\n");
				return;				
			}

			
			for (;;)
			{
				System.out.println ("O servidor esta ativo! Para desativa-lo,");
				System.out.println ("use o comando \"desativar\"\n");
				System.out.print   ("> ");

				String comando=null;

				try
				{
					comando = Teclado.getUmString();
				}
				catch (Exception erro)
				{}
				System.out.println("Jogadore conectados:  " + usuarios.size());
				System.out.println("nCartas no baralho:  " + baralho.getCartasNoBaralho());
				System.out.println("Baralho:  " + baralho.toString());

				if (comando.toLowerCase().equals("desativar"))
				{					
					synchronized (usuarios)
					{
						ComunicadoDeDesligamento comunicadoDeDesligamento = new ComunicadoDeDesligamento ();						
						for (Jogador usuario:usuarios)
						{
							try
							{
								usuario.receba (comunicadoDeDesligamento);
								usuario.adeus  ();
							}
							catch (Exception erro)
							{}
						}
					}
					System.out.println ("O servidor foi desativado!\n");
					System.exit(0);
				}
				else
					System.err.println ("Comando invalido!\n");
			}
    }       
}
