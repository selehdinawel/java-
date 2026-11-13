
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

interface Diagnosable {
    void diagnose();
}

abstract class NetworkDevice implements Diagnosable {
    private String id;
    private String name;
    private boolean faulty;

    public NetworkDevice(String id, String name) {
        this.id = id;
        this.name = name;
        this.faulty = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isFaulty() {
        return faulty;
    }

    public void setFaulty(boolean faulty) {
        this.faulty = faulty;
    }

    public abstract String getType();

    public void showInfo() {
        System.out.println(
            getType() + " | " + id + " | " + name
            + " | " + (faulty ? "FAULT" : "NORMAL")
        );
    }

    @Override
    public void diagnose() {
        if (faulty) {
            System.out.println(name + ": Fault detected!");
        } else {
            System.out.println(name + ": No fault detected.");
        }
    }
}

class Router extends NetworkDevice {
    public Router(String id, String name) {
        super(id, name);
    }

    @Override
    public String getType() {
        return "Router";
    }
}

class Switch extends NetworkDevice {
    public Switch(String id, String name) {
        super(id, name);
    }

    @Override
    public String getType() {
        return "Switch";
    }
}

class Computer extends NetworkDevice {
    public Computer(String id, String name) {
        super(id, name);
    }

    @Override
    public String getType() {
        return "Computer";
    }
}

// Connection class: OUTSIDE Main
class Connection {
    private NetworkDevice device1;
    private NetworkDevice device2;
    private boolean active;

    public Connection(NetworkDevice device1,
                     NetworkDevice device2) {
        this.device1 = device1;
        this.device2 = device2;
        this.active = true;
    }

    public NetworkDevice getDevice1() {
        return device1;
    }

