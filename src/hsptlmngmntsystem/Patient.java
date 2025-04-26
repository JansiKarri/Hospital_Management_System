package hsptlmngmntsystem;

	import java.sql.*;
	import java.util.Scanner;

	public class Patient {
	    private Connection conn;
	    private Scanner scanner;

	    public Patient(Connection conn, Scanner scanner){
	        this.conn= conn;
	        this.scanner = scanner;
	    }

	    public void addPatient(){
	        System.out.print("Enter Patient Name: ");
	        String name = scanner.next();
	        System.out.print("Enter Patient Age: ");
	        int age = scanner.nextInt();
	        System.out.print("Enter Patient Gender: ");
	        String gender = scanner.next();

	        try{
	            String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
	            PreparedStatement preparedStatement = conn.prepareStatement(query);
	            preparedStatement.setString(1, name);
	            preparedStatement.setInt(2, age);
	            preparedStatement.setString(3, gender);
	            int affectedRows = preparedStatement.executeUpdate();
	            if(affectedRows>0){
	                System.out.println("Patient Added Successfully!!");
	            }else{
	                System.out.println("Failed to add Patient!!");
	            }

	        }catch (SQLException e){
	            e.printStackTrace();
	        }
	    }

	    public void viewPatients(){
	        String query = "select * from patients";
	        try{
	            PreparedStatement preparedStatement = conn.prepareStatement(query);
	            ResultSet resultSet = preparedStatement.executeQuery();
	            System.out.println("Patients: ");
	            System.out.println("+------------+--------------------+----------+------------+");
	            System.out.println("| Patient Id | Name               | Age      | Gender     |");
	            System.out.println("+------------+--------------------+----------+------------+");
	            while(resultSet.next()){
	                int id = resultSet.getInt("id");
	                String name = resultSet.getString("name");
	                int age = resultSet.getInt("age");
	                String gender = resultSet.getString("gender");
	                System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", id, name, age, gender);
	                System.out.println("+------------+--------------------+----------+------------+");
	            }

	        }catch (SQLException e){
	            e.printStackTrace();
	        }
	    }

	    public boolean getPatientById(int id){
	        String query = "SELECT * FROM patients WHERE id = ?";
	        try{
	            PreparedStatement preparedStatement = conn.prepareStatement(query);
	            preparedStatement.setInt(1, id);
	            ResultSet resultSet = preparedStatement.executeQuery();
	            if(resultSet.next()){
	                return true;
	            }else{
	                return false;
	            }
	        }catch (SQLException e){
	            e.printStackTrace();
	        }
	        return false;
	    }
	    public void deletePatient() {
	        System.out.print("Enter Patient ID to delete: ");
	        int patientId = scanner.nextInt();

	        String checkQuery = "SELECT * FROM patients WHERE id = ?";
	        String deleteQuery = "DELETE FROM patients WHERE id = ?";

	        try {
	            // Check if patient exists
	            PreparedStatement pmst = conn.prepareStatement(checkQuery);
	            pmst.setInt(1, patientId);
	            ResultSet rs = pmst.executeQuery();

	            if (rs.next()) {
	                // If patient exists, delete the record
	                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
	                deleteStmt.setInt(1, patientId);
	                int rowsAffected = deleteStmt.executeUpdate();

	                if (rowsAffected > 0) {
	                    System.out.println("Patient with ID " + patientId + " has been deleted successfully.");
	                } else {
	                    System.out.println("Failed to delete patient.");
	                }
	            } else {
	                System.out.println("Patient with ID " + patientId + " does not exist.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }


	}



