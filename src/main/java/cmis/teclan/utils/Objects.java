package cmis.teclan.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Objects {
    public static Set<String>  duplicateRemoval(Set<String> collection){
        HashSet deletes = new HashSet();

        for(String item:collection){
            if(collection.contains(item)){
                deletes.add(item);
            }
        }
        collection.removeAll(deletes);

        return collection;
    }

}
