import java.io.*;
import java.util.*;

public class laba4
{
        public static void main(String[] args) {
            Owner ob = new Owner();
            ob.show();

            try {
                File file = new File("input.txt");
                Scanner fileScanner = new Scanner(file);

                PrintWriter writer = new PrintWriter(new FileWriter("output.txt"));

                writer.println("СТУДЕНТЫ-ОТЛИЧНИКИ");
            writer.println("==================");
            writer.println();

                boolean foundExcellent = false;

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
                        String middleName = data[2].trim();
                        int course = Integer.parseInt(data[3].trim());
                        String group = data[4].trim();

                        // Проверяем, что 6-й элемент - число (номер сессии)
                        if (isInteger(data[5])) {
                            int sessionNumber = Integer.parseInt(data[5].trim());
                            String subject = data[6].trim();
                            String gradeInfo = data[7].trim();
                            String type = (data.length > 8) ? data[8].trim() : "экзамен";

                            boolean isExcellent = true;

                            if (type.equals("экзамен"))
                            {
                                // Проверяем, что оценка - число
                                if (isInteger(gradeInfo)) {
                                    int grade = Integer.parseInt(gradeInfo);
                                    if (grade < 9) {
                                        isExcellent = false;
                                    }
                                } else {
                                    isExcellent = false;
                                }
                            }
                            else if (type.equals("зачет"))
                            {
                                if (!gradeInfo.equals("сдал") && !gradeInfo.equals("зачет")) {
                                    isExcellent = false;
                                }
                            }

                            if (isExcellent)
                            {
                                foundExcellent = true;
                                writer.println("Фамилия: " + lastName);
                                writer.println("Имя: " + firstName);
                                writer.println("Отчество: " + middleName);
                                writer.println("Курс: " + course);
                                writer.println("Группа: " + group);
                                writer.println("Номер сессии: " + sessionNumber);
                                writer.println("Предмет: " + subject);
                                writer.println("Отметка/Зачет: " + gradeInfo);
                                writer.println("------------------");
                            }
                        }
                    }
                }
            }

                if (!foundExcellent) {
                    writer.println("Отличники не найдены.");
                }

                fileScanner.close();
                writer.close();
                System.out.println("Обработка завершена. Результат в файле output.txt");

            } catch (IOException ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
        }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
        private String middleName;
        private int course;
        private String group;

        public RecordBook(String lastName, String firstName, String middleName, int course, String group)
        {
            this.lastName = lastName;
            this.firstName = firstName;
            this.middleName = middleName;
            this.course = course;
            this.group = group;
        }

        public class SessionRecord {
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

        public void addRecord(int sessionNumber, String subject, String grade, String type) {
            SessionRecord record = new SessionRecord(sessionNumber, subject, grade, type);

        }

        // Геттеры
        public String getLastName() { return lastName; }
        public String getFirstName() { return firstName; }
        public String getMiddleName() { return middleName; }
        public int getCourse() { return course; }
        public String getGroup() { return group; }


    }
