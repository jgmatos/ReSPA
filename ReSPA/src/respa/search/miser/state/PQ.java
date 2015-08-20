package respa.search.miser.state;


public interface PQ {

	public Node pop();

	public void push(Node hs) ;

	public Node peek();
	
	public int size() ;
	
	public boolean isEmpty() ;


}
