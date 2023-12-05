import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final String DB_USERNAME = "alexey";
    private static final String DB_PASSWORD = "111";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/javatestapp1";

    public static class Student {
        private int id;
        private String name;
        private int age;

        public Student(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    public static class StudentRetrieval {
        private Connection connection;

        public StudentRetrieval(Connection connection) {
            this.connection = connection;
        }

        public ArrayList<Student> getAllStudents() {
            ArrayList<Student> students = new ArrayList<>();
            try {
                Statement statement = connection.createStatement();
                String SQL_SELECT_TASKS = "SELECT id, name, age FROM students";
                ResultSet result = statement.executeQuery(SQL_SELECT_TASKS);

                while (result.next()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    int age = result.getInt("age");
                    students.add(new Student(id, name, age));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return students;
        }
    }

    public static class StudentDisplay {
        public static void showAllStudents(ArrayList<Student> students) {
            for (Student student : students) {
                System.out.println("ID: " + student.getId());
                System.out.println("Имя: " + student.getName());
                System.out.println("Возраст: " + student.getAge());
                System.out.println("");
            }
        }

        public static void showStudents35AndOlder(ArrayList<Student> students) {
            for (Student student : students) {
                if (student.getAge() > 35) {
                    System.out.println("ID: " + student.getId());
                    System.out.println("Имя: " + student.getName());
                    System.out.println("Возраст: " + student.getAge());
                    System.out.println("");
                }
            }
        }
    }

    public static class StudentModification {
        private Connection connection;

        public StudentModification(Connection connection) {
            this.connection = connection;
        }

        public void addNewStudent(String name, int age) {
            try {
                String SQL_INSERT_STUDENT = "INSERT INTO students (name, age) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_STUDENT);

                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, age);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Студент успешно добавлен в базу данных.");
                } else {
                    System.out.println("Ошибка при добавлении студента в базу данных.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void deleteStudent(int studentId) {
            try {
                String SQL_DELETE_STUDENT = "DELETE FROM students WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_STUDENT);

                preparedStatement.setInt(1, studentId);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Студент успешно удален из базы данных.");
                } else {
                    System.out.println("Студент с указанным ID не найден.");
                }
            } catch (SQLException e) {
                System.out.println("Ошибка при удалении студента из базы данных.");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

            StudentRetrieval studentRetrieval = new StudentRetrieval(connection);
            StudentDisplay studentDisplay = new StudentDisplay();
            StudentModification studentModification = new StudentModification(connection);

            while (true) {
                System.out.println("1: Показать всех студентов");
                System.out.println("2: Добавить нового студента");
                System.out.println("3: Показать студентов, которым больше 35 лет");
                System.out.println("4: Удалить студента");
                System.out.println("5: Выход");
                int command = scanner.nextInt();

                switch (command) {
                    case 1:
                        ArrayList<Student> allStudents = studentRetrieval.getAllStudents();
                        StudentDisplay.showAllStudents(allStudents);
                        break;
                    case 2:
                        System.out.println("Введите имя нового студента:");
                        String newName = scanner.next();
                        System.out.println("Введите возраст нового студента:");
                        int newAge = scanner.nextInt();
                        studentModification.addNewStudent(newName, newAge);
                        break;
                    case 3:
                        ArrayList<Student> students35AndOlder = studentRetrieval.getAllStudents();
                        StudentDisplay.showStudents35AndOlder(students35AndOlder);
                        break;
                    case 4:
                        System.out.println("Введите ID студента для удаления (или введите 0 для возврата в меню):");
                        int studentIdToDelete = scanner.nextInt();
                        if (studentIdToDelete == 0) {
                            continue; // Вернуться в меню
                        }
                        studentModification.deleteStudent(studentIdToDelete);
                        break;
                    case 5:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Неверная команда. Пожалуйста, введите корректную команду.");
                }
            }
        }
    }
}