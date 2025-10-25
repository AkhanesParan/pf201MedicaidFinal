//KONNIRE DALACE
package main;

import java.util.*;
import config.Config;
import main.TesterClass;
import java.util.Scanner;

public class Main {
//COLORED OUTPUTS
                public static final String ANSI_RESET = "\u001B[0m";
                public static final String ANSI_BLACK = "\u001B[30m";
                public static final String ANSI_RED = "\u001B[31m";
                public static final String ANSI_GREEN = "\u001B[32m";
                public static final String ANSI_YELLOW = "\u001B[33m";
                public static final String ANSI_BLUE = "\u001B[34m";
                public static final String ANSI_PURPLE = "\u001B[35m";
                public static final String ANSI_CYAN = "\u001B[36m";
                public static final String ANSI_WHITE = "\u001B[37m";
                public static Scanner scan = new Scanner(System.in);
                public static Config config = new Config();
                public static TesterClass functions = new TesterClass();
                public static int choice;
                public static String queries;
                public static String fname, lname, email, password, role, u_id, status, birthday, credentials;
                public static String mname, frequency, instruct, mfor;
                public static String sqlAdd,sqlDel,sqlUpd,sqlView, sqlFetch;
                
//INTRO
        /*done*/    public static void mein(){ 
                    
                //FIRST LOOK AT SYSTEM        
                config.connectDB();
                
                System.out.println(ANSI_BLUE + "== WELCOME TO MEDICARE ==" + ANSI_RESET);
                
                try {
                    Thread.sleep(2000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                int x;
                for (x = 0; x <= 3; x++){
                    System.out.println(" ");
                }  
                
                System.out.print("press any key to continue . . .");
                scan.nextLine();
                scan.nextLine();
                login();
            }    

//LOGIN AND REGISTER            
        /*done*/    public static void login() {
            // FIRST LOOK AT LOGIN PAGE
            while (true) {
                System.out.println(ANSI_BLUE + "LOG IN OR REGISTER\n" + ANSI_RESET);
                System.out.println("1. Log in to existing account");
                System.out.println("2. Register new account");
                System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                choice = scan.nextInt();

                // ERROR IN LOGIN    
                while ((choice != 1) && (choice != 2)) {
                    System.out.println(" ");
                    System.out.println("INVALID OPTION!");
                    System.out.println(" ");
                    System.out.println("1. Log in to existing account");
                    System.out.println("2. Register new account");
                    System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                    choice = scan.nextInt();
                }

                // CORRECT LOGIN OPTION
                switch (choice) {
                    case 1: // LOGIN TO ACCOUNT
                        System.out.println(ANSI_BLUE + "LOG IN\n" + ANSI_RESET);
                        System.out.print("Email: ");
                        email = scan.next();
                        System.out.print("Password: ");
                        password = scan.next();

                        while (true) { // CHECKS IF PASS AND USER IS CORRECT
                            sqlFetch = "SELECT * FROM users WHERE email = ? AND password = ?";
                            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, email, password);

                            if (result.isEmpty()) { // ACCOUNT DOESN'T EXIST
                                System.out.println("INVALID CREDENTIALS");
                                System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                                scan.nextLine(); // consumes leftover newline
                                scan.nextLine(); // wait for Enter
                                email = "";
                                password = "";
                                break; // go back to main login/register menu
                            } else { // ACCOUNT EXISTS
                                Map<String, Object> user = result.get(0);
                                status = user.get("u_status").toString();
                                role = user.get("role").toString();
                                u_id = user.get("u_id").toString();
                                
                                if (status.equalsIgnoreCase("pending")) { // ACCOUNT IS STILL PENDING
                                    System.out.println("ACCOUNT PENDING. CONTACT ADMIN FOR APPROVAL!");
                                    System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                                    scan.nextLine();
                                    scan.nextLine();
                                    email = "";
                                    password = "";
                                    status = "";
                                    role = "";
                                    break; // back to main login/register menu
                                } else { // ACCOUNT IS OPEN
                                    System.out.println("LOGIN SUCCESS!");
                                    System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                                    scan.nextLine();
                                    scan.nextLine();

                                    switch (role) {
                                        case "patient":
                                            patient_dash(u_id);
                                            break; // end of case patient
                                        case "caretaker":
                                            caretaker_dash(u_id);
                                            break; // end of case caretaker
                                        case "admin":
                                            admin_dash();
                                            break; // end of case admin
                                        case "superadmin":
                                            super_dash();
                                            break; // end of case superadmin
                                    }
                                    break; // end login loop 
                                }
                            }
                        }
                        break; // end of case 1 login

                    case 2: // CREATE NEW ACCOUNT
                        System.out.println(ANSI_BLUE + "REGISTER\n" + ANSI_RESET);

                        System.out.println("\t1. Patient\n\t2. Caretaker\n\t3. Admin");
                        System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                        choice = scan.nextInt();

                        if (choice == 1) {
                            role = "patient";
                            status = "pending";
                        } 
                        else if (choice == 2) {
                            role = "caretaker";
                            status = "pending";
                        } 
                        else if (choice == 3) {
                            role = "admin";
                            status = "pending";
                        }

                        // FILL IN INFORMATION 
                        System.out.print("First name: ");
                        fname = scan.next();
                        System.out.print("Surname: ");
                        lname = scan.next();
                        System.out.print("Date of birth [mm/dd/yyyy]: ");
                        birthday = scan.next();
                        System.out.print("Email: ");
                        email = scan.next();

                        while (true) { // CHECK REDUNDANT EMAIL ADDRESS
                            sqlFetch = "SELECT * FROM users WHERE email = ?";
                            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, email);
                            if (result.isEmpty()) {
                                break;
                            } else {
                                System.out.print("REDUNDANT EMAIL\nEnter other Email: ");
                                email = scan.next();
                            }
                        }

                        System.out.print("Password: ");
                        password = scan.next();

                        String part1 = "INSERT INTO users (u_fname, u_lname, email, password, role, u_status, dob, u_credentials) ";
                        String part2 = "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        sqlAdd = part1 + part2;
                        config.addRecord(sqlAdd, fname, lname, email, password, role, status, birthday, "-");

                        System.out.println("\nACCOUNT SUCCESSFULLY REGISTERED.");
                        System.out.println("ACCOUNT PENDING. . .");
                        break; // end of case 2 register account
                }

                System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                scan.nextLine();
                scan.nextLine(); // Wait for Enter before looping again
            }
        }


        
//PATIENT DASHBOARD UNFINISHED
            public static void patient_dash(String patient_id) {
            System.out.println(ANSI_BLUE + "PATIENT DASHBOARD " + patient_id + ANSI_RESET);

            // Fetch user information
            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
            java.util.List<java.util.Map<String, Object>> result = config.fetchRecords(sqlFetch, patient_id);

            if (result.isEmpty()) { // Prevent crash if no patient found
                System.out.println("Error: No patient found with ID " + patient_id);
                System.out.println("Returning to login menu...");
                return; // Exit dashboard safely
            }

            java.util.Map<String, Object> users = result.get(0);
            fname = users.get("u_fname").toString();
            lname = users.get("u_lname").toString();
            birthday = users.get("dob").toString();
            email = users.get("email").toString();

            // Fetch caretaker connection
            String sqlFetch1 = "SELECT * FROM p_to_c_connect WHERE patient_id = ?";
            java.util.List<java.util.Map<String, Object>> result1 = config.fetchRecords(sqlFetch1, patient_id);

            String care_fullname = "No assigned caregiver";
            if (!result1.isEmpty()) { // Only read if there’s an actual record
                java.util.Map<String, Object> p_to_c_connect = result1.get(0); 
                String care_fname = p_to_c_connect.get("care_fname").toString();
                String care_lname = p_to_c_connect.get("care_lname").toString();
                care_fullname = care_fname + " " + care_lname;
            }

            // Combine names
            String fullname = fname + " " + lname;
            
            // Display information
            System.out.println("Full Name: " + fullname);
            System.out.println("Birthdate: " + birthday);
            System.out.println("Email: " + email);
            System.out.println("Caregiver: " + care_fullname);

            System.out.println("\nDaily Task Reminder");
            System.out.println("Careplans:");
            // TODO: tasks
            // TODO: mark tasks as finished || date and time

            System.out.println("\nArchived Careplans:");
        }//missing careplan view

            
                
//CARETAKER DASHBOARD UNFINISHED
            public static void caretaker_dash(String caretaker_id){
                // Fetch user information
                    sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                    java.util.List<java.util.Map<String, Object>> result = config.fetchRecords(sqlFetch, caretaker_id);
                    if (result.isEmpty()) { 
                        System.out.println("Database error. No caretaker found with ID " + caretaker_id);
                        System.out.println("Returning to login menu...");
                        return;
                    }
                    Map<String, Object> users = result.get(0);
                    fname = users.get("u_fname").toString();
                    lname = users.get("u_lname").toString();
                    birthday = users.get("dob").toString();
                    email = users.get("email").toString();
                    credentials = users.get("u_credentials").toString();
                    String fullname = fname + " " + lname;
                //show account information    
                    System.out.println("Name: " + fullname);
                    System.out.println("birthdate: " + birthday);
                    System.out.println("Certification: " + credentials);
                    System.out.println("Email: " + email);
                    System.out.println("============================================");
                //menu
                System.out.println("1. Monitor patients");
                    //patient view> patent info> patient careplans> patient tasks
                System.out.println("2. Monitor individual careplans"); 
                    //careplan view> careplan info > tasks
                System.out.println("2. View archived cases");   
                    //patient view> patent info> patient careplans> patient tasks
                    //careplan view> careplan info > tasks
                System.out.println("3. edit account credentials");
                    //edit_users(caretaker_id);
                System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                choice = scan.nextInt();
                
                switch(choice){
    //MONITOR PATIENTS                
                    case 1:
                            System.out.println("PATIENTS\n");
                        // Step 1: Fetch patients under caretaker
                            sqlFetch = "SELECT u.u_id, u.u_fname, u.u_lname, u.email" +
                                "FROM p_to_c_connect p" +
                                "JOIN users u ON p.patient_id = u.u_id" +
                                "WHERE p.care_id = ?";
                            List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, caretaker_id);
                            if (!result1.isEmpty()) {
                            int x = 1;
                            // Step 2: Display all patients
                            for (Map user : result1) {
                                String fullName = user.get("u_fname") + " " + user.get("u_lname");
                                email = user.get("email").toString();
                            // list all the patients    
                                System.out.println("[" + x + "] " + fullName + " ( Email: " + email + ")");
                                x++;
                            }
                            System.out.print("\nSelect patient [#]: ");
                            int p_choice = scan.nextInt();
                            //Validate input 
                                if (p_choice >= 1 && p_choice <= result1.size()) {
                                    Map<String, Object> selected = result1.get(p_choice - 1);
                                    int patient_id = (int) selected.get("u_id");//id from the users table
                                    //menu for what to do with patient
                                        System.out.println("1. View patient careplans");
                                        System.out.println("2. Drop patient");
                                        System.out.println("3. Return to main dashboard");
                                        System.out.print("\nSelect patient [#]: ");
                                        choice = scan.nextInt();
                                        
                                        switch(choice){
                    //VIEW CAREPLANS                        
                                            case 1:
                                                break;
                    //DROP PATIENTS                            
                                            case 2:
                                                break;
                    //RETURN TO MENU                            
                                            case 3:
                                                break;
                                        }
                                } else {
                                    System.out.println("Invalid choice. Please try again.");
                                }
                            } else {
                                System.out.println("No patients found.");
                            }

                            break; //end of case 1 monitor patients
                            
                }
               
                    
                    
