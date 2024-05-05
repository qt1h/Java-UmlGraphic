package forms;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import forms.GeometricShapes.Shape;

public interface RemoteShapeService extends Remote {
	void saveShapes(List<Shape> shapes) throws RemoteException;
	List<Shape> loadShapes() throws RemoteException;
}
