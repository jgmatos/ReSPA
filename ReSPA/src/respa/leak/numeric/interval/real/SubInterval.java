package respa.leak.numeric.interval.real;

import respa.leak.numeric.interval.DisjointException;
import respa.leak.numeric.interval.IsVoidException;




public class SubInterval {
		
		private double bottom;
		
		private double top;
		
		private boolean isVoid;
		
		
		public SubInterval(double edge1, double edge2) {
			
			if(edge1 > edge2) {
				bottom = edge2;
				top = edge1;
			}
			else {
				
				bottom = edge1;
				top = edge2;
				
			}
			
			this.isVoid = false;
			
		}
		
		public SubInterval() {
			
			isVoid = true;
			
		}
		
		public void setEdges(double edge1, double edge2) {
			
			if(edge1 > edge2) {
				bottom = edge2;
				top = edge1;
			}
			else {
				
				bottom = edge1;
				top = edge2;
				
			}
			
			this.isVoid = false;
			
		}
		
		public double [] edges() throws IsVoidException{

			if(isVoid)
				throw new IsVoidException();
			
			double [] thisEdges = new double[2];
			thisEdges[0]= this.bottom;
			thisEdges[1]= this.top;
			
			return thisEdges;
			
		}
		
		
		public double bottom()  throws IsVoidException{
			
			if(isVoid)
				throw new IsVoidException();
			
			return bottom;
			
		}
		
		
		public double top() throws IsVoidException {
			
			if(isVoid)
				throw new IsVoidException();
			
			return top;
			
		}
		
		public double size() {
			
			if(!isVoid)
				return 1+(top-bottom);
			else
				return 0.0;
			
		}
		
		
		public boolean contains(double value){
			
			if(isVoid)
				return false;
			
			if(value > bottom && value < top)
				return true;
			
			return false;
			
		}
		
		
		public boolean contains(SubInterval subinterval) {
			
			if(isVoid)
				return false;
			
			try {
				if(subinterval.bottom() > this.bottom)
					if(subinterval.top() < this.top)
						return true;
			} catch (IsVoidException e) {
				return true;
			}
			
			return false;
			
		}
		
		
		public boolean isDisjoint(SubInterval interval) {
			
			if(this.top < interval.bottom)
				return true;
				
			if(this.bottom > interval.top)
				return true;
			
			return false;
			
		}
		
		
		public boolean equals(SubInterval interval) {
			
			if(this.contains(interval) && interval.contains(this))
				return true;
			
			return false;
			
		}
		
		
		
		public void clear(){
			
			this.isVoid = true;
			
		}
		
		public boolean isVoid() {
			
			return isVoid;
			
		}
		
		
		
		public void add(SubInterval subinterval) throws DisjointException{
			
			try {
				
				if(subinterval.isVoid()) {
					/**do nothing**/
				}
				else if(isVoid){
					this.bottom = subinterval.bottom();
					this.top = subinterval.top();
					this.isVoid= false;
				}
				else {
			
					if(isDisjoint(subinterval))
						throw new DisjointException();
			
			
					if(bottom > subinterval.bottom())
						this.bottom = subinterval.bottom();
			
					if(top < subinterval.top())
						this.top = subinterval.top();
			
					}
				}
				catch(IsVoidException ive) {
					/**do nothing**/
				}
		}
		
		
		
		
		public void sub(SubInterval subinterval) throws DisjointException{
			
			try {
				
				if(this.isDisjoint(subinterval))
					throw new DisjointException();
				
				if(subinterval.isVoid() || this.isVoid)
					throw new IsVoidException();
				
				if(this.equals(subinterval)) {
					/**Do nothing**/
				}
				else if(this.contains(subinterval)){
					this.bottom = subinterval.bottom();
					this.top = subinterval.top();
					this.isVoid = false;
				}
				else if(subinterval.contains(this)){
					/**Do nothing**/
				}
				else {
					if(this.bottom < subinterval.bottom())
						this.bottom = subinterval.bottom();
					
					if(this.top > subinterval.top())
						this.top = subinterval.top();
				}
					
				
			}
			catch(IsVoidException ive) {
				/**Do nothing**/
			}
		}
		
		public void intersect(SubInterval subinterval) {
			
			if(this.isDisjoint(subinterval) ||
				subinterval.isVoid() ||
				this.isVoid)
				this.isVoid = true;
			else
				try { this.sub(subinterval); } catch (DisjointException e) {}
			

			
		}
		
		
		public SubInterval subtract(SubInterval subinterval) throws DisjointException{
			
			try{
			
				if(this.isDisjoint(subinterval))
					throw new DisjointException();
			
				if(this.contains(subinterval))
					return subinterval;
			
				if(subinterval.contains(this))
					return this;
		
				
			
				SubInterval result = new SubInterval();
				double edge1;
				double edge2;
			
				if(this.bottom > subinterval.bottom())
					edge1 = this.bottom;
				else
					edge1 = subinterval.bottom();
				
				if(this.top < subinterval.top())
					edge2 = this.top;
				else
					edge2 = subinterval.top();
			
				result.setEdges(edge1, edge2);
			
				return result;
			
			}
			catch(IsVoidException ive) {
				return this;
			}
		}
		

		
		
		
		
		
		
		
	}