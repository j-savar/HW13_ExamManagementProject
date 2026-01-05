package ir.oliateaching.main;

import ir.oliateaching.utils.ApplicationContext;
import ir.oliateaching.dto.*;
import ir.oliateaching.enums.*;
import ir.oliateaching.domains.*;
import ir.oliateaching.services.*;
import ir.oliateaching.validator.DTOValidator;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class ExamSystemApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static UserService userService;
    private static CourseService courseService;
    private static AdminService adminService;

    private static User currentUser;
    private static boolean isAdmin;
    private static boolean isTeacher;
    private static boolean isStudent;

    public static void main(String[] args) {
        try {
            initializeServices();
            adminService.initializeAdminAccount();

            System.out.println("🎓 Welcome to Exam Management System 🎓");
            System.out.println("=======================================");

            while (true) {
                if (currentUser == null) {
                    showMainMenu();
                } else {
                    if (isAdmin) {
                        showAdminMenu();
                    } else if (isTeacher) {
                        showTeacherMenu();
                    } else if (isStudent) {
                        showStudentMenu();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
        } finally {
            ApplicationContext.shutdown();
            scanner.close();
        }
    }

    private static void initializeServices() {
        EntityManager entityManager = ApplicationContext.getEntityManager();
        userService = new UserService(entityManager);
        courseService = new CourseService(entityManager);
        adminService = new AdminService(entityManager);
    }

    private static void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.print("Select an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    signUp();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void login() {
        System.out.println("\n=== LOGIN ===");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (userService.authenticate(username, password)) {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                updateUserRoles();

                System.out.println("\n✅ Login successful!");
                System.out.println("Welcome, " + currentUser.getFullName() + "!");

                if (!currentUser.isApproved()) {
                    System.out.println("⚠️  Your account is pending approval. Please wait for admin approval.");
                    currentUser = null;
                    updateUserRoles();
                }
            }
        } else {
            System.out.println("❌ Invalid username, password, or account not approved!");
        }
    }

    private static void signUp() {
        System.out.println("\n=== SIGN UP ===");

        UserSignUpDTO dto = new UserSignUpDTO();

        try {
            System.out.print("Username: ");
            dto.setUsername(scanner.nextLine().trim());

            System.out.print("Password: ");
            dto.setPassword(scanner.nextLine().trim());

            System.out.print("First Name: ");
            dto.setFirstName(scanner.nextLine().trim());

            System.out.print("Last Name: ");
            dto.setLastName(scanner.nextLine().trim());

            System.out.print("Email: ");
            dto.setEmail(scanner.nextLine().trim());

            System.out.print("Phone Number (optional, format: 09123456789): ");
            String phone = scanner.nextLine().trim();
            if (!phone.isEmpty()) dto.setPhoneNumber(phone);

            System.out.print("National ID (optional, 10 digits): ");
            String nationalId = scanner.nextLine().trim();
            if (!nationalId.isEmpty()) dto.setNationalId(nationalId);

            System.out.println("\nSelect Role:");
            System.out.println("1. Student");
            System.out.println("2. Teacher");
            System.out.print("Choose role (1-2): ");

            int roleChoice = Integer.parseInt(scanner.nextLine());
            if (roleChoice == 1) {
                dto.setRole(UserRole.STUDENT);

                System.out.print("Student Code (optional): ");
                dto.setStudentCode(scanner.nextLine().trim());

                System.out.print("Field of Study (optional): ");
                dto.setFieldOfStudy(scanner.nextLine().trim());

                System.out.print("Entry Year (optional): ");
                String year = scanner.nextLine().trim();
                if (!year.isEmpty()) dto.setEntryYear(Integer.parseInt(year));

            } else if (roleChoice == 2) {
                dto.setRole(UserRole.TEACHER);

                System.out.print("Teacher Code (optional): ");
                dto.setTeacherCode(scanner.nextLine().trim());

                System.out.print("Expertise (optional): ");
                dto.setExpertise(scanner.nextLine().trim());

                System.out.print("Degree (optional): ");
                dto.setDegree(scanner.nextLine().trim());

            } else {
                System.out.println("Invalid role selection!");
                return;
            }

            // Validate DTO
            String validationMessage = DTOValidator.getValidationMessages(dto);
            if (!validationMessage.equals("Data is valid.")) {
                System.out.println("❌ " + validationMessage);
                return;
            }

            User user = userService.signUp(dto);
            System.out.println("\n✅ Registration successful!");
            System.out.println("Your account is pending admin approval. You will be notified once approved.");

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format!");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void updateUserRoles() {
        if (currentUser != null) {
            isAdmin = currentUser.getRole() == UserRole.ADMIN;
            isTeacher = currentUser.getRole() == UserRole.TEACHER;
            isStudent = currentUser.getRole() == UserRole.STUDENT;
        }
    }

    private static void logout() {
        System.out.println("\n✅ Logged out successfully!");
        currentUser = null;
        isAdmin = false;
        isTeacher = false;
        isStudent = false;
    }

    // ==================== ADMIN MENU ====================
    private static void showAdminMenu() {
        System.out.println("\n=== ADMIN PANEL ===");
        System.out.println("Logged in as: " + currentUser.getFullName());
        System.out.println("1. Manage Users");
        System.out.println("2. Manage Courses");
        System.out.println("3. View Statistics");
        System.out.println("4. Logout");
        System.out.print("Select an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    manageUsers();
                    break;
                case 2:
                    manageCourses();
                    break;
                case 3:
                    viewStatistics();
                    break;
                case 4:
                    logout();
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void manageUsers() {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== USER MANAGEMENT ===");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending Approvals");
            System.out.println("3. Approve/Reject User");
            System.out.println("4. Edit User");
            System.out.println("5. Search Users");
            System.out.println("6. Delete User");
            System.out.println("7. Back to Admin Menu");
            System.out.print("Select an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllUsers();
                        break;
                    case 2:
                        viewPendingApprovals();
                        break;
                    case 3:
                        approveRejectUser();
                        break;
                    case 4:
                        editUser();
                        break;
                    case 5:
                        searchUsers();
                        break;
                    case 6:
                        deleteUser();
                        break;
                    case 7:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private static void viewAllUsers() {
        List<User> users = adminService.getAllUsersForAdmin();

        System.out.println("\n=== ALL USERS ===");
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.printf("%-5s %-15s %-20s %-15s %-12s %-10s%n",
                    "ID", "Username", "Full Name", "Role", "Status", "Registered");
            System.out.println("--------------------------------------------------------------------------------");

            for (User user : users) {
                System.out.printf("%-5d %-15s %-20s %-15s %-12s %-10s%n",
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole(),
                        user.getStatus(),
                        user.getRegistrationDate().toLocalDate()
                );
            }
            System.out.println("Total: " + users.size() + " users");
        }
    }

    private static void viewPendingApprovals() {
        List<User> pendingUsers = adminService.getPendingRegistrations();

        System.out.println("\n=== PENDING APPROVALS ===");
        if (pendingUsers.isEmpty()) {
            System.out.println("No pending approvals.");
        } else {
            System.out.printf("%-5s %-15s %-20s %-15s %-12s%n",
                    "ID", "Username", "Full Name", "Role", "Phone");
            System.out.println("-------------------------------------------------------");

            for (User user : pendingUsers) {
                System.out.printf("%-5d %-15s %-20s %-15s %-12s%n",
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole(),
                        user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"
                );
            }
        }
    }

    private static void approveRejectUser() {
        System.out.print("Enter User ID to approve/reject: ");
        try {
            Long userId = Long.parseLong(scanner.nextLine());

            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                System.out.println("\nUser Details:");
                System.out.println("ID: " + user.getId());
                System.out.println("Name: " + user.getFullName());
                System.out.println("Role: " + user.getRole());
                System.out.println("Status: " + user.getStatus());

                System.out.println("\n1. Approve");
                System.out.println("2. Reject");
                System.out.print("Select action: ");

                int action = Integer.parseInt(scanner.nextLine());

                if (action == 1) {
                    User approvedUser = adminService.approveUserRegistration(userId, currentUser.getId());
                    System.out.println("✅ User approved successfully!");
                } else if (action == 2) {
                    User rejectedUser = adminService.rejectUserRegistration(userId, currentUser.getId());
                    System.out.println("❌ User rejected!");
                } else {
                    System.out.println("Invalid action!");
                }
            } else {
                System.out.println("User not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void editUser() {
        System.out.print("Enter User ID to edit: ");
        try {
            Long userId = Long.parseLong(scanner.nextLine());

            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserUpdateDTO dto = new UserUpdateDTO();
                dto.setId(userId);

                System.out.println("\nCurrent Details:");
                System.out.println("1. First Name: " + user.getFirstName());
                System.out.println("2. Last Name: " + user.getLastName());
                System.out.println("3. Email: " + user.getEmail());
                System.out.println("4. Phone: " + user.getPhoneNumber());
                System.out.println("5. Role: " + user.getRole());
                System.out.println("6. Status: " + user.getStatus());

                System.out.print("\nEnter new First Name (press Enter to keep current): ");
                String firstName = scanner.nextLine().trim();
                dto.setFirstName(firstName.isEmpty() ? user.getFirstName() : firstName);

                System.out.print("Enter new Last Name (press Enter to keep current): ");
                String lastName = scanner.nextLine().trim();
                dto.setLastName(lastName.isEmpty() ? user.getLastName() : lastName);

                System.out.print("Enter new Email (press Enter to keep current): ");
                String email = scanner.nextLine().trim();
                dto.setEmail(email.isEmpty() ? user.getEmail() : email);

                System.out.print("Enter new Phone (press Enter to keep current): ");
                String phone = scanner.nextLine().trim();
                dto.setPhoneNumber(phone.isEmpty() ? user.getPhoneNumber() : phone);

                System.out.print("Enter new Role (STUDENT/TEACHER/ADMIN, press Enter to keep current): ");
                String roleStr = scanner.nextLine().trim();
                if (!roleStr.isEmpty()) {
                    try {
                        dto.setRole(UserRole.valueOf(roleStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid role! Keeping current role.");
                        dto.setRole(user.getRole());
                    }
                } else {
                    dto.setRole(user.getRole());
                }

                System.out.print("Enter new Status (PENDING/APPROVED/REJECTED/SUSPENDED, press Enter to keep current): ");
                String statusStr = scanner.nextLine().trim();
                if (!statusStr.isEmpty()) {
                    try {
                        dto.setStatus(UserStatus.valueOf(statusStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid status! Keeping current status.");
                        dto.setStatus(user.getStatus());
                    }
                } else {
                    dto.setStatus(user.getStatus());
                }

                // Validate and update
                String validationMessage = DTOValidator.getValidationMessages(dto);
                if (!validationMessage.equals("Data is valid.")) {
                    System.out.println("❌ " + validationMessage);
                    return;
                }

                User updatedUser = userService.updateUser(dto);
                System.out.println("✅ User updated successfully!");

            } else {
                System.out.println("User not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }

    private static void searchUsers() {
        System.out.println("\n=== SEARCH USERS ===");

        System.out.print("First Name (optional, press Enter to skip): ");
        String firstName = scanner.nextLine().trim();
        if (firstName.isEmpty()) firstName = null;

        System.out.print("Last Name (optional, press Enter to skip): ");
        String lastName = scanner.nextLine().trim();
        if (lastName.isEmpty()) lastName = null;

        UserRole role = null;
        System.out.print("Role (STUDENT/TEACHER/ADMIN, optional, press Enter to skip): ");
        String roleStr = scanner.nextLine().trim();
        if (!roleStr.isEmpty()) {
            try {
                role = UserRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid role! Ignoring role filter.");
            }
        }

        UserStatus status = null;
        System.out.print("Status (PENDING/APPROVED/REJECTED/SUSPENDED, optional, press Enter to skip): ");
        String statusStr = scanner.nextLine().trim();
        if (!statusStr.isEmpty()) {
            try {
                status = UserStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status! Ignoring status filter.");
            }
        }

        List<User> users = userService.searchUsers(firstName, lastName, role, status);

        System.out.println("\n=== SEARCH RESULTS ===");
        if (users.isEmpty()) {
            System.out.println("No users found matching your criteria.");
        } else {
            System.out.printf("%-5s %-15s %-20s %-15s %-12s %-10s%n",
                    "ID", "Username", "Full Name", "Role", "Status", "Email");
            System.out.println("--------------------------------------------------------------------------------");

            for (User user : users) {
                System.out.printf("%-5d %-15s %-20s %-15s %-12s %-10s%n",
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole(),
                        user.getStatus(),
                        user.getEmail()
                );
            }
            System.out.println("Found: " + users.size() + " users");
        }
    }

    private static void deleteUser() {
        System.out.print("Enter User ID to delete: ");
        try {
            Long userId = Long.parseLong(scanner.nextLine());

            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                System.out.println("Are you sure you want to delete user: " + user.getFullName() + "?");
                System.out.print("Type 'YES' to confirm: ");
                String confirmation = scanner.nextLine().trim();

                if (confirmation.equalsIgnoreCase("YES")) {
                    userService.deleteUser(userId);
                    System.out.println("✅ User deleted successfully!");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            } else {
                System.out.println("User not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }

    private static void manageCourses() {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== COURSE MANAGEMENT ===");
            System.out.println("1. View All Courses");
            System.out.println("2. Create New Course");
            System.out.println("3. Edit Course");
            System.out.println("4. Assign Teacher to Course");
            System.out.println("5. Add Student to Course");
            System.out.println("6. View Course Participants");
            System.out.println("7. Search Courses");
            System.out.println("8. Delete Course");
            System.out.println("9. Back to Admin Menu");
            System.out.print("Select an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllCourses();
                        break;
                    case 2:
                        createCourse();
                        break;
                    case 3:
                        editCourse();
                        break;
                    case 4:
                        assignTeacherToCourse();
                        break;
                    case 5:
                        addStudentToCourse();
                        break;
                    case 6:
                        viewCourseParticipants();
                        break;
                    case 7:
                        searchCourses();
                        break;
                    case 8:
                        deleteCourse();
                        break;
                    case 9:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private static void viewAllCourses() {
        List<Course> courses = adminService.getAllCourses();

        System.out.println("\n=== ALL COURSES ===");
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            System.out.printf("%-5s %-10s %-30s %-15s %-12s %-12s %-10s%n",
                    "ID", "Code", "Title", "Teacher", "Start Date", "End Date", "Status");
            System.out.println("---------------------------------------------------------------------------------------------------");

            for (Course course : courses) {
                System.out.printf("%-5d %-10s %-30s %-15s %-12s %-12s %-10s%n",
                        course.getId(),
                        course.getCourseCode(),
                        course.getTitle().length() > 30 ? course.getTitle().substring(0, 27) + "..." : course.getTitle(),
                        course.getTeacher() != null ? course.getTeacher().getLastName() : "Not assigned",
                        course.getStartDate(),
                        course.getEndDate(),
                        course.getStatus()
                );
            }
            System.out.println("Total: " + courses.size() + " courses");
        }
    }

    private static void createCourse() {
        System.out.println("\n=== CREATE NEW COURSE ===");

        CourseDTO dto = new CourseDTO();

        try {
            System.out.print("Course Code: ");
            dto.setCourseCode(scanner.nextLine().trim());

            System.out.print("Title: ");
            dto.setTitle(scanner.nextLine().trim());

            System.out.print("Description (optional): ");
            dto.setDescription(scanner.nextLine().trim());

            System.out.print("Start Date (yyyy-MM-dd): ");
            dto.setStartDate(LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER));

            System.out.print("End Date (yyyy-MM-dd): ");
            dto.setEndDate(LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER));

            System.out.print("Credit (1-6, optional): ");
            String creditStr = scanner.nextLine().trim();
            if (!creditStr.isEmpty()) dto.setCredit(Integer.parseInt(creditStr));

            System.out.print("Status (PLANNED/ACTIVE/COMPLETED/CANCELLED): ");
            dto.setStatus(CourseStatus.valueOf(scanner.nextLine().trim().toUpperCase()));

            System.out.print("Teacher ID (optional, press Enter to skip): ");
            String teacherIdStr = scanner.nextLine().trim();
            if (!teacherIdStr.isEmpty()) dto.setTeacherId(Long.parseLong(teacherIdStr));

            // Validate DTO
            String validationMessage = DTOValidator.getValidationMessages(dto);
            if (!validationMessage.equals("Data is valid.")) {
                System.out.println("❌ " + validationMessage);
                return;
            }

            Course course = courseService.createCourse(dto);
            System.out.println("✅ Course created successfully!");
            System.out.println("Course ID: " + course.getId());

        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format! Use yyyy-MM-dd");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format!");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void editCourse() {
        System.out.print("Enter Course ID to edit: ");
        try {
            Long courseId = Long.parseLong(scanner.nextLine());

            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                CourseDTO dto = new CourseDTO();

                System.out.println("\nCurrent Details:");
                System.out.println("1. Course Code: " + course.getCourseCode());
                System.out.println("2. Title: " + course.getTitle());
                System.out.println("3. Description: " + course.getDescription());
                System.out.println("4. Start Date: " + course.getStartDate());
                System.out.println("5. End Date: " + course.getEndDate());
                System.out.println("6. Credit: " + course.getCredit());
                System.out.println("7. Status: " + course.getStatus());
                System.out.println("8. Teacher: " + (course.getTeacher() != null ? course.getTeacher().getFullName() : "Not assigned"));

                System.out.print("\nEnter new Course Code (press Enter to keep current): ");
                String code = scanner.nextLine().trim();
                dto.setCourseCode(code.isEmpty() ? course.getCourseCode() : code);

                System.out.print("Enter new Title (press Enter to keep current): ");
                dto.setTitle(scanner.nextLine().trim());
                if (dto.getTitle().isEmpty()) dto.setTitle(course.getTitle());

                System.out.print("Enter new Description (press Enter to keep current): ");
                dto.setDescription(scanner.nextLine().trim());
                if (dto.getDescription().isEmpty()) dto.setDescription(course.getDescription());

                System.out.print("Enter new Start Date (yyyy-MM-dd, press Enter to keep current): ");
                String startDateStr = scanner.nextLine().trim();
                if (!startDateStr.isEmpty()) {
                    dto.setStartDate(LocalDate.parse(startDateStr, DATE_FORMATTER));
                } else {
                    dto.setStartDate(course.getStartDate());
                }

                System.out.print("Enter new End Date (yyyy-MM-dd, press Enter to keep current): ");
                String endDateStr = scanner.nextLine().trim();
                if (!endDateStr.isEmpty()) {
                    dto.setEndDate(LocalDate.parse(endDateStr, DATE_FORMATTER));
                } else {
                    dto.setEndDate(course.getEndDate());
                }

                System.out.print("Enter new Credit (press Enter to keep current): ");
                String creditStr = scanner.nextLine().trim();
                if (!creditStr.isEmpty()) {
                    dto.setCredit(Integer.parseInt(creditStr));
                } else {
                    dto.setCredit(course.getCredit());
                }

                System.out.print("Enter new Status (press Enter to keep current): ");
                String statusStr = scanner.nextLine().trim();
                if (!statusStr.isEmpty()) {
                    dto.setStatus(CourseStatus.valueOf(statusStr.toUpperCase()));
                } else {
                    dto.setStatus(course.getStatus());
                }

                System.out.print("Enter new Teacher ID (press Enter to keep current, 0 to remove): ");
                String teacherIdStr = scanner.nextLine().trim();
                if (!teacherIdStr.isEmpty()) {
                    if (teacherIdStr.equals("0")) {
                        dto.setTeacherId(null);
                    } else {
                        dto.setTeacherId(Long.parseLong(teacherIdStr));
                    }
                } else {
                    dto.setTeacherId(course.getTeacher() != null ? course.getTeacher().getId() : null);
                }

                // Validate and update
                String validationMessage = DTOValidator.getValidationMessages(dto);
                if (!validationMessage.equals("Data is valid.")) {
                    System.out.println("❌ " + validationMessage);
                    return;
                }

                Course updatedCourse = courseService.updateCourse(courseId, dto);
                System.out.println("✅ Course updated successfully!");

            } else {
                System.out.println("Course not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format! Use yyyy-MM-dd");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void assignTeacherToCourse() {
        try {
            System.out.print("Enter Course ID: ");
            Long courseId = Long.parseLong(scanner.nextLine());

            System.out.print("Enter Teacher ID: ");
            Long teacherId = Long.parseLong(scanner.nextLine());

            adminService.assignTeacherToCourse(courseId, teacherId);
            System.out.println("✅ Teacher assigned successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addStudentToCourse() {
        try {
            System.out.print("Enter Course ID: ");
            Long courseId = Long.parseLong(scanner.nextLine());

            System.out.print("Enter Student ID: ");
            Long studentId = Long.parseLong(scanner.nextLine());

            adminService.enrollStudentInCourse(courseId, studentId);
            System.out.println("✅ Student enrolled successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewCourseParticipants() {
        System.out.print("Enter Course ID: ");
        try {
            Long courseId = Long.parseLong(scanner.nextLine());

            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();

                System.out.println("\n=== COURSE: " + course.getTitle() + " ===");
                System.out.println("Course Code: " + course.getCourseCode());
                System.out.println("Status: " + course.getStatus());
                System.out.println("Teacher: " + (course.getTeacher() != null ?
                        course.getTeacher().getFullName() : "Not assigned"));

                List<Student> students = adminService.getCourseStudents(courseId);

                System.out.println("\n=== ENROLLED STUDENTS (" + students.size() + ") ===");
                if (students.isEmpty()) {
                    System.out.println("No students enrolled.");
                } else {
                    System.out.printf("%-5s %-15s %-20s %-12s %-10s%n",
                            "ID", "Student Code", "Full Name", "Field", "Status");
                    System.out.println("------------------------------------------------------------");

                    for (Student student : students) {
                        System.out.printf("%-5d %-15s %-20s %-12s %-10s%n",
                                student.getId(),
                                student.getStudentCode() != null ? student.getStudentCode() : "N/A",
                                student.getFullName(),
                                student.getFieldOfStudy() != null ?
                                        (student.getFieldOfStudy().length() > 12 ?
                                                student.getFieldOfStudy().substring(0, 9) + "..." :
                                                student.getFieldOfStudy()) : "N/A",
                                student.getStatus()
                        );
                    }
                }
            } else {
                System.out.println("Course not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }

    private static void searchCourses() {
        System.out.println("\n=== SEARCH COURSES ===");

        System.out.print("Title (optional, press Enter to skip): ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) title = null;

        CourseStatus status = null;
        System.out.print("Status (PLANNED/ACTIVE/COMPLETED/CANCELLED, optional, press Enter to skip): ");
        String statusStr = scanner.nextLine().trim();
        if (!statusStr.isEmpty()) {
            try {
                status = CourseStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status! Ignoring status filter.");
            }
        }

        LocalDate startDate = null;
        System.out.print("Start Date From (yyyy-MM-dd, optional, press Enter to skip): ");
        String startDateStr = scanner.nextLine().trim();
        if (!startDateStr.isEmpty()) {
            try {
                startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Ignoring start date filter.");
            }
        }

        LocalDate endDate = null;
        System.out.print("End Date To (yyyy-MM-dd, optional, press Enter to skip): ");
        String endDateStr = scanner.nextLine().trim();
        if (!endDateStr.isEmpty()) {
            try {
                endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Ignoring end date filter.");
            }
        }

        List<Course> courses = courseService.searchCourses(title, status, startDate, endDate);

        System.out.println("\n=== SEARCH RESULTS ===");
        if (courses.isEmpty()) {
            System.out.println("No courses found matching your criteria.");
        } else {
            System.out.printf("%-5s %-10s %-30s %-15s %-12s %-12s %-10s%n",
                    "ID", "Code", "Title", "Teacher", "Start Date", "End Date", "Students");
            System.out.println("---------------------------------------------------------------------------------------------------");

            for (Course course : courses) {
                System.out.printf("%-5d %-10s %-30s %-15s %-12s %-12s %-10d%n",
                        course.getId(),
                        course.getCourseCode(),
                        course.getTitle().length() > 30 ? course.getTitle().substring(0, 27) + "..." : course.getTitle(),
                        course.getTeacher() != null ? course.getTeacher().getLastName() : "N/A",
                        course.getStartDate(),
                        course.getEndDate(),
                        course.getStudentCount()
                );
            }
            System.out.println("Found: " + courses.size() + " courses");
        }
    }

    private static void deleteCourse() {
        System.out.print("Enter Course ID to delete: ");
        try {
            Long courseId = Long.parseLong(scanner.nextLine());

            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();

                System.out.println("Are you sure you want to delete course: " + course.getTitle() + "?");
                System.out.println("This will remove " + course.getStudentCount() + " student enrollments.");
                System.out.print("Type 'YES' to confirm: ");
                String confirmation = scanner.nextLine().trim();

                if (confirmation.equalsIgnoreCase("YES")) {
                    courseService.deleteCourse(courseId);
                    System.out.println("✅ Course deleted successfully!");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            } else {
                System.out.println("Course not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }

    private static void viewStatistics() {
        AdminService.AdminStatistics stats = adminService.getStatistics();
        System.out.println("\n" + stats.toString());
    }

    // ==================== TEACHER MENU ====================
    private static void showTeacherMenu() {
        System.out.println("\n=== TEACHER PANEL ===");
        System.out.println("Logged in as: " + currentUser.getFullName());
        System.out.println("1. View My Courses");
        System.out.println("2. View Course Details");
        System.out.println("3. View My Profile");
        System.out.println("4. Logout");
        System.out.print("Select an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    viewTeacherCourses();
                    break;
                case 2:
                    viewTeacherCourseDetails();
                    break;
                case 3:
                    viewProfile();
                    break;
                case 4:
                    logout();
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void viewTeacherCourses() {
        List<Course> courses = courseService.getCoursesByTeacher(currentUser.getId());

        System.out.println("\n=== MY COURSES ===");
        if (courses.isEmpty()) {
            System.out.println("You are not assigned to any courses.");
        } else {
            System.out.printf("%-5s %-10s %-30s %-12s %-12s %-10s %-10s%n",
                    "ID", "Code", "Title", "Start Date", "End Date", "Status", "Students");
            System.out.println("----------------------------------------------------------------------------------------------");

            for (Course course : courses) {
                System.out.printf("%-5d %-10s %-30s %-12s %-12s %-10s %-10d%n",
                        course.getId(),
                        course.getCourseCode(),
                        course.getTitle().length() > 30 ? course.getTitle().substring(0, 27) + "..." : course.getTitle(),
                        course.getStartDate(),
                        course.getEndDate(),
                        course.getStatus(),
                        course.getStudentCount()
                );
            }
            System.out.println("Total: " + courses.size() + " courses");
        }
    }

    private static void viewTeacherCourseDetails() {
        System.out.print("Enter Course ID: ");
        try {
            Long courseId = Long.parseLong(scanner.nextLine());

            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();

                // Check if this teacher is assigned to this course
                if (course.getTeacher() == null || !course.getTeacher().getId().equals(currentUser.getId())) {
                    System.out.println("You are not assigned to this course!");
                    return;
                }

                System.out.println("\n=== COURSE DETAILS ===");
                System.out.println("Course Code: " + course.getCourseCode());
                System.out.println("Title: " + course.getTitle());
                System.out.println("Description: " + course.getDescription());
                System.out.println("Start Date: " + course.getStartDate());
                System.out.println("End Date: " + course.getEndDate());
                System.out.println("Credit: " + course.getCredit());
                System.out.println("Status: " + course.getStatus());
                System.out.println("Created: " + course.getCreatedAt());
                System.out.println("Last Updated: " + course.getUpdatedAt());

                List<Student> students = courseService.getCourseStudents(courseId);
                System.out.println("\n=== ENROLLED STUDENTS (" + students.size() + ") ===");

                if (students.isEmpty()) {
                    System.out.println("No students enrolled.");
                } else {
                    System.out.printf("%-5s %-15s %-20s %-12s%n",
                            "ID", "Student Code", "Full Name", "Field of Study");
                    System.out.println("--------------------------------------------------");

                    for (Student student : students) {
                        System.out.printf("%-5d %-15s %-20s %-12s%n",
                                student.getId(),
                                student.getStudentCode() != null ? student.getStudentCode() : "N/A",
                                student.getFullName(),
                                student.getFieldOfStudy() != null ?
                                        (student.getFieldOfStudy().length() > 12 ?
                                                student.getFieldOfStudy().substring(0, 9) + "..." :
                                                student.getFieldOfStudy()) : "N/A"
                        );
                    }
                }
            } else {
                System.out.println("Course not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }

    // ==================== STUDENT MENU ====================
    private static void showStudentMenu() {
        System.out.println("\n=== STUDENT PANEL ===");
        System.out.println("Logged in as: " + currentUser.getFullName());
        System.out.println("1. View My Courses");
        System.out.println("2. View Available Courses");
        System.out.println("3. View My Profile");
        System.out.println("4. Logout");
        System.out.print("Select an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    viewStudentCourses();
                    break;
                case 2:
                    viewAvailableCourses();
                    break;
                case 3:
                    viewProfile();
                    break;
                case 4:
                    logout();
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }

    private static void viewStudentCourses() {
        List<Course> courses = courseService.getCoursesByStudent(currentUser.getId());

        System.out.println("\n=== MY COURSES ===");
        if (courses.isEmpty()) {
            System.out.println("You are not enrolled in any courses.");
        } else {
            System.out.printf("%-5s %-10s %-30s %-15s %-12s %-12s %-10s%n",
                    "ID", "Code", "Title", "Teacher", "Start Date", "End Date", "Status");
            System.out.println("---------------------------------------------------------------------------------------------------");

            for (Course course : courses) {
                System.out.printf("%-5d %-10s %-30s %-15s %-12s %-12s %-10s%n",
                        course.getId(),
                        course.getCourseCode(),
                        course.getTitle().length() > 30 ? course.getTitle().substring(0, 27) + "..." : course.getTitle(),
                        course.getTeacher() != null ? course.getTeacher().getLastName() : "Not assigned",
                        course.getStartDate(),
                        course.getEndDate(),
                        course.getStatus()
                );
            }
            System.out.println("Total: " + courses.size() + " courses");
        }
    }

    private static void viewAvailableCourses() {
        List<Course> courses = courseService.getActiveCourses();

        System.out.println("\n=== AVAILABLE ACTIVE COURSES ===");
        if (courses.isEmpty()) {
            System.out.println("No active courses available.");
        } else {
            System.out.printf("%-5s %-10s %-30s %-15s %-12s %-12s %-10s%n",
                    "ID", "Code", "Title", "Teacher", "Start Date", "End Date", "Students");
            System.out.println("---------------------------------------------------------------------------------------------------");

            for (Course course : courses) {
                // Check if student is already enrolled
                boolean isEnrolled = courseService.getCoursesByStudent(currentUser.getId())
                        .stream()
                        .anyMatch(c -> c.getId().equals(course.getId()));

                String enrolledMarker = isEnrolled ? " (Enrolled)" : "";

                System.out.printf("%-5d %-10s %-30s %-15s %-12s %-12s %-10d%s%n",
                        course.getId(),
                        course.getCourseCode(),
                        (course.getTitle().length() > 30 ? course.getTitle().substring(0, 27) + "..." : course.getTitle()) + enrolledMarker,
                        course.getTeacher() != null ? course.getTeacher().getLastName() : "Not assigned",
                        course.getStartDate(),
                        course.getEndDate(),
                        course.getStudentCount(),
                        ""
                );
            }
            System.out.println("\nNote: Courses marked with '(Enrolled)' are courses you're already enrolled in.");
        }
    }

    // ==================== COMMON FUNCTIONS ====================
    private static void viewProfile() {
        System.out.println("\n=== MY PROFILE ===");
        System.out.println("ID: " + currentUser.getId());
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Full Name: " + currentUser.getFullName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Phone: " + currentUser.getPhoneNumber());
        System.out.println("National ID: " + currentUser.getNationalId());
        System.out.println("Role: " + currentUser.getRole());
        System.out.println("Status: " + currentUser.getStatus());
        System.out.println("Registration Date: " + currentUser.getRegistrationDate());

        if (currentUser.isApproved()) {
            System.out.println("Approved Date: " + currentUser.getApprovedDate());
        }

        // Role-specific information
        if (currentUser instanceof Student student) {
            System.out.println("\n=== STUDENT INFORMATION ===");
            System.out.println("Student Code: " + student.getStudentCode());
            System.out.println("Field of Study: " + student.getFieldOfStudy());
            System.out.println("Entry Year: " + student.getEntryYear());
            System.out.println("Enrolled Courses: " + student.getEnrolledCourses().size());

        } else if (currentUser instanceof Teacher teacher) {
            System.out.println("\n=== TEACHER INFORMATION ===");
            System.out.println("Teacher Code: " + teacher.getTeacherCode());
            System.out.println("Expertise: " + teacher.getExpertise());
            System.out.println("Degree: " + teacher.getDegree());
            System.out.println("Teaching Courses: " + teacher.getTeachingCourses().size());

        } else if (currentUser instanceof Admin admin) {
            System.out.println("\n=== ADMIN INFORMATION ===");
            System.out.println("Admin Level: " + admin.getAdminLevel());
            System.out.println("Department: " + admin.getDepartment());
        }
    }
}
