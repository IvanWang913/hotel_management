import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainGUI extends JFrame {
    private static final String FILE_NAME = "room.txt";
    private Map<String, String> credentialsMap;

    private JTextArea outputTextArea;

    public MainGUI() {
        setTitle("Hotel Room Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        credentialsMap = new HashMap<>();
        credentialsMap.put("Ivan", "Ivan0901");
        credentialsMap.put("John", "John1234");
        credentialsMap.put("Alice", "Alice5678");

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginMenu();
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(loginButton, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setVisible(true);
    }

    public void createFileIfNotExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    outputTextArea.append("File " + fileName + " created successfully.\n");
                } else {
                    outputTextArea.append("Unable to create file " + fileName + "\n");
                }
            } catch (IOException e) {
                outputTextArea.append("Error creating file " + fileName + ": " + e.getMessage() + "\n");
            }
        }
    }

    public void loginMenu() {
        outputTextArea.setText("");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        String time = now.format(formatter);

        String username = JOptionPane.showInputDialog(this, "Enter Username:");
        String password = JOptionPane.showInputDialog(this, "Enter Password:");

        if (credentialsMap.containsKey(username) && credentialsMap.get(username).equals(password)) {
            outputTextArea.append("Welcome " + username + "\n");
            mainMenu();
        } else {
            outputTextArea.append("Login Failed!!! Please try again\n");
            outputTextArea.append(time + "\n");
        }
    }

    public void mainMenu() {
        while (true) {
            String choice = JOptionPane.showInputDialog(this,
                    "Welcome to the main menu! Please enter:\n" +
                            "1) Add\n" +
                            "2) View All\n" +
                            "3) Search\n" +
                            "4) Modify\n" +
                            "5) Delete\n" +
                            "6) Quit Program\n" +
                            "What would you like to do?");

            if (choice == null)
                return;

            switch (choice) {
                case "1":
                    roomAdd();
                    break;
                case "2":
                    roomView();
                    break;
                case "3":
                    roomSearch();
                    break;
                case "4":
                    roomModify();
                    break;
                case "5":
                    roomDelete();
                    break;

                case "6":
                    return;
                default:
                    outputTextArea.append("Error! Enter only 1-6\n");
            }
        }
    }

    public void roomView() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            while ((line = reader.readLine()) != null) {
                outputTextArea.append(line + "\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            outputTextArea.append("Room text file has not been created\n");
        } catch (IOException e) {
            outputTextArea.append("Error accessing room.txt\n");
        }
    }
    private void roomSearch() {
        String roomId = JOptionPane.showInputDialog(this, "Enter room ID and Date (eg. A001 12/05/2023):");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            boolean roomFound = false;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && values[0].equals(roomId)) {
                    outputTextArea.setText(line);
                    roomFound = true;
                    break;
                }
            }

            reader.close();

            if (!roomFound) {
                outputTextArea.setText("No room found with the given ID: " + roomId);
            }
        } catch (IOException e) {
            outputTextArea.setText("Error accessing room.txt");
        }
    }
    public void roomAdd() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            boolean isRoomIdUnique = true;

            String roomId = JOptionPane.showInputDialog(this, "Please enter new room ID and date (eg. A001 12/05/2023): ");

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && values[0].equals(roomId)) {
                    isRoomIdUnique = false;
                    break;
                }
            }

            reader.close();

            if (isRoomIdUnique) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));

                if (roomId != null && !roomId.isEmpty()) {
                    String icNo = JOptionPane.showInputDialog(this, "Please enter customer's IC No. (e.g., 012345-02-0123):");
                    String name = JOptionPane.showInputDialog(this, "Please enter customer's name (e.g., Adam Tan):");
                    String contactNo = JOptionPane.showInputDialog(this, "Please enter customer's contact number (e.g., 0123456789):");
                    String email = JOptionPane.showInputDialog(this, "Please enter customer's email (e.g., adamtan@gmail.com):");
                    int dos = Integer.parseInt(JOptionPane.showInputDialog(this, "Please enter days of stay: "));

                    // Calculate charges and taxes
                    int taxRate = 10;  // Assume 10% tax rate
                    int chargesperday = 350 + (350 * taxRate / 100);
                    int charges = chargesperday * dos;

                    // Get current date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = dateFormat.format(new Date());

                    // Write room record to file
                    writer.write(roomId + "," + icNo + "," + name + "," + contactNo + "," + email + "," + dos + "\n");
                    outputTextArea.append("Record successfully added\n");

                    // Display receipt
                    String receipt = "Receipt\n\n";
                    receipt += "Room ID and Date: " + roomId + "\n";
                    receipt += "Customer IC No.: " + icNo + "\n";
                    receipt += "Name: " + name + "\n";
                    receipt += "Contact No.: " + contactNo + "\n";
                    receipt += "Email: " + email + "\n";
                    receipt += "Days of stay: " + dos + "\n";
                    receipt += "Charges (including taxes): RM " + charges + "\n";
                    receipt += "Date issued: " + currentDate;

                    JOptionPane.showMessageDialog(this, receipt, "Receipt", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    outputTextArea.append("Please enter a valid room ID! Try again!\n");
                }

                writer.close();
            } else {
                outputTextArea.append("Room ID already exists! Please enter another room ID.\n");
            }
        } catch (IOException e) {
            outputTextArea.append("Error accessing room.txt\n");
        }
    }

    public void roomModify() {
        try {
            File inputFile = new File(FILE_NAME);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String roomId = JOptionPane.showInputDialog(this, "Enter room ID and date to replace record (eg. A001 12/05/2023):");

            String line;
            boolean recordModified = false;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && values[0].equals(roomId)) {
                    String newRoomId = JOptionPane.showInputDialog(this, "Please enter new room ID:");
                    String newIcNo = JOptionPane.showInputDialog(this, "Please enter customer's IC No. (e.g., 012345-02-0123):");
                    String newname = JOptionPane.showInputDialog(this, "Please enter customer's name (e.g., Adam Tan):");
                    String newcontactNo = JOptionPane.showInputDialog(this, "Please enter customer's contact number (e.g., 0123456789):");
                    String newemail = JOptionPane.showInputDialog(this, "Please enter customer's email (e.g., adamtan@gmail.com):");
                    int newdos = Integer.parseInt(JOptionPane.showInputDialog(this, "Please enter days of stay: "));


                    writer.write(newRoomId + "," + newIcNo + "," + newname + "," + newcontactNo + "," + newemail + "," + newdos + "\n");
                    recordModified = true;
                } else {
                    writer.write(line + "\n");
                }
            }

            reader.close();
            writer.close();

            if (recordModified) {
                if (inputFile.delete()) {
                    if (tempFile.renameTo(inputFile)) {
                        outputTextArea.append("Record successfully modified\n");
                    } else {
                        outputTextArea.append("Error renaming temp file\n");
                    }
                } else {
                    outputTextArea.append("Error deleting input file\n");
                }
            } else {
                outputTextArea.append("No record found for the given room ID\n");
            }
        } catch (IOException e) {
            outputTextArea.append("Error accessing room.txt\n");
        }
    }

    public void roomDelete() {
        try {
            File inputFile = new File(FILE_NAME);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String roomId = JOptionPane.showInputDialog(this, "Enter room ID and date to delete record (eg. A001 12/05/2023):");

            String line;
            boolean recordDeleted = false;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && values[0].equals(roomId)) {
                    recordDeleted = true;
                } else {
                    writer.write(line + "\n");
                }
            }

            reader.close();
            writer.close();

            if (recordDeleted) {
                if (inputFile.delete()) {
                    if (tempFile.renameTo(inputFile)) {
                        outputTextArea.append("Record successfully deleted\n");
                    } else {
                        outputTextArea.append("Error renaming temp file\n");
                    }
                } else {
                    outputTextArea.append("Error deleting input file\n");
                }
            } else {
                outputTextArea.append("No record found for the given room ID\n");
            }
        } catch (IOException e) {
            outputTextArea.append("Error accessing room.txt\n");
        }
    }

    public static void main(String[] args) {
        MainGUI mainGUI = new MainGUI();
        mainGUI.createFileIfNotExists(FILE_NAME);
    }
}

