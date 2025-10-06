import java.io.*;
import java.util.*;

public class laba4
{
    public static void main(String[] args)
    {
        Owner ob = new Owner();
        ob.show();

        try {
            File file = new File("input.txt");
            Scanner fileScanner = new Scanner(file);

            PrintWriter writer = new PrintWriter(new FileWriter("output.txt"));

            List<RecordBook> allStudents = new ArrayList<>();
            List<RecordBook> excellentStudents = new ArrayList<>();

            while (fileScanner.hasNextLine())
            {
                String line = fileScanner.nextLine();
                if (!line.trim().isEmpty())
                {
                    String[] data = line.split(",");

                    if (data.length >= 8 && isInteger(data[3]))
                    {
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

            //разделение на отличников и всех
            for (RecordBook student : allStudents) {
                if (student.isExcellentStudent()) {
                    excellentStudents.add(student);
                }
            }

            //компараторы
            Comparator<RecordBook> byCourse = new Comparator<RecordBook>()
            {
                @Override
                public int compare(RecordBook s1, RecordBook s2) {
                    return Integer.compare(s1.getCourse(), s2.getCourse());
                }
            };

            Comparator<RecordBook> byAverageGrade = new Comparator<RecordBook>()
            {
                @Override
                public int compare(RecordBook s1, RecordBook s2)
                {
                    return Double.compare(s2.getAverageGrade(), s1.getAverageGrade());
                }
            };

            Comparator<RecordBook> byGroup = new Comparator<RecordBook>()
            {
                @Override
                public int compare(RecordBook s1, RecordBook s2) {
                    return s1.getGroup().compareTo(s2.getGroup());
                }
            };

            Comparator<RecordBook> byLastName = new Comparator<RecordBook>()
            {
                @Override
                public int compare(RecordBook s1, RecordBook s2) {
                    return s1.getLastName().compareTo(s2.getLastName());
                }
            };

            // ВЫВОД ОТЛИЧНИКОВ
            writer.println("СТУДЕНТЫ-ОТЛИЧНИКИ");
            writer.println("==================");
            writer.println();

            if (!excellentStudents.isEmpty()) {
                writer.println("Всего отличников: " + excellentStudents.size());
                writer.println();

                //отличники по сред баллу
                Collections.sort(excellentStudents, byAverageGrade);
                for (RecordBook student : excellentStudents)
                {
                    writer.println("Фамилия: " + student.getLastName());
                    writer.println("Имя: " + student.getFirstName());
                    writer.println("Отчество: " + student.getOtchestvo());
                    writer.println("Курс: " + student.getCourse());
                    writer.println("Группа: " + student.getGroup());
                    writer.println("Средний балл: " + String.format("%.2f", student.getAverageGrade()));
                    writer.println("------------------");
                }
            } else {
                writer.println("Отличники не найдены.");
            }

            writer.println();
            writer.println("================================================");
            writer.println();

            if (!allStudents.isEmpty()) {
                writer.println("Всего студентов: " + allStudents.size());
                writer.println();

                // 2.1 Сортировка по курсу
                writer.println("1. СОРТИРОВКА ПО КУРСУ (возрастание):");
                writer.println("=====================================");
                Collections.sort(allStudents, byCourse);
                printStudents(writer, allStudents);
                writer.println();

                // 2.2 Сортировка по среднему баллу (убывание)
                writer.println("2. СОРТИРОВКА ПО СРЕДНЕМУ БАЛЛУ (убывание):");
                writer.println("===========================================");
                Collections.sort(allStudents, byAverageGrade);
                printStudents(writer, allStudents);
                writer.println();

                // 2.3 Сортировка по группе
                writer.println("3. СОРТИРОВКА ПО ГРУППЕ");
                writer.println("=============================================");
                Collections.sort(allStudents, byGroup);
                printStudents(writer, allStudents);
                writer.println();

                // 2.4 Сортировка по фамилии
                writer.println("4. СОРТИРОВКА ПО ФАМИЛИИ (алфавитный порядок):");
                writer.println("==============================================");
                Collections.sort(allStudents, byLastName);
                printStudents(writer, allStudents);
                writer.println();

            } else {
                writer.println("Студенты не найдены.");
            }

            fileScanner.close();
            writer.close();
            System.out.println("Обработка завершена. Результат в файле output.txt");

        } catch (IOException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }

    //метод вывода студентов
    private static void printStudents(PrintWriter writer, List<RecordBook> students)
    {
        for (RecordBook student : students) 
        {
            String excellentMark = student.isExcellentStudent() ? " (ОТЛИЧНИК)" : "";
            writer.println("Фамилия: " + student.getLastName() + excellentMark);
            writer.println("Имя: " + student.getFirstName());
            writer.println("Отчество: " + student.getOtchestvo());
            writer.println("Курс: " + student.getCourse());
            writer.println("Группа: " + student.getGroup());
            writer.println("Средний балл: " + String.format("%.2f", student.getAverageGrade()));
            writer.println("------------------");
        }
    }

    private static boolean isInteger(String str)
    {
        try
        {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static RecordBook findStudent(List<RecordBook> students, String lastName, String firstName, String otchestvo,
                                          int course, String group) {
        for (RecordBook student : students)
        {
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

        public SessionRecord(int sessionNumber, String subject, String grade, String type) {
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

    public double getAverageGrade()
    {
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

    public boolean isExcellentStudent() {
        // Проверяем ВСЕ экзаменационные оценки студента
        for (SessionRecord record : sessionRecords) {
            if (record.getType().equals("экзамен")) {
                if (isInteger(record.getGrade())) {
                    int grade = Integer.parseInt(record.getGrade());
                    if (grade < 9) { // Если есть хоть одна оценка ниже 9
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        // Если нет ни одного экзамена, студент не отличник
        if (getExamCount() == 0) {
            return false;
        }

        return true;
    }

    // Вспомогательный метод для подсчета кол ва экзаменов
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

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


