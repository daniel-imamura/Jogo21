import java.io.*;
import java.net.*;
import java.util.*;

public class Proativo2{

	public static final String HOST_PADRAO  = "localhost";
	public static final int    PORTA_PADRAO = 12345;

	public static void main (String[] args)
	{

		byte descarte = 0;
		byte pontos = 0;
		try
		{
			if (args.length>2)
			{
				System.err.println ("Uso esperado: java Cliente [HOST [PORTA]]\n");
				return;
			}

			Socket conexao=null;
			try
			{
				String host = Proativo.HOST_PADRAO;
				int    porta= Proativo.PORTA_PADRAO;

				if (args.length>0)
					host = args[0];

				if (args.length==2)
					porta = Integer.parseInt(args[1]);

				conexao = new Socket (host, porta);
			}
			catch (Exception erro)
			{
				System.err.println ("Erro Socket!\n");
				return;
			}

			ObjectOutputStream transmissor=null;
			try
			{
				transmissor = new ObjectOutputStream(conexao.getOutputStream());
			}
			catch (Exception erro)
			{
				System.err.println ("Erro transmissor!\n");
				return;
			}

			ObjectInputStream receptor=null;
			try
			{
				receptor = new ObjectInputStream(conexao.getInputStream());
			}
			catch (Exception erro)
			{
				System.err.println ("Erro receptor\n");
				return;
			}

			Jogador servidor=null;
			try
			{
				servidor = new Jogador (conexao, receptor, transmissor);
			}
			catch (Exception erro)
			{
				System.err.println ("Erro no Jogador!\n");
				return;
			}

			TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;
			try
			{
				tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento (servidor);
			}
			catch (Exception erro)
			{} // sei que servidor foi instanciado

			tratadoraDeComunicadoDeDesligamento.start();

			//servidor.entraNaPartida(baralho);

			byte pontuacao = 0;
			String opcao=" ";
			String opcaoDeCompra = " ";
			boolean primeiraRodada = true;
			String continuarJogo  = " ";
			boolean reiniciar = false;
			/**/
			Comunicado comunicado = null;

			servidor.receba (new Nomear ());
			do
			{
				comunicado = servidor.espie ();
			}
			while (!(comunicado instanceof Get));
			Get idJogador = (Get)servidor.envie ();

			System.out.println ("Esperando os demais jogadores");

			servidor.receba(new EntreiNoJogo());

			Comunicado qtdJogadores;
			do
			{
				qtdJogadores = servidor.espie();
			}
			while (!(qtdJogadores instanceof ComunicadoDeProntidao));

			System.out.println ("Servidor pronto");
			servidor.envie();

			servidor.receba(new EntrarNaPartida());

			do
			{
				servidor.receba (new GetCarta1 ());
				do
				{
					comunicado = servidor.espie ();
					//System.out.println(comunicado);

				}
				while (!(comunicado instanceof Get));
				Get resultado = (Get)servidor.envie ();

				servidor.receba (new GetCarta2 ());
				do
				{
					comunicado = servidor.espie ();
					//System.out.println(comunicado);

				}
				while (!(comunicado instanceof Get));
				Get resultado2 = (Get)servidor.envie ();
				servidor.receba (new GetCarta3 ());
				do
				{
					comunicado = servidor.espie ();
					//System.out.println(comunicado);

				}
				while (!(comunicado instanceof Get));
				Get resultado3 = (Get)servidor.envie ();

				byte carta1 = resultado.getValorResultante();
				byte carta2 = resultado2.getValorResultante();
				byte carta3 = resultado3.getValorResultante();
				byte carta4 = 0;



				if(primeiraRodada == true) {
					System.out.println("ID: " + idJogador.getValorResultante());
					System.out.println("[ENTER] Para comprar uma carta:");
					System.out.println("Suas cartas: " + carta1 + ", " + carta2 + ", " + carta3);

					if (idJogador.getValorResultante() == 1) {
						servidor.receba(new PodeJogar());
					} else
						servidor.receba(new EspereSeuTurno());

					Comunicado inicioDoJogo;

					do {
						inicioDoJogo = servidor.espie();
					}
					while (!(inicioDoJogo instanceof PodeJogar));


					try
					{
						opcao = Teclado.getUmString();
					}
					catch (Exception erro)
					{
						System.err.println ("Opcao invalida!\n");
						continue;
					}
					servidor.receba(new Comprar());
				}
				else
				{
					if (idJogador.getValorResultante() == 1) {
						servidor.receba(new InicioDeTurno());
					} else if (idJogador.getValorResultante() == 2) {
						servidor.receba(new SegundoTurno());
					} else if (idJogador.getValorResultante() == 3) {
						servidor.receba(new TerceiroTurno());
					} else
						servidor.receba(new EspereSeuTurno());

					do
					{
						System.out.println ("Suas cartas: " + carta1 + ", " + carta2 + ", " + carta3);
						System.out.println ("Deseja comprar o descarte? [Sim/Nao]");
						try
						{
							opcaoDeCompra = Teclado.getUmString().toUpperCase();
						}
						catch (Exception erro)
						{
							System.err.println ("Opcao invalida!\n");
							continue;
						}
					}
					while( !opcaoDeCompra.equals("SIM") && !opcaoDeCompra.equals("NAO"));
				}
				servidor.envie();
				if(opcaoDeCompra.equals("SIM"))
				{
					servidor.receba(new ComprarDescarte());

					System.out.println ("Vai comprar o descarte");
				}
				else if(opcaoDeCompra.equals("NAO"))
				{
					servidor.receba(new Comprar());

					System.out.println ("Vai comprar do baralho");
				}

				servidor.receba (new GetCarta4 ());
				do
				{
					comunicado = servidor.espie ();
				}
				while (!(comunicado instanceof Get));
				Get resultado4 = (Get)servidor.envie ();

				carta4 = resultado4.getValorResultante();

				servidor.receba (new GetPontuacao ());
				do
				{
					comunicado = servidor.espie ();
				}
				while (!(comunicado instanceof Get));
				Get resultadoPontuacao = (Get)servidor.envie ();

				pontuacao = resultadoPontuacao.getValorResultante();
				pontos = pontuacao;
				System.out.println ("Voce comprou um: " + carta4);
				System.out.println ("Suas cartas agora: " + carta1 + ", " + carta2 + ", " + carta3 + " e " + carta4);
				System.out.println ("Sua pontucao:" + pontuacao );
				do
				{
					System.out.println ("Qual carta deseja descartar?");

					try
					{
						descarte = Teclado.getUmByte();
					}
					catch (Exception erro)
					{
						System.err.println ("Opcao invalida!\n");
						continue;
					}

					if(descarte != carta1 && descarte != carta2 && descarte != carta3 && descarte != carta4)
						System.err.println ("Voce nao tem essa carta!\n");
				}
				while(descarte == 0 || descarte != carta1 && descarte != carta2 && descarte != carta3 && descarte != carta4);

				servidor.receba(new Descartar(descarte));

				servidor.receba (new GetPontuacao ());
				Comunicado cartaNova;
				do
				{
					cartaNova = servidor.espie ();
				}
				while (!(cartaNova instanceof Get));
				Get resultadoPontuacao2 = (Get)servidor.envie ();

				pontos = resultadoPontuacao2.getValorResultante();

				System.err.println ("Seus pontos: \n" + pontos);

				if(pontos == 21)
				{
					do
					{
						System.err.println ("**************** Você ganhou o jogo! Parabéns! **************** ");
						System.err.println (" Deseja começar outra partida de 21? [Sim/Nao]");
						try
						{
							continuarJogo = Teclado.getUmString().toUpperCase();
						}
						catch (Exception erro)
						{
							System.err.println ("Opcao invalida!\n");
							continue;
						}
					}
					while(!continuarJogo.equals("SIM") && !continuarJogo.equals("NAO"));

					if(continuarJogo.equals("SIM"))
					{
						servidor.receba(new ReiniciarPartida());
						pontos = 0;
						primeiraRodada = true;
						reiniciar = true;
					}
					else if(continuarJogo.equals("NAO"))
					{
						servidor.receba (new PedidoParaSair());
						reiniciar = true;
					}
					comunicado = servidor.envie();
				}
				if(reiniciar == false ){

					System.out.println("Espere seu turno novamente");

					if (primeiraRodada == true) {
						primeiraRodada = false;
						servidor.receba(new PodeJogar());
					} else if (idJogador.getValorResultante() == 1) {
						servidor.receba(new JogadorDoisJoga());
					} else if (idJogador.getValorResultante() == 2) {
						servidor.receba(new PodeJogar());
					} else if (idJogador.getValorResultante() == 3) {
						servidor.receba(new PodeJogar());
					}

					Comunicado fimDeRodada;
					do {
						fimDeRodada = servidor.espie();
					}
					while (!(fimDeRodada instanceof PodeJogar));
					servidor.envie();
					System.out.println("Pode jogar");
				}
			}
			while (pontos != 21 && !opcaoDeCompra.equals("SIM"));

			System.out.println ("Obrigado por usar este programa!");
			System.exit(0);
			return;

		}
		catch (Exception erro)
		{
			System.err.println (erro.getMessage());
		}
	}
}
