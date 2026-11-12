import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotingSystem {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Boolean> userVoteStatus = new HashMap<>();
    private final Map<String, Integer> candidateVotes = new HashMap<>();
    private final Map<String, Candidate> candidates = new HashMap<>();
    private int nextCandidateId = 1;

    public VotingSystem() {
        addCandidate("Alice Johnson");
        addCandidate("Benjamin Lee");
        addCandidate("Catherine Patel");
    }

    public void registerUser(String fullName, String username, String password) throws DuplicateUserException {
        if (users.containsKey(username)) {
            throw new DuplicateUserException("User already exists: " + username);
        }
        User user = new User(username, password, fullName);
        users.put(username, user);
        userVoteStatus.put(username, false);
    }

    public User loginUser(String username, String password) throws UserNotFoundException, InvalidLoginException {
        User user = users.get(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        if (!user.getPassword().equals(password)) {
            throw new InvalidLoginException("Incorrect password for user: " + username);
        }
        return user;
    }

    public void addCandidate(String candidateName) {
        String normalizedName = candidateName.trim();
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Candidate name cannot be empty.");
        }
        if (candidates.containsKey(normalizedName.toLowerCase())) {
            throw new IllegalArgumentException("Candidate already exists: " + candidateName);
        }
        Candidate candidate = new Candidate(nextCandidateId++, normalizedName);
        candidates.put(normalizedName.toLowerCase(), candidate);
        candidateVotes.putIfAbsent(normalizedName, 0);
    }

    private Candidate findCandidateByName(String candidateName) {
        String target = candidateName.trim();
        for (Candidate candidate : candidates.values()) {
            if (candidate.getName().equalsIgnoreCase(target)) {
                return candidate;
            }
        }
        return null;
    }

    public List<Candidate> listCandidates() {
        List<Candidate> candidateList = new ArrayList<>(candidates.values());
        candidateList.sort(Comparator.comparingInt(Candidate::getId));
        return candidateList;
    }

    public void castVote(String username, String candidateName) throws UserNotFoundException, AlreadyVotedException, CandidateNotFoundException {
        User user = users.get(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }

        if (Boolean.TRUE.equals(userVoteStatus.get(username))) {
            throw new AlreadyVotedException("This user has already cast a vote: " + username);
        }

        Candidate candidate = findCandidateByName(candidateName);
        if (candidate == null) {
            throw new CandidateNotFoundException("Candidate not found: " + candidateName);
        }

        userVoteStatus.put(username, true);
        candidateVotes.put(candidate.getName(), candidateVotes.getOrDefault(candidate.getName(), 0) + 1);
    }

    public int getTotalVotes() {
        int total = 0;
        for (int votes : candidateVotes.values()) {
            total += votes;
        }
        return total;
    }

    public Map<String, Integer> getCandidateVotes() {
        return new HashMap<>(candidateVotes);
    }

    public ResultSummary getResultSummary() {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(candidateVotes.entrySet());
        entries.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()));

        String finalist = entries.isEmpty() ? "No candidate" : entries.get(0).getKey();
        String runnerUp = entries.size() < 2 ? "No runner-up" : entries.get(1).getKey();

        return new ResultSummary(finalist, runnerUp, entries);
    }

    public void displayResults() {
        ResultSummary summary = getResultSummary();
        System.out.println("\n===== ELECTION RESULTS =====");
        System.out.println("Finalist: " + summary.getFinalist());
        System.out.println("Runner-Up: " + summary.getRunnerUp());
        System.out.println("\nCandidate vote standings:");
        List<Map.Entry<String, Integer>> ranking = summary.getRanking();
        int position = 1;
        for (Map.Entry<String, Integer> entry : ranking) {
            System.out.println(position + ". " + entry.getKey() + " -> " + entry.getValue() + " votes");
            position++;
        }
    }

    public static class DuplicateUserException extends Exception {
        public DuplicateUserException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends Exception {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidLoginException extends Exception {
        public InvalidLoginException(String message) {
            super(message);
        }
    }

    public static class AlreadyVotedException extends Exception {
        public AlreadyVotedException(String message) {
            super(message);
        }
    }

    public static class CandidateNotFoundException extends Exception {
        public CandidateNotFoundException(String message) {
            super(message);
        }
    }

    public static class ResultSummary {
        private final String finalist;
        private final String runnerUp;
        private final List<Map.Entry<String, Integer>> ranking;

        public ResultSummary(String finalist, String runnerUp, List<Map.Entry<String, Integer>> ranking) {
            this.finalist = finalist;
            this.runnerUp = runnerUp;
            this.ranking = ranking;
        }

        public String getFinalist() {
            return finalist;
        }

        public String getRunnerUp() {
            return runnerUp;
        }

        public List<Map.Entry<String, Integer>> getRanking() {
            return Collections.unmodifiableList(ranking);
        }
    }
}
