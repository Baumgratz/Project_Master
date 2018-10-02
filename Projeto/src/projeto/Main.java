package projeto;

import gurobi.*;

public class Main {

	public static void main(String[] args) {
		Modelo1 model;
//		tarefas
		int n = 1;
//		máquinas
		int m = 1;
//		tempo de execução
		double t[][] = {{0},
						{5}};
//		preparação
		double p[][][] = new double[n+1][n+1][m];
		p[0][0][0] = 1000;
		p[0][1][0] = 5;
		p[1][0][0] = 1000;
		p[1][1][0] = 1000;
		
		try {
			model = new Modelo1(n,m,t,p);
			model.criarModel();
			System.out.println("Criou o Modelo");
			System.out.println("modelo : " + model.getModel());
			model.optimize();
			model.printVars();
		} 
		catch (GRBException e) {
			System.out.println("Não foi possivel resolver!");
//			e.printStackTrace();
		}
//		catch(Exception e) {
//			System.out.println("???");
//		}
	}

}
