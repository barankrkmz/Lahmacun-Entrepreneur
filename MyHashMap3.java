import java.util.ArrayList;
import java.util.List;

public class MyHashMap3<K, V extends Employee> {
    private static final int DEFAULT_CAPACITY = 101;
    private static final double LOAD_FACTOR = 0.75;

    public Entry<K, V>[] table;
    private int size;
    private int deleted;
    public MyHashMap3() {
        table = new Entry[DEFAULT_CAPACITY];
        size = 0;
    }
    public MyHashMap3(int initialCapacity){
        this.table = new Entry[initialCapacity];
        this.size = 0;
        this.deleted = 0;
    }


    public ArrayList<V> getAllValues() {
        ArrayList<V> values = new ArrayList<>();
        for (MyHashMap3.Entry<K, V> entry : table) {
            if (entry != null && entry.isActive) {
                values.add(entry.getValue());
            }
        }
        return values;
    }

    public ArrayList<V> getEmployeesByPlace(String placeName) {
        ArrayList<V> employeesInPlace = new ArrayList<>();
        List<String> addedEmployeeIds = new ArrayList<>();

        for (MyHashMap3.Entry<K, V> entry : table) {
            if (entry != null && entry.isActive) {
                V employee = entry.getValue();
                if (employee.place.equals(placeName) && !addedEmployeeIds.contains(employee.name)) {
                    employeesInPlace.add(employee);
                    addedEmployeeIds.add(employee.name);
                }
            }
        }

        return employeesInPlace;
    }

    public boolean insert(K key, V value) {
        if ((double) size / table.length > LOAD_FACTOR) {
            resizeTable();
        }

        int index = findPos(key, hash(key));
        if (isActive(index)) {
            return false;
        }
        if(table[index] == null) {
            deleted++;
        }

        table[index] = new Entry<>(key, value);
        size++;
        if(deleted > table.length / 2) {
            resizeTable();
        }
        return true;
    }

    private boolean isActive(int index) {
        return table[index] != null && table[index].isActive;
    }

    public V get(K key) {
        int index = findPos(key, hash(key));

        if (table[index] != null) {
            return table[index].getValue();
        } else {
            return null;
        }
    }

    public boolean contains(K key){
        int index = findPos(key,hash(key));
        return isActive(index);
    }

    public boolean remove(K key) {
        int index = findPos(key, hash(key));
        if (isActive(index)) {
            table[index].isActive = false;
            size--;
            return true;
        } else {
            return false;
        }
    }

    public int findPos(K key, int hash) {
        int index = hash;
        if (index<0) index += table.length;
        int quadraticProbingValue = 1;

        while (table[index] != null && !table[index].getKey().equals(key)){
            index = (index + quadraticProbingValue * quadraticProbingValue) % table.length;
            quadraticProbingValue++;
        }

        return index;
    }

    public int hash(K key) {
        return key.hashCode() % table.length;
    }

    private void resizeTable() {
        int newCapacity = nextPrime(table.length * 2);
        MyHashMap3<K,V> newTable = new MyHashMap3<>(newCapacity);
        for (Entry<K, V> entry : table) {
            if (entry != null && entry.isActive) {
                newTable.insert(entry.getKey(), entry.getValue());
            }
        }
        table = newTable.table;
    }

    public static class Entry<K, V> {
        private final K key;
        private V value;
        private boolean isActive;

        public Entry(K key, V value, boolean isActive){
            this.key = key;
            this.value = value;
            this.isActive = isActive;
        }

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.isActive = true;
        }


        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

    }


    private static int nextPrime(int currentPrime) {
        if (currentPrime % 2 == 0) {
            currentPrime++;
        }
        while (!isPrime(currentPrime)) {
            currentPrime += 2;
        }
        return currentPrime;
    }

    private static boolean isPrime(int number) {
        if (number == 2 || number == 3) {
            return true;
        }
        if (number == 1 || number % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
