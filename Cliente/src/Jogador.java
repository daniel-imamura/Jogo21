
import java.io.*;
import java.net.*; 
import java.util.*;
import java.util.concurrent.Semaphore; 

public class Jogador
{
    private Socket             conexao;
    private ObjectInputStream  receptor;
    private ObjectOutputStream transmissor;
    
    private Comunicado proximoComunicado=null;
    
    private Semaphore mutEx = new Semaphore (1,true);
   
   	public byte carta1    = 0;
	public byte carta2    = 0;
	public byte carta3    = 0;
	public byte carta4 	  = 0;
	public byte descarte  = 0;
	public byte pontuacao = 0;
    public byte idJogador;
    public boolean rodada;


    public Jogador(Socket conexao, ObjectInputStream  receptor, ObjectOutputStream transmissor)throws Exception // se parametro nulos
    {
        if (conexao==null)
            throw new Exception ("Conexao ausente");

        if (receptor==null)
            throw new Exception ("Receptor ausente");

        if (transmissor==null)
            throw new Exception ("Transmissor ausente");

        this.conexao     = conexao;
        this.receptor    = receptor;
        this.transmissor = transmissor;
    }

    public void receba (Comunicado x) throws Exception
    {
        try
        {
            this.transmissor.writeObject (x);
            this.transmissor.flush       ();
        }
        catch (IOException erro)
        {
            throw new Exception ("Erro de transmissao");
        }
    }
    
    public void comprar (Baralho baralho) throws Exception
    {
		Random random = new Random(); 
		byte posicaoSorteada = 0;
		byte cartaSorteada  = 0;
		
		posicaoSorteada = (byte)random.nextInt(baralho.getCartasNoBaralho());
		cartaSorteada = baralho.get(posicaoSorteada);
		baralho.remove(posicaoSorteada);

		carta4 = cartaSorteada;
		
		pontuacao += carta4;
    }
    public void nomearJogador(byte id)
    {
        this.idJogador = id;
    }

    public void rodadaAtual(boolean rodada){this.rodada = rodada;}

    public boolean getRodada(){return  this.rodada;}

    public void comprarDescarte (byte cartaNoMonte) throws Exception
    {
		this.carta4 = cartaNoMonte;
		
		pontuacao += carta4;
    }
    
    public void descartar (byte cartaDescartada) throws Exception
    {
		
		this.descarte = cartaDescartada;		
		this.pontuacao  -=  cartaDescartada;
				
		if(cartaDescartada == carta1)
			this.carta1 = carta4;
		else if(cartaDescartada == carta2)
			this.carta2 = carta4;
		else if(cartaDescartada == carta3)
			this.carta3 = carta4;			

    }
    
    public byte getCarta1()
    {
		return carta1;
	}	
	    
	public byte getCarta2()
    {
		return carta2;
	}    
	
	public byte getCarta3()
    {
		return carta3;
	}
	
	public byte getCarta4()
    {
		return carta4;
	}	
	
	public byte getPontuacao()
    {
		return pontuacao;
	}
    
    public byte getDescarte()
    {
		return descarte; 
	}

    public byte getIdJogador() {
        return idJogador;
    }

    public void entraNaPartida (Baralho baralho) throws Exception
    {
		Random random = new Random(); 
		byte posicaoSorteada =0;
		byte[] cartasSorteadas = new byte[3];
		
		for(byte nCartas = 0; nCartas < 3; nCartas++) 
		{
			posicaoSorteada = (byte)random.nextInt(baralho.getCartasNoBaralho());
			cartasSorteadas[nCartas] = baralho.get(posicaoSorteada);
			baralho.remove(posicaoSorteada);
		}
		
		carta1 = cartasSorteadas[0];
		carta2 = cartasSorteadas[1];
		carta3 = cartasSorteadas[2];
		
		pontuacao = (byte)(carta1 + carta2 + carta3);
    }
    

    public Comunicado espie () throws Exception
    {
        try
        {
            this.mutEx.acquireUninterruptibly();
            if (this.proximoComunicado == null) 
				this.proximoComunicado = (Comunicado)this.receptor.readObject();
				
            this.mutEx.release();
            return this.proximoComunicado;
        }
        catch (Exception erro)
        {
            throw new Exception ("Erro de recepcao");
        }
    }

    public Comunicado envie () throws Exception
    {
        try
        {
            if (this.proximoComunicado==null) 
				this.proximoComunicado = (Comunicado)this.receptor.readObject();
				
            Comunicado ret         = this.proximoComunicado;
            this.proximoComunicado = null;
            return ret;
        }
        catch (Exception erro)
        {
            throw new Exception ("Erro de recepcao");
        }
    }

    public void adeus () throws Exception
    {
        try
        {
            this.transmissor.close();
            this.receptor   .close();
            this.conexao    .close();
        }
        catch (Exception erro)
        {
            throw new Exception ("Erro de desconexao");
        }
    }
    
    public Baralho reiniciarBaralho(Baralho baralhoNovo)
    {
		return baralhoNovo.reiniciarBaralho();		
	}
		
}
