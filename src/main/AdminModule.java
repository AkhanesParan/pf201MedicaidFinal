package main;

import java.util.*;
import config.Config;
/**
 * AdminModule.java
 * --------------------------------------------------
 * Handles all admin dashboard functions for the system.
 * Uses Con fig.java for database operations.
 * --------------------------------------------------
 * Menu Cases:
 * 1. View existing accounts
 * 2. Approve new accounts
 * 3. Approve account deactivation
 * 4. Manage caretaker-patient connection
 * 5. Monitor care plans and tasks
 * 6. Edit account credentials
 * 7. Deactivate account
 * 8. Exit (return to login)
 * 9. Close program
 */

public class AdminModule {

    public static Scanner scan = new Scanner(System.in);
    public static Config config = new Config();
    public static String sqlAdd,sqlDel,sqlUpd,sqlView, sqlFetch;
    public static int choice;
    public static int u_id;
    // Entry point for the Admin Dashboard
    public static void adminDashboard(String adminId) {
        while (true) {
            System.out.println("===== ADMIN DASHBOARD =====");
            System.out.println("\t1. View existing accounts");
            System.out.println("\t2. Approve new accounts");
            System.out.println("\t3. Approve account deactivation");
            System.out.println("\t4. Manage caretaker-patient connection");
            System.out.println("\t5. Monitor careplans and tasks");
            System.out.println("\t6. Edit account credentials");
            System.out.println("\t7. Deactivate account");
            System.out.println("\t8. Exit");
            System.out.println("\t9. Close program");
            System.out.print("Enter #: ");
            choice = scan.nextInt();

            switch (choice) {
                case 1: viewExistingAccounts(); break;
                case 2: approveNewAccounts(); break;
                case 3: approveDeactivation(); break;
                case 4: manageConnections(); break;
                case 5: monitorCareplansAndTasks(); break;
                case 6: editAdminAccount(adminId); break;
                case 7: deactivateAccount(adminId); break;
                case 8: return; // go back to login
                case 9: System.exit(0);
                default: System.out.println("Invalid input. Try again.");
            }
        }
    }

    // ===========================================================================
    // CASE 1: View existing accounts
    private static void viewExistingAccounts() {
        while (true) {
            System.out.println("===== VIEW EXISTING ACCOUNTS =====");
            System.out.println("\t1. View Patients");
            System.out.println("\t2. View Caretakers");
            System.out.println("\t3. Exit");
            System.out.print("Enter #: ");
            choice = scan.nextInt();

            if (choice == 1) showAccountsByRole("patient");
            else if (choice == 2) showAccountsByRole("caretaker");
            else if (choice == 3) return;
            else System.out.println("Invalid choice. Try again.");
        }
    }

    private static void showAccountsByRole(String role) {
        sqlFetch = "SELECT u_id, u_fname, u_lname, email FROM users WHERE role = ?";        
        List<Map<String, Object>> accounts = config.fetchRecords(sqlFetch, role);
        if (accounts.isEmpty()) {
            System.out.println("No " + role + " accounts found.");
            System.out.println("Press any key to continue...");
            scan.nextLine();
            scan.nextLine();
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            Map<String, Object> acc = accounts.get(i);
            System.out.println((i + 1) + ". " + acc.get("u_fname") + " " + acc.get("u_lname") + " - " + acc.get("email"));
        }

        System.out.print("Enter number to view full details [0 to exit]: ");
        int num = scan.nextInt();
        if (num == 0) return;
        if (num > 0 && num <= accounts.size()) {
            int id = (int) accounts.get(num - 1).get("u_id");
            viewAccountDetails(id);
        }
    }

    private static void viewAccountDetails(int u_id) {
        sqlFetch = "SELECT * FROM users WHERE u_id = ?";
        List<Map<String, Object>> fullinfo = config.fetchRecords(sqlFetch, u_id);
        
        if (fullinfo.isEmpty()) return;
        Map<String, Object> acc = fullinfo.get(0);
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Name: " + acc.get("u_fname") + " " + acc.get("u_lname"));
        System.out.println("Email: " + acc.get("email"));
        System.out.println("Role: " + acc.get("role"));
        System.out.println("Status: " + acc.get("status"));
        System.out.println("Birthday: " + acc.get("birthday"));
        System.out.println("Credentials: " + acc.get("credentials"));
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }

