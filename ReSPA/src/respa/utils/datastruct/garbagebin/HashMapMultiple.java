package respa.utils.datastruct.garbagebin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HashMapMultiple<K,V>{

	
	
	
	
	
	private HashMap<K,MultipleValues<V>> map = new HashMap<K,MultipleValues<V>>();
	
	private int size = 0;
	
	public void put(K key, V value) {
		

		MultipleValues<V> values;
		
		if(map.containsKey(key)) 
			values = map.get(key);
		else 
			values = new MultipleValues<V>();

		values.values.add(value);
		map.put(key, values);
		size++;
		
	}
	
	
	public List<V> get(K key) {
		
		MultipleValues<V> values;
		
		if(map.containsKey(key)) 
			values = map.get(key);
		else 
			values = new MultipleValues<V>();
		
		return values.values;
		
	}
	
	
	
	public boolean containsKey(K key) {
		
		return map.containsKey(key);
		
	}
	
	
	public boolean isEmpty() {
		
		return map.isEmpty();
		
	}
	
	
	
	public void remove(K key) {
		
		size = size - map.remove(key).values.size();
		
		
	}

	
	public Set<K> keySet() {
		
		return map.keySet();
		
	}
	
	public void clear() {
		
		map.clear();
		size = 0;
		
	}
	
	
	
	
	
	
	
	private class MultipleValues<E> {
		
		
		public ArrayList<E> values = new ArrayList<E>();
		
		
	}
	
	
	public int size() {
		return size;
	}
	
	
	
	
	
	
	
	
}
