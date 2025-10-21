import java.io.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

class Owner
{
    class Content
    {
        private int count = 220;
        public int getCount() {
            return count;
        }
    }
    void show()
    {
        Content c = new Content();
        System.out.println(c.getCount());
    }
}

class RecordBook
{
    private String lastName;
    private String firstName;
    private String otchestvo;
    private int course;
    private String group;
    private List<SessionRecord> sessionRecords;

    public RecordBook(String lastName, String firstName, String otchestvo, int course, String group)
    {
        this.lastName = lastName;
        this.firstName = firstName;
        this.otchestvo = otchestvo;
        this.course = course;
        this.group = group;
        this.sessionRecords = new ArrayList<>();
    }

    public class SessionRecord
    {
        private int sessionNumber;
        private String subject;
        private String grade;
        private String type;

        public SessionRecord(int sessionNumber, String subject, String grade, String type)
        {
            this.sessionNumber = sessionNumber;
            this.subject = subject;
            this.grade = grade;
            this.type = type;
        }

        public int getSessionNumber() { return sessionNumber; }
        public String getSubject() { return subject; }
        public String getGrade() { return grade; }
        public String getType() { return type; }
    }

    public void addRecord(int sessionNumber, String subject, String grade, String type)
    {
        SessionRecord record = new SessionRecord(sessionNumber, subject, grade, type);
        sessionRecords.add(record);
    }

    public List<SessionRecord> getSessionRecords() {
        return sessionRecords;
    }

