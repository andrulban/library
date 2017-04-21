package comparator;

import java.util.Comparator;

public class ListComparator {

    private static ObjectComparator listComparator;

    public static Comparator getInstance() {
        if (listComparator == null) {
            listComparator = new ObjectComparator();
        }

        return listComparator;
    }

    private static class ObjectComparator implements Comparator {

        @Override
        public int compare(Object a1, Object a2) {
            return a1.toString().compareTo(a2.toString());
        }
    }

   
}
