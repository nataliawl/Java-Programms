import java.util.*;
public class JavaTetris implements Tetris{
	private int kolumny;
	private int wiersze;
	private boolean[][] plansza;
	private void stworz() {
		if (kolumny > 0 && wiersze > 0) {
			plansza = new boolean[kolumny][wiersze + 1];
		}
	}
	@Override
	public void rows(int rows) {
		// TODO Auto-generated method stub
		this.wiersze=rows;
		stworz();
	}

	@Override
	public void cols(int cols) {
		// TODO Auto-generated method stub
		this.kolumny=cols;
		stworz();
	}

	@Override
	public void drop(Block block) {
		// TODO Auto-generated method stub
		int dno=ZnajdzWiersz(block, block.base().col(), plansza);
		if(dno!=-1) {
			postaw(block, block.base().col(), dno, plansza);
			wyczysc(plansza);
		}
	}

	@Override
	public void optimalDrop(Block block) {
		// TODO Auto-generated method stub
		int NajlepszaKolumna=-1;
		int minimalnaWysokosc=kolumny+1;
		int zakres[]=Zasieg(block);
		int minimalnaKolumna=zakres[0], maksymalnaKolumna=zakres[1];
		for(int kolumna = -minimalnaKolumna; kolumna<kolumny-maksymalnaKolumna; kolumna++) {
			int dno=ZnajdzWiersz(block, kolumna, plansza);
			if(dno!=-1) {
				int maxWysokoscBloku=dno;
				for(Vector blok : block.squares()) {
					MaksymalnaWysokoscBloku(block, maxWysokoscBloku);					
				}
				if(minimalnaWysokosc>maxWysokoscBloku) {
					minimalnaWysokosc=maxWysokoscBloku;
					NajlepszaKolumna=kolumna;
				}else if(maxWysokoscBloku==minimalnaWysokosc&&(NajlepszaKolumna==-1 || kolumna<NajlepszaKolumna)) {
						NajlepszaKolumna=kolumna;
					}
				}
			}
		if(NajlepszaKolumna!=-1) {
			wrzucDo(block, NajlepszaKolumna);
		}
	}

	@Override
	public List<Integer> state() {
		// TODO Auto-generated method stub
		List<Integer> wyniki = new ArrayList<>();
		for(int kolumna=0; kolumna<kolumny;kolumna++) {
			int wysokosc=0;
			for(int wiersz=wiersze; wiersz>=1; wiersz--) {
				if(plansza[kolumna][wiersz]) {
					wysokosc=wiersz;
					break;
				}
			}
			wyniki.add(wysokosc);
		}
		return wyniki;
	}	
	private void wrzucDo(Block block, int kolumna) {
		int dno=ZnajdzWiersz(block, kolumna, plansza);
		if(dno!=-1) {
			postaw(block, kolumna, dno, plansza);
			wyczysc(plansza);
		}
	}
	private int ZnajdzWiersz(Block block, int kolumna, boolean[][] plansza) {
		int ostatnia=-1;
		for(int wiersz=wiersze+10;wiersz>=1;wiersz--) {
			if(CzyPasuje(block, kolumna, wiersz, plansza)) {
				ostatnia=wiersz;
			}
		}
		if(ostatnia!=-1) {
			boolean dobra=(ostatnia>=1 && ostatnia <=wiersze);
			for(Vector blok : block.squares()) {
				if(ostatnia + blok.dRow()>=1 && ostatnia+blok.dRow() <= wiersze) {
					dobra=true;
				}
			}
			if(!dobra) {
				return -1;
			}
		}
		return ostatnia;
	}
	private boolean CzyPasuje(Block block, int kolumna, int wiersz, boolean[][] plansza) {
		if(czyWolne(kolumna, wiersz, plansza)==false) {
			return false;
		}
		for(Vector blok : block.squares()) {
			if(!czyWolne(kolumna + blok.dCol(), wiersz+blok.dRow(),plansza)) {
				return false;
			}
		}
		return true;
	}
	private boolean czyWolne(int kolumna, int wiersz, boolean[][] plansza){
		if(kolumna<0 || kolumna>=kolumny || wiersz<1) {
			return false;
		}
		return wiersz>wiersze || !plansza[kolumna][wiersz];
	}
	private void postaw(Block klocek, int kolumna, int rzad, boolean[][] plansza) {
		if(rzad>=1&&rzad<=wiersze) {
			plansza[kolumna][rzad]=true;
		}
		for(Vector blok : klocek.squares()) {
			int wiersz=rzad+blok.dRow();
			if(kolumna+blok.dCol()>=0 && kolumna+blok.dCol()<kolumny && wiersz>=1 && wiersz <=wiersze) {
				plansza[kolumna+blok.dCol()][wiersz]=true;
			}
		}
	}
	private void wyczysc(boolean[][] plansza) {
		int zapisz = 1;
        for (int rzad = 1; rzad <= wiersze; rzad++) {
            boolean full = true;
            for (int kolumna = 0; kolumna < kolumny; kolumna++) 
                if (!plansza[kolumna][rzad]) full = false;
            if (!full) {
                if (zapisz != rzad) 
                    for (int kolumna = 0; kolumna < kolumny; kolumna++) plansza[kolumna][zapisz] = plansza[kolumna][rzad];
                zapisz++;
            }
        }
        while (zapisz <= wiersze) { 
            for (int kolumna = 0; kolumna < kolumny; kolumna++) plansza[kolumna][zapisz] = false; 
            zapisz++; 
        }
	}
	private int[] Zasieg(Block block) {
		int min=0;
		int max=0;
		for(Vector kwadrat : block.squares()) {
			if (kwadrat.dCol()<min) {
				min=kwadrat.dCol();
			}
			if(kwadrat.dCol()>max) {
				max=kwadrat.dCol();		
			}
		}
		return new int[] {min, max};
	}
	private int MaksymalnaWysokoscBloku(Block block, int MaxWysokosc) {
		int wysokosc=0;
		for(Vector blok: block.squares()) {
			wysokosc=MaxWysokosc+blok.dRow();
			if(wysokosc>MaxWysokosc) {
				wysokosc=MaxWysokosc;
			}
		}
		return wysokosc;
	}
}
