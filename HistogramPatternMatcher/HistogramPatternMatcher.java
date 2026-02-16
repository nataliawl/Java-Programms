import java.util.*;

public class HistogramPatternMatcher extends AbstractHistogramPatternMatcher {
	private TreeMap<Integer, Integer> Histogram = new TreeMap<>(); //Tu przechowujemy liczby () i ich czestosc wystepowania
	private HashSet<Integer> Keys = new HashSet<>(); //Tu przechowuje klucze pasujace do wzorca
	
	@Override
	public void data(int value) {
		//Tu uzyc mape
		if(Histogram.containsKey(value)) {
			Histogram.replace(value, Histogram.get(value) + 1);
		}
		else{
			Histogram.put(value, 1);
		}
	}

	@Override
	//podejrzane...
	public Map<Integer, Integer> histogram() {
		/*if(Histogram.size() == 0) {
			return Histogram;
		}*/		
		return new TreeMap<>(Histogram);
	}

	@Override
	public Set<Integer> match(List<Integer> pattern) {
		TreeMap<Double, Double> PomHistogram = new TreeMap<>(); 
		double zero = 0;
		
		if(Histogram.isEmpty()){
            return Keys;
        }
		
		//Kopiujemy do mapy pomocniczej
		//Szukamy brakujacych liczb i je dodajemy
		for(double i = (double)Histogram.firstKey(); i <= (double)Histogram.lastKey(); i++) {
			if(Histogram.containsKey((int)i) == false) {
				PomHistogram.put(i, zero);
			}
			else PomHistogram.put(i, (double)Histogram.get((int)i) );
		}
		
		int size_pattern = pattern.size(); //Ilosc liczb w patternie
		//zmienna h to sprawdzana w danym momencie liczba
		for(double h = PomHistogram.firstKey(); h < PomHistogram.lastKey() - size_pattern + 2; h ++) {
			boolean if_pattern = true;
			int k = 1; //Liczby wystepujace po h
			while(k < size_pattern) {
				//Sprawdzamy czy nie ma dwoch zer, wtedy pattern 1:1
				if(PomHistogram.get(h) == 0 && PomHistogram.get(h + k) == 0 && pattern.get(k) == 1) {
					k++;
					continue;
				}
				//Jesli kolejna liczba nie jest zerem, to pierwsza tez nie moze
				if(PomHistogram.get(h) == 0){
					System.out.println("Nie mozna dzielic przez zero!");
					if_pattern = false;
					break;
				}
				//Finalnie sprawdzamy czy pasuje do pattern
				if(PomHistogram.get(h + k)/ PomHistogram.get(h) != pattern.get(k)) {
					if_pattern = false;
					break;
				}
				k++;
			
			}
			if(if_pattern == true) {
				Keys.add((int)h);
			}
		}
		return Keys;
	}
	public static void main(String[] args) {
		//Dla pierwszego przykładu działa
		//Dla drugiego tez
		AbstractHistogramPatternMatcher test = new HistogramPatternMatcher();
		ArrayList<Integer> test_pattern = new ArrayList<Integer>();
		test_pattern.add(1);
		test_pattern.add(1);
		//test_pattern.add(2);
		//test_pattern.add(1);
		//test_pattern.add(1);
		System.out.println(test_pattern);
		System.out.println(test.histogram());
		test.data(-2);
		test.data(-1);
		test.data(2);
		test.data(2);
		test.data(3);
		test.data(3);
		test.data(4);
		test.data(4);
		test.data(4);
		test.data(5);
		test.data(5);
		test.data(5);
		/*System.out.println("Liczba	 zliczenia");
		for (int i : Histogram.keySet()) {
		      System.out.println(i + "             " + Histogram.get(i));
		    }*/
		System.out.println(test.histogram());
		/*test.data(6);
		test.data(6);
		test.data(6);
		test.data(6);
		test.data(6);
		test.data(6);*/
		test.histogram();
		System.out.println(test.match(test_pattern));

	}
}
