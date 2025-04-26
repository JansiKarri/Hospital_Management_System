package hsptlmngmntsystem;

import java.sql.*;
import java.util.Scanner;

public class AppointmentSystem {
	
	public static void main(String[] args) {
		
		try (Scanner scanner = new Scanner(System.in)) {
			try {
				Connection conn = DataBaseConnection.getConnection();
				Patient patient = new Patient(conn, scanner);
				Doctor doctor = new Doctor(conn);
				while (true) {
					System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
					System.out.println("1. Add Patient");
					System.out.println("2. View Patients");
					System.out.println("3. View Doctors");
					System.out.println("4. Book Appointment");
					System.out.println("5. Add Doctor");
					System.out.println("6. Checking Doctor Availability");
					System.out.println("7. View Appointments");
					System.out.println("8: Cancel Appointments");
					System.out.println("9. Reschedule Appointments");
					System.out.println("10. Delete Patients");
					System.out.println("11. Exit");
					System.out.println("Enter your choice: ");
					int choice = scanner.nextInt();

					switch (choice) {
					case 1:
						// Add Patient
						patient.addPatient();
						System.out.println();
						break;
					case 2:
						// View Patient
						patient.viewPatients();
						System.out.println();
						break;

					case 3:
						// View Doctors
						doctor.viewDoctors();
						System.out.println();
						break;
					case 4:
						// Book Appointment
						bookAppointment(patient, doctor, conn, scanner);
						System.out.println();
						break;
					case 5:
						// Add Doctor
						doctor.addDoctor();
						System.out.println();
						break;
					case 6:
						//checking doctor availability
						 System.out.print("Enter Doctor Id: ");
						    int doctorId = scanner.nextInt();
						    System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
						    String appointmentDate = scanner.next();
						    if (checkDoctorAvailability(doctorId, appointmentDate, conn)) {
						        System.out.println("Doctor is available on this date.");
						    } else {
						        System.out.println("Doctor is NOT available on this date.");
						    }
						    break;
					case 7:
						viewAppointments(conn, scanner);
						System.out.println();
						break;
						
					case 8:
						cancelAppointment( conn, scanner);
						System.out.println();
						break;
						
					case 9:
						rescheduleAppointment(conn,  scanner);
						System.out.println();
						break;
					case 10:patient.deletePatient();
					      System.out.println();
					      break;
					
						
					case 11:
						System.out.println("THANK YOU 😊! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
						System.out.println("HAVE A HAPPY & SAFE RECOVERY!👍");
						return;
					default:
						System.out.println("Enter valid choice!!!");
						break;
					}

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void bookAppointment(Patient patient, Doctor doctor, Connection conn, Scanner scanner) {
		System.out.print("Enter Patient Id: ");
		int patientId = scanner.nextInt();
		System.out.print("Enter Doctor Id: ");
		int doctorId = scanner.nextInt();
		System.out.print("Enter appointment date (YYYY-MM-DD): ");
		String appointmentDate = scanner.next();
		if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
			if (checkDoctorAvailability(doctorId, appointmentDate, conn)) {
				String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
				try {
					PreparedStatement preparedStatement = conn.prepareStatement(appointmentQuery);
					preparedStatement.setInt(1, patientId);
					preparedStatement.setInt(2, doctorId);
					preparedStatement.setString(3, appointmentDate);
					int rowsAffected = preparedStatement.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Appointment Booked!");
					} else {
						System.out.println("Failed to Book Appointment!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Doctor not available on this date!!");
			}
		} else {
			System.out.println("Either doctor or patient doesn't exist!!!");
		}
	}

	public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
		String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, doctorId);
			preparedStatement.setString(2, appointmentDate);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				if (count == 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void viewAppointments(Connection connection, Scanner scanner) {
	    System.out.print("Enter filter type (1: All, 2: By Doctor ID, 3: By Patient ID, 4: By Date): ");
	    int filter = scanner.nextInt();
	    String query = "SELECT * FROM appointments";

	    switch (filter) {
	        case 2:
	            System.out.print("Enter Doctor ID: ");
	            int doctorId = scanner.nextInt();
	            query += " WHERE doctor_id = " + doctorId;
	            break;
	        case 3:
	            System.out.print("Enter Patient ID: ");
	            int patientId = scanner.nextInt();
	            query += " WHERE patient_id = " + patientId;
	            break;
	        case 4:
	            System.out.print("Enter Date (YYYY-MM-DD): ");
	            String date = scanner.next();
	            query += " WHERE appointment_date = '" + date + "'";
	            break;
	    }

	    try {
	        Statement stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        if(rs.next()) {
	        	System.out.println("+--------------+------------+------------+-----------------+");
		        System.out.println("| Appointment | Patient ID | Doctor ID  | Appointment Date|");
		        System.out.println("+--------------+------------+------------+-----------------+");
		         do {
	            System.out.printf("| %-12d | %-10d | %-10d | %-15s |\n",
	                    rs.getInt("id"),
	                    rs.getInt("patient_id"),
	                    rs.getInt("doctor_id"),
	                    rs.getString("appointment_date"));
	            System.out.println("+--------------+------------+------------+-----------------+");
	        }while(rs.next());
	    }
	        else {
	    	System.out.println("No appointments found");
	    }
	    }  catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	

}
	
	public static void cancelAppointment(Connection conn, Scanner scanner) {
	    System.out.print("Enter Appointment ID to cancel: ");
	    int id = scanner.nextInt();

	    String query = "DELETE FROM appointments WHERE id = ?";
	    try {
	        PreparedStatement ps = conn.prepareStatement(query);
	        ps.setInt(1, id);
	        int rows = ps.executeUpdate();
	        if (rows > 0) {
	            System.out.println("Appointment Cancelled Successfully!");
	        } else {
	            System.out.println("No Appointment found with the given ID.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void rescheduleAppointment(Connection conn, Scanner scanner) {
	    System.out.print("Enter Appointment ID to reschedule: ");
	    int id = scanner.nextInt();
	    System.out.print("Enter New Appointment Date (YYYY-MM-DD): ");
	    String newDate = scanner.next();

	    String query = "UPDATE appointments SET appointment_date = ? WHERE id = ?";
	    try {
	        PreparedStatement ps = conn.prepareStatement(query);
	        ps.setString(1, newDate);
	        ps.setInt(2, id);
	        int rows = ps.executeUpdate();
	        if (rows > 0) {
	            System.out.println("Appointment Rescheduled Successfully!");
	        } else {
	            System.out.println("No Appointment found with the given ID.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


}