    // ===========================================================================
    // CASE 2: Approve new accounts
    private static void approveNewAccounts() {
        while(true){
        sqlFetch ="SELECT u_id, u_fname, u_lname, email FROM users WHERE status = 'pending'";
        List<Map<String, Object>> pending = config.fetchRecords(sqlFetch);

        if (pending.isEmpty()) {
            System.out.println("No accounts pending approval.");
            System.out.println("Press any key to continue...");
            scan.nextLine();
            return;
        }
        //view all pending accounts
        System.out.println("Pending accounts:");
        Map<String, Object> acc = pending.get(0);
        for (int i = 0; i < pending.size(); i++) {
            System.out.println( "\t" + acc.get("u_fname") + " " + acc.get("u_lname") + " - " + acc.get("email"));
        }
        //loop through accounts        
            System.out.println("\n============================================");
            System.out.println("Pending account: " + acc.get("u_fname") + " " + acc.get("u_lname") + " - " + acc.get("email"));
            System.out.print("\t1. Approve\n\t2. Decline\n\t0.exit");
            System.out.println("Enter #: ");
            choice = scan.nextInt();
            if (choice == 0) return;
            else if (choice == 1)
                config.updateRecord("UPDATE users SET status='approved' WHERE u_id=?", acc.get("u_id"));
            else
                config.updateRecord("DELETE FROM users WHERE u_id=?", acc.get("u_id"));
            System.out.println("Press any key to continue...");
            scan.nextLine();            
        }
    }

    // ===========================================================================
    // CASE 3: Approve account deactivation
    private static void approveDeactivation() {
        while (true) {
            List<Map<String, Object>> closing = config.fetchRecords(
                "SELECT u_id, u_fname, u_lname, email FROM users WHERE status='closing'");
            if (closing.isEmpty()) {
                System.out.println("No accounts pending for deletion.");
                System.out.println("Press any key to continue...");
                scan.nextLine();
                return;
            }

            
             //view all pending accounts
            System.out.println("Pending accounts:");
            Map<String, Object> acc = closing.get(0);
            for (int i = 0; i < closing.size(); i++) {
                System.out.println( "\t" + acc.get("u_fname") + " " + acc.get("u_lname") + " - " + acc.get("email"));
            }            
            System.out.println("\n============================================");
            System.out.println("Pending deactivation: " + acc.get("u_fname") + " " + acc.get("u_lname"));
            System.out.print("Approve deletion? (y/n, 0 to exit): ");
            String ans = scan.nextLine();
            if (ans.equals("0")) return;
            if (ans.equalsIgnoreCase("y"))
                config.updateRecord("UPDATE users SET status='archived' WHERE u_id=?", acc.get("u_id"));
            else
                config.updateRecord("UPDATE users SET status='approved' WHERE u_id=?", acc.get("u_id"));
        }
    }

    // ===========================================================================
    // CASE 4: Manage caretaker-patient connection
    private static void manageConnections() {
        while(true){
            System.out.println("===== MANAGE CARETAKER-PATIENT CONNECTION =====");
            System.out.println("\t1. Assign connection");
            System.out.println("\t2. Approve disconnection request");
            System.out.println("\t3. Exit");
            System.out.print("Enter #: ");
            choice = scan.nextInt();

            switch (choice) {
                case 1: assignConnection(); break;
                case 2: approveDisconnection(); break;
                case 3: return;
                default: System.out.println("Invalid input.");
            }
        }    
    }

    private static void assignConnection() {
        List<Map<String, Object>> patients = config.fetchRecords(
                "SELECT u_id, u_fname, u_lname FROM users WHERE role='patient' AND status='approved'");
        List<Map<String, Object>> carers = config.fetchRecords(
                "SELECT u_id, u_fname, u_lname FROM users WHERE role='caretaker' AND status='approved'");

        if (patients.isEmpty() || carers.isEmpty()) {
            System.out.println("No available patients or caretakers.");
            System.out.println("Press any key to continue...");
            scan.nextLine();
            return;
        }
        //loop through the patients
        for (int i = 0; i < patients.size(); i++)
            System.out.println((i + 1) + ". " + patients.get(i).get("u_fname") + " " + patients.get(i).get("u_lname"));
        System.out.println("Select Patient:");
        int p = scan.nextInt();
        int patientId = (int) patients.get(p - 1).get("u_id");

        
        for (int i = 0; i < carers.size(); i++)
            System.out.println((i + 1) + ". " + carers.get(i).get("u_fname") + " " + carers.get(i).get("u_lname"));
        System.out.println("Select Caretaker:");
        int c = scan.nextInt();
        int careId = (int) carers.get(c - 1).get("u_id");

        List<Map<String, Object>> existing = config.fetchRecords("SELECT * FROM p_to_c_connect WHERE patient_id=?", patientId);
        if (existing.isEmpty())
            config.updateRecord("INSERT INTO p_to_c_connect(patient_id, care_id, messages) VALUES(?, ?, '-')", patientId, careId);
        else
            config.updateRecord("UPDATE p_to_c_connect SET care_id=? WHERE patient_id=?", careId, patientId);

        System.out.println("Connection updated. Press any key to continue...");
        scan.nextLine();
    }