    public double getAverageGrade() {
        int sum = 0;
        int count = 0;

        for (SessionRecord record : sessionRecords) {
            if (record.getType().equals("экзамен") && isInteger(record.getGrade())) {
                int gradeValue = Integer.parseInt(record.getGrade());
                sum += gradeValue;
                count++;
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return (double) sum / count;
    }

    public boolean isExcellentStudent()
    {
        for (SessionRecord record : sessionRecords) {
            if (record.getType().equals("экзамен")) {
                if (isInteger(record.getGrade())) {
                    int grade = Integer.parseInt(record.getGrade());
                    if (grade < 9) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        if (getExamCount() == 0) {
            return false;
        }

        return true;
    }

    private int getExamCount()
    {
        int count = 0;
        for (SessionRecord record : sessionRecords) {
            if (record.getType().equals("экзамен")) {
                count++;
            }
        }
        return count;
    }

    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getOtchestvo() { return otchestvo; }
    public int getCourse() { return course; }
    public String getGroup() { return group; }

    private boolean isInteger(String str)
    {
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

public class laba4
{
    public static void main(String[] args)
    {
        Owner ob = new Owner();
        ob.show();

        //TXT -> JSON
        processTextToJson();

        //JSON -> TXT
        processJsonToText();
    }

    public static void processTextToJson() {
        try {
            System.out.println("=== ОБРАБОТКА: input.txt -> output.json ===");

            File file = new File("input.txt");
            Scanner fileScanner = new Scanner(file);

            List<RecordBook> allStudents = new ArrayList<>();
            List<RecordBook> excellentStudents = new ArrayList<>();

            while (fileScanner.hasNextLine())
            {
                String line = fileScanner.nextLine();
                if (!line.trim().isEmpty()) {
                    String[] data = line.split(",");

                    if (data.length >= 8 && isInteger(data[3])) {
                        String lastName = data[0].trim();
                        String firstName = data[1].trim();
                        String otchestvo = data[2].trim();
                        int course = Integer.parseInt(data[3].trim());
                        String group = data[4].trim();

                        if (isInteger(data[5])) {
                            int sessionNumber = Integer.parseInt(data[5].trim());
                            String subject = data[6].trim();
                            String gradeInfo = data[7].trim();
                            String type = (data.length > 8) ? data[8].trim() : "экзамен";

                            RecordBook student = findStudent(allStudents, lastName, firstName, otchestvo, course, group);
                            if (student == null) {
                                student = new RecordBook(lastName, firstName, otchestvo, course, group);
                                allStudents.add(student);
                            }

                            student.addRecord(sessionNumber, subject, gradeInfo, type);
                        }
                    }
                }
            }

            for (RecordBook student : allStudents)
            {
                if (student.isExcellentStudent()) {
                    excellentStudents.add(student);
                }
            }

            JSONObject outputJson = createJsonOutput(allStudents, excellentStudents);

            PrintWriter writer = new PrintWriter(new FileWriter("output.json"));
            writer.write(outputJson.toString(4));
            writer.close();

            System.out.println("✓ Данные записаны в output.json");
            System.out.println("Всего студентов: " + allStudents.size());
            System.out.println("Отличников: " + excellentStudents.size());

            fileScanner.close();

        } catch (IOException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }

    public static void processJsonToText()
    {
        try {
            System.out.println("\ninput.json -> output.txt ===");

            File jsonFile = new File("input.json");
            JSONTokener tokener = new JSONTokener(new FileReader(jsonFile));
            JSONObject jsonInput = new JSONObject(tokener);

            List<RecordBook> studentsFromJson = parseStudentsFromJson(jsonInput);

            PrintWriter writer = new PrintWriter(new FileWriter("output.txt"));

            writer.println("ДАННЫЕ ИЗ JSON ФАЙЛА");
            writer.println("====================");
            writer.println();

            for (RecordBook student : studentsFromJson) {
                writer.println("Фамилия: " + student.getLastName());
                writer.println("Имя: " + student.getFirstName());
                writer.println("Отчество: " + student.getOtchestvo());
                writer.println("Курс: " + student.getCourse());
                writer.println("Группа: " + student.getGroup());
                writer.println("Средний балл: " + String.format("%.2f", student.getAverageGrade()));
                writer.println("Отличник: " + (student.isExcellentStudent() ? "Да" : "Нет"));

                writer.println("Сессии:");
                for (RecordBook.SessionRecord record : student.getSessionRecords()) {
                    writer.println("  - " + record.getSubject() + ": " + record.getGrade() +
                            " (сессия " + record.getSessionNumber() + ", " + record.getType() + ")");
                }
                writer.println("------------------");
            }

            writer.close();
            System.out.println("✓ Данные записаны в output_from_json.txt");
            System.out.println("Обработано студентов: " + studentsFromJson.size());

        } catch (IOException ex) {
            System.out.println("Ошибка при чтении JSON: " + ex.getMessage());
        }
    }

    private static List<RecordBook> parseStudentsFromJson(JSONObject json)
    {
        List<RecordBook> students = new ArrayList<>();

        JSONArray studentsArray = json.getJSONArray("студенты");

        for (int i = 0; i < studentsArray.length(); i++) {
            JSONObject studentJson = studentsArray.getJSONObject(i);

            String lastName = studentJson.getString("фамилия");
            String firstName = studentJson.getString("имя");
            String otchestvo = studentJson.getString("отчество");
            int course = studentJson.getInt("курс");
            String group = studentJson.getString("группа");

            RecordBook student = new RecordBook(lastName, firstName, otchestvo, course, group);

            JSONArray sessionsArray = studentJson.getJSONArray("сессии");
            for (int j = 0; j < sessionsArray.length(); j++) {
                JSONObject sessionJson = sessionsArray.getJSONObject(j);

                int sessionNumber = sessionJson.getInt("номер_сессии");
                String subject = sessionJson.getString("предмет");
                String grade = sessionJson.getString("оценка");
                String type = sessionJson.getString("тип");

                student.addRecord(sessionNumber, subject, grade, type);
            }

            students.add(student);
        }

        return students;
    }

    private static JSONObject createJsonOutput(List<RecordBook> allStudents, List<RecordBook> excellentStudents)
    {
        JSONObject outputJson = new JSONObject();

        JSONArray excellentArray = new JSONArray();
        for (RecordBook student : excellentStudents) {
            excellentArray.put(createStudentJson(student));
        }
        outputJson.put("отличники", excellentArray);
        outputJson.put("всего_отличников", excellentStudents.size());

        JSONArray allStudentsArray = new JSONArray();
        for (RecordBook student : allStudents) {
            allStudentsArray.put(createStudentJson(student));
        }
        outputJson.put("студенты", allStudentsArray);
        outputJson.put("всего_студентов", allStudents.size());

        return outputJson;
    }

    private static JSONObject createStudentJson(RecordBook student) {
        JSONObject studentJson = new JSONObject();
        studentJson.put("фамилия", student.getLastName());
        studentJson.put("имя", student.getFirstName());
        studentJson.put("отчество", student.getOtchestvo());
        studentJson.put("курс", student.getCourse());
        studentJson.put("группа", student.getGroup());
        studentJson.put("средний_балл", String.format("%.2f", student.getAverageGrade()));
        studentJson.put("отличник", student.isExcellentStudent());

        JSONArray sessionsArray = new JSONArray();
        for (RecordBook.SessionRecord record : student.getSessionRecords()) {
            JSONObject sessionJson = new JSONObject();
            sessionJson.put("номер_сессии", record.getSessionNumber());
            sessionJson.put("предмет", record.getSubject());
            sessionJson.put("оценка", record.getGrade());
            sessionJson.put("тип", record.getType());
            sessionsArray.put(sessionJson);
        }
        studentJson.put("сессии", sessionsArray);

        return studentJson;
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static RecordBook findStudent(List<RecordBook> students, String lastName, String firstName, String otchestvo,
                                          int course, String group) {
        for (RecordBook student : students) {
            if (student.getLastName().equals(lastName) &&
                    student.getFirstName().equals(firstName) &&
                    student.getOtchestvo().equals(otchestvo) &&
                    student.getCourse() == course &&
                    student.getGroup().equals(group)) {
                return student;
            }
        }
        return null;
    }
}
