package projeto;

import gurobi.*;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
//		tarefas
		int n = 2;
//		máquinas
		int m = 2;
//		tempo de execução
		double t[][] = genTempo(n,m);
//		print(t,n,m);
//		preparação
		double p[][][] = genPreparo(n,m);
//		print(p,n,m);

//		Modelo1 model1 = new Modelo1(n,m,t,p);
//		Modelo2 model2 = new Modelo2(n,m,t,p);

		Modelo1 model1 = new Modelo1("VFR10_5_1_Gap.txt");
		Modelo2 model2 = new Modelo2(model1);
		model1.criarModel();
		model2.criarModel();
		System.out.println("Criou o Modelo");
		System.out.println("modelo1 : " + model1.getModel());
		System.out.println("modelo2 : " + model2.getModel());
		try {
			model1.optimize();
			model2.optimize();
		} 
		catch (GRBException e) {
			System.out.println("Modelo Inviável");
		}
		try {
			System.out.println("=============================");
			System.out.println("Modelo 1:");
			model1.printVars();
			System.out.println("=============================");
			System.out.println("Modelo 2:");
			model2.printVars();
		}
		catch(GRBException e) {
			System.out.println("Não é possível imprimir as variáveis");
		}
	}
	
	public static double[][] genTempo(int n, int m){
		Random gen = new Random();
		double[][] t = new double[n+2][m];
		for(int k=0;k<m;k++) {
			for(int i=1;i<n+1;i++) {
				t[i][k] = (double)(gen.nextInt(1000))/100;
			}
			t[0][k] = 0;
			t[n+1][k] = 0;
		}
		return t.clone();
	}
	
	public static double[][][] genPreparo(int n, int m){
		Random gen = new Random();
		double[][][] p = new double[n+2][n+2][m];
		for(int k=0;k<m;k++) {
			for(int i=1;i<n+1;i++) {
				for(int j=1;j<n+1;j++) {
					if(i!=j)
						p[i][j][k] = (double)(gen.nextInt(1000))/100;
				}
				p[0][i][k] = 0;
				p[i][n+1][k] = 0;
			}
		}
//		for(int k=0;k<m;k++) {
//			for(int i=1;i<n+1;i++) {
//				for(int j=i+1;j<n+1;j++) {
//					p[i][j][m] = gen.nextInt(10);
//				}
//			}
//		}
		return p.clone();
	}
	
	public static void print(double[][][] p,int n, int m) {
		for(int k=0;k<m;k++) {
			System.out.println("Máquina " + (k+1) + ":");
			System.out.print("End\\Begin ");
			for(int i=1;i<n+1;i++) {
				System.out.printf(" %5d", i);
			}
			System.out.println("");
			for(int i=1;i<n+1;i++) {
				System.out.printf("%9s ",i);
				for(int j=1;j<n+1;j++) {
					if(i==j)
						System.out.printf(" %5s","-");
					else
						System.out.printf(" %5.02f",p[i][j][k]);
				}
				System.out.println("");
			}
			System.out.println("");
			System.out.println("--------------------------------------");
			System.out.println("");
		}
	}
	public static void print(double[][] t,int n, int m) {
		System.out.printf("Maq \\ Ta");
		for(int i=1;i<n+1;i++) {
			System.out.printf(" %6d", i);
		}
		System.out.println("");
		for(int k=0;k<m;k++) {
			System.out.printf("Maqui %2d ",k+1);
			for(int i=1;i<n+1;i++) {
				System.out.printf("%6.02f ",t[i][k]);
			}
			System.out.println("");
		}

		System.out.println("");
		System.out.println("--------------------------------------");
		System.out.println("");
	}
	
	

}
