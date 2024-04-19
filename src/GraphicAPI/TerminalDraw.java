package GraphicAPI;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TerminalDraw {
	
	public class Point{
		private int val[]=new int[2]; //allocation mémoire
		
		public Point(int i, int j) {
			// TODO Auto-generated constructor stub
			this.val[0]=i;
			this.val[1]=j;
		}

		/*public static void main(String[] args) {
			// TODO Auto-generated method stub
			Point A = new Point(1,2);
			Point B = new Point(3,4);
			System.out.println(A.plus(B));
		}*/
		
		public Point mult(Point b) { // x*x y*y
			// TODO Auto-generated method stub
			return new Point(this.val[0]*b.value()[0],this.val[1]*b.value()[1]);
		}

		public Point plus(Point b) {
			return new Point(this.val[0]+b.value()[0],this.val[1]+b.value()[1]);
		}
		

		public Array value() {
			// TODO Auto-generated method stub
			return this.val;
		} 
		
		public String toString() {
			String res = "(";
			res+= Integer.toString(this.val[0]);
			res+=",";
			res+= Integer.toString(this.val[1]);
			res+=")";
			return res;
		}

	}

	
	public static class Signal{
		private List<Integer> val; //liste java

		public Signal(List<Integer> v) {
		// TODO Auto-generated constructor stub
			this.val=v;
		}

		public Signal plus(Signal b) {
			List<Integer> res = new ArrayList(); //array list ou liste chainée
			Iterator<Integer> i1 = val.iterator();
			Iterator<Integer> i2 = b.value().iterator();
			while(i1.hasNext()) {
				res.add( i1.next()+i2.next() );
		
			}
			return new Signal(res);
		}
	

		public List<Integer> value() {
			// TODO Auto-generated method stub
			return this.val;
		}
		
		public String toString() {
			return this.val.toString();
		}
		
		
		public int distance(int x1, int x2) { //x1->x2 ou y1->y2
			int distx=x2-x1;
			System.out.println(distx);
			return (int) Math.sqrt(distx*distx); 
		}

		public void displayRectangle() {
			Iterator<Integer> iter=val.iterator();
			while(iter.hasNext()) {
				
				int x1=iter.next();
				int y1=iter.next();
				int x2=iter.next();
				int y2=iter.next();
				
				int xmin = Math.min(x1, x2);
		        int ymin = Math.min(y1, y2);
		        int xmax = Math.max(x1, x2);
		        int ymax = Math.max(y1, y2);

		        int width = distance(x1, x2);
		        int height = distance(y1, y2);

		        for (int i = 0; i < ymin + height; i++) {
		            for (int j = 0; j < xmin + width; j++) {
		                if (j >= x1 && j <= x2 && i >= y1 && i <= y2) {
		                    System.out.print("X");
		                } else {
		                    System.out.print(" ");
		                }
		            }
		            System.out.println();
		        }
			}
		  
		}
		
	    public void displayCircle() {
	        Iterator<Integer> iter = val.iterator();
	        while (iter.hasNext()) {
	            int x1 = iter.next(); //10
	            int y1 = iter.next(); //10
	            int r = iter.next(); //5

	            int width = 2 * r; //10
	            int height = 2 * r; //10

	            int centerX = x1; //10
	            int centerY = y1; //10
	            //5... < 15
	            for (int i = y1-r; i <y1+r+1; i++) { //on peut retirer 1 si on souhaite l'affichage exacte
	                for (int j = x1-r; j <x1+r+1; j++) {
	                    double distanceFromCenter = Math.sqrt((i - centerY) * (i - centerY) + (j - centerX) * (j - centerX));
	                    if (distanceFromCenter <= r) {
	                        System.out.print("X");
	                    } else {
	                        System.out.print(" ");
	                    }
	                }
	                System.out.println();
	            }
	        }
	    }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			Signal A = new Signal(List.of(30,30,15)); //signal à n coordonnées=liste
			Signal B = new Signal(List.of(15,30,800,70));
			Signal C = new Signal(List.of(1, 1, 5, 40, 30, 75));
			Signal D = new Signal(List.of(1,4,30,20)); // enregistrer les coord dans le bon sens
			//Signal C = new Signal(List.of());
			//System.out.println(A.plus(B));
			A.displayCircle();
			D.displayRectangle();
		}
}
