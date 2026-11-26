
import java.util.*;

class Bus {
    int busId;
    String busName;
    String source;
    String destination;
    int totalSeats;
    int fare;
    HashMap<Integer, Booking> bookedSeats = new HashMap<>();

    Bus(int busId, String busName, String source,
        String destination, int totalSeats, int fare) {
        this.busId = busId;
        this.busName = busName;
        this.source = source;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.fare = fare;
    }

    int availableSeats() {
        return totalSeats - bookedSeats.size();
    }

    void display() {
        System.out.printf("%-5d %-18s %-12s %-12s %-8d Rs.%d%n",
                busId, busName, source, destination,
                availableSeats(), fare);
    }
}

class Booking {
    String passengerName;
    String phone;
    int busId;
    int seatNumber;
    int fare;

    Booking(String passengerName, String phone,
            int busId, int seatNumber, int fare) {
        this.passengerName = passengerName;
        this.phone = phone;
        this.busId = busId;
        this.seatNumber = seatNumber;
        this.fare = fare;
    }

    void display() {
        System.out.println("+------------------------------------------+");
        System.out.println("| Passenger : " + passengerName);
        System.out.println("| Phone     : " + phone);
        System.out.println("| Bus ID    : " + busId);
        System.out.println("| Seat No.  : " + seatNumber);
        System.out.println("| Fare      : Rs. " + fare);
        System.out.println("+------------------------------------------+");
    }
}

public class BusReservationSystem {
    static Scanner sc = new Scanner(System.in);

    static ArrayList<Bus> buses = new ArrayList<>();
    static HashMap<String, Booking> reservations = new HashMap<>();
    static Queue<Booking> waitingList = new LinkedList<>();
    static Stack<Booking> recentBookings = new Stack<>();