                    System.out.println("Careplans");
                    //create new careplan || add_careplan;
                    //manage careplans 
                        //select plan || c_id = ?
                            //edit existing careplans ||edit_careplan()
                                //edit name + edit description + edit all
                            //stop careplan ||SEND TO ARCHIVE
                System.out.println("Archived Cases"); 
                    //patients and their careplans
                System.out.println("Edit Account Information");
                        
                    
                
            } //empty
            
            

//ADIN DASHBOARD NO DATA
            public static void admin_dash(){
                System.out.println("Name: ");
                System.out.println("");      
                System.out.println("");
                System.out.println("");       
            } //empty
            

            
//SUPER ADMIN DASHBOARD NO DATA
            public static void super_dash(){
            } //empty
            
            
            
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
                
//USERS DONE
        /*done*/   public static void view_users(String role){
    //VIEW PATIENTS IN SYSTEM    
                System.out.println("PATIENTS\n");
                sqlFetch = "SELECT * FROM users WHERE role = ?";
                    List<Map<String, Object>> result = config.fetchRecords(sqlFetch, "patient");

                    if (!result.isEmpty()) {
                        for (Map user : result) {
                            String fullName = user.get("u_fname").toString() + " " + user.get("u_lname").toString();
                            email = (String) user.get("email");
                            System.out.println("Name: " + fullName);
                            System.out.println("Email: " + email);
                            System.out.println("---------------------------");
                        }
                    } else {
                        System.out.println("No patients found.");
                    }
    //VIEW CARETAKERS IN SYSTEM
                System.out.println("List of Caretakers:\n");
                sqlFetch = "SELECT * FROM users WHERE role = ?";
                    List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, "caregiver");

                    if (!result1.isEmpty()) {
                        for (Map user : result1) {
                            String fullName = user.get("u_fname").toString() + " " + user.get("u_lname").toString();
                            email = user.get("email").toString();
                            System.out.println("Name: " + fullName);
                            System.out.println("Email: " + email);
                            System.out.println("---------------------------");
                        }
                    } else {
                        System.out.println("No caretakers found.");
                    }    
    //VIEW ADMIN IN SYSTEM
                System.out.println("List of Admins:\n");
                sqlFetch = "SELECT * FROM users WHERE role = ?";
                    List<Map<String, Object>> result2 = config.fetchRecords(sqlFetch, "admin");

                    if (!result2.isEmpty()) {
                        for (Map user : result2) {
                            String fullName = user.get("u_fname") + " " + user.get("u_lname");
                            email = user.get("email").toString();
                            System.out.println("Name: " + fullName);
                            System.out.println("Email: " + email);
                            System.out.println("---------------------------");
                        }
                    } else {
                        System.out.println("No admins found.");
                    }
    //VIEW SUPER ADMIN IN SYSTEM
         if (role.equals("superadmin")) {
                    System.out.println("List of Super Admin:\n");
                    sqlFetch = "SELECT * FROM users WHERE role = ?";
                    List<Map<String, Object>> result3 = config.fetchRecords(sqlFetch, "superadmin");

                    if (!result3.isEmpty()) {
                        for (Map user : result3) {
                            String fullName = user.get("u_fname") + " " + user.get("u_lname");
                            email = (String) user.get("email");
                            System.out.println("Name: " + fullName);
                            System.out.println("Email: " + email);
                            System.out.println("---------------------------");
                        }
                    } else {
                        System.out.println("Database error. No super admin detected.");
                    }   
        }                 
    else if (status.equals("pending")) { 
                    System.out.println("Pending accounts:\n");
                    sqlFetch = "SELECT * FROM users WHERE status = ?";
                    List<Map<String, Object>> result4 = config.fetchRecords(sqlFetch, "pending");

                    if (!result4.isEmpty()) {
                        for (Map user : result4) {
                            String fullName = user.get("u_fname") + " " + user.get("u_lname");
                            email = (String) user.get("email");
                            role = (String) user.get("role");
                            System.out.println("Name: " + fullName);
                            System.out.println("Email: " + email);
                            System.out.println("Role: " + role);
                            System.out.println("---------------------------");
                        }
                    } else {
                        System.out.println("No pending accounts found.");
                    }
    }   
            } 
        
        
        /*done*/    public static void edit_users(String user_id) {

            while (true) {
                System.out.print("1. Edit Account Name");
                System.out.print("2. Edit Account Email");
                System.out.print("3. Edit Account Password");
                System.out.print("4. Edit Birthdate");
                System.out.println("5. Exit");
                System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                int choice = scan.nextInt();

                if (choice > 4 || choice < 1) { 
                    System.out.println("Invalid option"); 
                    System.out.print("press any key to continue . . .");
                    scan.nextLine();
                    scan.nextLine();
                    continue;
                } else {
                    switch (choice) {
                        case 1:
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, user_id);

                            if (!result.isEmpty()) {
                                Map<String, Object> user = result.get(0);
                                String fullname = user.get("u_fname").toString() + " " + user.get("u_lname").toString();
                                System.out.println("Current Account Name: " + fullname);
                            } else {
                                System.out.println("Database Error. User not found.");
                            }

                            System.out.println("Enter New First Name: ");
                            fname = scan.nextLine();
                            System.out.println("Enter New Last Name: ");
                            lname = scan.nextLine();

                            sqlUpd = "UPDATE students SET u_fname = ?, u_lname = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, fname, lname, user_id);
                            break; // end of case 1 name

                        case 2:
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, user_id);

                            if (!result1.isEmpty()) {
                                Map<String, Object> user = result1.get(0);
                                email = user.get("email").toString();
                                System.out.println("Current Email: " + email);
                            } else {
                                System.out.println("Database Error. User not found.");
                            }

                            System.out.println("Enter Email: ");
                            email = scan.nextLine();

                            sqlUpd = "UPDATE students SET email = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, email, user_id);
                            break; // end of case 2 email

                        case 3:
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> result2 = config.fetchRecords(sqlFetch, user_id);

                            if (!result2.isEmpty()) {
                                Map<String, Object> user = result2.get(0);
                                password = user.get("password").toString();
                                while (true) {
                                    System.out.println("Enter Current Password: ");
                                    String verify = scan.next();
                                    if(!verify.equals(password)){
                                        System.out.println("INCORRECT PASSWORD.");
                                    }
                                    else{
                                        break;
                                    }
                                }
                            } else {
                                System.out.println("Database Error. User not found.");
                            }

                            System.out.println("Enter New Password: ");
                            password = scan.nextLine();

                            sqlUpd = "UPDATE students SET password = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, password, user_id);
                            break; // end of case 3 password

                        case 4:
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> result3 = config.fetchRecords(sqlFetch, user_id);

                            if (!result3.isEmpty()) {
                                Map<String, Object> user = result3.get(0);
                                birthday = user.get("dob").toString();
                                System.out.println("Current Birthdate: " + birthday);
                            } else {
                                System.out.println("Database Error. User not found.");
                            }

                            System.out.println("Enter New Birthdate (MM-DD-YYYY): ");
                            birthday = scan.nextLine();

                            sqlUpd = "UPDATE students SET dob = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, birthday, user_id);
                            break; // end of case 4 birthday
                    }
                }
            }
}
        /*done*/    public static void delete_users(String user_id){
               sqlDel = "DELETE FROM students WHERE id = ?";
               config.deleteRecord(sqlDel, user_id);
            }  



