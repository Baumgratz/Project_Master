package projeto;

import gurobi.*;

public class Modelo1 {

//	N : Conjunto de Tarefas
	private int      N;
//	M : Conjunto de Máquinas
	private int      M;
//	t_i_m: tempo da tarefa i na maquina j
	private double   t[][];
//	p_i_j_m : tempo de preparo entre a tarefa i e a tarefa j na máquina m
	private double   p[][][];
//	delta_m: tempo total da máquina m
	private GRBVar   delta[];
//	makespan : max(forall m. delta_m)
	private GRBVar   makespan;
//	x_i_j_m: se a tarefa i é feita primeiro que j na máquina m
	private GRBVar   x[][][];
    private GRBEnv   env  ;// = new GRBEnv("mip1.log");
    private GRBModel model;// = new GRBModel(env);
	
	public Modelo1(int N, int M, double t[][], double p[][][]) {
		this.N = N+1;
		this.M = M;
		this.t = t.clone();
		this.p = p.clone();
		delta = new GRBVar[M];
		x = new GRBVar[N+1][N+1][M];
	}
	
	public void criarModel() throws GRBException {
		env   = new GRBEnv("mip1.log");
		model = new GRBModel(env);
		
		for(int m=0;m<M;m++) {
			delta[m] = model.addVar(0, 1000000, 0, GRB.CONTINUOUS, "delta_"+(m+1));
			for(int i=0;i<N;i++) {
				for(int j=0;j<N;j++) {
					x[i][j][m] = model.addVar(0, 1, 0, GRB.BINARY, "x_"+i+","+j+","+(m+1));
				}
			}
		}
		makespan = model.addVar(0, 1000000, 1, GRB.CONTINUOUS, "Makespan");
		
//		Add Constraint : forall m. sum_i_j(x[i][j][m]) 
		for(int i=0;i<N;i++) {
			GRBLinExpr const1 = new GRBLinExpr();
			for(int j=0;j<N;j++) {
				for(int m=0;m<M;m++) {
					if(i!=j)
						const1.addTerm(1, x[i][j][m]);
				}
			}
			model.addConstr(const1, GRB.EQUAL, 1, "begin_"+i);
		}

//		Add Constraint : forall i,m. sum_j(x[i][j][m]) = sum_j(x[j][i][m]) 
		for(int i=0;i<N;i++) {
			for(int m=0;m<M;m++) {
				GRBLinExpr const3 = new GRBLinExpr();
				GRBLinExpr const2 = new GRBLinExpr();
				for(int j=1;j<N;j++) {
					if(i!=j) {
						const3.addTerm(1, x[i][j][m]);
						const2.addTerm(1, x[j][i][m]);
					}
				}
				model.addConstr(const3, GRB.EQUAL, const2, "end_"+i+","+(m+1));
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
		
//		forall m,. sum_i(x_i_o_m) = 0
		for(int m=0;m<M;m++) {
			GRBLinExpr const1 = new GRBLinExpr();
			for(int i=1;i<N;i++) {
				const1.addTerm(1, x[i][0][m]);
			}
			model.addConstr(const1, GRB.EQUAL, 0, "var_begin_"+m);
		}
		
//		Add constraint : forall m : makespan >= delta_m
		for(int m=0;m<M;m++) {
			model.addConstr(makespan, GRB.LESS_EQUAL, delta[m], "makespan");
		}
		model.update();
	}

	public void optimize() throws GRBException{
		model.optimize();
	}
	
	public GRBModel getModel() throws GRBException {
		model.update();
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
			System.out.println();
		}
	}
	
}
