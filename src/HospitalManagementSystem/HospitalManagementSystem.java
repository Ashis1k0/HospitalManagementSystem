package HospitalManagementSystem;


import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    //these are jst encrypted bcz of security, then pushed
    private static final String url   ="70472";
    private static final String useName ="3506402";
    private static final String password ="-925313407";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url,useName,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true)
            {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient\n2. View patient\n3. View Doctors\n4. Book Appointment\n5. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice)
                {
                    case 1:
                        //add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        //view patients
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        //view doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //book appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        //exit
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                }
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner)
    {
        System.out.println("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId =  scanner.nextInt();
        System.out.println("Enter appointment date(YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId))
        {
            if(checkDoctorAvailability(doctorId,appointmentDate,connection))
            {
                String query = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);

                    int affectedRows = preparedStatement.executeUpdate();

                    if(affectedRows >0)
                        System.out.println("Appointment Booked!!");
                    else
                        System.out.println("Failed to book appointment");
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }else System.out.println("Doctor is not available on this date.");
        }
        else{
            System.out.println("Either Doctor or Patient doesn't exists!!");
        }
    }
    public static boolean checkDoctorAvailability(int docId,String appointmentDate,Connection connection)
    {
        String query = "SELECT count(*) FROM appointments WHERE doctor_id = ? AND appointment_date =?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,docId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                int cnt = resultSet.getInt(1);
                if(cnt ==0)
                        return true;
                else
                    return false;
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