//MEDICINE UNFINISHED  

        /*done*/    public static void add_medicine() {
            System.out.println("=== ADD NEW MEDICATION ===");

            System.out.print("Enter Medicine Name: ");
            String m_name = scan.nextLine();
            System.out.print("Enter Frequency: ");
            String m_frequency = scan.nextLine();
            System.out.print("Enter Instructions: ");
            String m_instruct = scan.nextLine();
            System.out.print("Enter Medication For: ");
            String m_for = scan.nextLine();

            sqlUpd = "INSERT INTO medication (m_name, m_frequency, m_instruct, m_for) VALUES (?, ?, ?, ?)";
            config.updateRecord(sqlUpd, m_name, m_frequency, m_instruct, m_for);

            System.out.println("Medication added successfully!");
            System.out.print("Press any key to continue . . .");
            scan.nextLine();
} // end of add_medicine

            public static void view_medicine() {
    
} // end of view_medicine

        /*done*/ public static void edit_medicine(String m_id) {

    while (true) {
        System.out.print("1. Edit Medicine Name");
        System.out.print("2. Edit Frequency");
        System.out.print("3. Edit Instructions");
        System.out.print("4. Edit 'For'");
        System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
        int choice = scan.nextInt();

        if (choice > 4 || choice < 1) { 
            System.out.println("Invalid option"); 
            System.out.print("press any key to continue . . .");
            scan.nextLine();
            scan.nextLine();
            continue;
        } else {
            switch (choice) {
                case 1://edit name
                    sqlFetch = "SELECT * FROM medication WHERE m_id = ?";
                    List<Map<String, Object>> result = config.fetchRecords(sqlFetch, m_id);

                    if (!result.isEmpty()) {
                        Map<String, Object> med = result.get(0);
                        mname = med.get("m_name").toString();
                        System.out.println("Current Medicine Name: " + mname);
                    } else {
                        System.out.println("Database Error. Medicine not found.");
                    }

                    System.out.println("Enter New Medicine Name: ");
                    mname = scan.nextLine();

                    sqlUpd = "UPDATE medication SET m_name = ? WHERE m_id = ?";
                    config.updateRecord(sqlUpd, mname, m_id);
                    break; // end of case 1 name

                case 2:
                    sqlFetch = "SELECT * FROM medication WHERE m_id = ?";
                    List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, m_id);

                    if (!result1.isEmpty()) {
                        Map<String, Object> med = result1.get(0);
                        frequency = med.get("m_frequency").toString();
                        System.out.println("Current Frequency: " + frequency);
                    } else {
                        System.out.println("Database Error. Medicine not found.");
                    }

                    System.out.println("Enter New Frequency: ");
                    frequency = scan.nextLine();

                    sqlUpd = "UPDATE medication SET m_frequency = ? WHERE m_id = ?";
                    config.updateRecord(sqlUpd, frequency, m_id);
                    break; // end of case 2 frequency

                case 3:
                    sqlFetch = "SELECT * FROM medication WHERE m_id = ?";
                    List<Map<String, Object>> result2 = config.fetchRecords(sqlFetch, m_id);

                    if (!result2.isEmpty()) {
                        Map<String, Object> med = result2.get(0);
                        instruct = med.get("m_instruct").toString();
                        System.out.println("Current Instructions: " + instruct);
                    } else {
                        System.out.println("Database Error. Medicine not found.");
                    }

                    System.out.println("Enter New Instructions: ");
                    instruct = scan.nextLine();

                    sqlUpd = "UPDATE medication SET m_instruct = ? WHERE m_id = ?";
                    config.updateRecord(sqlUpd, instruct, m_id);
                    break; // end of case 3 instructions

                case 4:
                    sqlFetch = "SELECT * FROM medication WHERE m_id = ?";
                    List<Map<String, Object>> result3 = config.fetchRecords(sqlFetch, m_id);

                    if (!result3.isEmpty()) {
                        Map<String, Object> med = result3.get(0);
                        mfor = med.get("m_for").toString();
                        System.out.println("Current 'For': " + mfor);
                    } else {
                        System.out.println("Database Error. Medicine not found.");
                    }

                    System.out.println("Enter New 'For' Description: ");
                    mfor = scan.nextLine();

                    sqlUpd = "UPDATE medication SET m_for = ? WHERE m_id = ?";
                    config.updateRecord(sqlUpd, mfor, m_id);
                    break; // end of case 4 'for'
            }
        }
    }
} // end of edit_medicine  

        /*done*/    public static void delete_medicine() {
    System.out.println("=== DELETE MEDICATION ===");

    System.out.print("Enter Medication ID to delete: ");
    String m_id = scan.nextLine();

    sqlFetch = "SELECT * FROM medication WHERE m_id = ?";
    List<Map<String, Object>> result = config.fetchRecords(sqlFetch, m_id);

    if (result.isEmpty()) {
        System.out.println("Medication not found.");
        System.out.print("Press any key to continue . . .");
        scan.nextLine();
        return;
    }

    Map<String, Object> med = result.get(0);
    System.out.println("Are you sure you want to delete this medication?");
    System.out.println("Name: " + med.get("m_name"));
    System.out.print("Type 'YES' to confirm: ");
    String confirm = scan.nextLine();

    if (confirm.equalsIgnoreCase("YES")) {
        sqlUpd = "DELETE FROM medication WHERE m_id = ?";
        config.updateRecord(sqlUpd, m_id);
        System.out.println("Medication deleted successfully!");
    } else {
        System.out.println("Deletion cancelled.");
    }

    System.out.print("Press any key to continue . . .");
    scan.nextLine();
} // end of delete_medicine




