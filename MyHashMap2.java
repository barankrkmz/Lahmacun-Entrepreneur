import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MyHashMap2<K extends String, V extends Integer>{
    private static final int DEFAULT_CAPACITY = 100001;
    private static final double LOAD_FACTOR = 0.75;

    public Entry<K,V>[] table;
    public int size;

    public MyHashMap2(){
        table = new Entry[DEFAULT_CAPACITY];
        size = 0;
    }
    public static class Entry<K,V>{
        private final K key;
        private V value;

        public boolean isActive;
        public Entry(K key, V value, boolean isActive){
            this.key = key;
            this.value=value;
            this.isActive=isActive;
        }
        public K getKey(){
            return key;
        }
        public V getValue(){
            return value;
        }
        public void setValue(V value){

            this.value=value;
        }
    }

    public int getSize(){
        return size;
    }

    public V get(K key){
        int index = findPos(key);
        if(table[index] != null){
            return table[index].getValue();
        }else{
            return null;
        }
    }
    public boolean contains(K key){
        int currentPos = findPos(key);
        return isActive(currentPos);
    }

    public boolean put(K key, V value){
        if((double)size/ table.length > LOAD_FACTOR){
            resize();
        }
        int index = findPos(key);
        if(isActive(index)){
            table[index].setValue(value);
            return false;
        }
        if(table[index]==null){
            table[index] = new Entry<>(key,value,true);
            size++;
        }
        return true;
    }

    public boolean removeByKey(K key) {
        int index = findPos(key);
        if (isActive(index)) {
            table[index].isActive = false;
            size--;
            return true;
        }else{
            return false;
        }
    }

    private boolean isActive(int currentPos) {

        return table[currentPos] != null && table[currentPos].isActive;
    }


    private int hash(K key) {
        int hashVal = key.hashCode();
        hashVal = hashVal % table.length;
        if(hashVal < 0) {
            hashVal += table.length;
        }
        return hashVal;
    }

    private void resize(){
        int newTableCapacity = table.length*2;
        Entry<K,V>[] NewTable = new Entry[nextPrime(newTableCapacity)];
        for(Entry<K,V> entry : table){
            if(entry !=null && entry.isActive){
                int index = findPos(entry.getKey());
                NewTable[index] = entry;
            }
        }
        table=NewTable;
    }

    private static int nextPrime(int currentPrime) {
        if(currentPrime<=1){
            currentPrime =2;
        }else if(currentPrime % 2 == 0) {
            currentPrime++;
        }
        while(!isPrime(currentPrime)) {
            currentPrime += 2;
        }
        return currentPrime;
    }

    private static boolean isPrime(int n) {
        if(n == 2 || n == 3) {
            return true;
        }
        if (n == 1 || n % 2 == 0) {
            return false;
        }
        for(int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    private int findPos(K key) {
        int offset = 1;
        int currentPos = hash(key);

        int i =1;
        while(table[currentPos] != null && !table[currentPos].getKey().equals(key)) {
            currentPos = (currentPos + i*i) % table.length;
            i++;
        }
        return currentPos;
    }

    public ArrayList<V> getAllValues() {
        ArrayList<V> values = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            if (entry != null && entry.isActive) {
                values.add(entry.getValue());
            }
        }
        return values;
    }

    public ArrayList<K> getAllKeys() {
        ArrayList<K> keys = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            if (entry != null && entry.isActive) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }
    public V getValueForKey(K key) {
        int index = findPos(key);
        if (table[index] != null) {
            return table[index].getValue();
        } else {
            return null;
        }
    }

    public void clear() {
        table = new Entry[DEFAULT_CAPACITY];
        size = 0;
    }


}