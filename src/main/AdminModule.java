package main;

import java.util.*;
import config.Config;

public class AdminModule {
    //colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
     public static final String ANSI_WHITE = "\u001B[37m";
    //variables
    public static Scanner scan = new Scanner(System.in); 
    public static Config config = new Config();
    public static String sqlAdd,sqlDel,sqlUpd,sqlView, sqlFetch;
    public static int choice,u_id, t_id, m_id;
    // Entry point for the Admin Dashboard
    public static void adminDashboard(String adminId) {
        while (true) {
            space();
            System.out.println(ANSI_BLUE + "===== ADMIN DASHBOARD =====" + ANSI_RESET);
            System.out.println("\t1. View existing accounts");
            System.out.println("\t2. Unarchive data");
            System.out.println("\t3. Approve new accounts");
            System.out.println("\t4. Approve account deactivation");
            System.out.println("\t5. Manage caretaker-patient connection");
            System.out.println("\t6. Monitor careplans and tasks");
            System.out.println("\t7. Edit account credentials");
            System.out.println("\t8. Deactivate account");
            System.out.println("\t9. Exit");
            System.out.println("\t10. Close program");
            System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
            choice = scan.nextInt();

            switch (choice) {
                case 1: viewExistingAccounts(); break;
                case 2: unarchive(); break
                case 3: approveNewAccounts(); break;
                case 4: approveDeactivation(); break;
                case 5: manageConnections(); break;
                case 6: monitorCareplansAndTasks(adminId); break;
                case 7: editAdminAccount(adminId); break;
                case 8: deactivateAccount(adminId); break;
                case 9: return; // go back to login
                case 10: System.exit(0);
                default: System.out.println("Invalid input. Try again.");
            }
        }
    }

    // ===========================================================================
    // CASE 1: View existing accounts
    private static void viewExistingAccounts() {
        while (true) {
            space();
            System.out.println(ANSI_BLUE + "===== VIEW EXISTING ACCOUNTS =====" + ANSI_RESET);
            System.out.println("\t1. View Patients");
            System.out.println("\t2. View Caretakers");
            System.out.println("\t3. Exit");
            System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
            choice = scan.nextInt();

            if (choice == 1) showAccountsByRole("Patient");
            else if (choice == 2) showAccountsByRole("Caretaker");
            else if (choice == 3) return;
            else System.out.println("Invalid choice. Try again.");
        }
    }

    private static void showAccountsByRole(String role) {
        
        sqlFetch = "SELECT u_id, u_fname, u_lname, email, status FROM users WHERE role = ? AND status != 'archived' AND status != 'pending'";        
        List<Map<String, Object>> accounts = config.fetchRecords(sqlFetch, role);
        if (accounts.isEmpty()) {
            System.out.println("No " + role + " accounts found.");
            presskey();
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            Map<String, Object> acc = accounts.get(i);
            String status = acc.get("status").toString();
            if((status.equals("pending") || status.equals("closing")) || status.equals("archived")){
                continue;
            }
            System.out.println((i + 1) + ". " + acc.get("u_fname") + " " + acc.get("u_lname") + " - " + acc.get("email"));
        }   

        System.out.print(ANSI_YELLOW +  "Enter # [0 to exit]: " + ANSI_RESET);
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
        System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
        System.out.println("Name: " + acc.get("u_fname") + " " + acc.get("u_lname"));
        System.out.println("Email: " + acc.get("email"));
        System.out.println("Role: " + acc.get("role"));
        System.out.println("Status: " + acc.get("status"));
        System.out.println("Birthday: " + acc.get("birthday"));
        System.out.println("Credentials: " + acc.get("credentials"));
        System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
        System.out.println("Delete account?");
        System.out.println("\t1. Yes\t2. No");
        System.out.println("Enter #: ");
        choice = scan.nextInt();    
        
        if(choice == 1){
            sqlUpd = "UPDATE  SET users SET status = ? WHERE u_id = ?";            
        }
        else return;
    }
    // ===========================================================================
    // CASE 2: Approve new accounts
    