//CAREPLAM UNFINISHED

        public static void add_careplan() {
            System.out.println("=== ADD NEW CAREPLAN ===");

            System.out.print("Enter Patient User ID: ");
            String u_id_patient = scan.nextLine();
            System.out.print("Enter Caregiver User ID: ");
            String u_id_care = scan.nextLine();
            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            String start_date = scan.nextLine();
            System.out.print("Enter End Date (YYYY-MM-DD): ");
            String end_date = scan.nextLine();
            System.out.print("Enter Description: ");
            String description = scan.nextLine();
            System.out.print("Enter Status: ");
            String status = scan.nextLine();

            sqlUpd = "INSERT INTO careplan (u_id_patient, u_id_care, start_date, end_date, description, status) VALUES (?, ?, ?, ?, ?, ?)";
            config.updateRecord(sqlUpd, u_id_patient, u_id_care, start_date, end_date, description, status);

            System.out.println("Careplan added successfully!");
            System.out.print("Press any key to continue . . .");
            scan.nextLine();
        } // end of add_careplan


        public static void view_careplan(int u_id) {
            System.out.println("=== VIEW CAREPLANS ===");

            sqlFetch = "SELECT * FROM careplan WHERE u_id = ?";
            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, u_id);

            if (!result.isEmpty()) {
                for (Map cp : result) {
                    System.out.println("Title: " + cp.get("c_name"));
                    System.out.println("Start Date: " + cp.get("start_date"));
                    System.out.println("End Date: " + cp.get("end_date"));
                    System.out.println("Description: " + cp.get("description"));
                    System.out.println("Status: " + cp.get("status"));
                    System.out.println("-----------------------------");
                }
            } else {
                System.out.println("No careplans found in the database.");
            }

            System.out.print("Press any key to continue . . .");
            scan.nextLine();
        } // end of view_careplan


        public static void edit_careplan(String c_id) {
            while (true) {
                System.out.print("1. Edit Start Date");
                System.out.print("2. Edit End Date");
                System.out.print("3. Edit Description");
                System.out.print("4. Edit Status");
                System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                choice = scan.nextInt();

                if (choice > 4 || choice < 1) {
                    System.out.println("Invalid option");
                    System.out.print("press any key to continue . . .");
                    scan.nextLine();
                    scan.nextLine();
                    continue;
                } else {
                    switch (choice) {
                        case 1:
                            sqlFetch = "SELECT * FROM careplan WHERE c_id = ?";
                            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, c_id);

                            if (!result.isEmpty()) {
                                Map<String, Object> cp = result.get(0);
                                String start_date = cp.get("start_date").toString();
                                System.out.println("Current Start Date: " + start_date);
                            } else {
                                System.out.println("Database Error. Careplan not found.");
                            }

                            System.out.println("Enter New Start Date (YYYY-MM-DD): ");
                            String new_start = scan.nextLine();

                            sqlUpd = "UPDATE careplan SET start_date = ? WHERE c_id = ?";
                            config.updateRecord(sqlUpd, new_start, c_id);
                            break;

                        case 2:
                            sqlFetch = "SELECT * FROM careplan WHERE c_id = ?";
                            List<Map<String, Object>> result2 = config.fetchRecords(sqlFetch, c_id);

                            if (!result2.isEmpty()) {
                                Map<String, Object> cp = result2.get(0);
                                String end_date = cp.get("end_date").toString();
                                System.out.println("Current End Date: " + end_date);
                            } else {
                                System.out.println("Database Error. Careplan not found.");
                            }

                            System.out.println("Enter New End Date (YYYY-MM-DD): ");
                            String new_end = scan.nextLine();

                            sqlUpd = "UPDATE careplan SET end_date = ? WHERE c_id = ?";
                            config.updateRecord(sqlUpd, new_end, c_id);
                            break;

                        case 3:
                            sqlFetch = "SELECT * FROM careplan WHERE c_id = ?";
                            List<Map<String, Object>> result3 = config.fetchRecords(sqlFetch, c_id);

                            if (!result3.isEmpty()) {
                                Map<String, Object> cp = result3.get(0);
                                String desc = cp.get("description").toString();
                                System.out.println("Current Description: " + desc);
                            } else {
                                System.out.println("Database Error. Careplan not found.");
                            }

                            System.out.println("Enter New Description: ");
                            String new_desc = scan.nextLine();

                            sqlUpd = "UPDATE careplan SET description = ? WHERE c_id = ?";
                            config.updateRecord(sqlUpd, new_desc, c_id);
                            break;

                        case 4:
                            sqlFetch = "SELECT * FROM careplan WHERE c_id = ?";
                            List<Map<String, Object>> result4 = config.fetchRecords(sqlFetch, c_id);

                            if (!result4.isEmpty()) {
                                Map<String, Object> cp = result4.get(0);
                                status = cp.get("status").toString();
                                System.out.println("Current Status: " + status);
                            } else {
                                System.out.println("Database Error. Careplan not found.");
                            }

                            System.out.println("Enter New Status: ");
                            String new_status = scan.nextLine();

                            sqlUpd = "UPDATE careplan SET status = ? WHERE c_id = ?";
                            config.updateRecord(sqlUpd, new_status, c_id);
                            break;
                    }
                }
            }
        } // end of edit_careplan


        public static void delete_careplan() {
            System.out.println("=== DELETE CAREPLAN ===");

            System.out.print("Enter Careplan ID to delete: ");
            String c_id = scan.nextLine();

            sqlFetch = "SELECT * FROM careplan WHERE c_id = ?";
            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, c_id);

            if (result.isEmpty()) {
                System.out.println("Careplan not found.");
                System.out.print("Press any key to continue . . .");
                scan.nextLine();
                return;
            }

            Map<String, Object> cp = result.get(0);
            System.out.println("Are you sure you want to delete this careplan?");
            System.out.println("Description: " + cp.get("description"));
            System.out.print("Type 'YES' to confirm: ");
            String confirm = scan.nextLine();

            if (confirm.equalsIgnoreCase("YES")) {
                sqlUpd = "DELETE FROM careplan WHERE c_id = ?";
                config.updateRecord(sqlUpd, c_id);
                System.out.println("Careplan deleted successfully!");
            } else {
                System.out.println("Deletion cancelled.");
            }

            System.out.print("Press any key to continue . . .");
            scan.nextLine();
        } // end of delete_careplan