    static String readLine(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    static Bus findBus(int id) {
        for (Bus bus : buses) {
            if (bus.busId == id) return bus;
        }
        return null;
    }

    static void header() {
        System.out.println("\n==========================================================");
        System.out.println("                 CITYLINE BUS SERVICES");
        System.out.println("              BUS RESERVATION TERMINAL");
        System.out.println("==========================================================");
        System.out.println("             YOUR JOURNEY, OUR PRIORITY");
        System.out.println("----------------------------------------------------------");
    }

    static void menu() {
        System.out.println("\n+--------------------------------------------------------+");
        System.out.println("|                    BOOKING DESK                         |");
        System.out.println("+--------------------------------------------------------+");
        System.out.println("| 1. View Available Buses                                 |");
        System.out.println("| 2. Check Seat Availability                              |");
        System.out.println("| 3. Book a Ticket                                        |");
        System.out.println("| 4. Cancel Reservation                                   |");
        System.out.println("| 5. View All Reservations                                |");
        System.out.println("| 6. View Waiting List                                    |");
        System.out.println("| 7. Process Waiting List                                 |");
        System.out.println("| 8. View Recent Booking                                  |");
        System.out.println("| 9. Exit                                                 |");
        System.out.println("+--------------------------------------------------------+");
    }

    static void displayBuses() {
        System.out.println("\n==================== ROUTE BOARD ====================");
        System.out.printf("%-5s %-18s %-12s %-12s %-8s %s%n",
                "ID", "BUS", "FROM", "TO", "SEATS", "FARE");
        System.out.println("-----------------------------------------------------");
        for (Bus bus : buses) bus.display();
        System.out.println("=====================================================");
    }

    static void checkSeats() {
        int id;
        try {
            id = Integer.parseInt(readLine("Enter Bus ID: "));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid bus ID.");
            return;
        }

        Bus bus = findBus(id);
        if (bus == null) {
            System.out.println("[ERROR] Bus not found.");
            return;
        }

        System.out.println("\nSeat status for " + bus.busName);
        for (int seat = 1; seat <= bus.totalSeats; seat++) {
            String status = bus.bookedSeats.containsKey(seat)
                    ? "[XX]" : "[ " + seat + " ]";
            System.out.print(status + " ");
            if (seat % 5 == 0) System.out.println();
        }
        System.out.println("\nAvailable seats: " + bus.availableSeats());
    }

    static void bookTicket() {
        System.out.println("\n--------------- NEW TICKET BOOKING ---------------");
        int id;
        try {
            id = Integer.parseInt(readLine("Enter Bus ID: "));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid bus ID.");
            return;
        }

        Bus bus = findBus(id);
        if (bus == null) {
            System.out.println("[ERROR] Bus not found.");
            return;
        }

        String name = readLine("Passenger name: ");
        String phone = readLine("Phone number : ");

        if (name.isEmpty() || phone.isEmpty()) {
            System.out.println("[ERROR] Passenger name and phone are required.");
            return;
        }
        if (reservations.containsKey(phone)) {
            System.out.println("[INFO] This phone number already has a booking.");
            return;
        }

        if (bus.availableSeats() == 0) {
            Booking waiting = new Booking(name, phone, id, -1, bus.fare);
            waitingList.add(waiting);
            System.out.println("[WAITING LIST] Bus is full. Passenger added to queue.");
            return;
        }

        int seat;
        try {
            seat = Integer.parseInt(readLine("Choose seat number (1-" +
                    bus.totalSeats + "): "));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid seat number.");
            return;
        }

        if (seat < 1 || seat > bus.totalSeats) {
            System.out.println("[ERROR] Seat number is out of range.");
            return;
        }
        if (bus.bookedSeats.containsKey(seat)) {
            System.out.println("[ERROR] Seat is already booked.");
            return;
        }

        Booking booking = new Booking(name, phone, id, seat, bus.fare);
        bus.bookedSeats.put(seat, booking);
        reservations.put(phone, booking);
        recentBookings.push(booking);

        System.out.println("\n[BOOKING CONFIRMED] Your ticket is reserved!");
        booking.display();
    }

    static void cancelReservation() {
        System.out.println("\n--------------- CANCEL TICKET --------------------");
        String phone = readLine("Enter booking phone number: ");
        Booking booking = reservations.remove(phone);

        if (booking == null) {
            System.out.println("[ERROR] Reservation not found.");
            return;
        }

        Bus bus = findBus(booking.busId);
        if (bus != null) bus.bookedSeats.remove(booking.seatNumber);

        System.out.println("[SUCCESS] Reservation cancelled.");
        System.out.println("Released seat number: " + booking.seatNumber);
    }

    static void viewReservations() {
        if (reservations.isEmpty()) {
            System.out.println("[INFO] No confirmed reservations.");
            return;
        }
        System.out.println("\n--------------- CONFIRMED PASSENGERS -------------");
        for (Booking booking : reservations.values()) booking.display();
    }

    static void viewWaitingList() {
        if (waitingList.isEmpty()) {
            System.out.println("[INFO] Waiting list is empty.");
            return;
        }
        System.out.println("\n--------------- WAITING LIST (FIFO) --------------");
        for (Booking booking : waitingList) {
            System.out.println("Passenger: " + booking.passengerName
                    + " | Phone: " + booking.phone
                    + " | Bus ID: " + booking.busId);
        }
    }

    static void processWaitingList() {
        if (waitingList.isEmpty()) {
            System.out.println("[INFO] No passengers in waiting list.");
            return;
        }

        Booking next = waitingList.peek();
        Bus bus = findBus(next.busId);

        if (bus == null || bus.availableSeats() == 0) {
            System.out.println("[INFO] No seat is available for this bus yet.");
            return;
        }

        waitingList.poll();
        if (reservations.containsKey(next.phone)) {
            System.out.println("[INFO] Passenger already has a reservation.");
            return;
        }

        int seat = 1;
        while (bus.bookedSeats.containsKey(seat)) seat++;

        next.seatNumber = seat;
        bus.bookedSeats.put(seat, next);
        reservations.put(next.phone, next);
        recentBookings.push(next);

        System.out.println("[SUCCESS] Waiting-list passenger has been booked.");
        next.display();
    }

    static void viewRecentBooking() {
        if (recentBookings.isEmpty()) {
            System.out.println("[INFO] No recent bookings.");
            return;
        }
        System.out.println("\n--------------- LAST BOOKING (LIFO) ---------------");
        recentBookings.peek().display();
    }

    public static void main(String[] args) {
        buses.add(new Bus(101, "City Express", "Raipur", "Bhilai", 10, 120));
        buses.add(new Bus(102, "Metro Travels", "Raipur", "Durg", 10, 150));
        buses.add(new Bus(103, "Highway Star", "Bhilai", "Bilaspur", 10, 220));

        header();

        while (true) {
            menu();
            String input = readLine("Select service (1-9): ");
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Enter a valid menu number.");
                continue;
            }

            switch (choice) {
                case 1: displayBuses(); break;
                case 2: checkSeats(); break;
                case 3: bookTicket(); break;
                case 4: cancelReservation(); break;
                case 5: viewReservations(); break;
                case 6: viewWaitingList(); break;
                case 7: processWaitingList(); break;
                case 8: viewRecentBooking(); break;
                case 9:
                    System.out.println("\nThank you for choosing CITYLINE BUS SERVICES!");
                    System.out.println("Have a safe journey. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("[ERROR] Invalid option. Choose 1 to 9.");
            }
        }
    }
}
