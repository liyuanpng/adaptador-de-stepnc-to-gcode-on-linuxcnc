package br.UNB.LAB.FAcessorias;

import br.UNB.LAB.GerarCodG.GeneradorCodidoG;

public class ImprimirSecuenciaLineaNo {
	
	static int LineNo=0;

	public static void EscribirLinea(String Comando) {
		LineNo = GeneradorCodidoG.LineNo;
		System.out.println("N" + LineNo + " " + Comando.replace(",", "."));
		LineNo += 1;
		/*if(LineNo==52){
			System.out.println(LineNo);
		}*/
		GeneradorCodidoG.LineNo = LineNo;
		

	}

}