    public NetworkDevice getDevice2() {
        return device2;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void showConnection() {
        System.out.println(
            device1.getName() + " <----> "
            + device2.getName() + " | "
            + (active ? "CONNECTED" : "BROKEN")
        );
    }
}

public class Main {

   
    public static void checkReachability(
            ArrayList<NetworkDevice> devices,
            ArrayList<Connection> connections) {

        NetworkDevice router = null;

       
        for (NetworkDevice d : devices) {
            if (d instanceof Router) {
                router = d;
                break;
            }
        }

        if (router == null) {
            System.out.println("No Router found!");
            return;
        }

        System.out.println("\n=== NETWORK DIAGNOSIS ===");

        if (router.isFaulty()) {
            System.out.println("Router has a fault!");
            for (NetworkDevice d : devices) {
                if (d.isFaulty()) {
                    System.out.println(
                        d.getName() + " : DEVICE FAULT");
                } else {
                    System.out.println(
                        d.getName() + " : UNREACHABLE");
                }
            }
            return;
        }

        ArrayList<NetworkDevice> reachable =
                new ArrayList<>();

        reachable.add(router);

        boolean changed;

        do {
            changed = false;

            for (Connection c : connections) {

                if (!c.isActive()) {
                    continue;
                }

                NetworkDevice a = c.getDevice1();
                NetworkDevice b = c.getDevice2();

                if (a.isFaulty() || b.isFaulty()) {
                    continue;
                }

                if (reachable.contains(a)
                        && !reachable.contains(b)) {
                    reachable.add(b);
                    changed = true;
                }

                if (reachable.contains(b)
                        && !reachable.contains(a)) {
                    reachable.add(a);
                    changed = true;
                }
            }

        } while (changed);

        for (NetworkDevice d : devices) {
            if (d.isFaulty()) {
                System.out.println(
                    d.getName() + " : DEVICE FAULT");
            } else if (reachable.contains(d)) {
                System.out.println(
                    d.getName() + " : REACHABLE");
            } else {
                System.out.println(
                    d.getName() + " : UNREACHABLE");
            }
        }
    }
    
    
public static void saveNetworkData(
        ArrayList<NetworkDevice> devices,
        ArrayList<Connection> connections) {

    try (BufferedWriter writer =
            new BufferedWriter(new FileWriter("network_data.txt"))) {

        // Save devices
        for (NetworkDevice d : devices) {
            writer.write("DEVICE|" + d.getType()
                    + "|" + d.getId()
                    + "|" + d.getName()
                    + "|" + d.isFaulty());
            writer.newLine();
        }

        // Save connections
        for (Connection c : connections) {
            writer.write("CONNECTION|"
                    + c.getDevice1().getId()
                    + "|" + c.getDevice2().getId()
                    + "|" + c.isActive());
            writer.newLine();
        }

        System.out.println("Network data saved successfully!");

    } catch (IOException e) {
        System.out.println("Error saving data: " + e.getMessage());
    }
}

    
    
    
public static boolean isDuplicateId(ArrayList<NetworkDevice> devices, String id) {
    for (NetworkDevice d : devices) {
        if (d.getId().equalsIgnoreCase(id)) {
            return true;
        }
    }
    return false;
}


    
public static ArrayList<NetworkDevice> findReachableDevices(
        ArrayList<NetworkDevice> devices,
        ArrayList<Connection> connections) {

    ArrayList<NetworkDevice> reachable = new ArrayList<>();

   
    NetworkDevice router = null;

    for (NetworkDevice d : devices) {
        if (d instanceof Router) {
            router = d;
            break;
        }
    }

    // No router or faulty router
    if (router == null || router.isFaulty()) {
        return reachable;
    }

  
    reachable.add(router);

    boolean changed;

    do {
        changed = false;

        for (Connection c : connections) {

        
            if (!c.isActive()) {
                continue;
            }

            NetworkDevice a = c.getDevice1();
            NetworkDevice b = c.getDevice2();

            // Ignore faulty devices
            if (a.isFaulty() || b.isFaulty()) {
                continue;
            }

            // Travel from device A to device B
            if (reachable.contains(a)
                    && !reachable.contains(b)) {
                reachable.add(b);
                changed = true;
            }

            // Travel from device B to device A
            if (reachable.contains(b)
                    && !reachable.contains(a)) {
                reachable.add(a);
                changed = true;
            }
        }

    } while (changed);

    return reachable;
}

    
public static void searchDeviceById(ArrayList<NetworkDevice> devices, Scanner sc) {
    System.out.print("Enter device ID to search: ");
    String id = sc.nextLine();

    for (NetworkDevice d : devices) {
        if (d.getId().equalsIgnoreCase(id)) {
            System.out.println("\n--- DEVICE FOUND ---");
            d.showInfo();
            return;
        }
    }

    System.out.println("Device not found!");
}

public static void loadNetworkData(
        ArrayList<NetworkDevice> devices,
        ArrayList<Connection> connections) {

    ArrayList<String> connectionLines = new ArrayList<>();

    try (BufferedReader reader =
            new BufferedReader(new FileReader("network_data.txt"))) {

        String line;

      
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|", 5);

            if (parts[0].equals("DEVICE") && parts.length == 5) {

                String type = parts[1];
                String id = parts[2];
                String name = parts[3];
                boolean faulty = Boolean.parseBoolean(parts[4]);

                if (isDuplicateId(devices, id)) {
                    continue;
                }

                NetworkDevice d;

                if (type.equals("Router")) {
                    d = new Router(id, name);
                } else if (type.equals("Switch")) {
                    d = new Switch(id, name);
                } else if (type.equals("Computer")) {
                    d = new Computer(id, name);
                } else {
                    continue;
                }

                d.setFaulty(faulty);
                devices.add(d);

            } else if (line.startsWith("CONNECTION|")) {
                connectionLines.add(line);
            }
        }

    
        for (String savedLine : connectionLines) {
            String[] p = savedLine.split("\\|", -1);

            if (p.length != 4) {
                continue;
            }

            NetworkDevice d1 = null;
            NetworkDevice d2 = null;

            for (NetworkDevice d : devices) {
                if (d.getId().equals(p[1])) d1 = d;
                if (d.getId().equals(p[2])) d2 = d;
            }

            if (d1 != null && d2 != null) {
                Connection c = new Connection(d1, d2);

                if (Boolean.parseBoolean(p[3])) {
                    // Connection is active by default
                } else {
                    c.setActive(false);
                }

                connections.add(c);
            }
        }

        System.out.println("Network data loaded successfully!");

    } catch (IOException e) {
        System.out.println("Error loading data: " + e.getMessage());
    }
}


public static void generateFaultReport(
        ArrayList<NetworkDevice> devices,
        ArrayList<Connection> connections) {

    System.out.println("\n========== NETWORK FAULT REPORT ==========");

    int faultyDevices = 0;
    int brokenConnections = 0;
    int activeConnections = 0;

   
    
    int reachableDevices = 0;
    int unreachableDevices = 0;
    ArrayList<NetworkDevice> reachable =
            findReachableDevices(devices, connections);

    System.out.println("\n--- DEVICE REPORT ---");

   
     for (NetworkDevice d : devices) {

    String status;

    if (d.isFaulty()) {
        status = "DEVICE FAULT";
        faultyDevices++;

    } else if (reachable.contains(d)) {
        status = "REACHABLE";
        reachableDevices++;

    } else {
        status = "UNREACHABLE";
        unreachableDevices++;
    }

    System.out.println(
            "ID: " + d.getId()
            + " | Name: " + d.getName()
            + " | Type: " + d.getType()
            + " | Status: " + status
    );
}


    System.out.println("\n--- CONNECTION REPORT ---");

    for (int i = 0; i < connections.size(); i++) {

        Connection c = connections.get(i);

        if (c.isActive()) {
            activeConnections++;
        } else {
            brokenConnections++;
        }

        System.out.println(
                "Connection " + (i + 1)
                + " | " + c.getDevice1().getName()
                + " <--> " + c.getDevice2().getName()
                + " | Status: "
                + (c.isActive() ? "ACTIVE" : "BROKEN")
        );
    }

   
    
System.out.println("\n--- SUMMARY ---");

System.out.println("Total devices: " + devices.size());
System.out.println("Reachable devices: " + reachableDevices);
System.out.println("Unreachable devices: " + unreachableDevices);
System.out.println("Faulty devices: " + faultyDevices);

System.out.println("Active connections: " + activeConnections);
System.out.println("Broken connections: " + brokenConnections);

double percentage = 0;

if (!devices.isEmpty()) {
    percentage = (reachableDevices * 100.0) / devices.size();
}

System.out.printf("Network reachability: %.2f%%%n", percentage);

System.out.println("==========================================");

}

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        ArrayList<NetworkDevice> devices =
                new ArrayList<>();

