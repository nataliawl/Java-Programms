import java.util.*;

public class RectangularIntegration implements NumericalIntegration {
private Function function;

    public RectangularIntegration() {}

	@Override
	public void setFunction(Function f) {
		function = f;
	}

	@Override
	public double integrate(Range range, int subintervals) {
		if(subintervals <= 0) {
			return 0; // Ma zwracac blad!
		}
		
		//Pobieramy najwiekszy i najmniejszy element
        double min = range.min();
        double max = range.max();
		
		//Lista czesci prostokatnych
		//ArrayList<Double> subs = new ArrayList<Double>();
		double sub;
		
		//Dlugosc podstawy prostokata
		double lenght;

		//Wyliczamy długośc przedzialu h
		lenght = (max - min)/subintervals;
        Set<Range> exclude = function.domainExclusions();
		boolean if_excluded;
		double result = 0.0;
        double current;
		//Sprawdzamy czy podprzedział nie jest zabroniony
		
		//Wyliczamy funkcje dla przedzialu 
		for(int i = 0; i < subintervals; i++) {
			if_excluded = false;
            current = min + (i + 0.5)*lenght;
			//Sprawdzamy czy podprzedział nie jest zabroniony
			for(Range value : exclude) {
				if(current <= value.max() && current >= value.min()) {
					if_excluded = true; 
					break;
				}
			}
			if(!if_excluded) {
				result = result + function.apply(current);
			}
		}
		
		//Liczymy s = h(f1 + f2 + ...+ fn)
		result = result * lenght;
		
		//Zwraca wynik
		return result;
	}

}