    private static void unarchive(){
        space();
        System.out.println(ANSI_BLUE + "===== UNARCHIVE DATA =====" + ANSI_RESET);
        System.out.println("\t1. Archived patient accounts");
        System.out.println("\t2. Archived careplans");
        System.out.println("\t3. Archived tasks");
        System.out.println("\t4. Archived medicines");
        System.out.println("\t5.Exit");
        System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
        System.out.print("Enter #: ");
        choice = scan.nextInt();
        unarchiveData(choice);
    }            
    
    private static void unarchiveData(int choice){    
        switch(choice){
            case 1: 
                sqlView = "SELECT u_id, u_fname,u_lname, role ,email FROM users WHERE status = 'archived'";
                String[] columnHeaders = {"ID","First name", "Last name","Role", "email"};
                String[] columnNames = {"u_id", "u_fname", "u_lname", "role", "email"};
                config.viewRecords(sqlView, columnHeaders, columnNames);
                
                System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
                System.out.print("Enter ID: ");
                choice = scan.nextInt();
                
                List<Map<String, Object>> getarch = config.fetchRecords("SELECT * FROM user WHERE u_id = ?", choice);                              
                if(getarch.isEmpty()){
                    System.out.println("Invalid ID");
                } else{
                    config.updateRecord("UPDATE users SET status = 'approved' WHERE u_id = ?", choice);
                }
                break;
            case 2:
                sqlView = "SELECT c_id, c_title, u_id_patient FROM careplan WHERE status = 'archived'";
                String[] columnHeaderscare = {"ID","Title", "Patient"};
                String[] columnNamescare = {"c_id", "c_title", "u_id_patient"};
                config.viewRecords(sqlView, columnHeaderscare, columnNamescare);
                System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
                System.out.println("Enter ID: ");              
                choice = scan.nextInt();
                List<Map<String, Object>> getarchcare =config.fetchRecords("SELECT * FROM careplan WHERE c_id = ?", choice);                              
                if(getarchcare.isEmpty()){
                    System.out.println("Invalid ID");
                } else{
                    config.updateRecord(
                            "UPDATE caretaker SET status = 'ongoing' WHERE c_id = ?", choice);
                }
                break;
            case 3:    
                sqlView = "SELECT u_fname,u_lname,email FROM users WHERE status = 'archived'";
                String[] columnHeaderstask = {"ID","Title"};
                String[] columnNamestask = {"t_id", "t_type"};
                config.viewRecords(sqlView, columnHeaderstask, columnNamestask);
                
                System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
                System.out.println("Enter ID: ");
                choice = scan.nextInt();
                
                List<Map<String, Object>> getarchtask =config.fetchRecords("SELECT * FROM task WHERE t_id = ?", choice);                              
                if(getarchtask.isEmpty()){
                    System.out.println("Invalid ID");
                } else{
                    config.updateRecord(
                            "UPDATE tasks SET t_status = 'pending' WHERE c_id = ?", choice);
                }
                break;
            case 4:
                sqlView = "SELECT u_fname,u_lname,email FROM users WHERE status = 'archived'";
                String[] columnHeadersmed = {"ID","Name", "For"};
                String[] columnNamesmed = {"m_id", "m_name", "m_for"};
                config.viewRecords(sqlView, columnHeadersmed, columnNamesmed);
                
                System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
                System.out.println("Enter ID: ");
                choice = scan.nextInt();
                
                List<Map<String, Object>> getarchmed =config.fetchRecords("SELECT * FROM medicine WHERE m_id = ?", choice);                              
                if(getarchmed.isEmpty()){
                    System.out.println("Invalid ID");
                } else{
                    config.updateRecord(
                            "UPDATE medicine SET status = ''pending' WHERE m_id = ?", choice);
                }
                break;
            case 5:
                return;    
        }        
    }
    // ===========================================================================
    // CASE 3: Approve new accounts
    private static void approveNewAccounts() {
        while(true){
        space();
            System.out.println(ANSI_BLUE + "===== APPROVE ACCOUNTS =====" + ANSI_RESET);
        sqlFetch ="SELECT u_id, u_fname, u_lname, email FROM users WHERE status = 'pending'";
        List<Map<String, Object>> pending = config.fetchRecords(sqlFetch);

        if (pending.isEmpty()) {
            System.out.println("No accounts pending approval.");
            presskey();
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
            System.out.println("\t1. Approve\n\t2. Decline\n\t0.exit");
            System.out.print(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
            choice = scan.nextInt();
            if (choice == 0) return;
            else if (choice == 1)
                config.updateRecord("UPDATE users SET status='approved' WHERE u_id=?", acc.get("u_id"));
            else
                config.updateRecord("DELETE FROM users WHERE u_id=?", acc.get("u_id"));
            presskey();           
        }
    }

    // ===========================================================================
    // CASE 4: Approve account deactivation
    private static void approveDeactivation() {
        while (true) {
            space();
            System.out.println(ANSI_BLUE + "===== APPROVE DEACTIVATION =====" + ANSI_RESET);
            List<Map<String, Object>> closing = config.fetchRecords(
                "SELECT u_id, u_fname, u_lname, email FROM users WHERE status='closing'");
            if (closing.isEmpty()) {
                System.out.println("No accounts pending for deletion.");
                presskey();
                return;
            }

            
             //view all pending accounts
            System.out.println("Pending accounts:");
            Map<String, Object> acc = closing.get(0);
            for (int i = 0; i < closing.size(); i++) {
                System.out.println( "\t" + acc.get("u_fname") + " " + acc.get("u_lname") + " - " + acc.get("email"));
            }            
            
            System.out.println("\n============================================");
            System.out.println("Pending account: " + acc.get("u_fname") + " " + acc.get("u_lname") + " - " + acc.get("email"));
            System.out.println("\t1. Approve\n\t2. Decline\n\t0.exit");
            System.out.print(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
            choice = scan.nextInt();
            if (choice == 0) return;
            if (choice == 1)
                config.updateRecord("UPDATE users SET status='archived' WHERE u_id=?", acc.get("u_id"));
            else if (choice == 2)
                config.updateRecord("UPDATE users SET status='approved' WHERE u_id=?", acc.get("u_id"));
        }
    }

    // ===========================================================================
    // CASE 5: Manage caretaker-patient connection
    private static void manageConnections() {
        while(true){
            space();
            System.out.println(ANSI_BLUE + "===== MANAGE CARETAKER-PATIENT CONNECTION =====" + ANSI_RESET);
            System.out.println("\t1. Assign connection");
            System.out.println("\t2. Approve disconnection request");
            System.out.println("\t3. Exit");  
            System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
            choice = scan.nextInt();

            switch (choice) {
                case 1: assignConnection(); break;
                case 2: approveDisconnection(); break;
                case 3: return;
                default: System.out.println("Invalid option.");
            }
        }    
    }

    private static void assignConnection() {
        List<Map<String, Object>> patients = config.fetchRecords(
                "SELECT u_id, u_fname, u_lname FROM users WHERE role='Patient' AND status='approved'");
        List<Map<String, Object>> carers = config.fetchRecords(
                "SELECT u_id, u_fname, u_lname FROM users WHERE role='Caretaker' AND status='approved'");

        if (patients.isEmpty() || carers.isEmpty()) {
            System.out.println("No available patients or caretakers.");
            presskey();
            return;
        }
        //loop through the patients
        for (int i = 0; i < patients.size(); i++)
            System.out.println((i + 1) + ". " + patients.get(i).get("u_fname") + " " + patients.get(i).get("u_lname"));
        System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
        System.out.print("Select Patient:");
        int p = scan.nextInt();
        int patientId = (int) patients.get(p - 1).get("u_id");

        
        for (int i = 0; i < carers.size(); i++)
            System.out.println((i + 1) + ". " + carers.get(i).get("u_fname") + " " + carers.get(i).get("u_lname"));
        System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
        System.out.print("Select Caretaker:");
        int c = scan.nextInt();
        int careId = (int) carers.get(c - 1).get("u_id");

        List<Map<String, Object>> existing = config.fetchRecords("SELECT * FROM p_to_c_connect WHERE patient_id=?", patientId);
        if (existing.isEmpty())
            config.updateRecord("INSERT INTO p_to_c_connect(patient_id, care_id, messages) VALUES(?, ?, '-')", patientId, careId);
        else
            config.updateRecord("UPDATE p_to_c_connect SET care_id=? WHERE patient_id=?", careId, patientId);

        presskey();
    }

    private static void approveDisconnection() {

    List<Map<String, Object>> closing = config.fetchRecords(
        "SELECT connect_id, patient_id, care_id FROM p_to_c_connect WHERE messages='100'"
    );

    if (closing.isEmpty()) {
        System.out.println("No pending disconnection requests.");
        presskey();
        return;
    }

    System.out.println(ANSI_BLUE + "===== APPROVE DISCONNECTION =====");

    for (int i = 0; i < closing.size(); i++) {
        Map<String, Object> row = closing.get(i);
        System.out.println((i + 1) + ". Patient: " + row.get("patient_id") +
                           " | Caregiver: " + row.get("care_id"));
    }

    System.out.print("\nSelect Connection (0 to exit): ");
    int sel = scan.nextInt();

    if (sel == 0) return;

    if (sel < 1 || sel > closing.size()) {
        System.out.println("Invalid selection.");
        presskey();
        return;
    }

    Map<String, Object> selected = closing.get(sel - 1);
    int connectId = (int) selected.get("connect_id");

    System.out.println("\n1. Approve");
    System.out.println("2. Decline");
    System.out.println("0. Exit");
    System.out.print("Enter #: ");
    int choice = scan.nextInt();

    if (choice == 0) return;

    if (choice == 1) {
        config.updateRecord(
            "DELETE FROM p_to_c_connect WHERE connect_id=?",
            connectId
        );
        System.out.println("Disconnection approved.");
    }

    else if (choice == 2) {
        config.updateRecord(
            "UPDATE p_to_c_connect SET messages='-' WHERE connect_id=?",
            connectId
        );
        System.out.println("Disconnection request declined.");
    }

    else {
        System.out.println("Invalid input.");
    }

    presskey();
}

    // ===========================================================================
    // CASE 6: Monitor careplans and tasks
    private static void monitorCareplansAndTasks(String u_id) {
        space();
        System.out.println(ANSI_BLUE + "===== MONITOR CAREPLANS AND TASKS =====" + ANSI_RESET);
        List<Map<String, Object>> plans = config.fetchRecords(
                "SELECT c_id, c_title, u_id_patient FROM careplan WHERE status != 'archived'");
        List<Map<String, Object>> patients = config.fetchRecords(
                "SELECT u_fname, u_lname FROM users WHERE status = 'approved'");
        
        if (patients.isEmpty() || plans.isEmpty()) {
            System.out.println("No active careplans found.");
            presskey();
            return;
        }

        int i = 0;
        for (Map p : plans) {
            System.out.println("\t" + (i + 1) + ". " + p.get("c_title"));
            i++;
        }
        System.out.println(ANSI_BLUE + "\n============================================" + ANSI_RESET);
        System.out.print(ANSI_YELLOW + "Enter #:" + ANSI_RESET);
        Map<String, Object> p = plans.get(0);
            int num = scan.nextInt();
            if (num == 0) return;
            if (num > 0 && num <= p.size()) {
                int cid = (int) p.get("c_id");
                cid = cid-1;
                manageSingleCareplan(cid, u_id);
            }
    }

    private static void manageSingleCareplan(int cid, String u_id) {
        while (true) {
            System.out.println("\t1. Delete careplan");
            System.out.println("\t2. Edit careplan");
            System.out.println("\t3. Open careplan");
            System.out.println("\t4. Exit");
            System.out.println(ANSI_BLUE + "\n============================================" + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
            choice = scan.nextInt();

            if (choice == 1) {
                System.out.println("Delete Careplan?");    
                System.out.println("\t1. Yes\t2. No ");  
                choice = scan.nextInt();
                if(choice == 1){
                    config.updateRecord("UPDATE careplan SET status='archived' WHERE c_id=?", cid);
                    System.out.println("Careplan archived.");
                    return;
                } 
                else if(choice == 2){
                    continue;
                }
                else{
                    System.out.println("Invalid option");
                    presskey();
                }
            } else if (choice == 2) {
                edit_careplan(cid, u_id);
            } else if (choice == 3) {
                openCareplan(cid);
            } else if (choice == 4) return;
        }
    }

    private static void openCareplan(int cid) {
        while(true){        
            List<Map<String, Object>> tasks = config.fetchRecords("SELECT * FROM tasks WHERE c_id=?", cid);
            List<Map<String, Object>> meds = config.fetchRecords("SELECT * FROM medicine WHERE c_id=?", cid);
            System.out.println("Tasks:");
            for (int i = 0; i < tasks.size(); i++)
                System.out.println((i + 1) + ". " + tasks.get(i).get("t_desc"));
            System.out.println("Medicine:");
            for (int i = 0; i < meds.size(); i++)
                System.out.println((i + 1) + ". " + meds.get(i).get("m_name"));

            System.out.print("\t1.Delete");
            System.out.println("\t2. Edit");
            System.out.println("\t3. Exit");
            System.out.println(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
            choice = scan.nextInt();
            if (choice == 1) {//DELETE
                System.out.println("\t1. Delete task"); 
                System.out.println("\t2. Delete medicine");
                System.out.println("Exit");
                System.out.println(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
                choice = scan.nextInt();
                    if(choice == 1){
                        int t_id = select_task(cid);
                        System.out.println("Delete task?");
                        System.out.println("\t1.Yes\t2.No");
                        System.out.println(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
                        choice = scan.nextInt();
                        if(choice == 1) config.updateRecord(
                                "UPDATE task SET t_status = 'archived' WHERE t_id = ?", t_id);
                    }
                    else if(choice == 2){
                        int m_id = select_medicine(cid);
                        System.out.println("Delete medicine?");
                        System.out.println("\t1.Yes\t2.No");
                        System.out.println(ANSI_YELLOW + "Enter #: " + ANSI_RESET);
                        choice = scan.nextInt();
                        if(choice == 1) config.updateRecord(
                                "UPDATE medicine SET status = 'archived' WHERE m_id = ?", m_id);
                    }
                    else{
                        continue;
                    }
            } 
            else if (choice == 2) {//EDIT
                
            } 
            else if (choice == 3) return;//EXIT            
        }    
    }

    public static int select_task(int c_id){       
        sqlFetch = "SELECT * FROM tasks WHERE c_id=?";
                List<Map<String, Object>> result = config.fetchRecords(sqlFetch, c_id);
                int x = 1;
                    if (!result.isEmpty()) {
                        for (Map cp : result) {
                            x++;
                        }
                    }
                if(choice > x || choice <1) {
                    System.out.println(ANSI_RED + "Invalid option." + ANSI_RESET);
                    presskey();
                }   
                int finder;
                java.util.Map<String, Object> select = result.get(0);
                for (finder = 1; finder == choice; finder++){
                   t_id = (int) select.get("t_id");
                    
                }
                return(t_id);
    } 
    
    public static int select_medicine(int c_id){       
        sqlFetch = "SELECT * FROM medicine WHERE c_id=?";
                List<Map<String, Object>> result = config.fetchRecords(sqlFetch, c_id);
                int x = 1;
                    if (!result.isEmpty()) {
                        for (Map cp : result) {
                            x++;
                        }
                    }
                if(choice > x || choice <1) {
                    System.out.println(ANSI_RED + "Invalid option" + ANSI_RESET);
                    presskey();
                }   
                int finder;
                java.util.Map<String, Object> select = result.get(0);
                for (finder = 1; finder == choice; finder++){
                   m_id = (int) select.get("m_id");
                    
                }
                return(m_id);
    }
    
    public static void edit_careplan (int c_id, String u_id){
                while(true){
                    System.out.println("1. Edit title");
                    System.out.println("2. Edit Start date");
                    System.out.println("3. Edit End date");
                    System.out.println("4. Edit description");
                    System.out.println("5. Exit");
                    System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                    choice = scan.nextInt();
                    switch(choice){
                        case 1:
                            System.out.println("Enter new title: ");
                            String title = scan.next();
                            sqlUpd = "UPDATE careplan SET c_title = ? WHERE c_id = ?";
                            config.updateRecord(sqlUpd, title, c_id);
                            System.out.println("Title updated sucessfully");
                            break;//end of case 1 edit title
                        case 2:
                            System.out.println("Enter new start date: ");
                            String sdate = scan.next();
                            sqlUpd = "UPDATE careplan SET start_date = ? WHERE c_id = ?";
                            config.updateRecord(sqlUpd, sdate, c_id);
                            System.out.println("Date updated sucessfully");
                            break;//end of case 1 edit date start
                        case 3:
                            System.out.println("Enter new end date: ");
                            String edate = scan.next();
                            sqlUpd = "UPDATE careplan SET end_date = ? WHERE c_id = ?";
                            config.updateRecord(sqlUpd, edate, c_id);
                            System.out.println("Date updated sucessfully");
                            break;//end of case 1 edit date end
                        case 4:
                            System.out.println("Enter new description: ");
                            String desc = scan.next();
                            sqlUpd = "UPDATE careplan SET description = ? WHERE c_id = ?";
                            config.updateRecord(sqlUpd, desc, c_id);
                            System.out.println("Description updated sucessfully");
                            break;//end of case 1 edit description
                        case 5:
                            adminDashboard(u_id);
                            break;//end of case 1 exit 
                        default:
                            System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                            break;
                    }
                }
            }
    
    public static void edit_task(String patient_id, String u_id, int c_id){
                int task_id;
                int medicine_id;
                
                while(true){                                 
                    System.out.println("\t1. Edit task");
                    System.out.println("\t2. Edit medicine");
                    System.out.println("\t3. Exit");
                    System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
                    System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                    choice = scan.nextInt();
                    
                    switch(choice){
                        case 1:
                            while(true){
                            Main.view_tasks (c_id, u_id);
                            
                            System.out.println("\n============================================");
                                System.out.print("\nSelect Task [#]: ");
                                int option = scan.nextInt();

                                sqlFetch = "SELECT * FROM tasks WHERE c_id = ?";
                                List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, c_id);
                                if (option >= 1 && option <= result1.size()) {
                                    Map<String, Object> selected = result1.get(option - 1);
                                    task_id = (int) selected.get("t_id");
                                    break;
                                }
                                else {
                                    System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                                }
                                    
                            }
                                System.out.println("\t1. Edit title");
                                System.out.println("\t2. Edit description");
                                System.out.println("\t3. Edit time");
                                System.out.println("\t4. Edit status");
                                System.out.println("\t5. Exit");
                                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                                choice = scan.nextInt();

                                        switch(choice){
                                            case 1://edit title
                                                System.out.println("Enter new title: ");
                                                String title = scan.next();
                                                sqlUpd = "UPDATE tasks SET t_type = ? WHERE t_id = ?";
                                                config.updateRecord(sqlUpd, title, task_id);
                                                System.out.println("Title updated successfully!");
                                                presskey();
                                                break;
                                            case 2://edit description
                                                System.out.println("Enter new description: ");
                                                String desc = scan.next();
                                                sqlUpd = "UPDATE tasks SET t_desk = ? WHERE t_id = ?";
                                                config.updateRecord(sqlUpd, desc, task_id);
                                                System.out.println("Description updated successfully!");
                                                presskey();
                                                break;
                                            case 3://edit time
                                                System.out.println("Enter new time: ");
                                                String time = scan.next();
                                                sqlUpd = "UPDATE tasks SET t_time = ? WHERE t_id = ?";
                                                config.updateRecord(sqlUpd, time, task_id);
                                                System.out.println("Time updated successfully!");
                                                presskey();
                                                break;
                                            case 4://edit status
                                                System.out.println("Enter new status: ");
                                                String status = scan.next();
                                                sqlUpd = "UPDATE tasks SET t_status = ? WHERE t_id = ?";
                                                config.updateRecord(sqlUpd, status, task_id);
                                                System.out.println("Status updated successfully!");
                                                presskey();
                                                break;
                                            case 5:
                                                return;
                                        }                                   
                            break;//end of case 1 edit tasks
                        case 2:
                            while(true){
                            Main.view_medicine (c_id, u_id);
                            
                            System.out.println("\n============================================");
                                
                                System.out.print("\nSelect Medicine [#]: ");
                                int option = scan.nextInt();

                                sqlFetch = "SELECT * FROM medicine WHERE c_id = ?";
                                List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, c_id);
                                if (option >= 1 && option <= result1.size()) {
                                    Map<String, Object> selected = result1.get(option - 1);
                                    medicine_id = (int) selected.get("m_id");
                                    break;
                                }
                                else {
                                    System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                                }
                                    
                            }
                                System.out.println("\t1. Edit Name");
                                System.out.println("\t2. Edit Frequency");
                                System.out.println("\t3. Edit Instruction");
                                System.out.println("\t4. Edit What For");
                                System.out.println("\t5. Exit");
                                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                                choice = scan.nextInt();

                                        switch(choice){
                                            case 1://edit name
                                                System.out.println("Enter new Name: ");
                                                String name = scan.next();
                                                sqlUpd = "UPDATE medicine SET m_name = ? WHERE m_id = ?";
                                                config.updateRecord(sqlUpd, name, medicine_id);
                                                System.out.println("Name updated successfully!");
                                                presskey();
                                                break;
                                            case 2://edit frequency
                                                System.out.println("Enter new Frequency: ");
                                                String freq = scan.next();
                                                sqlUpd = "UPDATE medicine SET m_frequency = ? WHERE m_id = ?";
                                                config.updateRecord(sqlUpd, freq, medicine_id);
                                                System.out.println("Frequency updated successfully!");
                                                presskey();
                                                break;
                                            case 3://edit instruction
                                                System.out.println("Enter new Instruction: ");
                                                String instruct = scan.next();
                                                sqlUpd = "UPDATE medicine SET m_instruct = ? WHERE m_id = ?";
                                                config.updateRecord(sqlUpd, instruct, medicine_id);
                                                System.out.println("Instruction updated successfully!");
                                                presskey();
                                                break;
                                            case 4://edit what for
                                                System.out.println("Enter new medicine Usage: ");
                                                String whatfor = scan.next();
                                                sqlUpd = "UPDATE medicine SET m_for = ? WHERE m_id = ?";
                                                config.updateRecord(sqlUpd, whatfor, medicine_id);
                                                System.out.println("Usage updated successfully!");
                                                presskey();
                                                break;
                                            case 5:
                                                 return;
                                                
                                        }
                            break;//end of case 2 edit medicine                            
                        case 3:
                            break;
                            
                        default:
                            System.out.println("INVALID OPTION.");
                            presskey();
                            break;

                    }
                    
                }
            }
    // ===========================================================================
    // CASE 7: Edit admin account credentials
    private static void editAdminAccount(String adminId) {
        while(true){
        System.out.println(ANSI_BLUE + "===== EDIT ACCOUNT CREDENTIALS =====" + ANSI_RESET);
        System.out.println("\t1. Edit name");
        System.out.println("\t2. Edit email");
        System.out.println("\t3. Edit password");
        System.out.println("\t4. Exit");
        System.out.println(ANSI_BLUE + "============================================" + ANSI_RESET);
        System.out.println("Enter #: ");
        choice = scan.nextInt();
            switch(choice){
                case 1:
                    System.out.print("Enter new first name: ");
                    String fname = scan.nextLine();
                    System.out.print("Enter new last name: ");
                    String lname = scan.nextLine();
                    config.updateRecord("UPDATE users SET u_fname=?, u_lname=? WHERE u_id=?",
                    fname, lname, adminId);
                    break;
                case 2:
                    System.out.print("Enter new email: ");
                    String email = scan.nextLine();
                    config.updateRecord("UPDATE users SET email=? WHERE u_id=?",
                    email, adminId);
                    break;
                case 3:
                    System.out.print("Enter new password: ");
                    String password = scan.nextLine();
                    config.updateRecord("UPDATE users SET password=? WHERE u_id=?",
                    password, adminId);
                    break;
                case 4:
                    return;
            }
        }
    }

    // ===========================================================================
    // CASE 8: Deactivate account
    private static void deactivateAccount(String adminId) {
        System.out.print("Are you sure you want to deactivate your account? (y/n): ");
        String ans = scan.nextLine();
        if (ans.equalsIgnoreCase("y")) {
            config.updateRecord("DELETE FROM users WHERE u_id=?", adminId);
            System.out.println("Account has been deleted. Logging out now will mean you wont be able to log in the next time.");
        }
        presskey();
    }

    
    //RANDOM UTILITIES
            public static void presskey(){
                System.out.print(ANSI_GREEN + "Press any key to continue . . ." + ANSI_RESET);
                scan.nextLine();
                scan.nextLine();
            }
            
            public static void space(){
                int x;  
                for(x = 1 ; x <= 40; x++){
                    System.out.println(" \n");
                }
            }
            
}
