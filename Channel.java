
public class Channel {
	int sb[];
	int ab[];
	int nb[];
	String body[];
	int N;
	
	public Channel(int N) {
		this.N= N;
		this.sb = new int[N];
		this.ab = new int[N];
		this.nb = new int[N];
		this.body = new String[N];
		for(int i = 0; i < N; ++i) {
			this.sb[i] = 0;
			this.ab[i] = 0;
			this.nb[i] = 1;
		}
	}
   
}
