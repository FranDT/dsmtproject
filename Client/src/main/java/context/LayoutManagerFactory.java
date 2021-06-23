package context;

public class LayoutManagerFactory {

    private static LayoutManager manager = null;

    public static LayoutManager getManager() {
        if(manager == null){
            manager = new LayoutManager();
        }
        return manager;
    }
}