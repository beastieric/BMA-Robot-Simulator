
public class Pair {
	int a, b;
	
	public Pair(int x, int y) {
		a = x;
		b = y;
	}
	
	@Override
	public boolean equals(Object o) {
		return ((Pair)o).a == this.a && ((Pair)o).b == this.b;
	}
}