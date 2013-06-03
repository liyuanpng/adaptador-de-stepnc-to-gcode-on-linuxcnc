package br.UNB.LAB.GerarCodG;

import br.UNB.LAB.FAcessorias.CalularNoPasosEspiral;
import br.UNB.LAB.FAcessorias.CalularNoPasosProfundidadeCorte;
import br.UNB.LAB.FAcessorias.ImprimirPlanoSeg;
import br.UNB.LAB.FAcessorias.ImprimirSecuenciaLineaNo;


public class RH_Gcode {
		
	private static double DiametroToolAnterior=0;
	private static boolean imprimirBase=false;
	
		public static void GCode_Furos(double CentroX, double CentroY, double Profundidade, double DiatroInicialFuro,double DiatroFinalFuro, double ProfundidadeOffset, String ToolData,int TipoFuro_1Simple_2Segmentado){
			//Profundidade = Profundidade+1;
		double DiametroTool = TrocaDeFerramenta.DatoDiametroTool(ToolData);
		
		TrocaDeFerramenta.LineNo=GeneradorCodidoG.LineNo;
		TrocaDeFerramenta.ImpriFer(ToolData);
		
		switch (TipoFuro_1Simple_2Segmentado) {
		case 1://furo simple
			GCode_FuroSimple(CentroX, CentroY, Profundidade, DiametroTool,TrocaDeFerramenta.TloNum,ProfundidadeOffset);
			break;
		case 2://furo segmentado recto
			imprimirBase=false;
			GCode_FuroSegmentado(CentroX, CentroY, Profundidade, DiatroInicialFuro, DiametroTool, TrocaDeFerramenta.TloNum, ProfundidadeOffset);
			break;

		default://para el caso 3 firo segmentado en V
			//GCode_FuroSegmentado(CentroX, CentroY, Profundidade, DiatroInicialFuro,DiatroFinalFuro,DiametroTool, TrocaDeFerramenta.TloNum, ProfundidadeOffset);
			break;
		}

		
		
		
	}
	
	
	//falta adicionar el offset de profundidad
	public static void GCode_FuroSimple(double CentroX, double CentroY, double Profundidade, double DiametroTool,double Tlo,double ProfundidadeOffset){
		
		/** Compenso la Profundidad si hubiera Necesidad**/
		if(Profundidade>Tlo & TrocaDeFerramenta.tipo.startsWith("CENTER_DRILL")==true){ //pregunta si es center drill e si la profundidad es mayor que el TLO de la herramienta
			Profundidade=Tlo; //actualizo la profundidade para el center drill
			}
		
		/**calcular la cantidad de pasadas dependiendo del diametro de la Tool **/
		CalularNoPasosProfundidadeCorte.PassadasPC(Profundidade, DiametroTool);
		
		/*** Imprimir Plano de Seguranza ***/
		ImprimirPlanoSeg.ImpPlanoSeguranza();
		
		/*** Imprimir centro del Furo ***/
		ImprimirSecuenciaLineaNo.EscribirLinea("G01 F" + GeneradorCodidoG.MaxVelAvanceMaquina + " X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY));
		
		/*** Operacion de Furacion ***/
		if(CalularNoPasosProfundidadeCorte.NpasosEnterosPC ==1 & CalularNoPasosProfundidadeCorte.NpasosDecimalesPC==0){//aqui se cuando la profundidade es igual al diametro dela Herramienta
			ImprimirSecuenciaLineaNo.EscribirLinea("G00 Z" + GeneradorCodidoG.df.format(0));
			ImprimirSecuenciaLineaNo.EscribirLinea("G91 G01 F" + GeneradorCodidoG.df.format(GeneradorCodidoG.MaxVelcorteTool*1.5) + " Z-" + GeneradorCodidoG.df.format(Profundidade));
		}else{
			for (int i = 0; i < CalularNoPasosProfundidadeCorte.NpasosEnterosPC; i++) {
				ImprimirSecuenciaLineaNo.EscribirLinea("G00 Z" + GeneradorCodidoG.df.format(0));
				ImprimirSecuenciaLineaNo.EscribirLinea("G01 Z-" + GeneradorCodidoG.df.format(DiametroTool));
				}
			if(CalularNoPasosProfundidadeCorte.NpasosDecimalesPC>0 & CalularNoPasosProfundidadeCorte.NpasosDecimalesPC<1){
				ImprimirSecuenciaLineaNo.EscribirLinea("G00 Z" + GeneradorCodidoG.df.format(0));
				ImprimirSecuenciaLineaNo.EscribirLinea("G01 Z-" + GeneradorCodidoG.df.format(Profundidade));
				}
			}
		DiametroToolAnterior=DiametroTool;
		}
	
	public static void GCode_FuroSegmentado(double CentroX, double CentroY, double Profundidade, double DiatroInicialFuro,double DiametroTool,double ProfundidadeOffset, double FeedRate){
		
		ImprimirPlanoSeg.ImpPlanoSeguranza();
		CalularNoPasosProfundidadeCorte.PassadasPC(Profundidade, GeneradorCodidoG.ProfundidadeCorte);
		
		double FazerEspiral = (DiatroInicialFuro-DiametroToolAnterior);//encuentro la cantidad de material que todavia falta por usinar
		FazerEspiral = (FazerEspiral/2)/DiametroTool;//verifico que la herramienta pode fazer o resto da usinagem sim fazer o espiral
		
		/****************************** si es menor que 1 quiere decir que solo se hace el barrido del borde para retirar el material que falta ****************************************************/
		if(FazerEspiral<=1){//
			if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
				System.out.println("(furo segmentado Recorrer borde)");
			}
			ImprimirSecuenciaLineaNo.EscribirLinea("G90 G01 F" + GeneradorCodidoG.df.format(GeneradorCodidoG.MaxVelAvanceMaquina) + " X" + GeneradorCodidoG.df.format(CentroX-(DiatroInicialFuro/2)) + " Y" + GeneradorCodidoG.df.format(CentroY-(DiatroInicialFuro/2)));
			ImprimirSecuenciaLineaNo.EscribirLinea("G00 Z" + GeneradorCodidoG.df.format(0));
			ImprimirSecuenciaLineaNo.EscribirLinea("G01 F" + GeneradorCodidoG.df.format(GeneradorCodidoG.MaxVelcorteTool));
			imprimirBase=true;
			GCode_FS_RecorrerBorde(CentroX, CentroY, DiatroInicialFuro, DiametroTool,Profundidade);
		}else{
			if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
				System.out.println("(furo segmentado espiral)");
			}
			
			CalularNoPasosEspiral.PassadasEspiral(DiatroInicialFuro/2, DiametroTool*GeneradorCodidoG.PorcentageToolPassoEspiral);
			
			ImprimirSecuenciaLineaNo.EscribirLinea("G90 G01 F" + GeneradorCodidoG.df.format(GeneradorCodidoG.MaxVelAvanceMaquina) + " X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY-(DiametroToolAnterior/2)));
			ImprimirSecuenciaLineaNo.EscribirLinea("G00 Z" + GeneradorCodidoG.df.format(0));
			ImprimirSecuenciaLineaNo.EscribirLinea("G01 F" + GeneradorCodidoG.df.format(GeneradorCodidoG.MaxVelcorteTool));
			
			//CalularNoPasosProfundidadeCorte.NpasosDecimalesPC =0.3;
			
			double addPasoPC =0;
			if(CalularNoPasosProfundidadeCorte.NpasosDecimalesPC>0){
				addPasoPC =1;
			}
			
			for (int i = 0; i < (CalularNoPasosProfundidadeCorte.NpasosEnterosPC + addPasoPC); i++) {
				ImprimirSecuenciaLineaNo.EscribirLinea("G41 D" + TrocaDeFerramenta.numeroD);
				double Radio1raPasada=(DiametroToolAnterior/2)+(DiametroTool/2);
				ImprimirSecuenciaLineaNo.EscribirLinea("G91  X" + GeneradorCodidoG.df.format(Radio1raPasada) + " Y" + GeneradorCodidoG.df.format(DiametroToolAnterior/2));
				
				if (addPasoPC==1 & i == (CalularNoPasosProfundidadeCorte.NpasosEnterosPC) ) {
					GCode_FS_HacerEspiral(CentroX, CentroY, DiatroInicialFuro, DiametroTool,GeneradorCodidoG.ProfundidadeCorte * CalularNoPasosProfundidadeCorte.NpasosDecimalesPC);
					ImprimirSecuenciaLineaNo.EscribirLinea("G40 G90 G01 F" + GeneradorCodidoG.df.format(GeneradorCodidoG.MaxVelAvanceMaquina) + " X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY));

				}else{
					GCode_FS_HacerEspiral(CentroX, CentroY, DiatroInicialFuro, DiametroTool,GeneradorCodidoG.ProfundidadeCorte );
					
					if (i==(CalularNoPasosProfundidadeCorte.NpasosEnterosPC - 1) & addPasoPC==0 ) {
						ImprimirSecuenciaLineaNo.EscribirLinea("G40 G90 G01 F" + GeneradorCodidoG.df.format(GeneradorCodidoG.MaxVelAvanceMaquina) + " X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY));
					}else{
						ImprimirSecuenciaLineaNo.EscribirLinea("G40 G90 G01 F" + GeneradorCodidoG.df.format(GeneradorCodidoG.MaxVelAvanceMaquina) + " X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY-(DiametroToolAnterior/2)));
					}

				}
			//ImprimirPlanoSeg.ImpPlanoSeguranza();
			}//fin for
		}

		DiametroToolAnterior=DiametroTool;		
	}
	
	public static void GCode_FS_RecorrerBorde(double CentroX, double CentroY, double DiatroInicialFuro, double DiametroTool,double Profundidade){
		ImprimirSecuenciaLineaNo.EscribirLinea("G41 D" + TrocaDeFerramenta.numeroD);
		ImprimirSecuenciaLineaNo.EscribirLinea("G91  X" + GeneradorCodidoG.df.format(DiatroInicialFuro) + " Y" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
		//ImprimirSecuenciaLineaNo.EscribirLinea("G91 G01 Z-" + GeneradorCodidoG.df.format(GeneradorCodidoG.ProfundidadeCorte));
		double pasosEspiralBorde=0;
		if(CalularNoPasosProfundidadeCorte.NpasosDecimalesPC>0){
			pasosEspiralBorde = CalularNoPasosProfundidadeCorte.NpasosEnterosPC +1;
		}else{
			pasosEspiralBorde = CalularNoPasosProfundidadeCorte.NpasosEnterosPC;
		}

		//hago la cantidad de pasos enteros
		for (int i = 0; i < pasosEspiralBorde; i++) {
			// cuadrante 1 (x- , y)
			if(i<pasosEspiralBorde-1){
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " Z-" + GeneradorCodidoG.df.format(GeneradorCodidoG.ProfundidadeCorte) +  " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y-" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y-" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
				}else{
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " Z-" + GeneradorCodidoG.df.format(GeneradorCodidoG.ProfundidadeCorte * CalularNoPasosProfundidadeCorte.NpasosDecimalesPC) +  " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y-" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y-" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
				}
			}
		//DEJO REDONDO EL CIRCULO	
		ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
		if(imprimirBase==true){
			ImprimirSecuenciaLineaNo.EscribirLinea("G40 G90 G01 X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY));
			ImprimirSecuenciaLineaNo.EscribirLinea("X" + GeneradorCodidoG.df.format(CentroX + DiametroTool/2));
			ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(CentroX - DiametroTool/2) + " R" + GeneradorCodidoG.df.format(DiametroTool/2));
			ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(CentroX + DiametroTool/2) + " R" + GeneradorCodidoG.df.format(DiametroTool/2));
			}
		ImprimirSecuenciaLineaNo.EscribirLinea("G01 X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY));
		ImprimirPlanoSeg.ImpPlanoSeguranza();
		DiametroToolAnterior=DiametroTool;
	}
		
	public static void GCode_FS_HacerEspiral(double CentroX, double CentroY, double DiatroInicialFuro, double DiametroTool, double ProfundidadeCorte){
		double Radio1raPasada=(DiametroToolAnterior/2)+(DiametroTool/2);
		double pasosEspiralBorde=0;
		if(CalularNoPasosEspiral.NpasosDecimalesEsp>0){
			pasosEspiralBorde = CalularNoPasosEspiral.NpasosEnterosEsp+1;
		}else{
			pasosEspiralBorde = CalularNoPasosEspiral.NpasosEnterosEsp;
		}

		//hago la cantidad de pasos enteros
		for (int i = 1; i < (pasosEspiralBorde+1); i++) {
			if(i==1){//tiene que haber un primero G03
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(Radio1raPasada) +" Y" + GeneradorCodidoG.df.format(Radio1raPasada) + " Z-" + GeneradorCodidoG.df.format(ProfundidadeCorte) +  " R" + GeneradorCodidoG.df.format(Radio1raPasada));
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(Radio1raPasada) +" Y-" + GeneradorCodidoG.df.format(Radio1raPasada) + " R" + GeneradorCodidoG.df.format(Radio1raPasada));
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(Radio1raPasada) +" Y-" + GeneradorCodidoG.df.format(Radio1raPasada) + " R" + GeneradorCodidoG.df.format(Radio1raPasada));
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(Radio1raPasada+(DiametroTool/2)) +" Y" + GeneradorCodidoG.df.format(Radio1raPasada) + " R" + GeneradorCodidoG.df.format(Radio1raPasada+(DiametroTool/2)));
			}else{
			if(i<=(pasosEspiralBorde-1)){
				double XYRadio=(i*DiametroTool/2)+(DiametroToolAnterior/2);
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(XYRadio) +" Y" + GeneradorCodidoG.df.format(XYRadio) + " R" + GeneradorCodidoG.df.format(XYRadio));
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(XYRadio) +" Y-" + GeneradorCodidoG.df.format(XYRadio) + " R" + GeneradorCodidoG.df.format(XYRadio));
				ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(XYRadio) +" Y-" + GeneradorCodidoG.df.format(XYRadio) + " R" + GeneradorCodidoG.df.format(XYRadio));
				
				double rad1 = (XYRadio + (DiametroTool/2));
				double rad2 = DiatroInicialFuro/2;
				if(rad1<rad2){
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(XYRadio + (DiametroTool/2)) +" Y" + GeneradorCodidoG.df.format(XYRadio) + " R" + GeneradorCodidoG.df.format(XYRadio + (DiametroTool/2)));
				}else{
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(rad2) +" Y" + GeneradorCodidoG.df.format(XYRadio) + " R" + GeneradorCodidoG.df.format(rad2));
				}
				
				}else{
					//llmar a hacer el borde/--
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X-"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y-" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y-" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
					ImprimirSecuenciaLineaNo.EscribirLinea("G03 X"+ GeneradorCodidoG.df.format(DiatroInicialFuro/2) +" Y" + GeneradorCodidoG.df.format(DiatroInicialFuro/2) + " R" + GeneradorCodidoG.df.format(DiatroInicialFuro/2));
				}//fin if passos restantes
			}//finif_i=1

			
			
		}//finfor
		
		//ImprimirSecuenciaLineaNo.EscribirLinea("G40 G90 G01 X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY));
		
	}
	
	
	
	
	//para hacer el paso final de un furo
	public static void FuroSimplePassoFinal(double CentroX, double CentroY, double ProfundidadeAnterior, double ProfundidadeFinal, double FeedRate) {
		ImprimirSecuenciaLineaNo.EscribirLinea("G40 G00 X" + GeneradorCodidoG.df.format(CentroX) + " Y" + GeneradorCodidoG.df.format(CentroY) + " Z-" + GeneradorCodidoG.df.format(ProfundidadeAnterior));
		ImprimirSecuenciaLineaNo.EscribirLinea("G01 F" + GeneradorCodidoG.df.format(FeedRate*1.5) + " Z-" + GeneradorCodidoG.df.format(ProfundidadeFinal));
		}
	
	
	/**** Funciones accesorias ***/
	

	

}
