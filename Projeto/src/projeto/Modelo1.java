package projeto;

import gurobi.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Modelo1 {

//	N : Conjunto de Tarefas
	int      N;
//	M : Conjunto de Máquinas
	int      M;
//	t_i_m: tempo da tarefa i na maquina j
	double   t[][];
//	p_i_j_m : tempo de preparo entre a tarefa i e a tarefa j na máquina m
	double   p[][][];
//	delta_m: tempo total da máquina m
	GRBVar   delta[];
//	makespan : max(forall m. delta_m)
	GRBVar   makespan;
//	x_i_j_m: se a tarefa i é feita primeiro que j na máquina m
	GRBVar   x[][][];
    GRBEnv   env  ;// = new GRBEnv("mip1.log");
    GRBModel model;// = new GRBModel(env);
	
	public Modelo1(int N, int M, double t[][], double p[][][]) {
		this.N = N+1;
		this.M = M;
		this.t = t.clone();
		this.p = p.clone();
		delta = new GRBVar[M];
		x = new GRBVar[N+1][N+1][M];
	}
	
	public Modelo1(String file) {
		readFile(file);
	}
	
	public void criarModel() {//throws GRBException {
		try {
			env   = new GRBEnv("mip1.log");
			model = new GRBModel(env);

		makespan = model.addVar(0, GRB.INFINITY, 1, GRB.CONTINUOUS, "Makespan");
//		for(int m=0;m<M;m++) {
//			delta[m] = model.addVar(0, GRB.INFINITY, 0, GRB.CONTINUOUS, "delta_"+(m+1));
//		}
		for(int m=0;m<M;m++) {
			delta[m] = model.addVar(0, GRB.INFINITY, 0, GRB.CONTINUOUS, "delta_"+(m+1));
			for(int i=0;i<N;i++) {
				for(int j=0;j<N;j++) {
					x[i][j][m] = model.addVar(0, 1, 0, GRB.BINARY, "x_"+i+","+j+","+(m+1));
				}
			}
		}
		
//		Add Constraint : forall m. sum_i_j(x[i][j][m]) 
		for(int j=1;j<N;j++) {
			GRBLinExpr const1 = new GRBLinExpr();
			for(int i=0;i<N;i++) {
				for(int m=0;m<M;m++) {
					if(i!=j)
						const1.addTerm(1, x[i][j][m]);
				}
			}
			model.addConstr(const1, GRB.EQUAL, 1, "begin_"+j);
		}

//		Add Constraint : forall i,m. sum_j(x[i][j][m]) = sum_j(x[j][i][m]) 
		for(int i=1;i<N;i++) {
			for(int m=0;m<M;m++) {
				GRBLinExpr const3 = new GRBLinExpr();
				GRBLinExpr const2 = new GRBLinExpr();
				const2.addTerm(1,x[0][i][m]);
				for(int j=1;j<N;j++) {
					if(i!=j) {
						const3.addTerm(1, x[i][j][m]);
						const2.addTerm(1, x[j][i][m]);
					}
				}
				model.addConstr(const3, GRB.LESS_EQUAL, const2, "end_"+i+","+(m+1));
			}
		}
		
//		Add constraint : forall m. delta_m = sum_i_j((p_i_j_m + t_j_m)x_i_j_m)
		for(int m=0;m<M;m++) {
			GRBLinExpr const4 = new GRBLinExpr();
			for(int i=0;i<N;i++) {
				for(int j=0;j<N;j++) {
					if(i!=j)
						const4.addTerm((t[j][m] + p[i][j][m]),x[i][j][m]);
				}
			}
			model.addConstr(delta[m], GRB.EQUAL, const4, "time_"+(m+1));
		}
		
//		forall m. sum_i(x_i_o_m) = 0
		for(int m=0;m<M;m++) {
			GRBLinExpr const1 = new GRBLinExpr();
			for(int i=1;i<N;i++) {
				const1.addTerm(1, x[i][0][m]);
			}
			model.addConstr(const1, GRB.EQUAL, 0, "var_begin_"+m);
		}
		
//		forall m. sum_j(x_0_j_m) = 1 # não necessariamente toda máquina irá executar uma máquina
		for(int m=0;m<M;m++) {
			GRBLinExpr const1 = new GRBLinExpr();
			for(int j=1;j<N;j++) {
				const1.addTerm(1, x[0][j][m]);
			}
			model.addConstr(const1, GRB.LESS_EQUAL, 1, "var_begin_"+m);
		}
		
//		Add constraint : forall m : makespan >= delta_m
		for(int m=0;m<M;m++) {
			model.addConstr(makespan, GRB.GREATER_EQUAL, delta[m], "makespan");
		}
		
		model.update();
		}
		catch(GRBException e) {
			System.out.println("Suas instâncias ");
		}
	}

	public void optimize() throws GRBException{
		model.optimize();
	}
	
	public GRBModel getModel() {
		try {
			model.update();
		}
		catch(GRBException e) {
			System.out.println("Não foi possível realizar a atualização das informações");
		}
		return model;
	}
	
	public void printP() {
		String s = "";
		for(int i=0;i<N;i++) {
			for(int j=0;j<N;j++) {
				for(int m=0;m<M;m++) {
					System.out.println(p[j][i][m]);
					s+= p[i][j][m] + " ";
				}
			}
			s+="\n";
		}
		System.out.println(s);
	}
	
	public void printVars() throws GRBException{
		GRBVar[] var = model.getVars();
		for(int i=0;i<var.length;i++) {
			double l = var[i].get(GRB.DoubleAttr.X);
			if(l>0.00001) {
				String name = var[i].get(GRB.StringAttr.VarName);
				System.out.printf("  %10s : %8.2f\n",name, l);
			}
		}
	}

	public void readFile(String file) {
		BufferedReader br = null;
		FileReader fr = null;

		try {
		
			fr = new FileReader(file); // Máquinas identicas
			br = new BufferedReader(fr);
		
			String sCurrentLine = br.readLine();
			String[] s = sCurrentLine.split("  ");
//			for(int i=0;i<s.length;i++)
//				System.out.println(s[i]);
			N = Integer.parseInt(s[0])+2; 
			M = Integer.parseInt(s[1]);
			System.out.println(N + " - " + M);
			p = new double[N][N][M];
			for(int i=1;i<N-1;i++) {
				sCurrentLine = br.readLine();
				s = sCurrentLine.split("  ");
//				for(int v=0;v<s.length;v++)
//					System.out.println("|" + s[v] + "|");
				for(int j=1;j<N-1;j++) {
					System.out.println("( " + i + " , " + j + " ) = " +  Integer.parseInt(s[j]));
					for(int k=0;k<M;k++) {
						p[i][j][k] = Integer.parseInt(s[j]);
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		t = new double[N][M];
		for(int i=0;i<N;i++) {
			for(int k=0;k<M;k++) {
				t[i][k] = 0;
			}
		}
		delta = new GRBVar[M];
		x = new GRBVar[N+1][N+1][M];
		
	}

}
