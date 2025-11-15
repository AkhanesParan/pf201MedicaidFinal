package main;

import java.util.*;
import config.Config;



//headers blue
//choice yellow
//error red
//continue green

public class Main {
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
                
                public static int choice;
                public static String queries; 
                public static String u_id, fname, lname, email, password, role, status, birthday, credentials;
                public static String care_fname, care_lname, care_id;
                public static int c_id;
                public static String patient_id;
                public static String sqlAdd,sqlDel,sqlUpd,sqlView, sqlFetch;
                public static String careConn;
//INTRO
            public static void main (String[] args){ //===START MAIN FUNCTION
                    
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
    //LOGIN MENU
                while(true) {      
                System.out.println(ANSI_BLUE + "===== LOG IN OR REGISTER =====\n" + ANSI_RESET);
                System.out.println("\t1. Log in to existing account");
                System.out.println("\t2. Register new account");
                System.out.println("============================================");
                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                choice = scan.nextInt();
    // ERROR IN LOGIN    
                switch(choice){
                            case 1:
                                login();
                                break;
                            case 2:
                                register();
                                break;
                            default:
                                System.out.println(ANSI_RED + "INVALID OPTION." + ANSI_RESET);
                                System.out.print(ANSI_GREEN + "press any key to continue . . ." + ANSI_RESET);
                                scan.nextLine();
                                scan.nextLine();
                                break;
                        }
                }            
                    }   //end off main function

//LOGIN AND REGISTER            
            public static void login() {
    while (true) {
        // ENTER CREDENTIALS
        System.out.println(ANSI_BLUE + "\n===== LOG IN =====\n" + ANSI_RESET);
        System.out.print(ANSI_YELLOW + "Email: " + ANSI_RESET);
        email = scan.next();
        System.out.print(ANSI_YELLOW + "Password: " + ANSI_RESET);
        password = scan.next();
        System.out.println("============================================");

        // CHECK IF ACCOUNT EXISTS
        String hashed = config.hashPassword(password);
        sqlFetch = "SELECT * FROM users WHERE email = ? AND password = ?";
        List<Map<String, Object>> result = config.fetchRecords(sqlFetch, email, hashed);

        // ACCOUNT DOESN'T EXIST
        if (result.isEmpty()) {
            System.out.println(ANSI_RED + "INVALID CREDENTIALS" + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." + ANSI_RESET);
            scan.nextLine(); // Consume the newline
            scan.nextLine(); // Wait for user input
            continue; // Retry login
        } 

        // ACCOUNT EXISTS
        Map<String, Object> user = result.get(0);
        status = user.get("status").toString();
        role = user.get("role").toString();
        u_id = user.get("u_id").toString();

        // ACCOUNT NEEDS APPROVAL
        if (status.equalsIgnoreCase("pending")) {
            System.out.println(ANSI_RED + "ACCOUNT PENDING. CONTACT ADMIN FOR APPROVAL!" + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." + ANSI_RESET);
            scan.nextLine();
            scan.nextLine();
            continue; // Retry login
        } else if (status.equalsIgnoreCase("archived")) {
            System.out.println(ANSI_RED + "ACCOUNT DEACTIVATED. CONTACT ADMIN FOR REACTIVATION!" + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." + ANSI_RESET);
            scan.nextLine();
            scan.nextLine();
            continue; // Retry login
        } else {
            System.out.println("LOGIN SUCCESS!");
            System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." + ANSI_RESET);
            scan.nextLine();
            scan.nextLine();

            switch (role) {
                case "Patient":
                    patient(u_id);
                    break; // end of case patient
                case "Caretaker":
                    caretaker(u_id);
                    break; // end of case caretaker
                case "Admin":
                    AdminModule.adminDashboard(u_id);
                    break; // end of case admin
                case "Superadmin":
                    // Handle superadmin functionality
                    break; // end of case superadmin
            }
        }
    }
}

            
            public static void register (){
                System.out.println(ANSI_BLUE + "===== REGISTER =====\n" + ANSI_RESET);
                    while(true){ 
                        System.out.println("\t1. Patient\n\t2. Caretaker\n\t3. Admin");
                        System.out.println("============================================");
                        System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                        choice = scan.nextInt();

                        if(choice == 1){role = "Patient"; break;}
                        else if(choice == 2){role = "Caretaker"; break;}
                        else if(choice == 3){role = "Admin"; break;}
                        else{
                            System.out.println(ANSI_RED + "INVALID OPTION." + ANSI_RESET);
                            System.out.print(ANSI_GREEN + "press any key to continue . . ." + ANSI_RESET);
                            scan.nextLine();
                            scan.nextLine();
                        }
                    }
                System.out.print(ANSI_YELLOW + "First name: " + ANSI_RESET);
                    fname = scan.next();
                System.out.print(ANSI_YELLOW + "Surname: " + ANSI_RESET);
                    lname = scan.next();
                System.out.print(ANSI_YELLOW + "Date of birth [mm/dd/yyyy]: " + ANSI_RESET);
                    birthday = scan.next();
        //FIND EMAIL FROM DATABASE            
                        while(true){
                            System.out.print(ANSI_YELLOW + "Email: " + ANSI_RESET);
                                email = scan.next();
                            sqlFetch = "SELECT * FROM users WHERE email = ?";    
                            List<Map<String, Object>> result = config.fetchRecords(sqlFetch, email);
                                if (!result.isEmpty()) {
                                            System.out.println(ANSI_RED + "REDUNDANT EMAIL" + ANSI_RESET);
                                }        
                                else{
                                    break;
                                }         
                        }
                        
                System.out.print(ANSI_YELLOW + "Password: " + ANSI_RESET);
                    password = scan.next();
                     
                String hashed = config.hashPassword(password);    
                String part1 = "INSERT INTO users (u_fname, u_lname, email, password, role, status, birthday, credentials) ";
                String part2 = "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                sqlAdd = part1 + part2;
                config.addRecord(sqlAdd, fname, lname, email, hashed, role, "pending", birthday, "-");
                
                System.out.println("REGISTRATION SUCCESS. ACCOUNT PENDING");
                System.out.print(ANSI_YELLOW + "Press any key to continue . . ." +ANSI_RESET);
                scan.nextLine();
                scan.nextLine();                
            }   //end off register      
            
//PATIENT DASHBOARD
            public static void patient (String u_id){
                int task_id;
                while(true){
                System.out.println(ANSI_BLUE + "===== PATIENT DASHBOARD =====" + ANSI_RESET);
                
    //ADD THE TEMPOARY CONNECTION
                sqlFetch = "SELECT * FROM p_to_c_connect WHERE patient_id = ?";
                java.util.List<java.util.Map<String, Object>> tempo = config.fetchRecords(sqlFetch, u_id);         
                    if (tempo.isEmpty()){
                        sqlAdd = "INSERT INTO p_to_c_connect (patient_id, care_id, messages) VALUES (?, ?, ?)";
                        config.addRecord(sqlAdd, u_id, "-", "-");
                        
                    }                                    
    // FETCH METHOD 
                sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                java.util.List<java.util.Map<String, Object>> information = config.fetchRecords(sqlFetch, u_id);
    //FETCH NEEDED INFORMATION FROM THE DATABASE            
                    if (!information.isEmpty()){
                        java.util.Map<String, Object> info = information.get(0);
                        fname = info.get("u_fname").toString(); 
                        lname = info.get("u_lname").toString();
                        birthday = info.get("birthday").toString();
                        email = info.get("email").toString();  
                    }
                    else {
                        System.out.println("DATABASE ERROR IN INFO. NO ACCOUNT WITH ID " + u_id);
                        System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                        scan.nextLine();
                        scan.nextLine();
                        return;
                    }
    //FIND THE CARETAKER                
                String sqlFetch1 = "SELECT * FROM p_to_c_connect WHERE patient_id = ?";
                List<Map<String, Object>> connection = config.fetchRecords(sqlFetch1, u_id);
                
                //FIND CARETAKER
                    if (!connection.isEmpty()) {
                        java.util.Map<String, Object> connect = connection.get(0);
                        care_id = connect.get("care_id").toString();
                        if(care_id.equals("-")){
                            care_fname = "No";
                            care_lname = "Caretaker";
                        }else {
                           String sqlFetch2 = "SELECT * FROM users WHERE u_id = ?";
                            java.util.List<java.util.Map<String, Object>> caregiver = config.fetchRecords(sqlFetch2, care_id);
                            if (!caregiver.isEmpty()){
                                //CARETAKER CONNECTED                                       
                                java.util.Map<String, Object> care = caregiver.get(0);
                                care_fname = care.get("u_fname").toString(); 
                                care_lname = care.get("u_lname").toString();                                        
                            }
                            else {
                                System.out.println("DATABASE ERROR IN NAMES. NO ACCOUNT WITH ID " + u_id);
                                System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                                scan.nextLine();
                                scan.nextLine();
                                return;
                            } 
                        }
                    }
                    else {
                        System.out.println("DATABASE ERROR IN CONNECTION. NO ACCOUNT WITH ID " + u_id);
                        System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                        scan.nextLine();
                        scan.nextLine();
                        return;
                    }
                    
                String patient_fullname = fname + " " + lname;    
                String care_fullname = care_fname + " " + care_lname;
    // DISPLAY ACCOUNT INFORMATION
    
                System.out.println("Full Name: " + patient_fullname);
                System.out.println("Birthdate: " + birthday);
                System.out.println("Email: " + email);
                System.out.println("Caregiver: " + care_fullname);
                System.out.println("============================================");
    //DISPLAY ALL CAREPLANS W/ PATIENT ID
                System.out.println("===== CAREPLANS =====");
                view_careplans(u_id, 1);
                
                
                System.out.println("\n============================================");
                System.out.println("\t1. Select careplan");
                System.out.println("\t2. Contact Caregiver");
                System.out.println("\t3. Edit account credentials");
                System.out.println("\t4. End care");
                System.out.println("\t5. Exit");
                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                choice = scan.nextInt();
    //CHOOSE WHAT TO DO NEXT
                switch(choice){
                    case 1:
                            System.out.print(ANSI_YELLOW + "Enter Careplan [#]: " + ANSI_RESET);
                            choice = scan.nextInt();
                            c_id = Select_careplan(choice);
                                view_tasks(c_id,u_id);
                                view_medicine(c_id,u_id);
                    //SET THEM AS FINISHED
                                System.out.println("\t1. set task as finished");
                                System.out.println("\t2. Set medicine as finished");
                                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                                choice = scan.nextInt();
                                
                                if(choice == 1){
                                    System.out.print("\nSelect Task [#]: ");
                                    int option = scan.nextInt();

                                    sqlFetch = "SELECT * FROM tasks WHERE c_id = ?";
                                    List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, c_id);
                                        if (option >= 1 && option <= result1.size()) {
                                            Map<String, Object> selected = result1.get(option - 1);
                                            task_id = (int) selected.get("t_id");
                                            sqlUpd = "UPDATE tasks SET t_status = ? WHERE t_id = ?";
                                            config.updateRecord(sqlUpd, "Finished", task_id);
                                            System.out.println("Sucessfully updated status!");
                                        }
                                        else {
                                            System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                                        }                                    
                                }
                                else if(choice == 2){
                                    
                                }
                        break; //end of case 1 select careplan
                    case 2:
                        System.out.println("\t1. Request new caretaker");
                        System.out.println("\t2. Request edit in careplan");   
                        System.out.println("\t3. Request edit in tasks");
                        System.out.println("\t4. Inquire about care");
                        System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                        choice = scan.nextInt();
                        
                        sqlAdd = "UPDATE p_to_c_connect SET messages = ? WHERE patient_id = ?";
                        config.addRecord(sqlAdd, choice, u_id);
                        System.out.println("Message successfully sent!");
                        System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                        scan.nextLine();
                        scan.nextLine();
                       break; //end of case 2 contact caretaker; 
                    case 3:
                        edit_user(u_id); 
                        break;
                    case 4:
                        System.out.println("Note: Ending care will deactivate your account!");
                        System.out.println("Account deactivation and reactivation requires admin approval.");
                        System.out.println("Continue with account deactivation?");
                        System.out.println("1. Yes\t2. No");
                        System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                        choice = scan.nextInt();
                        if(choice == 1){
                            sqlUpd = "UPDATE users SET status = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, "closing", u_id);
                            System.out.println("Account deactivation request pending. Log out?");
                            System.out.println("1. Yes\t2. No");
                            System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                            choice = scan.nextInt();
                            if(choice == 1){
                                login();
                            }
                        }                        
                        break; //end of case 4 end care;
                    case 5:
                        login();
                        break; //end of case 5 exit dashboard
                }
                }
            }   //end off patient dashboard         
            
            
//VIEW CAREPLANS 
            public static void view_careplans (String u_id, int mode){
                
                switch(mode){
                    case 1:
                        String mess = "ongoing" ;
                        sqlFetch = "SELECT * FROM careplan WHERE u_id_patient = ? ";
                        List<Map<String, Object>> mode1 = config.fetchRecords(sqlFetch, u_id);
                        int x = 1;
                        if (!mode1.isEmpty()) {
                            for (Map cp : mode1) {
                                String stat = cp.get("status").toString();
                                if(stat.equals("archived")){
                                    continue;
                                }
                                System.out.println(x +"] Title: " + cp.get("c_name"));
                                System.out.println("End Date: " + cp.get("end_date"));
                                System.out.println("Status: " + cp.get("status"));
                                System.out.println("-----------------------------");
                                x++;
                            }
                        } 
                        else {
                            System.out.println("No careplans found in the database.");
                            
                        }
                        break;//end of case 1 mode 1 small info
                    case 2:
                         sqlFetch = "SELECT * FROM careplan WHERE u_id_patient = ?";
                        List<Map<String, Object>> mode2 = config.fetchRecords(sqlFetch, u_id);
                        int y = 1;
                        if (!mode2.isEmpty()) {
                            for (Map cp : mode2) {
                                System.out.println(y +"] Title: " + cp.get("c_name"));
                                System.out.println("Start Date: " + cp.get("start_date") + "\t" + "End Date: " + cp.get("end_date"));
                                System.out.println("Description: " + cp.get("description"));
                                System.out.println("Status: " + cp.get("status"));
                                System.out.println("-----------------------------");
                                y++;
                            }
                        } 
                        else {
                            System.out.println("No careplans found in the database.");
                            
                        }
                    break;//end of case 2 mode 2 all info
                }    
            }   //end off view careplan
 //SELECT CAREPLAN
            public static int Select_careplan (int choice){
    //COUNT HOW MANY CAREPLANS THERE ARE            
                sqlFetch = "SELECT * FROM careplan WHERE u_id_patient = ?";
                List<Map<String, Object>> result = config.fetchRecords(sqlFetch, u_id);
                int counter = 1;
                    if (!result.isEmpty()) {
                        for (Map cp : result) {
                            counter++;
                        }
                    }
    //SEE IF CHOICE IS VALID                
                if(choice > counter || choice <1) {
                    System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                    System.out.print(ANSI_YELLOW + "Press ENTER to continue . . ." +ANSI_RESET);
                    scan.nextLine();
                    scan.nextLine();
                }
    //IF VALID, SEND BACK THE CAREPLAN ID FOR OPENING             
                int finder;
                java.util.Map<String, Object> select = result.get(0);
                for (finder = 1; finder == choice; finder++){
                    c_id = (int) select.get("c_id");
                    
                }
                return(c_id);
            } //end of select careplan
//EDIT USER CREDENTIALS        
            public static void edit_user (String u_id) {
                while(true) {
                    System.out.println("1. Edit Account Name");
                    System.out.println("2. Edit Account Email");
                    System.out.println("3. Edit Account Password");
                    System.out.println("4. Edit Birthdate");
                    System.out.println("5. Exit");
                    System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                    choice = scan.nextInt();   

                    switch(choice){
                        case 1:
                //GET CURRENT NAME            
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> editname = config.fetchRecords(sqlFetch, u_id);

                            if (!editname.isEmpty()) {
                                Map<String, Object> user = editname.get(0);
                                String fullname = user.get("u_fname").toString() + " " + user.get("u_lname").toString();
                                System.out.println("Current Account Name: " + fullname);
                            } else {
                                System.out.println(ANSI_RED + "Database Error. User not found." + ANSI_RESET);
                            }
                //GET NEW NAME
                            System.out.println(ANSI_YELLOW + "Enter New First Name: " + ANSI_RESET);
                            fname = scan.next();
                            System.out.println(ANSI_YELLOW + "Enter New Last Name: " + ANSI_RESET);
                            lname = scan.next();
                            sqlUpd = "UPDATE users SET u_fname = ?, u_lname = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, fname, lname, u_id);

                            System.out.println("Name successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break; //end of case 1 edit name
                        case 2:
                //GET CURRENT EMAIL            
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> editemail = config.fetchRecords(sqlFetch, u_id);

                            if (!editemail.isEmpty()) {
                                Map<String, Object> user = editemail.get(0);
                                email = user.get("email").toString();
                                System.out.println("Current Account Email: " + email);
                            } else {
                                System.out.println(ANSI_RED + "Database Error. User not found." + ANSI_RESET);
                            }
                //GET NEW EMAIL
                            while(true){
                                System.out.print(ANSI_YELLOW + "Enter New Email: " + ANSI_RESET);
                                email = scan.next();
                                sqlFetch = "SELECT * FROM users WHERE email = ?";    
                                List<Map<String, Object>> result = config.fetchRecords(sqlFetch, email);
                                    if (!result.isEmpty()) {
                                                System.out.println(ANSI_RED + "REDUNDANT EMAIL" + ANSI_RESET);
                                    }
                                    else{
                                        break;
                                    }
                            }
                            sqlUpd = "UPDATE users SET email = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, email, u_id);
                            System.out.println("email successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break;//end of case 2 edit email
                        case 3:
                //GET OLD PASSWORD
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> newpassword = config.fetchRecords(sqlFetch, u_id);

                            if (!newpassword .isEmpty()) {
                            Map<String, Object> user = newpassword.get(0);
                            password = user.get("password").toString();
                                while (true) {
                                    System.out.println("Enter Current Password: ");
                                    String verify = scan.next();
                                    if(!verify.equals(password)){
                                        System.out.println("INCORRECT PASSWORD.");
                                    }
                                    else {
                                        break;
                                    }
                                }
                            } 
                            else {
                                System.out.println("Database Error. User not found.");
                            }
                //GET NEW PASSWORD
                            System.out.println("Enter New Password: ");
                            password = scan.next();
                            sqlUpd = "UPDATE users SET password = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, password, u_id); 
                            System.out.println("Password successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break;//end of case 3 edit password
                        case 4:
                //GET CURRENT BIRTHDAY            
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> editbirthday = config.fetchRecords(sqlFetch, u_id);
                            if (!editbirthday.isEmpty()) {
                                Map<String, Object> user = editbirthday.get(0);
                                birthday = user.get("birthday").toString();
                                System.out.println("Current Birthday: " + birthday);
                            } else {
                                System.out.println("Database Error. User not found.");
                            }
                //GET NEW BIRTHDAY            
                            System.out.println("Enter New Birthdate (MM-DD-YYYY): ");
                            birthday = scan.next();
                            sqlUpd = "UPDATE users SET birthday = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, birthday, u_id);
                            System.out.println("Birthday successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break;//end of case 4 edit birthday
                        case 5:
                            patient(u_id);
                            break;//end of case 5 exit back to dashboard
                        default:
                            System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET); 
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break;
                    }
                }    
            } //end of edit user
            
            public static void edit_care_user (String u_id) {
                while(true) {
                    System.out.println("1. Edit Account Name");
                    System.out.println("2. Edit Account Email");
                    System.out.println("3. Edit Account Password");
                    System.out.println("4. Edit Birthdate");
                    System.out.println("5. Edit Credentials");     
                    System.out.println("6. Exit");
                    System.out.print(ANSI_RED + "Enter [#]: " + ANSI_RESET);
                    choice = scan.nextInt();   

                    switch(choice){
                        case 1:
                //GET CURRENT NAME            
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> editname = config.fetchRecords(sqlFetch, u_id);

                            if (!editname.isEmpty()) {
                                Map<String, Object> user = editname.get(0);
                                String fullname = user.get("u_fname").toString() + " " + user.get("u_lname").toString();
                                System.out.println("Current Account Name: " + fullname);
                            } else {
                                System.out.println(ANSI_RED + "Database Error. User not found." + ANSI_RESET);
                            }
                //GET NEW NAME
                            System.out.println(ANSI_YELLOW + "Enter New First Name: " + ANSI_RESET);
                            fname = scan.next();
                            System.out.println(ANSI_YELLOW + "Enter New Last Name: " + ANSI_RESET);
                            lname = scan.next();
                            sqlUpd = "UPDATE users SET u_fname = ?, u_lname = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, fname, lname, u_id);

                            System.out.println("Name successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break; //end of case 1 edit name
                        case 2:
                //GET CURRENT EMAIL            
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> editemail = config.fetchRecords(sqlFetch, u_id);

                            if (!editemail.isEmpty()) {
                                Map<String, Object> user = editemail.get(0);
                                email = user.get("email").toString();
                                System.out.println("Current Account Email: " + email);
                            } else {
                                System.out.println(ANSI_RED + "Database Error. User not found." + ANSI_RESET);
                            }
                //GET NEW EMAIL
                            while(true){
                                System.out.print(ANSI_YELLOW + "Enter New Email: " + ANSI_RESET);
                                email = scan.next();
                                sqlFetch = "SELECT * FROM users WHERE email = ?";    
                                List<Map<String, Object>> result = config.fetchRecords(sqlFetch, email);
                                    if (!result.isEmpty()) {
                                                System.out.println(ANSI_RED + "REDUNDANT EMAIL" + ANSI_RESET);
                                    }
                                    else{
                                        break;
                                    }
                            }
                            sqlUpd = "UPDATE users SET email = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, email, u_id);
                            System.out.println("email successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break;//end of case 2 edit email
                        case 3:
                //GET OLD PASSWORD
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> newpassword = config.fetchRecords(sqlFetch, u_id);

                            if (!newpassword .isEmpty()) {
                            Map<String, Object> user = newpassword.get(0);
                            password = user.get("password").toString();
                                while (true) {
                                    System.out.println("Enter Current Password: ");
                                    String verify = scan.next();
                                    if(!verify.equals(password)){
                                        System.out.println("INCORRECT PASSWORD.");
                                    }
                                    else {
                                        break;
                                    }
                                }
                            } 
                            else {
                                System.out.println("Database Error. User not found.");
                            }
                //GET NEW PASSWORD
                            System.out.println("Enter New Password: ");
                            password = scan.next();
                            sqlUpd = "UPDATE users SET password = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, password, u_id); 
                            System.out.println("Password successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break;//end of case 3 edit password
                        case 4:
                //GET CURRENT BIRTHDAY            
                            sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                            List<Map<String, Object>> editbirthday = config.fetchRecords(sqlFetch, u_id);
                            if (!editbirthday.isEmpty()) {
                                Map<String, Object> user = editbirthday.get(0);
                                birthday = user.get("birthday").toString();
                                System.out.println("Current Birthday: " + birthday);
                            } else {
                                System.out.println("Database Error. User not found.");
                            }
                //GET NEW BIRTHDAY            
                            System.out.println("Enter New Birthdate (MM-DD-YYYY): ");
                            birthday = scan.next();
                            sqlUpd = "UPDATE users SET birthday = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, birthday, u_id);
                            System.out.println("Birthday successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break;//end of case 4 edit birthday
                        case 5:
                //GET NEW BIRTHDAY            
                            System.out.println("Enter Credentials: ");
                            credentials = scan.next();
                            sqlUpd = "UPDATE users SET credentials = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, credentials, u_id);
                            System.out.println("Credentials successfully edited!");
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();            
                            break; //end of case 5 edit credentials
                        case 6:
                            caretaker(u_id);
                            break;//end of case 6 exit back to dashboard
                            
                        default:
                            System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET); 
                            System.out.print("press any key to continue . . .");
                            scan.nextLine();
                            scan.nextLine();
                            break;
                    }
                }    
            } //end of edit user
//CARETAKER DASHBOARD
            public static void caretaker(String u_id)   {
                while(true){                        
                System.out.println(ANSI_BLUE + "===== CARETAKER DASHBOARD =====" + ANSI_RESET);
                
    // FETCH METHOD 
                sqlFetch = "SELECT * FROM users WHERE u_id = ?";
                java.util.List<java.util.Map<String, Object>> information = config.fetchRecords(sqlFetch, u_id);
    //FETCH NEEDED INFORMATION FROM THE DATABASE            
                    if (!information.isEmpty()){
                        java.util.Map<String, Object> info = information.get(0);
                        fname = info.get("u_fname").toString(); 
                        lname = info.get("u_lname").toString();
                        birthday = info.get("birthday").toString();
                        email = info.get("email").toString(); 
                        credentials = info.get("credentials").toString();
                    }
                    else {
                        System.out.println("DATABASE ERROR. NO ACCOUNT WITH ID " + u_id);
                        System.out.print("Press any key to continue . . .");
                        scan.nextLine();
                        scan.nextLine();
                        return;
                    }
                String patient_fullname = fname + " " + lname;                
    // DISPLAY ACCOUNT INFORMATION
                System.out.println("Full Name: " + patient_fullname);
                System.out.println("Birthdate: " + birthday);
                System.out.println("Email: " + email);
                System.out.println("Credentials: " + credentials);
                System.out.println("============================================");
                System.out.println("===== PATIENTS =====");
                
                view_patients(u_id);
                
                System.out.println("\n============================================");
                System.out.println("\t1. Select patient");
                System.out.println("\t2. Approve patient requests");
                System.out.println("\t3. Monitor patients WALA PA");
                System.out.println("\t4. Edit account credentials");
                System.out.println("\t5. Deactivate account");
                System.out.println("\t6. Exit");
                System.out.println("\t7. Close Program");
                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                choice = scan.nextInt();
                
                switch(choice){
                    case 1: 
                        System.out.println("\n============================================");
                        view_patients(u_id);
                        System.out.print("\nSelect patient [#]: ");
                        int option = scan.nextInt();
                        
                        sqlFetch = "SELECT * FROM p_to_c_connect WHERE care_id = ?";
                        List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, u_id);
                        if (option >= 1 && option <= result1.size()) {
                            Map<String, Object> selected = result1.get(option - 1);
                           patient_id = selected.get("patient_id").toString();
                        }
                        else {
                            System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                        }
                        
                        System.out.println("\t1. View careplans");
                        System.out.println("\t2. Create new careplans");
                        System.out.println("\t3. Drop patient WALA PA");     
                        System.out.println("\t4. Exit");
                        System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                        choice = scan.nextInt();
                            switch(choice){
                                case 1:
                                    view_careplans(patient_id, 1);
                                    System.out.println("\n============================================");
                                    System.out.println("\t1. Edit careplan");
                                    System.out.println("\t2. End careplan");
                                    System.out.println("\t3. View Tasks");
                                    System.out.println("\t4. Exit");    
                                    System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                                    choice = scan.nextInt();
                                    
                                    switch(choice){
                                        case 1:
                                            System.out.print("\nSelect careplan[#]: ");
                                            choice = scan.nextInt();
                                            sqlFetch = "SELECT * FROM careplans WHERE u_id_patients = ?";
                                            List<Map<String, Object>> select = config.fetchRecords(sqlFetch, patient_id);
                                            if(!select.isEmpty()) {
                                                Map<String, Object> sel = select.get(0);
                                                c_id = (int) sel.get("c_id");
                                                edit_careplan(c_id, u_id);
                                                System.out.print("Press any key to continue . . .");
                                                scan.nextLine();
                                                scan.nextLine();
                                            }
                                            break;//end of case 1 edit careplan
                                        case 2:
                                            System.out.println("Archive careplan?");
                                            System.out.println("1. Yes\t2.No");
                                            System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                                            choice = scan.nextInt();
                                            if(choice == 1){
                                            sqlUpd = "UPDATE careplan SET status = ? WHERE c_id = ?";
                                            config.updateRecord(sqlUpd, "archived", c_id);    
                                            }
                                            System.out.println("Careplan sucessfully archived!");
                                            System.out.print("Press any key to continue . . .");
                                            scan.nextLine();
                                            scan.nextLine();
                                            break;//end of case 2 end careplan
                                        case 3:
                                            while(true){
                                            view_tasks(c_id, u_id); 
                                            view_medicine(c_id, u_id);
                                            System.out.println("\n============================================");
                                            System.out.println("\t1. Add new task or medicine");
                                            System.out.println("\t2. Edit task or medicine");
                                            System.out.println("\t3. Delete Task");
                                            System.out.println("\t4. Exit");
                                            System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                                            choice = scan.nextInt();
                                            
                                            switch(choice){
                                                case 1: 
                                                    add_task(c_id);
                                                    break;//end of case 1 add new tasks
                                                case 2:
                                                    edit_task(patient_id, u_id, c_id);
                                                    System.out.println("Press any key to continue. . . ");
                                                    scan.nextLine();
                                                    scan.nextLine();
                                                    break;//end of case 3 edit task or medicine
                                                case 3:
                                                    delete_task(c_id, u_id);
                                                    System.out.println("Press any key to continue. . . ");
                                                    scan.nextLine();
                                                    scan.nextLine();
                                                    break;//end of case 4 delete task or medicine
                                                case 4:
                                                    System.out.println("Press any key to continue . . . ");
                                                    scan.nextLine();
                                                    scan.nextLine();
                                                    break;//exit
                                            }
                                            break;//end of case 3 view tasks
                                             }
                                        case 4:
                                            System.out.println("Press any key to continue . . . ");
                                            scan.nextLine();
                                            scan.nextLine();
                                            break;//end of case 4 exit
                                            
                                            
                                    }
                                    break;//end of case 1 view careplan
                                case 2:
                                    System.out.print("Enter Title: ");
                                    String title = scan.next();
                                    System.out.print("Enter Start Date [mm-dd-yyyy]: ");
                                    String start_date = scan.next();
                                    System.out.print("Enter End Date [mm-dd-yyyy]: ");
                                    String end_date = scan.next();
                                    System.out.print("Enter Description: ");
                                    String description = scan.next();
                                    

                                    sqlUpd = "INSERT INTO careplan (c_title, u_id_patient, u_id_care, start_date, end_date, description, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                                    config.updateRecord(sqlUpd, title, patient_id, u_id, start_date, end_date, description, "ongoing");

                                    System.out.println("Careplan added successfully!");
                                    System.out.print("Press any key to continue . . .");
                                    scan.nextLine();
                                    scan.nextLine();
                                    break; //end of case 2 add careplan
                                case 3:
                                    break;//end of case 3 drop patients
                                case 4:       
                                    System.out.println("Press any key to continue . . . ");
                                    scan.nextLine();
                                    scan.nextLine();
                                    break;//end of case 4 exit    
                            }//end inner case
                            
                        break; //end of case 1 select patients
                        
                    case 2:
                        approve_message(u_id);
                        System.out.print("Press any key to continue . . .");
                                    scan.nextLine();
                                    scan.nextLine();
                        break; //end of case 2 approve patient requests
                    case 3:
                        view_patients(u_id);
                        System.out.print("\nSelect patient [#]: ");
                        option = scan.nextInt();
                        
                        sqlFetch = "SELECT * FROM p_to_c_connect WHERE care_id = ?";
                        List<Map<String, Object>> selectionPatient = config.fetchRecords(sqlFetch, u_id);
                        if (option >= 1 && option <= selectionPatient.size()) {
                            Map<String, Object> selected = selectionPatient.get(option - 1);
                            patient_id = selected.get("patient_id").toString();
                        }
                        else {
                            System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                        }
//ADD HERE VIEW TASKS========================================                        
                        
                        break; //end of case 3 monitor patients
                    case 4: 
                        edit_care_user(u_id);
                        break;//end of case 4 edit account credentials
                    case 5:
                        System.out.println("Note: Proceeding with deactivate your account!");
                        System.out.println("Account deactivation and reactivation requires admin approval.");
                        System.out.println("Continue with account deactivation?");
                        System.out.println("1. Yes\t2. No");
                        System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                        choice = scan.nextInt();
                        if(choice == 1){
                            sqlUpd = "UPDATE users SET status = ? WHERE u_id = ?";
                            config.updateRecord(sqlUpd, "closing", u_id);
                            System.out.println("Account deactivation request pending. Log out?");
                            System.out.println("1. Yes\t2. No");
                            System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                            choice = scan.nextInt();
                            if(choice == 1){
                                login();
                            }
                        }     
                        break;//end of case 5 deactivate account
                    case 6:
                        login();
                        break;//end of case 6 exit
                    case 7: 
                        System.exit(0);
                        break;//end of casee 7 close
                }

                }    
            }// end of caretaker dashboard
                    
                    
//VIEW PATIENTS
            public static void view_patients(String u_id){
            // Fetch all patient_ids linked to this caregiver
                    String sqlPatients = "SELECT patient_id FROM p_to_c_connect WHERE care_id = ?";
                    java.util.List<java.util.Map<String, Object>> patientLinks = config.fetchRecords(sqlPatients, u_id);

                    if (!patientLinks.isEmpty()) {
                        int x = 1;
                        for (Map link : patientLinks) {
                            int patientId = Integer.parseInt(link.get("patient_id").toString());

            // Fetch the patient’s details separately from the users table
                            String sqlUser = "SELECT u_fname, u_lname, email FROM users WHERE u_id = ?";
                            java.util.List<java.util.Map<String, Object>> userList = config.fetchRecords(sqlUser, patientId);

                            if (!userList.isEmpty()) {
                                Map user = userList.get(0);
                                String fullName = user.get("u_fname") + " " + user.get("u_lname");
                                email = user.get("email").toString();

                                System.out.println("[" + x + "] " + fullName + " (Email: " + email + ")");
                                x++;
                            }
                        }
                    } else {
                        System.out.println("No patients linked to this caregiver.");
                    }

            }//end of view patients                   
                    
//APPROVE MESSAGES
            public static void approve_message (String u_id) {
                    // Step 1: Fetch patient IDs and messages from p_to_c_connect
                    String sqlConnections = "SELECT patient_id, messages FROM p_to_c_connect WHERE care_id = ?";
                    java.util.List<java.util.Map<String, Object>> connections = config.fetchRecords(sqlConnections, u_id);

                    if (connections.isEmpty()) {
                        System.out.println("No patients linked to this caregiver.");
                        return;
                    }
                    else if(!connections.isEmpty()) {
                        int x = 1;
                        java.util.List<Integer> patientIds = new java.util.ArrayList<>();

                    // Step 2: Display all messages
                        for (Map connect : connections) {
                            int patientId = Integer.parseInt(connect.get("patient_id").toString());
                            String messageCodeStr = connect.get("messages").toString();

                            if (messageCodeStr.equals("-")) {
                                continue; // skip deleted
                            }

                            int messageCode = Integer.parseInt(messageCodeStr);

                            // Fetch patient details
                            String sqlUser = "SELECT u_fname, u_lname FROM users WHERE u_id = ?";
                            java.util.List<java.util.Map<String, Object>> users = config.fetchRecords(sqlUser, patientId);

                            if (!users.isEmpty()) {
                                Map user = users.get(0);
                                String fullName = user.get("u_fname") + " " + user.get("u_lname");

                                // Decode message type
                                String messageText;
                                switch (messageCode) {
                                    case 1: messageText = "Request new caretaker"; break;
                                    case 2: messageText = "Request edit in careplan"; break;
                                    case 3: messageText = "Request edit in tasks"; break;
                                    case 4: messageText = "Inquire about care"; break;
                                    default: messageText = "Unknown request";
                                }

                            // Display result
                            System.out.println("[" + x + "] " + fullName + " - " + messageText);
                            patientIds.add(patientId);
                            x++;
                            }
                        }                          
                        if (patientIds.isEmpty()) {
                            System.out.println("No active messages to manage.");  
                            
                            return;
                        }

                        // Step 3: Ask caretaker if they want to delete one
                        System.out.print("Enter the number of a message to delete (or 0 to cancel): ");
                        java.util.Scanner sc = new java.util.Scanner(System.in);
                        choice = sc.nextInt();

                        if (choice > 0 && choice <= patientIds.size()) {
                            int selectedPatientId = patientIds.get(choice - 1);
                            String sqlDelete = "UPDATE p_to_c_connect SET messages = '-' WHERE care_id = ? AND patient_id = ?";
                            config.updateRecord(sqlDelete, u_id, selectedPatientId);

                        } else {
                            System.out.println("No deletion performed.");
                        }
                    }   
            }//end of approve messages 
//EDIT CAREPLAN
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
                            caretaker(u_id);
                            break;//end of case 1 exit 
                        default:
                            System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                            break;
                    }
                }
            }//end of edit careplan
//VIEW TASKS
            public static void view_tasks (int c_id, String u_id) {            
                sqlFetch = "SELECT * FROM tasks WHERE c_id = ?";
                List<Map<String, Object>> viewtasks = config.fetchRecords(sqlFetch, c_id);
                
                int y = 1;
                    if (!viewtasks.isEmpty()) {
                    Map<String, Object> task = viewtasks.get(0);
                    System.out.println("===== TASKS =====");
                    String stat = task.get("t_status").toString();
                    
                        for (Map t : viewtasks) {
                            if(stat.equals("archived")){
                                continue;
                            }
                            System.out.println(y + "Type: " + t.get("t_type"));
                            System.out.println("Description: " + t.get("t_desc"));
                            System.out.println("Time: " + t.get("t_time"));
                            System.out.println("Status: " + t.get("t_status"));
                            System.out.println("-----------------------------");
                            y++;
                        }
                    }    
                    else{
                        System.out.println("no tasks found");
                    }    
            }//end of view tasks
//VIEW MEDICINE 
            public static void view_medicine (int c_id, String u_id){
                sqlFetch = "SELECT * FROM medicine WHERE c_id = ?";
                List<Map<String, Object>> viewmeds = config.fetchRecords(sqlFetch, c_id);
                
                int y = 1;
                    if (!viewmeds.isEmpty()) {
                    Map<String, Object> task = viewmeds.get(0);
                    System.out.println("===== MEDICINE =====");
                    for (Map t : viewmeds) {
                            System.out.println(y + "Name: " + t.get("m_name"));
                            System.out.println("frequency: " + t.get("m_frequency"));
                            System.out.println("Instruct: " + t.get("m_instruct"));
                            System.out.println("What for: " + t.get("m_for"));
                            System.out.println("-----------------------------");
                            y++;
                            
                        }
                    }    
                    else{
                        System.out.println("no medicine found");
                    }    
            }
//ADD TASKS
            public static void add_task(int c_id){
                System.out.println("1. Add task");
                System.out.println("2. Add Mecicine");
                System.out.println("3. Exit");
                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                choice = scan.nextInt();
                System.out.println("============================================");
                
                if(choice == 1){
                    System.out.print("Enter Title: ");
                    String type = scan.next();
                    System.out.print("Enter Description: ");
                    String desc = scan.next();
                    System.out.print("Enter Time (start - end): ");
                    String time = scan.next();    
                    
                    String part1 = "INSERT INTO tasks (c_id, t_type, t_desc, t_time, t_status) ";
                    String part2 = "VALUES (?, ?, ?, ?, ?)";
                    sqlAdd = part1 + part2;
                    config.addRecord(sqlAdd, c_id, type, desc, time, "pending");
                    System.out.println("Task added successfully!");
                    System.out.print("Press any key to continue . . .");
                    scan.nextLine();
                    scan.nextLine();
                }
                else if(choice == 2){
                    System.out.print("Enter Name: ");
                    String name = scan.next();
                    System.out.print("Enter Frequency (# times a day/week): ");
                    String freq = scan.next();
                    System.out.print("Enter Further Instruction: ");
                    String inst = scan.next();
                    System.out.println("Enter use for medicine: ");
                    String forwhat = scan.next();  
                    
                    String part1 = "INSERT INTO medicine (m_name, m_frequency, m_instruct, m_for, c_id) ";
                    String part2 = "VALUES (?, ?, ?, ?, ?)";
                    sqlAdd = part1 + part2;
                    config.addRecord(sqlAdd, name, freq, inst, forwhat, c_id);
                    System.out.println("Medicine added successfully!");
                    System.out.print("Press any key to continue . . .");
                    scan.nextLine();
                    scan.nextLine();
                }
                else if(choice == 3){
                    return;
                }
            }// end of add_task
//EDIT TASKS
            public static void edit_task(String patient_id, String u_id, int c_id){
                int task_id;
                int medicine_id;
                
                while(true){                                 
                    System.out.println("1. Edit task");
                    System.out.println("2. Edit medicine");
                    System.out.println("3. Exit");
                    System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                    choice = scan.nextInt();
                    System.out.println("============================================");
                    switch(choice){
                        case 1:
                            while(true){
                            view_tasks (c_id, u_id);
                            
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
                                System.out.println("1. Edit title");
                                System.out.println("2. Edit description");
                                System.out.println("3. Edit time");
                                System.out.println("4. Edit status");
                                System.out.println("5. Exit");
                                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                                choice = scan.nextInt();

                                        switch(choice){
                                            case 1://edit title
                                                System.out.println("Enter new title: ");
                                                String title = scan.next();
                                                sqlUpd = "UPDATE tasks SET t_type = ? WHERE t_id = ?";
                                                config.updateRecord(sqlUpd, title, task_id);
                                                System.out.println("Title updated successfully!");
                                                System.out.println("Press any key to continue");
                                                scan.nextLine();
                                                scan.nextLine();
                                                break;
                                            case 2://edit description
                                                System.out.println("Enter new description: ");
                                                String desc = scan.next();
                                                sqlUpd = "UPDATE tasks SET t_desk = ? WHERE t_id = ?";
                                                config.updateRecord(sqlUpd, desc, task_id);
                                                System.out.println("Description updated successfully!");
                                                System.out.println("Press any key to continue");
                                                scan.nextLine();
                                                scan.nextLine();
                                                break;
                                            case 3://edit time
                                                System.out.println("Enter new time: ");
                                                String time = scan.next();
                                                sqlUpd = "UPDATE tasks SET t_time = ? WHERE t_id = ?";
                                                config.updateRecord(sqlUpd, time, task_id);
                                                System.out.println("Time updated successfully!");
                                                System.out.println("Press any key to continue");
                                                scan.nextLine();
                                                scan.nextLine();
                                                break;
                                            case 4://edit status
                                                System.out.println("Enter new status: ");
                                                String status = scan.next();
                                                sqlUpd = "UPDATE tasks SET t_status = ? WHERE t_id = ?";
                                                config.updateRecord(sqlUpd, status, task_id);
                                                System.out.println("Status updated successfully!");
                                                System.out.println("Press any key to continue");
                                                scan.nextLine();
                                                scan.nextLine();
                                                break;
                                            case 5:
                                                return;
                                        }                                   
                            break;//end of case 1 edit tasks
                        case 2:
                            while(true){
                            view_medicine (c_id, u_id);
                            
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
                                System.out.println("1. Edit Name");
                                System.out.println("2. Edit Frequency");
                                System.out.println("3. Edit Instruction");
                                System.out.println("4. Edit What For");
                                System.out.println("5. Exit");
                                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                                choice = scan.nextInt();

                                        switch(choice){
                                            case 1://edit name
                                                System.out.println("Enter new Name: ");
                                                String name = scan.next();
                                                sqlUpd = "UPDATE medicine SET m_name = ? WHERE m_id = ?";
                                                config.updateRecord(sqlUpd, name, medicine_id);
                                                System.out.println("Name updated successfully!");
                                                System.out.println("Press any key to continue");
                                                scan.nextLine();
                                                scan.nextLine();
                                                break;
                                            case 2://edit frequency
                                                System.out.println("Enter new Frequency: ");
                                                String freq = scan.next();
                                                sqlUpd = "UPDATE medicine SET m_frequency = ? WHERE m_id = ?";
                                                config.updateRecord(sqlUpd, freq, medicine_id);
                                                System.out.println("Frequency updated successfully!");
                                                System.out.println("Press any key to continue");
                                                scan.nextLine();
                                                scan.nextLine();
                                                break;
                                            case 3://edit instruction
                                                System.out.println("Enter new Instruction: ");
                                                String instruct = scan.next();
                                                sqlUpd = "UPDATE medicine SET m_instruct = ? WHERE m_id = ?";
                                                config.updateRecord(sqlUpd, instruct, medicine_id);
                                                System.out.println("Instruction updated successfully!");
                                                System.out.println("Press any key to continue");
                                                scan.nextLine();
                                                scan.nextLine();
                                                break;
                                            case 4://edit what for
                                                System.out.println("Enter new medicine Usage: ");
                                                String whatfor = scan.next();
                                                sqlUpd = "UPDATE medicine SET m_for = ? WHERE m_id = ?";
                                                config.updateRecord(sqlUpd, whatfor, medicine_id);
                                                System.out.println("Usage updated successfully!");
                                                System.out.println("Press any key to continue");
                                                scan.nextLine();
                                                scan.nextLine();
                                                break;
                                            case 5:
                                                 return;
                                                
                                        }
                            break;//end of case 2 edit medicine                            
                        case 3:
                            break;
                            
                        default:
                            System.out.println("INVALID OPTION.");
                            System.out.println("Press any key to continue");
                            scan.nextLine();
                            scan.nextLine();
                            break;

                    }
                    
                }
            }//end of edit task
//DELETE TASKS
            public static void delete_task(int c_id, String u_id){
                int task_id, medicine_id;
                System.out.println("1. Delete task");
                System.out.println("2. Delete medicine");
                
                System.out.print(ANSI_YELLOW + "Enter [#]: " + ANSI_RESET);
                choice = scan.nextInt();
                System.out.println("============================================");
                
                switch(choice){
                    case 1://delete task
                        view_tasks(c_id, u_id);
                        System.out.println("\n============================================");
                                System.out.print("\nSelect task [#]: ");
                                int option = scan.nextInt();

                                sqlFetch = "SELECT * FROM tasks WHERE c_id = ?";
                                List<Map<String, Object>> result1 = config.fetchRecords(sqlFetch, c_id);
                                if (option >= 1 && option <= result1.size()) {
                                    Map<String, Object> selected = result1.get(option - 1);
                                    task_id = (int) selected.get("t_id");
                                    sqlDel = "DELETE *  FROM tasks WHERE t_id = ?";    
                                    config.deleteRecord(sqlDel, task_id);
                                    break;
                                }
                                else {
                                    System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                                }                       
                        break;
                    case 2://delete medicine
                        view_medicine(c_id, u_id);
                            System.out.println("\n============================================");
                                System.out.print("\nSelect medicine [#]: ");
                                int option1 = scan.nextInt();

                                sqlFetch = "SELECT * FROM medicine WHERE c_id = ?";
                                List<Map<String, Object>> result2 = config.fetchRecords(sqlFetch, c_id);
                                if (option1 >= 1 && option1 <= result2.size()) {
                                    Map<String, Object> selected = result2.get(option1 - 1);
                                    medicine_id = (int) selected.get("m_id");
                                    sqlDel = "DELETE *  FROM medicine WHERE m_id = ?";    
                                    config.deleteRecord(sqlDel, medicine_id);
                                    break;
                                }
                                else {
                                    System.out.println(ANSI_RED + "INVALID OPTION" + ANSI_RESET);
                                }    
                        break;
                    case 3://exit
                        break;
                }
            }

            
            
} //end off tester class     
            
            
            
            
            
            
            
            