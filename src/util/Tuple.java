package util;

public class Tuple<K,V> {
	
	private K key;
	private V value;
	
	public Tuple(K Key, V Value) {
		key = Key;
		value = Value;
	}
	
	public void clear() {
		key = null;
		value = null;
	}
	
	public boolean containsKey(K key) {
		return this.key.equals(key);
	}
	
	public boolean containsValue(V value) {
		return this.value.equals(value);
	}
	
	public boolean isEmpty() {
		return key==null;
	}
	
	public K key() {
		return key;
	}
	
	public V value() {
		return value;
	}
	
	public V put(K key, V value) {
		V old = this.value;
		this.key = key;
		return old;
	}	
	
	public String toString(){
		return "{ " + key + " : "+value + " }";
	}
}
