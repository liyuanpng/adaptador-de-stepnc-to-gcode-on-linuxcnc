package br.UNB.LAB.FAcessorias;

import br.UNB.LAB.GerarCodG.GeneradorCodidoG;
import br.UNB.LAB.InfBasicas.PlanoSeguranca;

public class ImprimirPlanoSeg {

	public static void ImpPlanoSeguranza(){
		double PlanoSeg = PlanoSeguranca.ComprimentoPlanoSegDouble;
		ImprimirSecuenciaLineaNo.EscribirLinea("G90 G00 Z" +  GeneradorCodidoG.df.format(PlanoSeg));
	}
}
