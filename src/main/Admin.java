package main;

import config.Config;
import java.util.*;

public class Admin {
   public static Scanner scan = new Scanner(System.in);
   public static Config config = new Config();
   public static String sqlAdd,sqlDel,sqlUpd,sqlView, sqlFetch;
   public static int choice;
   
    public static void admin(String adminUId) {
        while (true) {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. View Existing Accounts");
            System.out.println("2. Approve New Accounts");
            System.out.println("3. Approve Account Deactivation");
            System.out.println("4. Manage Caretaker–Patient Connections");
            System.out.println("5. Monitor Careplans and Tasks");
            System.out.println("6. Edit Own Account Credentials");
            System.out.println("7. Deactivate Account");
            System.out.println("8. Exit to Login");
            System.out.println("9. Close Program");
            System.out.print("Enter choice: ");
            choice = scan.nextInt();

            switch (choice) {
                case 1: viewExistingAccounts(); break;
                case 2: approveNewAccounts(); break;
                case 3: approveAccountDeactivation(); break;
                case 4: manageConnections(); break;
                case 5: monitorCareplansAndTasks(); break;
                case 6: editAccountCredentials(adminUId); break;
                case 7: deactivateAccount(adminUId); break;
                case 8: return; // back to login
                case 9: System.exit(0); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ---------------------------
    // Case 1 - View Existing Accounts
    // ---------------------------
    private static void viewExistingAccounts() {
        while (true) {
            System.out.println("\n--- VIEW EXISTING ACCOUNTS ---");
            sqlFetch = "SELECT u_id, u_fname, u_lname, email FROM users WHERE role = ?";
            List<Map<String, Object>> patients = config.fetchRecords(sqlFetch, "patient");
            sqlFetch = "SELECT u_id, u_fname, u_lname, email FROM users WHERE role = ?";
            List<Map<String, Object>> caretakers = config.fetchRecords(sqlFetch, "patient");
            
            System.out.println("\nPatients:");
            for (int i = 0; i < patients.size(); i++) {
                Map<String, Object> p = patients.get(0);
                System.out.printf("%d. %s %s - %s (u_id=%s)\n", i, p.get("u_fname"), p.get("u_lname"), p.get("email"), p.get("u_id"));
            }
            System.out.println("\nCaretakers:");
            for (int i = 0; i < caretakers.size(); i++) {
                Map<String, Object> c = caretakers.get(0);
                System.out.printf("%d. %s %s - %s (u_id=%s)\n", i, c.get("u_fname"), c.get("u_lname"), c.get("email"), c.get("u_id"));
            }

            System.out.println("============================================");
            System.out.println("1. View patient full account info");
            System.out.println("2. View caretaker full account info");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = scan.nextInt();

            if ("1".equals(choice)) {
                if (patients.isEmpty()) {
                    System.out.println("No patients available.");
                    System.out.println("Press any key to continue. . .");
                    scan.nextLine();
                    continue;
                }
                System.out.print("Enter patient number: ");
                String numStr = scan.nextLine().trim();
                int idx = parseIndex(numStr, patients.size());
                if (idx == -1) continue;
                Map<String, Object> p = patients.get(idx);
                showAccountDetails((String)p.get("u_id"));
                waitForEnter();
            } else if ("2".equals(choice)) {
                if (caretakers.isEmpty()) {
                    System.out.println("No caretakers available.");
                    continue;
                }
                System.out.print("Enter caretaker number: ");
                String numStr = scan.nextLine().trim();
                int idx = parseIndex(numStr, caretakers.size());
                if (idx == -1) continue;
                Map<String, Object> c = caretakers.get(idx);
                showAccountDetails((String)c.get("u_id"));
                waitForEnter();
            } else if ("3".equals(choice)) {
                return; // back to admin dashboard
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void showAccountDetails(String uId) {
        Map<String, Object> user = fetchSingleRow("SELECT u_fname, u_lname, email, role, status, birthday, credentials FROM users WHERE u_id = ?", uId);
        if (user == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.println("\n--- ACCOUNT INFORMATION ---");
        System.out.println("First name: " + valueOrEmpty(user.get("u_fname")));
        System.out.println("Last name:  " + valueOrEmpty(user.get("u_lname")));
        System.out.println("Email:      " + valueOrEmpty(user.get("email")));
        System.out.println("Role:       " + valueOrEmpty(user.get("role")));
        System.out.println("Status:     " + valueOrEmpty(user.get("status")));
        System.out.println("Birthday:   " + valueOrEmpty(user.get("birthday")));
        System.out.println("Credentials:" + valueOrEmpty(user.get("credentials")));
        // note: ID and password intentionally not shown
    }

    // ---------------------------
    // Case 2 - Approve New Accounts
    // ---------------------------
    private static void approveNewAccounts() {
        System.out.println("\n--- APPROVE NEW ACCOUNTS ---");
        while (true) {
            Map<String, Object> pending = fetchSingleRow("SELECT * FROM users WHERE status = 'pending' ORDER BY u_id LIMIT 1");
            if (pending == null) {
                System.out.println("No accounts pending.");
                waitForEnter();
                return;
            }
            System.out.println("Top pending account:");
            System.out.printf("Name: %s %s\nEmail: %s\nRole: %s\n",
                    pending.get("u_fname"), pending.get("u_lname"), pending.get("email"), pending.get("role"));
            System.out.println("\nOptions: 1=Approve, 2=Reject (delete), 0=Exit to Admin Dashboard");
            System.out.print("Enter choice: ");
            String choice = scan.nextLine().trim();
            if ("1".equals(choice)) {
                int updated = executeUpdate("UPDATE users SET status = 'approved' WHERE u_id = ?", (String)pending.get("u_id"));
                System.out.println(updated > 0 ? "Account approved." : "Failed to update account.");
                waitForEnter();
                return;
            } else if ("2".equals(choice)) {
                int deleted = executeUpdate("DELETE FROM users WHERE u_id = ?", (String)pending.get("u_id"));
                System.out.println(deleted > 0 ? "Account deleted." : "Failed to delete account.");
                waitForEnter();
                return;
            } else if ("0".equals(choice)) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------------------
    // Case 3 - Approve Account Deactivation
    // ---------------------------
    private static void approveAccountDeactivation() {
        System.out.println("\n--- APPROVE ACCOUNT DEACTIVATION ---");
        while (true) {
            Map<String, Object> pending = fetchSingleRow("SELECT * FROM users WHERE status = 'closing' ORDER BY u_id LIMIT 1");
            if (pending == null) {
                System.out.println("No accounts pending for deletion.");
                waitForEnter();
                return;
            }
            System.out.printf("Top closing account: %s %s (%s)\n", pending.get("u_fname"), pending.get("u_lname"), pending.get("email"));
            System.out.println("Options: 1=Approve deletion (archive), 2=Reject (restore approved), 0=Exit to Admin Dashboard");
            System.out.print("Enter choice: ");
            String choice = scan.nextLine().trim();
            if ("1".equals(choice)) {
                int updated = executeUpdate("UPDATE users SET status = 'archived' WHERE u_id = ?", (String)pending.get("u_id"));
                System.out.println(updated > 0 ? "Account archived." : "Failed to archive.");
            } else if ("2".equals(choice)) {
                int updated = executeUpdate("UPDATE users SET status = 'approved' WHERE u_id = ?", (String)pending.get("u_id"));
                System.out.println(updated > 0 ? "Account restored to approved." : "Failed to restore.");
            } else if ("0".equals(choice)) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------------------
    // Case 4 - Manage Connections
    // ---------------------------
    private static void manageConnections() {
        while (true) {
            System.out.println("\n--- MANAGE CARETAKER-PATIENT CONNECTIONS ---");
            System.out.println("1. Assign connections");
            System.out.println("2. Approve disconnection requests");
            System.out.println("3. Exit to Admin Dashboard");
            System.out.print("Enter choice: ");
            String choice = scan.nextLine().trim();
            if ("1".equals(choice)) {
                assignConnection();
            } else if ("2".equals(choice)) {
                approveDisconnectionRequests();
            } else if ("3".equals(choice)) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void assignConnection() {
        List<Map<String, Object>> patients = fetchRows("SELECT u_id, u_fname, u_lname FROM users WHERE role = 'patient' ORDER BY u_id");
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        System.out.println("\nPatients:");
        for (int i = 0; i < patients.size(); i++) {
            Map<String, Object> p = patients.get(i);
            System.out.printf("%d. %s %s (u_id=%s)\n", i + 1, p.get("u_fname"), p.get("u_lname"), p.get("u_id"));
        }
        System.out.print("Enter patient number: ");
        int pIdx = parseIndexOrReturn(scan.nextLine().trim(), patients.size());
        if (pIdx == -1) return;
        String patientId = (String)patients.get(pIdx).get("u_id");

        List<Map<String, Object>> carers = fetchRows("SELECT u_id, u_fname, u_lname FROM users WHERE role = 'caretaker' ORDER BY u_id");
        if (carers.isEmpty()) {
            System.out.println("No caretakers found.");
            return;
        }
        System.out.println("\nCaretakers:");
        for (int i = 0; i < carers.size(); i++) {
            Map<String, Object> c = carers.get(i);
            System.out.printf("%d. %s %s (u_id=%s)\n", i + 1, c.get("u_fname"), c.get("u_lname"), c.get("u_id"));
        }
        System.out.print("Enter caretaker number: ");
        int cIdx = parseIndexOrReturn(scan.nextLine().trim(), carers.size());
        if (cIdx == -1) return;
        String careId = (String)carers.get(cIdx).get("u_id");

        Map<String, Object> existing = fetchSingleRow("SELECT connect_id FROM p_to_c_connect WHERE patient_id = ?", patientId);
        if (existing != null) {
            int updated = executeUpdate("UPDATE p_to_c_connect SET care_id = ?, messages = '-' WHERE patient_id = ?", careId, patientId);
            System.out.println(updated > 0 ? "Connection updated." : "Failed to update connection.");
        } else {
            int inserted = executeUpdate("INSERT INTO p_to_c_connect (patient_id, care_id, messages) VALUES (?, ?, ?)", patientId, careId, "-");
            System.out.println(inserted > 0 ? "Connection created." : "Failed to create connection.");
        }
        waitForEnter();
    }

    private static void approveDisconnectionRequests() {
        Map<String, Object> pending = fetchSingleRow("SELECT connect_id, patient_id, care_id FROM p_to_c_connect WHERE messages = 'closing' ORDER BY connect_id LIMIT 1");
        if (pending == null) {
            System.out.println("No pending disconnection requests.");
            waitForEnter();
            return;
        }
        System.out.printf("Pending disconnection: connect_id=%s, patient_id=%s, care_id=%s\n",
                pending.get("connect_id"), pending.get("patient_id"), pending.get("care_id"));
        System.out.println("Options: 1=Approve (delete connection), 2=Reject (set message back to '-') , 0=Exit");
        System.out.print("Enter choice: ");
        String choice = scan.nextLine().trim();
        if ("1".equals(choice)) {
            int deleted = executeUpdate("DELETE FROM p_to_c_connect WHERE connect_id = ?", (String)pending.get("connect_id"));
            System.out.println(deleted > 0 ? "Connection deleted." : "Failed to delete connection.");
        } else if ("2".equals(choice)) {
            int updated = executeUpdate("UPDATE p_to_c_connect SET messages = '-' WHERE connect_id = ?", (String)pending.get("connect_id"));
            System.out.println(updated > 0 ? "Connection restored." : "Failed to update connection.");
        } else {
            System.out.println("Cancelled.");
        }
        waitForEnter();
    }

    // ---------------------------
    // Case 5 - Monitor Careplans and Tasks
    // ---------------------------
    private static void monitorCareplansAndTasks() {
        while (true) {
            System.out.println("\n--- MONITOR CAREPLANS AND TASKS ---");
            // Get list of careplans with corresponding patient name
            List<Map<String, Object>> careplans = fetchRows(
                    "SELECT c.c_id, c.c_name, c.status, u.u_id as patient_id, u.u_fname, u.u_lname " +
                            "FROM careplan c JOIN users u ON c.u_id_patient = u.u_id ORDER BY u.u_id, c.c_id");
            if (careplans.isEmpty()) {
                System.out.println("No careplans found.");
                waitForEnter();
                return;
            }
            System.out.println("Careplans (numbered):");
            for (int i = 0; i < careplans.size(); i++) {
                Map<String, Object> cp = careplans.get(i);
                System.out.printf("%d. Careplan ID:%s Name:%s (Patient: %s %s, status:%s)\n",
                        i + 1, cp.get("c_id"), cp.get("c_name"), cp.get("u_fname"), cp.get("u_lname"), cp.get("status"));
            }
            System.out.print("Enter careplan number to manage (0 to return to Admin Dashboard): ");
            String input = scan.nextLine().trim();
            int idx = parseIndexOrZero(input, careplans.size());
            if (idx == -2) continue; // invalid input
            if (idx == -1) return; // 0 pressed
            Map<String, Object> chosen = careplans.get(idx);
            String cId = (String) chosen.get("c_id");

            // actions for chosen careplan
            while (true) {
                System.out.println("\nCareplan Menu for c_id=" + cId);
                System.out.println("1. Delete Careplan (set status='archived')");
                System.out.println("2. Edit Careplan");
                System.out.println("3. Open Careplan (manage tasks/medicines)");
                System.out.println("4. Back to careplan list");
                System.out.print("Enter choice: ");
                String choice = scan.nextLine().trim();
                if ("1".equals(choice)) {
                    int updated = executeUpdate("UPDATE careplan SET status = 'archived' WHERE c_id = ?", cId);
                    System.out.println(updated > 0 ? "Careplan archived." : "Failed to archive careplan.");
                } else if ("2".equals(choice)) {
                    // Now calls the implemented editCareplan which will attempt to call existing functions first.
                    editCareplan(cId);
                } else if ("3".equals(choice)) {
                    openCareplan(cId);
                    break; // after openCareplan return to top of careplan list
                } else if ("4".equals(choice)) {
                    break;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
            // go back to listing
        }
    }

    private static void openCareplan(String cId) {
        while (true) {
            System.out.println("\n--- Careplan Contents (c_id=" + cId + ") ---");
            List<Map<String, Object>> tasks = fetchRows("SELECT t_id, t_type, t_desc, t_time, t_status FROM tasks WHERE c_id = ? ORDER BY t_id", cId);
            List<Map<String, Object>> meds = fetchRows("SELECT m_id, m_name, m_frequency, m_instruct, m_for FROM medicine WHERE c_id = ? ORDER BY m_id", cId);

            System.out.println("\nTasks:");
            for (int i = 0; i < tasks.size(); i++) {
                Map<String, Object> t = tasks.get(i);
                System.out.printf("T%d. [%s] %s at %s (status=%s) (t_id=%s)\n", i + 1, t.get("t_type"), t.get("t_desc"), t.get("t_time"), t.get("t_status"), t.get("t_id"));
            }
            System.out.println("\nMedicines:");
            for (int i = 0; i < meds.size(); i++) {
                Map<String, Object> m = meds.get(i);
                System.out.printf("M%d. %s (freq:%s, instruct:%s) (m_id=%s)\n", i + 1, m.get("m_name"), m.get("m_frequency"), m.get("m_instruct"), m.get("m_id"));
            }

            System.out.println("\nOptions:");
            System.out.println("1. Delete task");
            System.out.println("2. Delete medicine");
            System.out.println("3. Edit task");
            System.out.println("4. Edit medicine");
            System.out.println("5. Exit to Admin Dashboard");
            System.out.print("Enter choice: ");
            String choice = scan.nextLine().trim();
            if ("1".equals(choice)) {
                if (tasks.isEmpty()) { System.out.println("No tasks to delete."); continue; }
                System.out.print("Enter task number to delete: ");
                int idx = parseIndexOrReturn(scan.nextLine().trim(), tasks.size());
                if (idx == -1) continue;
                String tId = (String) tasks.get(idx).get("t_id");
                int updated = executeUpdate("UPDATE tasks SET t_status = 'archived' WHERE t_id = ?", tId);
                System.out.println(updated > 0 ? "Task archived." : "Failed to archive task.");
            } else if ("2".equals(choice)) {
                if (meds.isEmpty()) { System.out.println("No medicines to delete."); continue; }
                System.out.print("Enter medicine number to delete: ");
                int idx = parseIndexOrReturn(scan.nextLine().trim(), meds.size());
                if (idx == -1) continue;
                String mId = (String) meds.get(idx).get("m_id");
                int deleted = executeUpdate("DELETE FROM medicine WHERE m_id = ?", mId);
                System.out.println(deleted > 0 ? "Medicine deleted." : "Failed to delete medicine.");
            } else if ("3".equals(choice)) {
                if (tasks.isEmpty()) { System.out.println("No tasks to edit."); continue; }
                System.out.print("Enter task number to edit: ");
                int idx = parseIndexOrReturn(scan.nextLine().trim(), tasks.size());
                if (idx == -1) continue;
                String tId = (String) tasks.get(idx).get("t_id");
                editTask(tId);
            } else if ("4".equals(choice)) {
                if (meds.isEmpty()) { System.out.println("No medicines to edit."); continue; }
                System.out.print("Enter medicine number to edit: ");
                int idx = parseIndexOrReturn(scan.nextLine().trim(), meds.size());
                if (idx == -1) continue;
                String mId = (String) meds.get(idx).get("m_id");
                editMedicine(mId);
            } else if ("5".equals(choice)) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------------------
    // Case 6 - Edit Own Account Credentials
    // ---------------------------
    private static void editAccountCredentials(String adminUId) {
        System.out.println("\n--- EDIT OWN ACCOUNT CREDENTIALS ---");
        Map<String, Object> me = fetchSingleRow("SELECT u_fname, u_lname, email FROM users WHERE u_id = ?", adminUId);
        if (me == null) {
            System.out.println("Admin account not found.");
            return;
        }
        System.out.println("Current values (press ENTER to keep current):");
        System.out.print("First name [" + me.get("u_fname") + "]: ");
        String fn = scan.nextLine().trim();
        if (fn.isEmpty()) fn = (String)me.get("u_fname");
        System.out.print("Last name [" + me.get("u_lname") + "]: ");
        String ln = scan.nextLine().trim();
        if (ln.isEmpty()) ln = (String)me.get("u_lname");
        System.out.print("Email [" + me.get("email") + "]: ");
        String email = scan.nextLine().trim();
        if (email.isEmpty()) email = (String)me.get("email");
        System.out.print("New password (leave blank to keep current): ");
        String pw = scan.nextLine();
        if (pw.trim().isEmpty()) {
            int updated = executeUpdate("UPDATE users SET u_fname = ?, u_lname = ?, email = ? WHERE u_id = ?", fn, ln, email, adminUId);
            System.out.println(updated > 0 ? "Account updated." : "Failed to update.");
        } else {
            // Consider hashing password in production
            int updated = executeUpdate("UPDATE users SET u_fname = ?, u_lname = ?, email = ?, password = ? WHERE u_id = ?", fn, ln, email, pw, adminUId);
            System.out.println(updated > 0 ? "Account updated (password changed)." : "Failed to update.");
        }
        waitForEnter();
    }

    // ---------------------------
    // Case 7 - Deactivate Account (admin's own)
    // ---------------------------
    private static void deactivateAccount(String adminUId) {
        System.out.println("\n--- DEACTIVATE ACCOUNT ---");
        System.out.print("Are you sure you want to deactivate your account? (y/N): ");
        String ans = scan.nextLine().trim().toLowerCase();
        if ("y".equals(ans) || "yes".equals(ans)) {
            int updated = executeUpdate("UPDATE users SET status = 'closing' WHERE u_id = ?", adminUId);
            System.out.println(updated > 0 ? "Account set to closing." : "Failed to update.");
        } else {
            System.out.println("Cancelled. Returning to dashboard.");
        }
    }

    // ---------------------------
    // EDIT FUNCTIONS (try existing project functions first, fallback to local editor)
    // ---------------------------
    private static void editCareplan(String cId) {
        // Try to invoke external method first: common names checked
        if (tryInvokeExternalEdit("TesterClass", "editCareplan", cId)) return;
        if (tryInvokeExternalEdit("TesterClass", "editCarePlan", cId)) return;
        if (tryInvokeExternalEdit("SomeOtherClass", "editCareplan", cId)) return; // extra attempt

        // Fallback: interactive in-place editor
        Map<String, Object> cp = fetchSingleRow("SELECT c_title, start_date, end_date, description, status, c_name FROM careplan WHERE c_id = ?", cId);
        if (cp == null) {
            System.out.println("Careplan not found.");
            return;
        }
        System.out.println("\nEditing Careplan (press ENTER to keep current):");
        String cTitle = promptKeep("Title", cp.get("c_title"));
        String startDate = promptKeep("Start date (YYYY-MM-DD)", cp.get("start_date"));
        String endDate = promptKeep("End date (YYYY-MM-DD)", cp.get("end_date"));
        String description = promptKeep("Description", cp.get("description"));
        String status = promptKeep("Status", cp.get("status"));
        String cName = promptKeep("Careplan display name", cp.get("c_name"));

        int updated = executeUpdate("UPDATE careplan SET c_title = ?, start_date = ?, end_date = ?, description = ?, status = ?, c_name = ? WHERE c_id = ?",
                cTitle, startDate, endDate, description, status, cName, cId);
        System.out.println(updated > 0 ? "Careplan updated." : "Failed to update careplan.");
        waitForEnter();
    }

    private static void editTask(String tId) {
        if (tryInvokeExternalEdit("TesterClass", "editTask", tId)) return;
        if (tryInvokeExternalEdit("TesterClass", "edit_task", tId)) return;

        Map<String, Object> t = fetchSingleRow("SELECT t_type, t_desc, t_time, t_status FROM tasks WHERE t_id = ?", tId);
        if (t == null) {
            System.out.println("Task not found.");
            return;
        }
        System.out.println("\nEditing Task (press ENTER to keep current):");
        String type = promptKeep("Type", t.get("t_type"));
        String desc = promptKeep("Description", t.get("t_desc"));
        String time = promptKeep("Time (HH:MM)", t.get("t_time"));
        String status = promptKeep("Status", t.get("t_status"));

        int updated = executeUpdate("UPDATE tasks SET t_type = ?, t_desc = ?, t_time = ?, t_status = ? WHERE t_id = ?", type, desc, time, status, tId);
        System.out.println(updated > 0 ? "Task updated." : "Failed to update task.");
        waitForEnter();
    }

    private static void editMedicine(String mId) {
        if (tryInvokeExternalEdit("TesterClass", "editMedicine", mId)) return;
        if (tryInvokeExternalEdit("TesterClass", "edit_med", mId)) return;

        Map<String, Object> m = fetchSingleRow("SELECT m_name, m_frequency, m_instruct, m_for FROM medicine WHERE m_id = ?", mId);
        if (m == null) {
            System.out.println("Medicine not found.");
            return;
        }
        System.out.println("\nEditing Medicine (press ENTER to keep current):");
        String name = promptKeep("Name", m.get("m_name"));
        String freq = promptKeep("Frequency", m.get("m_frequency"));
        String instruct = promptKeep("Instructions", m.get("m_instruct"));
        String mfor = promptKeep("For (who/which)", m.get("m_for"));

        int updated = executeUpdate("UPDATE medicine SET m_name = ?, m_frequency = ?, m_instruct = ?, m_for = ? WHERE m_id = ?", name, freq, instruct, mfor, mId);
        System.out.println(updated > 0 ? "Medicine updated." : "Failed to update medicine.");
        waitForEnter();
    }

    /**
     * Attempt to invoke an external static method with signature (String) via reflection.
     * Returns true if method was found & invoked (even if invocation threw an exception),
     * false if method not found.
     */
    private static boolean tryInvokeExternalEdit(String className, String methodName, String arg) {
        try {
            Class<?> cls = Class.forName(className);
            Method m = cls.getMethod(methodName, String.class);
            // invoke static method
            m.invoke(null, arg);
            System.out.println("Delegated to existing method: " + className + "." + methodName + "(\"" + arg + "\")");
            return true;
        } catch (ClassNotFoundException cnfe) {
            return false;
        } catch (NoSuchMethodException nsme) {
            return false;
        } catch (Throwable t) {
            // method exists but threw exception — report and fall back to local editor
            System.out.println("Found method " + className + "." + methodName + " but invocation threw: " + t.getMessage());
            System.out.println("Falling back to built-in editor.");
            return false;
        }
    }

    private static String promptKeep(String prompt, Object currentVal) {
        System.out.print(prompt + " [" + (currentVal == null ? "" : currentVal.toString()) + "]: ");
        String input = scan.nextLine();
        if (input == null || input.trim().isEmpty()) {
            return currentVal == null ? "" : currentVal.toString();
        }
        return input.trim();
    }

    // ---------------------------
    // Database utility helpers
    // ---------------------------
    private static List<Map<String, Object>> fetchRows(String sql, Object... params) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= cols; i++) {
                        Object v = rs.getObject(i);
                        row.put(md.getColumnName(i), v == null ? null : v.toString());
                    }
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("DB error (fetchRows): " + e.getMessage());
        }
        return rows;
    }

    private static Map<String, Object> fetchSingleRow(String sql, Object... params) {
        List<Map<String, Object>> rows = fetchRows(sql, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * Execute update/insert/delete and return number of affected rows.
     */
    private static int executeUpdate(String sql, Object... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("DB error (executeUpdate): " + e.getMessage());
            return 0;
        }
    }

    private static void setParams(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            ps.setObject(i + 1, p);
        }
    }   
}
