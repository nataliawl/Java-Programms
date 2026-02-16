import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MazeSolver implements Maze {
    private int height; //Liczba wierszy
    private int width;  //Liczba kolumn
    //Pierwsze wiersze, drugie kolumny
    private int[][] maze; //Tu bedą przechowywane pola - kazdy kolor to inna sciezka
    private boolean if_created = false;
    private int last_color = 1; //Przy wyszukiwaniu nowej sciezki zwiekszamy o 1
    //Tutaj przechowujemy najdalsze wiersze
    private final List<Integer> farthest_squares = new ArrayList<>();
    //Tutaj pola powierzchni wypelnionych obszarow
    private final List<Integer> our_areas = new ArrayList<>();
    //O to puste, 1 to pierwsze zajete

    @Override
    public void rows(int rows) {
        height = rows;
    }

    @Override
    public void cols(int cols) {
        width = cols;
    }

    public void findAllSquares(Square current_square, int index) {
        maze[current_square.row()][current_square.col()] = last_color;
        our_areas.set(index, our_areas.get(index) + 1); //Zwiekszamy pole o 1

        if(current_square.row() > farthest_squares.get(index)){
            farthest_squares.set(index, current_square.row());//Szukamy
        }

        Set<Square> current_neighbors = current_square.neighbours();
        for(Square square : current_neighbors){
            if(square.row() < 0 || square.col() < 0) continue;
            if(square.row() >= height || square.col() >= width) continue;
            if(maze[square.row()][square.col()] != 0) continue;
            findAllSquares(square, index);
        }

    }

    //Tutaj dzieje sie magia
    public void fillSquares(){
        //int index = 0;
        for (int col = 0 ; col < width; col++) {
            farthest_squares.add(col, 0);
            our_areas.add(col, 0);
            if(maze[0][col] != 0) {
                for(int pom = 0; pom < col; pom++){
                    if(maze[0][col] == maze[0][pom]){
                        farthest_squares.set(col, farthest_squares.get(pom));
                        our_areas.set(col, our_areas.get(pom));
                        break;
                    }
                }
                continue;
            }

            last_color++;
            maze[0][col] = last_color;
            our_areas.set(col, 1);

            Square source_square = new Square(col, 0);
            Set<Square> source_neighbors = source_square.neighbours();

            for (Square square : source_neighbors) {
                if(square.row() < 0 || square.col() < 0) continue;
                if(square.row() >= height || square.col() >= width) continue;
                if(maze[square.row()][square.col()] != 0) continue;
                findAllSquares(square, col); //Albo zamiast col indeks
            }
        }
    }

    @Override
    public void occupiedSquare(Set<Square> squares) {
        if(!if_created) {
            maze = new int[height][width];
            for(int i = 0; i < height; i++) {
                for(int j = 0; j < width; j++) {
                    maze[i][j] = 0;
                }
            }
            if_created = true;
        }

        for (Square square : squares) {
            maze[square.row()][square.col()] = 1;
        }

        //Tutaj wywolujemy metode wyszukiwania
        fillSquares();
    }

    @Override
    public List<Integer> howFar() {
        return farthest_squares;

    }

    @Override
    public List<Integer> area() {
        return our_areas;
    }

    @Override
    public Set<Square> unreachableSquares() {
        Set<Square> unreachable = new HashSet<>();
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(maze[i][j] == 0) {
                    unreachable.add(new Square(j, i));
                }
            }
        }
        return unreachable;
    }
}
