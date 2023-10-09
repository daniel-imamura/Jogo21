import java.io.*;
import java.util.*;

public class Baralho implements Serializable
{
	private Vector<Byte> cartasDoBaralho;
	public byte descarte;

	public Baralho()
	{
		this.cartasDoBaralho = new Vector<Byte>(52);
		for(byte naipes=0; naipes < 4; naipes++)
		{
			for(byte nCarta = 0; nCarta < 13; nCarta++)
			{
				if( nCarta >= 9)
				{
					this.cartasDoBaralho.add((byte)10);
				}
				else
				{
					this.cartasDoBaralho.add((byte)(nCarta + 1));
				}
			}
		}
	}

	public byte getCartasNoBaralho()
	{
		return (byte)this.cartasDoBaralho.size();
	}

	public byte get(byte ret)
	{
		return (byte)this.cartasDoBaralho.get(ret);
	}

	public byte getDescarte()
	{
		return (byte)this.descarte;
	}

	public byte remove(byte ret)
	{
		return this.cartasDoBaralho.remove(ret);
	}

	public void set(byte ret)
	{
		 this.descarte = ret;
	}

	public Baralho reiniciarBaralho()
	{
		byte nCartas = this.getCartasNoBaralho();

		for(byte posicoes = nCartas; posicoes < 52; posicoes++)
		{
			this.cartasDoBaralho.add(posicoes);
		}

		int posicao = 0;
		for( posicao=0; posicao < 13; posicao++)
		{
			if( posicao >= 9)
			{
				this.cartasDoBaralho.set(posicao,(byte)(10));
			}
			else
			{
				this.cartasDoBaralho.set(posicao,(byte)( posicao + 1));
			}
		}

		for(posicao = 13; posicao < 26; posicao++)
		{
			if( posicao >= 22)
			{
				this.cartasDoBaralho.set(posicao,(byte)(10));
			}
			else
			{
				this.cartasDoBaralho.set(posicao,(byte)( posicao - 12));
			}
		}

		for(posicao=26; posicao < 39; posicao++)
		{
			if( posicao >= 35)
			{
				this.cartasDoBaralho.set(posicao,(byte)(10));
			}
			else
			{
				this.cartasDoBaralho.set(posicao,(byte)( posicao - 25));
			}
		}

		for(posicao = 39; posicao < 52; posicao++)
		{
			if( posicao >= 48)
			{
				this.cartasDoBaralho.set(posicao,(byte)(10));
			}
			else
			{
				this.cartasDoBaralho.set(posicao,(byte)( posicao - 38));
			}
		}

		return this;
	}

	public String toString()
	{
		return this.cartasDoBaralho.toString();
	}
}