        ArrayList<Connection> connections =
                new ArrayList<>();

        int choice;

        do {
            System.out.println("\n=== CAMPUS NETWORK ===");
            System.out.println("1. Add Router");
            System.out.println("2. Add Switch");
            System.out.println("3. Add Computer");
            System.out.println("4. Show Devices");
            System.out.println("5. Simulate Device Fault");
            System.out.println("6. Diagnose Devices");
            System.out.println("7. Connect Devices");
            System.out.println("8. Show Connections");
            System.out.println("9. Simulate Connection Fault");
            System.out.println("10. Repair Connection");
            System.out.println("11. Check Network Reachability");
            System.out.println("12. Generate Fault Report");
            System.out.println("13. Search Device by ID");
            System.out.println("14. Save Network Data");
            System.out.println("15. Load Network Data");
            System.out.println("16. Repair Device");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            choice = input.nextInt();
            input.nextLine();

            if (choice >= 1 && choice <= 3) {

               
    System.out.print("Enter ID: ");
    String id = input.nextLine();

    if (isDuplicateId(devices, id)) {
        System.out.println("Error: This device ID already exists!");
    } else {
        System.out.print("Enter name: ");
        String name = input.nextLine();

        NetworkDevice d;

        if (choice == 1) {
            d = new Router(id, name);
        } else if (choice == 2) {
            d = new Switch(id, name);
        } else {
            d = new Computer(id, name);
        }

        devices.add(d);
        System.out.println("Device added!");
    }

      

            } else if (choice == 4) {

                for (NetworkDevice d : devices) {
                    d.showInfo();
                }

            } else if (choice == 5) {

                if (devices.isEmpty()) {
                    System.out.println("No devices available.");
                } else {
                    for (int i = 0; i < devices.size(); i++) {
                        System.out.println(i + ". "
                                + devices.get(i).getName());
                    }

                    System.out.print("Device number: ");
                    int index = input.nextInt();

                    if (index >= 0 && index < devices.size()) {
                        devices.get(index).setFaulty(true);
                        System.out.println("Fault simulated!");
                    } else {
                        System.out.println("Invalid number.");
                    }
                }

            } else if (choice == 6) {

                for (NetworkDevice d : devices) {
                    d.diagnose();
                }

            } else if (choice == 7) {

                if (devices.size() < 2) {
                    System.out.println("Add at least 2 devices.");
                } else {
                    for (int i = 0; i < devices.size(); i++) {
                        System.out.println(i + ". "
                                + devices.get(i).getName());
                    }

                    System.out.print("First device number: ");
                    int a = input.nextInt();

                    System.out.print("Second device number: ");
                    int b = input.nextInt();

                    if (a < 0 || a >= devices.size()
                            || b < 0 || b >= devices.size()
                            || a == b) {
                        System.out.println("Invalid selection.");
                    } else {
                        Connection c = new Connection(
                                devices.get(a), devices.get(b));

                        connections.add(c);
                        System.out.println("Connection added!");
                    }
                }

            } else if (choice == 8) {

                if (connections.isEmpty()) {
                    System.out.println("No connections yet.");
                }

                for (Connection c : connections) {
                    c.showConnection();
                }

            } else if (choice == 9 || choice == 10) {

                if (connections.isEmpty()) {
                    System.out.println("No connections available.");
                } else {
                    for (int i = 0; i < connections.size(); i++) {
                        System.out.print(i + ". ");
                        connections.get(i).showConnection();
                    }

                    System.out.print("Connection number: ");
                    int index = input.nextInt();

                    if (index >= 0 && index < connections.size()) {
                        if (choice == 9) {
                            connections.get(index).setActive(false);
                            System.out.println("Connection broken!");
                        } else {
                            connections.get(index).setActive(true);
                            System.out.println("Connection repaired!");
                        }
                    } else {
                        System.out.println("Invalid connection number.");
                    }
                }

             } else if (choice == 11) {

            checkReachability(devices, connections);
            } else if (choice == 12) {
    generateFaultReport(devices, connections);
} else if (choice == 13) {
    searchDeviceById(devices, input);
}
else if (choice == 14) {
    saveNetworkData(devices, connections);
}
 else if (choice == 15) {
    devices.clear();
    connections.clear();

    loadNetworkData(devices, connections);
}
 else if (choice == 16) {
    if (devices.isEmpty()) {
        System.out.println("No devices available.");
    } else {
        System.out.println("=== REPAIR DEVICE ===");

        for (int i = 0; i < devices.size(); i++) {
            System.out.println(i + ". " + devices.get(i).getName()
                    + " | ID: " + devices.get(i).getId());
        }

        System.out.print("Device number: ");
        int deviceNumber = Integer.parseInt(input.nextLine());

        if (deviceNumber >= 0 && deviceNumber < devices.size()) {
            NetworkDevice device = devices.get(deviceNumber);

            device.setFaulty(false);
            System.out.println("Device repaired!");
        } else {
            System.out.println("Invalid device number.");
        }
    }
}
else if (choice != 0) {
    System.out.println("Invalid choice!");
 }
     

          
        } while (choice != 0);

        input.close();
    }
}
