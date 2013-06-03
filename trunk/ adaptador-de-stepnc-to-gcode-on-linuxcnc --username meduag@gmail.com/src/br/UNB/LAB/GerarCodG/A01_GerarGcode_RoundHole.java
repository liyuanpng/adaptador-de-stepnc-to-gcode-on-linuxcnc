package br.UNB.LAB.GerarCodG;

import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import br.UNB.LAB.Entidades_E_Atributos.Axis2Placement3D;
import br.UNB.LAB.FAcessorias.ExtrairDiametroTool;
import br.UNB.LAB.FAcessorias.ExtrairToken3_Profundidad_DiaInicial_DiaFinal;
import br.UNB.LAB.FAcessorias.ExtrairUbicacao;
import br.UNB.LAB.FuncoesDePosicao.*;
import br.UNB.LAB.InfAvançadas.MapearMachining_workingstep;
import br.UNB.LAB.InfBasicas.DadosDaPeca;
import br.UNB.LAB.InfBasicas.PlanoSeguranca;



public class A01_GerarGcode_RoundHole {
	
	int ctokens=0;//contador de tokens
	int salif=1;
	int paso_sig_lineNo = 0;
	//int ExisteDiaFin=0;
	double diametroInicialFuro=0;
	double diametroFinalFuro=0;
	double ProfundidadeFuro=0;
	double RadioBase=0;
	//double X=0,Y=0,Z=0;
	String HoleBoCond="";
	
	//activacion de herramientas
	int Cd=1,	Td=1,	FaM=1,	BaEM=0,	BuEM=0;
	
	//valores para generar el codigo G
	double CemtroX=0,CentroY=0,Z=0;
	public static double OffsetProfZ=0;
	
	public static int ValorPasoAngulo=10;
	public static double AngOffsetTwD=0;
	public static int LineNo=0;
	//public static double DiametroTempTwisDrill_Facemill=0;
	
	//boleanos
	//public static boolean FuroPasante=false;
	
	

