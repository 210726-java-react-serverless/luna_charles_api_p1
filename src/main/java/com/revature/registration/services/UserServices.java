package com.revature.registration.services;

import com.revature.registration.models.Faculty;
import com.revature.registration.models.Student;
import com.revature.registration.repositories.FacultyRepository;
import com.revature.registration.repositories.StudentRepository;
import com.revature.registration.util.exceptions.AuthenticationException;
import com.revature.registration.util.exceptions.DataSourceException;
import com.revature.registration.util.exceptions.InvalidInformationException;

/**
 * UserServices passes information between Repository Classes and screens so that information from the database gets
 * to the user and information given by the user is verified.
 */
public class UserServices {
    private StudentRepository studentRepo;
    private FacultyRepository facultyRepo;

    public UserServices(StudentRepository studentRepo, FacultyRepository facultyRepo) {
        this.studentRepo = studentRepo;
        this.facultyRepo = facultyRepo;
    }

    public Student findStudentById(String id) {
        Student foundStudent = studentRepo.findById(id);
        if (foundStudent == null) { throw new InvalidInformationException("you don't appear to be logged in as a student"); }
        return foundStudent;
    }

    public Faculty findFacultyById(String id) {
        Faculty foundFaculty = facultyRepo.findById(id);
        if (foundFaculty == null) { throw new InvalidInformationException("you don't appear to be logged in as a faculty"); }
        return foundFaculty;
    }

    /**
     * registerStudent() calls isStudentValid before saving a Student to the database. If a Student is not valid,
     * isStudentValid will throw an exception.
     * @param student
     * @return
     */
    public Student registerStudent(Student student) {
        isStudentValid(student);
        studentRepo.save(student);
        return student;
    }

    /**
     * loginStudent takes in a Student's email and password and uses the StudentRepository to check if those credentials
     * are accurate. If they are, loginStudent returns the Student associated with those credentials. Otherwise,
     * loginStudent throws an AuthenticationException.
     * @param email
     * @param password
     * @return
     * @throws AuthenticationException
     */
    public Student loginStudent(String email, String password) throws AuthenticationException {
        Student student = studentRepo.findByCredentials(email,password);
        if (student == null) {
            throw new AuthenticationException("Invalid Username/Password combo");
        }
        return student;
    }

    /**
     * loginFaculty takes in a Faculty's email and password and uses the FacultyRepository to check if those
     * credentials are accurate. If they are, loginFaculty returns the Faculty associated with those credentials.
     * Otherwise, loginFaculty throws an AuthenticationException.
     * @param email
     * @param password
     * @return
     * @throws AuthenticationException
     */
    public Faculty loginFaculty(String email, String password) throws AuthenticationException {
        Faculty faculty = facultyRepo.findByCredentials(email,password);
        if (faculty == null) {
            throw new AuthenticationException("Invalid Username/Password combo");
        }
        return faculty;
    }

    /**
     * isStudentValid takes in a Student and checks if their information is valid for persistence on the database.
     * An InvalidInformationException is thrown if a field is null or an empty string, the email doesn't contain an
     * at symbol, the password is less than 4 characters, or if the Student's email is already in the database as a
     * Student. If no exception is thrown, isStudentValid returns true.
     * @param student
     * @return
     * @throws InvalidInformationException
     */
    public boolean isStudentValid(Student student) throws InvalidInformationException{
        if (student.getFirstName() == null || student.getLastName() == null || student.getEmail() == null ||
                student.getPassword() == null) {
            throw new InvalidInformationException("No field can be null");
        }
        if (!student.getEmail().contains("@")) {
            throw new InvalidInformationException("Email provided was not a valid email");
        }
        if (student.getPassword().length()<4) {
            throw new InvalidInformationException("Password provided was not long enough");
        }
        if (student.getFirstName().trim().equals("") || student.getLastName().trim().equals("") || student.getEmail().trim().equals("") ||
                student.getPassword().trim().equals("")) {
            throw new InvalidInformationException("No field can be left blank");
        }
        if (studentRepo.findByEmail(student.getEmail()) != null) {
            throw new InvalidInformationException("That email is already registered with this application.");
        }

        return true;
    }
}
