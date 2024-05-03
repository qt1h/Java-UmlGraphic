package GraphicAPI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import GraphicAPI.GeometricShapes.Shape;

public interface RemoteShapeService extends Remote {
 void saveShapes(List<Shape> shapes) throws RemoteException;
 List<Shape> loadShapes() throws RemoteException;
}