//TASKS UNFINISHED

        public static void add_tasks() {
            System.out.println("=== ADD NEW TASK ===");

            System.out.print("Enter Careplan ID: ");
            String c_id = scan.nextLine();
            System.out.print("Enter Task Type: ");
            String t_type = scan.nextLine();
            System.out.print("Enter Medication ID (optional): ");
            String m_id = scan.nextLine();
            System.out.print("Enter Task Description: ");
            String t_desk = scan.nextLine();
            System.out.print("Enter Task Time (HH:MM): ");
            String t_time = scan.nextLine();
            System.out.print("Enter Task Status: ");
            String t_status = scan.nextLine();

            sqlUpd = "INSERT INTO tasks (c_id, t_type, m_id, t_desk, t_time, t_status) VALUES (?, ?, ?, ?, ?, ?)";
            config.updateRecord(sqlUpd, c_id, t_type, m_id, t_desk, t_time, t_status);

            System.out.println("Task added successfully!");
            System.out.print("Press any key to continue . . .");
            scan.nextLine();
        } // end of add_tasks


        public static void view_tasks() {
            System.out.println("=== VIEW TASKS ===");

            sqlFetch = "SELECT * FROM tasks";
            List<Map<String, Object>> result = config.fetchRecords(sqlFetch);

            if (!result.isEmpty()) {
                for (Map t : result) {
                    System.out.println("Task ID: " + t.get("t_id"));
                    System.out.println("Careplan ID: " + t.get("c_id"));
                    System.out.println("Type: " + t.get("t_type"));
                    System.out.println("Medication ID: " + t.get("m_id"));
                    System.out.println("Description: " + t.get("t_desk"));
                    System.out.println("Time: " + t.get("t_time"));
                    System.out.println("Status: " + t.get("t_status"));
                    System.out.println("-----------------------------");
                }
            } else {
                System.out.println("No tasks found in the database.");
            }

            System.out.print("Press any key to continue . . .");
            scan.nextLine();
        } // end of view_tasks


        public static void edit_tasks(int t_id) {
            while (true) {
                System.out.print("1. Edit Type");
                System.out.print("2. Edit Description");
                System.out.print("3. Edit Time");
                System.out.print("4. Edit Status");
                System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                int choice = scan.nextInt();

                if (choice > 4 || choice < 1) {
                    System.out.println("Invalid option");
                    System.out.print("press any key to continue . . .");
                    scan.nextLine();
                    scan.nextLine();
                    continue;
                } else {
                    switch (choice) {
                        case 1:
                            sqlFetch = "SELECT * FROM tasks WHERE t_id = ?";
                            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, t_id);

                            if (!result.isEmpty()) {
                                Map<String, Object> t = result.get(0);
                                String type = t.get("t_type").toString();
                                System.out.println("Current Type: " + type);
                            } else {
                                System.out.println("Database Error. Task not found.");
                            }

                            System.out.println("Enter New Type: ");
                            String new_type = scan.nextLine();

                            sqlUpd = "UPDATE tasks SET t_type = ? WHERE t_id = ?";
                            config.updateRecord(sqlUpd, new_type, t_id);
                            break;

                        case 2:
                            sqlFetch = "SELECT * FROM tasks WHERE t_id = ?";
                            List<Map<String, Object>> result2 = config.fetchRecords(sqlFetch, t_id);

                            if (!result2.isEmpty()) {
                                Map<String, Object> t = result2.get(0);
                                String desk = t.get("t_desk").toString();
                                System.out.println("Current Description: " + desk);
                            } else {
                                System.out.println("Database Error. Task not found.");
                            }

                            System.out.println("Enter New Description: ");
                            String new_desk = scan.nextLine();

                            sqlUpd = "UPDATE tasks SET t_desk = ? WHERE t_id = ?";
                            config.updateRecord(sqlUpd, new_desk, t_id);
                            break;

                        case 3:
                            sqlFetch = "SELECT * FROM tasks WHERE t_id = ?";
                            List<Map<String, Object>> result3 = config.fetchRecords(sqlFetch, t_id);

                            if (!result3.isEmpty()) {
                                Map<String, Object> t = result3.get(0);
                                String time = t.get("t_time").toString();
                                System.out.println("Current Time: " + time);
                            } else {
                                System.out.println("Database Error. Task not found.");
                            }

                            System.out.println("Enter New Time (HH:MM): ");
                            String new_time = scan.nextLine();

                            sqlUpd = "UPDATE tasks SET t_time = ? WHERE t_id = ?";
                            config.updateRecord(sqlUpd, new_time, t_id);
                            break;

                        case 4:
                            sqlFetch = "SELECT * FROM tasks WHERE t_id = ?";
                            List<Map<String, Object>> result4 = config.fetchRecords(sqlFetch, t_id);

                            if (!result4.isEmpty()) {
                                Map<String, Object> t = result4.get(0);
                                String status = t.get("t_status").toString();
                                System.out.println("Current Status: " + status);
                            } else {
                                System.out.println("Database Error. Task not found.");
                            }

                            System.out.println("Enter New Status: ");
                            String new_status = scan.nextLine();

                            sqlUpd = "UPDATE tasks SET t_status = ? WHERE t_id = ?";
                            config.updateRecord(sqlUpd, new_status, t_id);
                            break;
                    }
                }
            }
        } // end of edit_tasks


        public static void delete_tasks() {
            System.out.println("=== DELETE TASK ===");

            System.out.print("Enter Task ID to delete: ");
            String t_id = scan.nextLine();

            sqlFetch = "SELECT * FROM tasks WHERE t_id = ?";
            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, t_id);

            if (result.isEmpty()) {
                System.out.println("Task not found.");
                System.out.print("Press any key to continue . . .");
                scan.nextLine();
                return;
            }

            Map<String, Object> t = result.get(0);
            System.out.println("Are you sure you want to delete this task?");
            System.out.println("Description: " + t.get("t_desk"));
            System.out.print("Type 'YES' to confirm: ");
            String confirm = scan.nextLine();

            if (confirm.equalsIgnoreCase("YES")) {
                sqlUpd = "DELETE FROM tasks WHERE t_id = ?";
                config.updateRecord(sqlUpd, t_id);
                System.out.println("Task deleted successfully!");
            } else {
                System.out.println("Deletion cancelled.");
            }

            System.out.print("Press any key to continue . . .");
            scan.nextLine();
        } // end of delete_tasks

                        
        }