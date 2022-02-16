import java.io.*;
import java.util.*;

public class Pathfinder {
	int[][] dist;
	boolean[][] visited;
	Pair[][] predecessor;
	public ArrayList<Pair> pathfinder(int[][] walls, int[][] weights, int currX, int currY, int targetX, int targetY) {
		generateDistances(walls, weights, currX, currY);
		
		ArrayList<Pair> ret = new ArrayList<Pair>();
		Pair curr = new Pair(targetX, targetY);
		while(!curr.equals(new Pair(currX, currY))) {
			ret.add(0, curr);
			curr = predecessor[curr.a][curr.b];
		}
		return ret;
	}
	
	public int getDist(int[][] walls, int[][] weights, int currX, int currY, int targetX, int targetY) {
		generateDistances(walls, weights, currX, currY);
		return dist[targetX][targetY];
	}
	
	public void generateDistances(int[][] walls, int[][] weights, int currX, int currY) {
		dist = new int[walls.length][walls[0].length];
		visited = new boolean[walls.length][walls[0].length];
		for(int i = 0; i < dist.length; i++) {
			for(int j = 0; j < dist[0].length; j++) {
				dist[i][j] = 1000000;
				visited[i][j] = false;
			}
		}
		dist[currX][currY] = 0;
		predecessor = new Pair[walls.length][walls[0].length];
		PriorityQueue<Pair> q = new PriorityQueue<Pair>(walls.length*walls[0].length, new Comparator<Pair>() {
			@Override
			public int compare(Pair o1, Pair o2) {
				return dist[o1.a][o1.b]-dist[o2.a][o2.b];
			}
			
		});
		
		q.add(new Pair(currX, currY));
		while(!q.isEmpty()) {
			Pair curr = q.poll();
			if(visited[curr.a][curr.b]) {
				continue;
			}
			if(walls[curr.a][curr.b]%2 != 1) {
				if(dist[curr.a][curr.b]+(weights[curr.a][curr.b]+weights[curr.a-1][curr.b]) < dist[curr.a-1][curr.b]) {
					dist[curr.a-1][curr.b] = dist[curr.a][curr.b]+(weights[curr.a][curr.b]+weights[curr.a-1][curr.b])/2;
					predecessor[curr.a-1][curr.b] = curr;
				}
				q.add(new Pair(curr.a-1, curr.b));
			}
			if(walls[curr.a][curr.b]/2%2 != 1) {
				if(dist[curr.a][curr.b]+(weights[curr.a][curr.b]+weights[curr.a][curr.b+1]) < dist[curr.a][curr.b+1]) {
					dist[curr.a][curr.b+1] = dist[curr.a][curr.b]+(weights[curr.a][curr.b]+weights[curr.a][curr.b+1])/2;
					predecessor[curr.a][curr.b+1] = curr;
				}
				q.add(new Pair(curr.a, curr.b+1));
			}
			if(walls[curr.a][curr.b]/4%2 != 1) {
				if(dist[curr.a][curr.b]+(weights[curr.a][curr.b]+weights[curr.a+1][curr.b]) < dist[curr.a+1][curr.b]) {
					dist[curr.a+1][curr.b] = dist[curr.a][curr.b]+(weights[curr.a][curr.b]+weights[curr.a+1][curr.b])/2;
					predecessor[curr.a+1][curr.b] = curr;
				}
				q.add(new Pair(curr.a+1, curr.b));
			}
			if(walls[curr.a][curr.b]/8%2 != 1) {
				if(dist[curr.a][curr.b]+(weights[curr.a][curr.b]+weights[curr.a][curr.b-1]) < dist[curr.a][curr.b-1]) {
					dist[curr.a][curr.b-1] = dist[curr.a][curr.b]+(weights[curr.a][curr.b]+weights[curr.a][curr.b-1])/2;
					predecessor[curr.a][curr.b-1] = curr;
				}
				q.add(new Pair(curr.a, curr.b-1));
			}
			visited[curr.a][curr.b] = true;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Pathfinder pf = new Pathfinder();
		Scanner sc = new Scanner(new File("res/factory.in"));
		int rows = sc.nextInt();
		int cols = sc.nextInt();
		int[][] weights = new int[rows][cols];
		int[][] walls = new int[rows][cols];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				walls[i][j] = sc.nextInt();
				weights[i][j] = 1;
			}
		}
		
		
		int trials = 10000;
		List<List<Integer>> greedy = new ArrayList<List<Integer>>();
		for(int i = 0; i <= 10; i++) {
			greedy.add(new ArrayList<Integer>());
		}
		List<Integer> tb = new ArrayList<Integer>();
		List<Integer> lr = new ArrayList<Integer>();
		List<Integer> four = new ArrayList<Integer>();
		for(int i = 0; i < trials; i++) {
			int boxX = (int)(Math.random()*cols);
			int boxY = (int)(Math.random()*rows);
			
			//greedy robots, standard dijkstra's
			int botX = (int)(Math.random()*cols);
			int botY = (int)(Math.random()*rows); 
			for(int j = 1; j <= 10; j++) {
				int min = pf.getDist(walls, weights, boxX, boxY, botX, botY);
				for(int bots = 1; bots < j; bots++) {
					botX = (int)(Math.random()*cols);
					botY = (int)(Math.random()*rows); 
					int dist = pf.getDist(walls, weights, boxX, boxY, botX, botY);
					min = Math.min(min, dist);
				}
				greedy.get(j).add(min);
			}
			
			//two robots, top-bottom split
			botX = (int)(Math.random()*cols);
			botY = (int)(Math.random()*rows/2); 
			if(boxY >= rows/2) {
				botY+=rows/2;
			}
			tb.add(pf.getDist(walls, weights, boxX, boxY, botX, botY));
			
			//two robots, left-right split
			botX = (int)(Math.random()*cols/2);
			botY = (int)(Math.random()*rows); 
			if(boxX >= cols/2) {
				botX+=cols/2;
			}
			lr.add(pf.getDist(walls, weights, boxX, boxY, botX, botY));
			
			//four robots, quadrant split
			botX = (int)(Math.random()*cols/2);
			botY = (int)(Math.random()*rows/2); 
			if(boxX >= cols/2) {
				botX+=cols/2;
			}
			if(boxY >= rows/2) {
				botY+=rows/2;
			}
			four.add(pf.getDist(walls, weights, boxX, boxY, botX, botY));
		}
		try {
			PrintWriter outGreedy;
			PrintWriter outTB = new PrintWriter(new BufferedWriter(new FileWriter("res/outTB.txt")));
			PrintWriter outLR = new PrintWriter(new BufferedWriter(new FileWriter("res/outLR.txt")));
			PrintWriter outQuad = new PrintWriter(new BufferedWriter(new FileWriter("res/outQuad.txt")));
			for(int i = 1; i <= 10; i++) {
				outGreedy = new PrintWriter(new BufferedWriter(new FileWriter("res/outGreedy"+i+".txt")));
				output(greedy.get(i), outGreedy);
			}
			output(tb, outTB);
			output(lr, outLR);
			output(four, outQuad);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 1; i <= 10; i++) {
			System.out.println("Average distance using greedy with " + i + " robot: " + average(greedy.get(i)) + " with standard deviation: " + stddev(greedy.get(i)) + " and standard error: " + stddev(greedy.get(i))/Math.sqrt(trials));
		}
		System.out.println("Average distance using two robots with a top-bottom split: " + average(tb) + " with standard deviation: " + stddev(tb) + " and standard error: " + stddev(tb)/Math.sqrt(trials));
		System.out.println("Average distance using two robots with a left-right split: " + average(lr) + " with standard deviation: " + stddev(lr) + " and standard error: " + stddev(lr)/Math.sqrt(trials));
		System.out.println("Average distance using four robots with a quadrant split: " + average(four) + " with standard deviation: " + stddev(four) + " and standard error: " + stddev(four)/Math.sqrt(trials));
	}
	
	public static void output(List<Integer> list, PrintWriter out) {
		for(int i : list) {
			out.println(i);
		}
	}
	
	public static double average(List<Integer> list) {
		double ret = 0;
		for(int i : list) {
			ret += i;
		}
		return ret/list.size();
	}
	
	public static double stddev(List<Integer> list)
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = list.size();

        for(double num : list) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: list) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }
	
}
