package cmis.teclan.utils;

import java.util.HashSet;
import java.util.Set;

public class DefaultFileLineHandler implements FileLineHandler{

    public Set<String> content = new HashSet<String>();

    public void handle(int index, String line) {
        content.add(line);
    }

    public void clean(){
        content.clear();
    }

    public String get() {

        StringBuffer sb = new StringBuffer();

        for(String path:content){
            sb.append(path).append("\r\n");
        }
        return sb.toString();
    }
}
