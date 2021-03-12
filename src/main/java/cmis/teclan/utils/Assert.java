package cmis.teclan.utils;

public class Assert {

    public static boolean assertNullString(String value){
        return null==value||"".equals(value.trim());
    }

    public static boolean assertNotNullString(String value){
        return !assertNullString(value);
    }

    public static boolean assertNotEquals(Object v1,Object v2){
        return !assertEquals(v1,v2);
    }
    public static boolean assertEquals(Object v1,Object v2){

        if(v1!=null){
            return v1.equals(v2);
        }

        if(v1==null && v2==null){
            return true;
        }

        if(v1==null && v2!=null){
            return false;
        }
       return false;
    }
}
