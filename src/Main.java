import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        VotingSystem votingSystem = new VotingSystem();
        Scanner scanner = new Scanner(System.in);
        User currentUser = null;

        while (true) {
            System.out.println("\n=== ONLINE VOTING SYSTEM ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. View Candidates");
            System.out.println("4. Cast Vote");
            System.out.println("5. View Total Votes");
            System.out.println("6. View Results");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Full name: ");
                        String fullName = scanner.nextLine();
                        System.out.print("Username: ");
                        String username = scanner.nextLine();
                        System.out.print("Password: ");
                        String password = scanner.nextLine();
                        votingSystem.registerUser(fullName, username, password);
                        System.out.println("Registration successful for " + fullName + ".");
                        break;

                    case "2":
                        System.out.print("Username: ");
                        String loginUser = scanner.nextLine();
                        System.out.print("Password: ");
                        String loginPass = scanner.nextLine();
                        currentUser = votingSystem.loginUser(loginUser, loginPass);
                        System.out.println("Login successful. Welcome, " + currentUser.getFullName() + ".");
                        break;

                    case "3":
                        List<Candidate> candidates = votingSystem.listCandidates();
                        System.out.println("\nCandidates:");
                        for (Candidate candidate : candidates) {
                            System.out.println(candidate.getId() + ". " + candidate.getName());
                        }
                        break;

                    case "4":
                        if (currentUser == null) {
                            System.out.println("Please login first before voting.");
                            break;
                        }
                        System.out.print("Enter candidate name: ");
                        String candidateName = scanner.nextLine();
                        votingSystem.castVote(currentUser.getUsername(), candidateName);
                        System.out.println("Vote cast successfully for " + candidateName + ".");
                        break;

                    case "5":
                        System.out.println("Total votes cast: " + votingSystem.getTotalVotes());
                        Map<String, Integer> votes = votingSystem.getCandidateVotes();
                        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
                            System.out.println(entry.getKey() + " -> " + entry.getValue() + " votes");
                        }
                        break;

                    case "6":
                        votingSystem.displayResults();
                        break;

                    case "7":
                        System.out.println("Thank you for using the Online Voting System.");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (VotingSystem.DuplicateUserException | VotingSystem.UserNotFoundException |
                     VotingSystem.InvalidLoginException | VotingSystem.AlreadyVotedException |
                     VotingSystem.CandidateNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
