package forms;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TerminalDraw {

    public static class Point {
        private int[] val = new int[2];

        public Point(int i, int j) {
            this.val[0] = i;
            this.val[1] = j;
        }

        public Point mult(Point b) { // x*x y*y
            return new Point(this.val[0] * b.val[0], this.val[1] * b.val[1]);
        }

        public Point plus(Point b) {
            return new Point(this.val[0] + b.val[0], this.val[1] + b.val[1]);
        }

        public int[] value() {
            return this.val;
        }

        public String toString() {
            return "(" + this.val[0] + "," + this.val[1] + ")";
        }
    }

    public static class Signal {
        private List<Integer> val;

        public Signal(List<Integer> v) {
            this.val = v;
        }

        public Signal plus(Signal b) {
            List<Integer> res = new ArrayList<>();
            Iterator<Integer> i1 = val.iterator();
            Iterator<Integer> i2 = b.val.iterator();
            while (i1.hasNext() && i2.hasNext()) {
                res.add(i1.next() + i2.next());
            }
            return new Signal(res);
        }

        public List<Integer> value() {
            return this.val;
        }

        public String toString() {
            return this.val.toString();
        }

        public int distance(int x1, int x2) { // x1->x2 ou y1->y2
            int distx = x2 - x1;
            return (int) Math.sqrt(distx * distx);
        }

        public void displayRectangle() {
            Iterator<Integer> iter = val.iterator();
            while (iter.hasNext()) {
                int x1 = iter.next();
                int y1 = iter.next();
                int x2 = iter.next();
                int y2 = iter.next();

                int xmin = Math.min(x1, x2);
                int ymin = Math.min(y1, y2);
                int xmax = Math.max(x1, x2);
                int ymax = Math.max(y1, y2);

                int width = distance(x1, x2);
                int height = distance(y1, y2);

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (j >= xmin && j <= xmax && i >= ymin && i <= ymax) {
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
                int x1 = iter.next();
                int y1 = iter.next();
                int r = iter.next();

                int centerX = x1;
                int centerY = y1;

                for (int i = y1 - r; i <= y1 + r; i++) {
                    for (int j = x1 - r; j <= x1 + r; j++) {
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
        Signal A = new Signal(List.of(30, 30, 15)); 
        Signal D = new Signal(List.of(1, 4, 30, 20));
        A.displayCircle();
        D.displayRectangle();
    }
}

