package br.UNB.LAB.FuncoesDePosicao;

import java.text.DecimalFormat;
import java.util.ArrayList;

import br.UNB.LAB.GerarCodG.GeneradorCodidoG;

public class CalculoDosPontosDaReta {

	public static double DiaInicial=0;
	static double y1=0;
	
	public static double DiaFinal=0;
	public static double profFuro=0;
	public static double diaTool=0;
	
	public static double m=0;
	public static double b=0;
	
	public static double Avance=0.5;
	
	//public static double DiaSeg[] = new double[30];//array de pociciojnes para este Slot
	//public static double ProfSeg[] = new double[30];//array de pociciojnes para este Slot
	
	public static ArrayList<Double>  DiaSeg = new ArrayList<Double>();
	public static ArrayList<Double> ProfSeg = new ArrayList<Double>();
	private static ArrayList<Double>  DiaSegTemInv = new ArrayList<Double>();
	private static ArrayList<Double> ProfSegTemInv = new ArrayList<Double>();
	private static int conta;
	
	
	
	
	//public static void main(String[] args) {
		public static void CalPuntosLin(){
			m=0;
			b=0;
		
		DecimalFormat df = new DecimalFormat("0.00");
		
		//ecuacion de la recta y = m*x + b
		
		//diaTool=6;
		double RadTool=diaTool /2;
		
		
		//DiaInicial=20;//x inicial
		double RadInicial=DiaInicial/2;
		y1=0;//y inicial
		
		
		//DiaFinal=6;//x final
		double RadFinal=DiaFinal/2;
		//profFuro=-10;//valor de la profundidad (y final)
		
		
		//calculando m
		m=(profFuro-y1)/(RadFinal-RadInicial);
		//System.out.println(df.format(m));
		
		//calculado b apartir de la primera pocicion x1,y1
		b=y1-(m*RadInicial);
		//System.out.println(df.format(b));
		
		double yt=0,xt=0;
		conta=0;
		do{
			//calculando el valor de xt para hacer las camadas
			xt=((-b+Math.abs(yt))/m)-RadTool;
			
			//DiaSeg[conta]=(Math.round((xt*2)*1000)/1000.0);
			String xs=df.format(xt).replace(",", ".");
			DiaSeg.add(Double.parseDouble(xs));
			ProfSeg.add(-profFuro+ Math.abs(yt));
			
			//System.out.println("D"+DiaSeg[conta]+", Z"+(profFuro+ProfSeg[conta]));//el valor de y seria el de la circunferencia
			
			yt-=Avance;
			conta++;
		}while(yt>-profFuro);
		//para finalizar con el valor faltante
		
	xt=((-b+profFuro)/m)-RadTool;
	//System.out.println("D"+df.format(xt)+", Z"+df.format(profFuro));
		
		

	}

		
		//public static void main(String[] args) {
		public static void CalculosDoPuntosLinha_F2(double DiametroInicialFuro,double DiametroFinallFuro,double ProfundidadeFuro){
					
			m=0;
			b=0;
			
			Avance = GeneradorCodidoG.AvanceDaPendienteDaLinha;
			
			DecimalFormat df = new DecimalFormat("0.0000");
			//ecuacion de la recta y = m*x + b
			
			//diaTool=6;
			//double RadTool=DiametroTool/2;
			
			
			//DiaInicial=20;//x inicial
			double RadInicial=DiametroInicialFuro/2;
			y1=0;//y inicial
			
			
			//DiaFinal=6;//x final
			double RadFinal=DiametroFinallFuro/2;
			//profFuro=-10;//valor de la profundidad (y final)
			
			
			//calculando m
			m=(ProfundidadeFuro)/(RadFinal-RadInicial);
			//System.out.println(df.format(m));
			
			//calculado b apartir de la primera pocicion x1,y1
			b=(m*RadInicial);
			//System.out.println(df.format(b));
			
			
			
			
			double yt=0,xt=0;
			conta = 0;
			do{
				//calculando el valor de xt para hacer las camadas
				//xt=((-b+Math.abs(yt))/m);
				
				xt=Math.abs(((yt-b)/m));
				
				
				//DiaSeg[conta]=(Math.round((xt*2)*1000)/1000.0);
				String xs=df.format(xt).replace(",", ".");
				DiaSeg.add(Double.parseDouble(xs)*2);
				ProfSegTemInv.add(-ProfundidadeFuro+ Math.abs(yt));
				
				//System.out.println("D"+DiaSeg[conta]+", Z"+(profFuro+ProfSeg[conta]));//el valor de y seria el de la circunferencia
				
				yt-=Avance;
				conta++;
			}while(yt>-ProfundidadeFuro-1);
			
			//para invertir los valores
			int invirtiendo =0;
			for (int i = 1; i < ProfSegTemInv.size()+1; i++) {
				invirtiendo = ProfSegTemInv.size() -i;
				double valorPF= ProfSegTemInv.get(invirtiendo);
				ProfSeg.add(i-1, valorPF);
			}
			
/*			invirtiendo =0;
			for (int i = 1; i < DiaSegTemInv.size()+1; i++) {
				invirtiendo = DiaSegTemInv.size() -i;
				double valorPF= DiaSegTemInv.get(invirtiendo);
				DiaSeg.add(i-1, (valorPF*2));
			}*/
			
			//System.out.println();
			
			
			
			//para finalizar con el valor faltante
			
		//xt=((-b+ProfundidadeFuro)/m)-RadTool;
		//System.out.println("D"+df.format(xt)+", Z"+df.format(profFuro));
			
		}
}
