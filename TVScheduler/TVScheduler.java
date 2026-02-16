import java.util.*;

public class TVScheduler implements Scheduler {
	private List<Slot> TVtime = new ArrayList<>(); // Przechowuje wszystkie programy
	
	public TVScheduler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	//Chyba ok?
	public void addSlot(Slot program) {
		//Sprawdzamy czy nie zostal juz dodany
		if(TVtime.contains(program)) {
			return;
		}
		

		/*int our_start = startOfProgram(program);
		int our_end = endOfProgram(program);
		List<Slot> programs = programsOfStation(program.station());		
		
		//Sprawdzamy czy sie nie naklada
		for(int i = 0; i <= programs.size(); i++ ) {
			int other_end = endOfProgram(programs.get(i));
			int other_start = startOfProgram(programs.get(i));
			if(our_start < other_end || our_end > other_start) {
				return;
			}
		}*/
		
		TVtime.add(program);
		sort(TVtime);
	}

    public void add_match(int i, Set<List<Slot>> match_programs, Set<String> wanted, List<Slot> current, boolean found){
        Set<String> if_wanted;
        List<Slot> add_current = new ArrayList<>();

        if_wanted = wanted;

        //Tworze warunek za sukcesem
        if(if_wanted.isEmpty()){
            match_programs.add(add_current);
            return;
        }
        
        //Tworze warunek za przegrana
        if(!if_wanted.isEmpty() && i == TVtime.size() - 1){
            found = false;
            return;
        }

        //Sprawdzamy czy zaczyna sie po aktualnym programie
        //Jesli nie to sprawdzamy kolejny?
        //Warunek przeci2
        if(!if_wanted.contains(TVtime.get(i).program())){
            add_match(i+1, match_programs, if_wanted, add_current, found);
        }
        
        int end_current = endOfProgram(add_current.getLast());
        int start_other = startOfProgram(TVtime.get(i));

        //Dodaje i sprawdzam dla i + 1
        add_current.add(TVtime.get(i));
        if_wanted.remove(TVtime.get(i).program());

        if(start_other < end_current){
            add_match(i+1, match_programs, if_wanted, add_current, found);
        }

        //Usunac i sprawdzic dla i + 1
        add_current.remove(TVtime.get(i));
        if_wanted.add(TVtime.get(i).program());

    }

	@Override
	public Set<List<Slot>> match(Set<String> programs) {
		Set<List<Slot>> match_programs = new HashSet<>();
		//Moze taz current jako treemap? KLuczem bylby zakonczenie?

		List<Slot> current = new ArrayList<>();
		Set<String> wanted = new HashSet<>();

        for(int i = 0; i < TVtime.size(); i ++) {
            wanted = programs;
            boolean found = true;
            current.clear();
            //Sprawdzamy czy pierwszy wybór ma sens
            if (!wanted.contains(TVtime.get(i).program())) continue;
            add_match(i, match_programs, wanted, current, found);
        }

        System.out.println(match_programs);
        return match_programs;

		/*//Pierwsze sortowac?
		for(int i = 0; i < TVtime.size(); i ++) {
			wanted = programs;
			//Sprawdzamy czy pierwszy wybór ma sens
			if (!wanted.contains(TVtime.get(i).program())) continue;


			current.add(TVtime.get(i));
			wanted.remove(TVtime.get(i).program());
			Set<String> wanted_2 = new HashSet<>();

			//Numer kolejnego elementu?
			int k = i+1;
            wanted_2 = wanted;
			while(k < TVtime.size()) {
				//Sprawdzanie czy od elementu k sa nowe mozliwosci
				for(int j = k; j < TVtime.size(); j ++) {

                    if(wanted_2.isEmpty()) {
						match_programs.add(current);
						break;
					}

					//Sprawdzamy czy kolejny element na liscie posiada chciany program
					if(!wanted_2.contains(TVtime.get(j).program())) continue;

					//Porownujemy nowy z ostatnim elementem!
					int end_current = endOfProgram(current.getLast());
					int start_current = startOfProgram(current.getLast());
					int start_other = startOfProgram(TVtime.get(j));
					int end_other = endOfProgram(TVtime.get(j));

					//Sprawdzamy czy zaczyna sie po aktualnym programie
					if(start_other >= end_current || start_current >= end_other ) {
						wanted_2.remove(TVtime.get(j).program());
						current.add(TVtime.get(j));
						sort(current);
					}
				}

				//Sprawdzamy czy znaleziono wyszytkie programy
				if(wanted_2.isEmpty()) {
					match_programs.add(current);
				}

				k++;
			}

		}*/
		// TODO Auto-generated method stub
		//Tutaj uzyc rekordu?
		//petla przechodzaca przez wszystkie programy
		//koniec poczatkowo 0
		//sprawdzamy czy pierwszy program jest tym co chcemy ogladnac
		//dodajemy go
		//sprawdza czy poczatek programu jest większy? od konca
		//Jesli tak, to dodaje do listy wybranych
		//I ustawia koniec jako koniec dodanego*/
	}
	
	public int endOfProgram(Slot program) {
		int end = program.atM() + program.atH()* 60;
		int duration = program.duration();
		return end = end + duration;
			
	}
	
	public int startOfProgram(Slot program) {
		return program.atM() + program.atH()* 60;
	}
	
	//To jest raczej niepotrzebne xdd
	public List<Slot> programsOfStation(String station) {
		List<Slot> Programs = new ArrayList<>();
        for (Slot slot : TVtime) {
            if (station.equals(slot.station())) {
                Programs.add(slot);
            }
        }
		return Programs;
	}
	
	public List<Slot> sort(List<Slot> list){
		//List<Slot> new_list = new ArrayList<>();
		for(int i = 1; i < list.size(); i++) {
			Slot insert_program = list.get(i);
			int j = i - 1;
			int end_insert = endOfProgram(list.get(i));
			//int end_current = endOfProgram(list.get(j));
				while(j >= 0 && endOfProgram(list.get(j)) > end_insert) {
				list.set(j + 1, list.get(j));
				j = j - 1;
			}
				
			list.set(j + 1, insert_program);
		}
		System.out.println(list);
		return list;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub


	}

}