    private static void approveDisconnection() {
        List<Map<String, Object>> closing = config.fetchRecords(
                "SELECT connect_id, patient_id, care_id FROM p_to_c_connect WHERE messages='closing'");
        if (closing.isEmpty()) {
            System.out.println("No pending disconnection requests.");
            System.out.println("Press any key to continue...");
            scan.nextLine();
            return;
        }

        Map<String, Object> c = closing.get(0);
        System.out.println("Pending disconnection request found.");
        System.out.print("\t1. Approve\n\t2. Decline\n\t0. Exit");
        System.out.println("Enter #: ");
        choice = scan.nextInt();
        if (choice == 0) return;
        else if (choice == 1)
            config.updateRecord("DELETE FROM p_to_c_connect WHERE connect_id=?", c.get("connect_id"));
        else if (choice == 2)
            config.updateRecord("UPDATE p_to_c_connect SET messages='-' WHERE connect_id=?", c.get("connect_id"));
        System.out.println("Action completed. Press any key to continue...");
        scan.nextLine();
    }

    // ===========================================================================
    // CASE 5: Monitor careplans and tasks
    private static void monitorCareplansAndTasks() {
        System.out.println("===== MONITOR CAREPLANS AND TASKS =====");
        List<Map<String, Object>> plans = config.fetchRecords(
                "SELECT c_id, c_name, u_id_patient FROM careplan WHERE status != 'archived'");
        List<Map<String, Object>> patients = config.fetchRecords(
                "SELECT u_fname, u_lname FROM users WHERE status = 'approved'");
        
        if (patients.isEmpty() || plans.isEmpty()) {
            System.out.println("No active careplans found.");
            System.out.println("Press any key to continue...");
            scan.nextLine();
            return;
        }

        
        for (int i = 0; i < plans.size(); i++)
            
            System.out.println((i + 1) + ". " + plans.get(i).get("c_name"));
        System.out.print("Enter number to manage careplan  :");
        int num = Integer.parseInt(scan.nextLine());
        if (num == 0) return;
        if (num > 0 && num <= plans.size()) {
            int cid = (int) plans.get(num - 1).get("c_id");
            manageSingleCareplan(cid);
        }
    }

    private static void manageSingleCareplan(int cid) {
        while (true) {
            System.out.println("\t1. Delete careplan");
            System.out.println("\t2. Edit careplan");
            System.out.println("\t3. Open careplan");
            System.out.println("\t4. Exit");
            System.out.println("Enter #: ");
            choice = scan.nextInt();

            if (choice == 1) {
                config.updateRecord("UPDATE careplan SET status='archived' WHERE c_id=?", cid);
                System.out.println("Careplan archived.");
            } else if (choice == 2) {
                System.out.println("Refer to edit careplan function.");
            } else if (choice == 3) {
                openCareplan(cid);
            } else if (choice == 4) return;
        }
    }

    private static void openCareplan(int cid) {
        List<Map<String, Object>> tasks = config.fetchRecords("SELECT * FROM tasks WHERE c_id=?", cid);
        List<Map<String, Object>> meds = config.fetchRecords("SELECT * FROM medicine WHERE c_id=?", cid);
        System.out.println("Tasks:");
        for (int i = 0; i < tasks.size(); i++)
            System.out.println((i + 1) + ". " + tasks.get(i).get("t_desc"));
        System.out.println("Medicine:");
        for (int i = 0; i < meds.size(); i++)
            System.out.println((i + 1) + ". " + meds.get(i).get("m_name"));

        System.out.print("1.Delete | 2.Edit | 3.Exit: ");
        String ch = scan.nextLine();
        if (ch.equals("1")) {
            System.out.println("Refer to archive logic.");
        } else if (ch.equals("2")) {
            System.out.println("Refer to edit functions.");
        } else if (ch.equals("3")) return;
    }

    // ===========================================================================
    // CASE 6: Edit admin account credentials
    private static void editAdminAccount(String adminId) {
        System.out.println("===== EDIT ACCOUNT CREDENTIALS =====");
        System.out.print("Enter new first name: ");
        String fname = scan.nextLine();
        System.out.print("Enter new last name: ");
        String lname = scan.nextLine();
        System.out.print("Enter new email: ");
        String email = scan.nextLine();
        System.out.print("Enter new password: ");
        String pass = scan.nextLine();

        config.updateRecord("UPDATE users SET u_fname=?, u_lname=?, email=?, password=? WHERE u_id=?",
                fname, lname, email, pass, adminId);
        System.out.println("Account updated successfully. Press any key to continue...");
        scan.nextLine();
    }

    // ===========================================================================
    // CASE 7: Deactivate account
    private static void deactivateAccount(String adminId) {
        System.out.print("Are you sure you want to deactivate your account? (y/n): ");
        String ans = scan.nextLine();
        if (ans.equalsIgnoreCase("y")) {
            config.updateRecord("UPDATE users SET status='closing' WHERE u_id=?", adminId);
            System.out.println("Account marked for deactivation.");
        }
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }

}