	@SuppressWarnings("static-access")
	public void GenCodG_RoundHoleOper(){
		//FuroPasante=false;
		
		MapearMachining_workingstep ExtrairDatos = new MapearMachining_workingstep();
		//FuroSimples Fsimp = new FuroSimples();
		FuroComplexo Fcomp = new FuroComplexo();
		PlanoSeguranca SecPlan = new PlanoSeguranca();
		//Axis2Placement3D OffCD = new Axis2Placement3D();
		FuroSegmentado Fseg = new FuroSegmentado();
		//CalculoDosPontosDaReta CalPonRect = new CalculoDosPontosDaReta();
		CalculoDosPuntosDoCirculo CalPonCir = new CalculoDosPuntosDoCirculo();
		
		
		LineNo=GeneradorCodidoG.LineNo;
		String data2=SecPlan.ComprimentoPlanoSeg;
		
		StringTokenizer stp = new StringTokenizer(data2, "=;",true);//buscador de tokens con separadores activados
		ctokens=0;
		do{//asignacion de direcciones
			ctokens++;//contador de tokens para encontrar el parentesis
			String valores = stp.nextToken();//leo el proximo token
			//System.out.println(ctokens + " "+valores);
			double menosPS=0;
			if(ctokens==3){//copia el primer token con la primera ferramenta
				//System.out.println("Imprimiendo             "+valores);
				//Fsimp.SecPlan=Double.parseDouble(valores)-menosPS;
				Fcomp.SecPlan=Double.parseDouble(valores)-menosPS;
				Fseg.SecPlan=Double.parseDouble(valores)-menosPS;
			}
		}while(stp.hasMoreTokens());
		
		

		int TamList=ExtrairDatos.InformacoesAvancadas.size();
		int cDatList=0;
		
		do{
			String data=ExtrairDatos.InformacoesAvancadas.get(cDatList);
	
			Point3d PocisaoXYZ = null;//utilizo esta variable para adquirir los valores extraidos 
			if(data.startsWith("Ubicacion:")==true){
				PocisaoXYZ = ExtrairUbicacao.ExtPocisaoXYZ(data);
				//valores X

				Fcomp.CentroX=PocisaoXYZ.x;
				Fseg.CentroX=PocisaoXYZ.x;
				CemtroX=PocisaoXYZ.x;
				//valores Y
				Fcomp.CentroY=PocisaoXYZ.y;
				Fseg.CentroY=PocisaoXYZ.y;
				CentroY=PocisaoXYZ.y;
				//valores Z
				OffsetProfZ=PocisaoXYZ.z;
				Z=PocisaoXYZ.z;
			}
			
			if(data.startsWith("Profundidad:")==true){
				Fcomp.ProfTotalFuro=ExtrairToken3_Profundidad_DiaInicial_DiaFinal.ExtrairToken3(data,"P");
				ProfundidadeFuro=ExtrairToken3_Profundidad_DiaInicial_DiaFinal.ExtrairToken3(data,"P");
			}
			
			if(data.startsWith("Diametro Inicial:")==true){//diametro del furo en la cara Xy
				Fcomp.DiaFuro=ExtrairToken3_Profundidad_DiaInicial_DiaFinal.ExtrairToken3(data,"D");
				Fseg.DiaFuro=ExtrairToken3_Profundidad_DiaInicial_DiaFinal.ExtrairToken3(data,"D");
				diametroInicialFuro=ExtrairToken3_Profundidad_DiaInicial_DiaFinal.ExtrairToken3(data,"D");
			}
			
			
			if(data.startsWith("Diametro Final:")==true){//diametro del furo en la cara Xy
				Fseg.DiaFuro=ExtrairToken3_Profundidad_DiaInicial_DiaFinal.ExtrairToken3(data,"D");
				diametroFinalFuro=ExtrairToken3_Profundidad_DiaInicial_DiaFinal.ExtrairToken3(data,"D");
				//ExisteDiaFin=1;
			}
			
			
			if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
				System.out.println("("+data+")");
			}
			
			

			if(data.indexOf("Hole Buttom Condition")!=-1){
				StringTokenizer st4 = new StringTokenizer(data, ":,=;",true);//buscador de tokens con separadores activados
				ctokens=0;
				do{//asignacion de direcciones
					ctokens++;//contador de tokens para encontrar el parentesis
					String valores = st4.nextToken();//leo el proximo token
					//System.out.println(ctokens + " "+valores);
					if(ctokens==3){//copia el primer token con la primera ferramenta
						HoleBoCond=valores;
						//System.out.println("Imprimiendo             "+valores);
					}
					if(ctokens==7){//copia el primer token con la primera ferramenta
						RadioBase=Double.parseDouble(valores);
						//System.out.println("Imprimiendo             "+valores);
					}
				}while(st4.hasMoreTokens());
		
				if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
					System.out.println("(Fin feature)");
				}
				salif=0;
			}
			cDatList++;//sumno la siguiente linea del codigo de la feature
		}while(cDatList<TamList & salif==1);
		
		
		
		//Activar las herramientas dependiendo das operaciones de la feature (Hole Bottom Condition)
		
		if(HoleBoCond.startsWith("FLAT_HOLE_BOTTOM")==true){
			Cd=1;
			Td=1;
			FaM=1;
			BaEM=0;
			BuEM=0;
		}
		
		if(HoleBoCond.startsWith("CONICAL_HOLE_BOTTOM")==true){
			Cd=1;
			Td=1;
			FaM=0;
			BaEM=0;
			BuEM=0;
		}
		
		if(HoleBoCond.startsWith("SPHERICAL_HOLE_BOTTOM")==true){
			Cd=1;
			Td=1;
			FaM=1;
			BaEM=1;
			BuEM=0;
			//para colocar la compensacion de tool al inicio del cod g
			//Fcomp.Inicio=0;
		}
		
		if(HoleBoCond.startsWith("FLAT_WITH_RADIUS_HOLE_BOTTOM")==true){
			Cd=1;
			Td=1;
			FaM=1;
			BaEM=0;
			BuEM=1;
			//para colocar la compensacion de tool al inicio del cod g
			//Fcomp.Inicio=1;
		}
		
		if(HoleBoCond.startsWith("THROUGH_BOTTOM_CONDITION")==true){
			//FuroPasante=true;
			ProfundidadeFuro = Double.parseDouble(DadosDaPeca.ComprimentoZ_DoBloco);
			Cd=1;
			Td=1;
			FaM=1;
			BaEM=0;
			BuEM=0;
			//para colocar la compensacion de tool al inicio del cod g
			//Fcomp.Inicio=1;
		}

		/**************************************** Generar o Cod G *********************************************************/
		/**************************************** Generar o Cod G *********************************************************/
		/**************************************** Generar o Cod G *********************************************************/
		cDatList=1;
		int salir=0;
		salif=1;
		String data="";
		
		do{	
			data=ExtrairDatos.InformacoesAvancadas.get(cDatList);

			if(data.indexOf("Feature")!=-1){
				salir++;
			}

			/*************************************************************************************************************************************************************************************************************/
			/*************************************************************************************************************************************************************************************************************/
			/*************************************************************************************************************************************************************************************************************/
			///hace un furo simple
			if(data.indexOf("CENTER_DRILL")!=-1 & Cd==1){
			
				if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
					System.out.println("(Dados da Nova Ferramenta: "+data+")");
				}

				RH_Gcode.GCode_Furos(CemtroX, CentroY, ProfundidadeFuro, diametroInicialFuro, diametroFinalFuro, diametroFinalFuro, data,1);
			}
			
			
			/*************************************************************************************************************************************************************************************************************/
			/*************************************************************************************************************************************************************************************************************/
			/*************************************************************************************************************************************************************************************************************/
			//furo simples 
			if(data.indexOf("TWIST_DRILL")!=-1 & Td==1){
				if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
					System.out.println("(Dados da Nova Ferramenta: "+data+")");
				}
				
				double c=0,a=0,b=0;//para calcular la profundidad de la herramienta y sumarsela a la prof total
				if(BaEM==1 ){
					a=TrocaDeFerramenta.DatoDiametroTool(data)/2;
					c=a/(Math.cos(TrocaDeFerramenta.DatoAngBAseTool(data)));
					b=Math.sqrt(Math.abs((c*c) - (a*a)));
					b = (Math.round(b*10000)/10000.0);
				}
				
				if(Cd==1 & Td==1 & FaM==0 & BaEM==0 & BuEM==0){
					a=TrocaDeFerramenta.DatoDiametroTool(data)/2;
					c=a/(Math.cos(TrocaDeFerramenta.DatoAngBAseTool(data)));
					b=Math.sqrt(Math.abs((c*c) - (a*a)));
					b = (Math.round(b*10000)/10000.0);
				}
				
				RH_Gcode.GCode_Furos(CemtroX, CentroY, ProfundidadeFuro+b, diametroInicialFuro, diametroFinalFuro, diametroFinalFuro, data,1);//el 0 sig Offset
				
				Td=2;
				if(Cd==2 & Td==2 & FaM==0 & BaEM==0 & BuEM==0){//salir de Round Hole
					salir=2;
					}
			}//fin twisdrill
			
			
			/*************************************************************************************************************************************************************************************************************/
			/*************************************************************************************************************************************************************************************************************/
			/*************************************************************************************************************************************************************************************************************/
			//furo complexo
			if(data.indexOf("FACEMILL")!=-1 & FaM==1){
				if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
					System.out.println("(Dados da Nova Ferramenta: "+data+")");
				}
				
				if(diametroFinalFuro==0){//para saber si el furo es conico (ojo NOOO es base conica) 0 no es --- 1 si es
					
					if(TrocaDeFerramenta.DatoDiametroTool(data)==diametroInicialFuro){//herramienta con diametro igual al furo 
						RH_Gcode.GCode_Furos(CemtroX, CentroY, ProfundidadeFuro, diametroInicialFuro, diametroFinalFuro, diametroFinalFuro, data,1);//furo simples
					}else{//herramienta con diametro menor al furo
						RH_Gcode.GCode_Furos(CemtroX, CentroY, ProfundidadeFuro, diametroInicialFuro, diametroFinalFuro, diametroFinalFuro, data,2);//furo espiral recto
					}
				   
				}else{//para crear el furo conico----------------------------
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					RH_Gcode.GCode_Furos(CemtroX, CentroY, ProfundidadeFuro, diametroInicialFuro, diametroFinalFuro, diametroFinalFuro, data,3);//furo espiral conico 
				}
				
				//LineNo = Fcomp.LineNo;
				FaM=2;
				if(Cd==2 & Td==2 & FaM==2 & BaEM==0 & BuEM==0){
					salir=2;
					}
			}

			
			
			/*************************************************************************************************************************************************************************************************************/
			if(data.indexOf("BALL_ENDMILL")!=-1 & BaEM==1){
				if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
					System.out.println("(Dados da Nova Ferramenta: "+data+")");
				}
				
				if(TrocaDeFerramenta.DatoDiametroTool(data)==diametroInicialFuro){//herramienta con diametro igual al furo 
					RH_Gcode.GCode_Furos(CemtroX, CentroY, ProfundidadeFuro, diametroInicialFuro, diametroFinalFuro, diametroFinalFuro, data,1);//furo simples
				}else{//herramienta con diametro menor al furo
					
					CalPonCir.CalcularPuntosdaSemiCircunferencia(diametroInicialFuro, ExtrairDiametroTool.ExtrairDiametroFerrameta(data));
					aaaaaaaaaaaaaaaaaaaa
					
					
					RH_Gcode.GCode_Furos(CemtroX, CentroY, ProfundidadeFuro, diametroInicialFuro, diametroFinalFuro, diametroFinalFuro, data,3);//furo espiral recto
				}
				
				
				
