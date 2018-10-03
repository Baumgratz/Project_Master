package projeto;

import gurobi.*;

public class Main {

	public static void main(String[] args) {
//		tarefas
		int n = 4;
//		máquinas
		int m = 2;
//		tempo de execução
		double t[][] = {{GRB.INFINITY, GRB.INFINITY},
						{5,1},
						{3,5},
						{5,3},
						{2,5},
						{0,0}};
//		preparação
		double p[][][] = new double[n+2][n+2][m];
		for(int ma=0;ma<m;ma++) {
			for(int i=0;i<n+2;i++) {
				for(int j=0;j<n+2;j++) {
					if(i!=j || j!=0 || j!=n+1)
						p[i][j][ma] = 100;
				}
				p[0][i][ma] = 0;
				p[i][n+1][ma] = 0;
			}
		}
		p[4][2][0] = 1;
		p[1][3][1] = 1;
		
//		Modelo1 model = new Modelo1(n,m,t,p);
		Modelo2 model = new Modelo2(n,m,t,p);
		model.criarModel();
		System.out.println("Criou o Modelo");
		System.out.println("modelo : " + model.getModel());
		try {
			model.optimize();
		} 
		catch (GRBException e) {
			System.out.println("Modelo Inviável");
		}
		try {
			model.printVars();
		}
		catch(GRBException e) {
			System.out.println("Não é possível imprimir as variáveis");
		}
	}

}
