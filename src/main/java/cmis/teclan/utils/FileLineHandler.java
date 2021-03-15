package cmis.teclan.utils;

public interface FileLineHandler {

    public void handle(int index,String line);

    public void clean();

    public String get();
}