/*
				double ProfundidadeAnterior=0;
				System.out.println();
				
				RH_Gcode.GCode_Furos(CemtroX, CentroY, ProfundidadeFuro, diametroInicialFuro, diametroFinalFuro, diametroFinalFuro, data,3);//furo espiral conico 
				

				//double DiamTool =  br.UNB.LAB.FAcessorias.ExtrairDiametroTool(data);
				double DiaToolBallEM = br.UNB.LAB.FAcessorias.ExtrairDiametroTool.ExtrairDiametroFerrameta(data);
				
				CalPonCir.CalcularPuntosdaSemiCircunferencia(diametroInicialFuro, DiaToolBallEM);
				
				if(DiaToolBallEM<diametroInicialFuro){//ferramenta menor que el hueco
					Imprimi el primer circulo
					Fseg.ImpGcodeFuroSegmentado_F2(CemtroX, CentroY, diametroInicialFuro, ProfundidadeFuro , data, "Sim_Imprimir_cabecera",ProfundidadeFuro);
					//la profundida sera modificada despues (en la funcion de furo seg) para colocar el radio del hueco como prof adicional
					for (int i = 0; i < CalPonCir.ProfundidadeSegmentada.size()-1; i++) {
						if(CalPonCir.DiametroSegmentado.get(i+1)<=DiaToolBallEM){
							i=CalPonCir.ProfundidadeSegmentada.size()-1;
						}else{
							Fseg.ImpGcodeFuroSegmentado_F2(CemtroX, CentroY, CalPonCir.DiametroSegmentado.get(i+1), ProfundidadeFuro , data, "Nao_Imprimir_cabecera",ProfundidadeFuro+CalPonCir.ProfundidadeSegmentada.get(i+1));
							ProfundidadeAnterior=ProfundidadeFuro+CalPonCir.ProfundidadeSegmentada.get(i+1);
						}
					}
					
				}else{//ferramenta igual que el hueco

				}*/
			
			
			}//fin de ball end mill
			
			
			
			
			/*************************************************************************************************************************************************************************************************************/
			if(data.indexOf("BULLNOSE_ENDMILL")!=-1 & BuEM==1){
				if(br.UNB.LAB.Integrador.Integrador.ImpLedFer==1){
					System.out.println("(Dados da Nova Ferramenta: "+data+")");
				}
				
				if(FaM==2){
					Fseg.inicio=1;
					Fseg.ini1=1;
					Fseg.ini2=1;
					Fseg.ini3=1;
				}
						
				Fseg.Ferramenta=data;
				//Fsimp.Ferramenta=data;
				StringTokenizer st = new StringTokenizer(data, ":,=;",true);//buscador de tokens con separadores activados
				ctokens=0;
				do{//asignacion de direcciones
					ctokens++;//contador de tokens para encontrar el parentesis
					String valores = st.nextToken();//leo el proximo token
					//System.out.println(ctokens + " "+valores);
					if(ctokens==15){//diametro Herramienta
						Fseg.DiaTool=Double.parseDouble(valores);
					}
					if(ctokens==23){//diametro Herramienta
						//// Valores pára calcular el diametro y La profundidad
						CalPonCir.graAvan=GeneradorCodidoG.ValorPasoAngulo;//avance por grados
						CalPonCir.diaFuro = RadioBase*2;//valor del radio de la base del furo
						//System.out.println("Diametro Base" + CalPonCir.diaFuro );
						CalPonCir.diaTool=Double.parseDouble(valores)*2;//valor del radio de la base de la tool
						//System.out.println("Diametro Base Herramienta" + CalPonCir.diaTool );
						CalPonCir.CalPuntosCir();
						}
				}while(st.hasMoreTokens());
				
/*				double ProfAddRadioBase = TrocaDeFerramenta.DatoAngBAseTool(data);*/
				//Fsimp.BoreDepth = Fsimp.BoreDepth + RadioBase;//adiciono el radio base a la profundidade final
				
				if(Fseg.DiaTool==Fseg.DiaFuro & CalPonCir.diaTool==CalPonCir.diaFuro){
					//StringTokenizer st3 = new StringTokenizer(data, ":",true);//buscador de tokens con separadores activados
					//Fsimp.Ferramenta=data;-------------
					//Fsimp.LineNo = LineNo;
					//Fsimp.FSimplesNc();--------
					//LineNo = Fsimp.LineNo;
				}else{
					
					if(FaM==2){
						Fseg.inicio=1;
						Fseg.ini1=1;
						Fseg.ini2=1;
						Fseg.ini3=1;
					}
					
					//Fseg.LineNo = LineNo;
					//Fseg.ini3=1;
					if(CalPonCir.diaTool==CalPonCir.diaFuro){
						FuroComplexo.ImpGcodeFuroComplexo_F2(CemtroX, CentroY, diametroInicialFuro, diametroFinalFuro, ProfundidadeFuro+RadioBase, data);
						
					}else{
						CalculoDosPuntosDoCirculo.CalcularPuntosdaSemiCircunferencia(RadioBase, RadioBase);	//calculo el diametro y el passo de corte
						FuroSegmentado.ImpGcodeFuroSegmentado_F2(CemtroX, CentroY, diametroInicialFuro, ProfundidadeFuro + ProfundidadeFuro, data, "Sim_Imprimir_cabecera", ProfundidadeFuro);
						
						for (int i = 1; i < CalculoDosPuntosDoCirculo.DiametroSegmentado.size(); i++) {//pasando el valor del diametro
							
							//System.out.println("(Imprimiendooo           "+CalculoDosPuntosDoCirculo.DiametroSegmentado.get(i)+ "         Diametro Fer:       " + CalculoDosPuntosDoCirculo.ProfundidadeSegmentada.get(i)+")");
							
							FuroSegmentado.ImpGcodeFuroSegmentado_F2(CemtroX, CentroY, CalculoDosPuntosDoCirculo.DiametroSegmentado.get(i)+(diametroInicialFuro-RadioBase), ProfundidadeFuro + CalculoDosPuntosDoCirculo.ProfundidadeSegmentada.get(i), data, "Nao_Imprimir_cabecera", ProfundidadeFuro + CalculoDosPuntosDoCirculo.ProfundidadeSegmentada.get(i));
						}
					}
					
					

				}
				
				//utilizo la funcion de ret poiket para imprimir el plano de seguranza
				RectPocketSimples.FeedRate=GeneradorCodidoG.FeedRate;
				RectPocketSimples.PlanSeg=Fseg.SecPlan;
				//RectPocketSimples.LineNo=Fseg.LineNo;
				RectPocketSimples.ImprimirPlaSeg();
				LineNo=RectPocketSimples.LineNo;
				
				BuEM=2;
				
				if(Cd==2 & Td==2 & FaM==2 & BaEM==0 & BuEM==2){
					salir=2;
					}
			}
			
			
			

			
			if(salir==2){
				Cd=0;
				Td=0;
				FaM=0;
				BaEM=0;
				BuEM=0;
			}
			
			cDatList++;
			//GeneradorCodidoG.LineNo=LineNo;
		}while(cDatList<TamList & salif==1);
		
		
	}
	
	
	

}
