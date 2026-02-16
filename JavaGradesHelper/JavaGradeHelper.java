import java.io.*;
import java.util.*;

public class JavaGradesHelper implements GradesHelper {
    Map<Integer, String> final_grades = new TreeMap<>();
    Map<Integer, String> students_names = new TreeMap<>();
    Map<Integer, String> students_surnames = new TreeMap<>();
    Map<String, Float> min_grades = new TreeMap<>();
    Map<String, Float> max_grades = new TreeMap<>();

    public int how_many_semicolons(String line){
        int semicolons = 0;
        for(char ch : line.toCharArray()){
            if(ch == ';') semicolons++;
        }
        return semicolons;
    }

    @Override
    public void loadStudents(String file) {
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                if(line.isEmpty()) continue;
                if(how_many_semicolons(line) != 3) continue;
                String[] split = line.split(";");
                int student_id = Integer.parseInt(split[0]);
                students_names.put(student_id, split[1]);
                students_surnames.put(Integer.parseInt(split[0]), split[2]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadScoring(String file) throws RangeConflictException, MarkConflictException {
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                if(line.isEmpty()) continue;
                String[] split = line.split(";");
                float min = Float.parseFloat(split[1]);
                float max = Float.parseFloat(split[2]);
                if(min_grades.containsKey(split[0]) || max_grades.containsKey(split[0])){
                   if(!min_grades.get(split[0]).equals(min) || !max_grades.get(split[0]).equals(max)){
                       throw new MarkConflictException("Mark Conflict Exception");
                   }
                }
                for (String key: min_grades.keySet()) {
                    if((min_grades.get(key).equals(min) || max_grades.get(key).equals(max)) && !key.equals(split[0])){
                        throw new RangeConflictException();
                    }
                }
                min_grades.put(split[0], min);
                max_grades.put(split[0], max);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int find_id(String line){
        String[] split = line.split(";");
        String name = split[0];
        String surname = split[1];
        for(int key : students_names.keySet()){
            if(name.equals(students_names.get(key))){
                if(surname.equals(students_surnames.get(key))) return key;
            }
        }
        return -1;
    }

    public float mean_grade(String line){
        String[] split = line.split(";");
        int semicolons =  how_many_semicolons(line);
        List<Float> grades = new ArrayList<>();
        for(int i = 2; i < semicolons; i++){
            if(Float.parseFloat(split[i])< 0) return -1;
            grades.add(Float.parseFloat(split[i]));
        }
        float mean = 0;
        for (Float grade : grades) {
            mean += grade;
        }
        return mean / grades.size();
    }

    public String set_grade(float mean){
        for(String key: min_grades.keySet()){
            if(mean >= min_grades.get(key) &&  mean <= max_grades.get(key)){
                return key;
            }
        }
        return null;
    }

    @Override
    public Map<Integer, String> generateGrades(String data) throws AssessmentImpossible {
        try{
            BufferedReader reader = new BufferedReader(new FileReader(data));
            String line;
            while((line = reader.readLine()) != null){
                if(line.isEmpty()) continue;
                int id = find_id(line);
                String split[] = line.split(";");
                if(id == -1) throw new GradesHelper.AssessmentImpossible(split[0], split[1]);
                if(final_grades.containsKey(id)) continue;
                float student_mean = mean_grade(line);
                String student_grade =  set_grade(student_mean);
                if(student_grade == null) {
                    throw new AssessmentImpossible(students_names.get(id), students_surnames.get(id));
                }
                final_grades.put(id, student_grade);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return final_grades;
    }
}
