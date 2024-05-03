package GraphicAPI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import GraphicAPI.GeometricShapes.Shape;

// Implémentation du service distant pour la sauvegarde et le chargement de listes de formes
public class Serveurrmi extends UnicastRemoteObject implements RemoteShapeService {
    private List<Shape> shapeList;

    public Serveurrmi() throws RemoteException {
        super();
        shapeList = new ArrayList<>();
    }

    public void saveShapes(List<Shape> shapes) throws RemoteException {
        shapeList = new ArrayList<>(shapes);
        System.out.println("Shapes saved on server.");
    }

    public List<Shape> loadShapes() throws RemoteException {
        System.out.println("Shapes loaded from server.");
        return new ArrayList<>(shapeList);
    }

    public static void main(String[] args) {
        try {
            RemoteShapeService server = new Serveurrmi();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("//localhost/RemoteShapeService", server);
            System.out.println("Server is ready...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
