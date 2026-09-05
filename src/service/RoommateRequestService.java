package service;

import exception.AlreadyAllocatedException;
import exception.DuplicateRequestException;
import exception.RequestNotFoundException;
import exception.StudentNotFoundException;
import model.RoommateRequest;
import model.Student;
import util.FileManager;
import util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Service managing peer roommate requests, status transitions (PENDING, ACCEPTED, REJECTED, CANCELLED),
 * and business rules preventing self-requests, duplicate requests, and invalid allocations.
 */
public class RoommateRequestService {
    private static final String FILE_NAME = "requests.txt";
    private List<RoommateRequest> requestList;
    private StudentService studentService;

    public RoommateRequestService(StudentService studentService) {
        this.studentService = studentService;
        this.requestList = new ArrayList<>();
        loadRequests();
    }

    public void loadRequests() {
        requestList.clear();
        List<String> lines = FileManager.readLines(FILE_NAME);
        int maxId = 1000;
        for (String line : lines) {
            RoommateRequest r = RoommateRequest.fromFileString(line);
            if (r != null) {
                requestList.add(r);
                try {
                    String numStr = r.getRequestId().replaceAll("[^0-9]", "");
                    if (!numStr.isEmpty()) {
                        int num = Integer.parseInt(numStr);
                        if (num > maxId) maxId = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        IdGenerator.initCounters(100, maxId, 1000);
    }

    public void saveAllRequests() {
        List<String> lines = new ArrayList<>();
        for (RoommateRequest r : requestList) {
            lines.add(r.toFileString());
        }
        FileManager.writeLines(FILE_NAME, lines);
    }

    public RoommateRequest sendRequest(String senderId, String receiverId)
            throws StudentNotFoundException, AlreadyAllocatedException, DuplicateRequestException, IllegalArgumentException {

        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("Student IDs cannot be null.");
        }
        if (senderId.equalsIgnoreCase(receiverId.trim())) {
            throw new IllegalArgumentException("You cannot send a roommate request to yourself.");
        }

        Student sender = studentService.getStudentById(senderId);
        Student receiver = studentService.getStudentById(receiverId);

        if (sender.isAllocated()) {
            throw new AlreadyAllocatedException("You are already allocated to Room " + sender.getAllocatedRoomNo() + " and cannot send new requests.");
        }
        if (receiver.isAllocated()) {
            throw new AlreadyAllocatedException("Student " + receiver.getName() + " is already allocated to Room " + receiver.getAllocatedRoomNo() + ".");
        }

        // Check duplicate active pending request
        for (RoommateRequest req : requestList) {
            if (req.isPending()) {
                if (req.getSenderId().equalsIgnoreCase(senderId) && req.getReceiverId().equalsIgnoreCase(receiverId)) {
                    throw new DuplicateRequestException("You have already sent a pending request (" + req.getRequestId() + ") to " + receiver.getName() + ".");
                }
                if (req.getSenderId().equalsIgnoreCase(receiverId) && req.getReceiverId().equalsIgnoreCase(senderId)) {
                    throw new DuplicateRequestException(receiver.getName() + " has already sent you a pending request (" + req.getRequestId() + "). Please check received requests.");
                }
            }
        }

        String reqId = IdGenerator.generateRequestId();
        RoommateRequest newReq = new RoommateRequest(reqId, sender.getStudentId(), receiver.getStudentId());
        requestList.add(newReq);
        saveAllRequests();
        return newReq;
    }

    public List<RoommateRequest> getSentRequests(String studentId) {
        List<RoommateRequest> sent = new ArrayList<>();
        for (RoommateRequest r : requestList) {
            if (r.getSenderId().equalsIgnoreCase(studentId)) {
                sent.add(r);
            }
        }
        return sent;
    }

    public List<RoommateRequest> getReceivedRequests(String studentId) {
        List<RoommateRequest> received = new ArrayList<>();
        for (RoommateRequest r : requestList) {
            if (r.getReceiverId().equalsIgnoreCase(studentId)) {
                received.add(r);
            }
        }
        return received;
    }

    public RoommateRequest acceptRequest(String requestId, String loggedInStudentId)
            throws RequestNotFoundException, AlreadyAllocatedException, IllegalArgumentException, StudentNotFoundException {

        RoommateRequest request = findRequestById(requestId);
        if (!request.getReceiverId().equalsIgnoreCase(loggedInStudentId)) {
            throw new IllegalArgumentException("You are not authorized to accept this request as you are not the recipient.");
        }
        if (!request.isPending()) {
            throw new IllegalArgumentException("Request is already in status: " + request.getStatus());
        }

        Student sender = studentService.getStudentById(request.getSenderId());
        Student receiver = studentService.getStudentById(request.getReceiverId());

        if (sender.isAllocated()) {
            request.setStatus("CANCELLED");
            saveAllRequests();
            throw new AlreadyAllocatedException("The sender (" + sender.getName() + ") has already been allocated to another room.");
        }
        if (receiver.isAllocated()) {
            request.setStatus("CANCELLED");
            saveAllRequests();
            throw new AlreadyAllocatedException("You are already allocated to a room and cannot accept new roommate pairings.");
        }

        request.setStatus("ACCEPTED");
        saveAllRequests();
        return request;
    }

    public RoommateRequest rejectRequest(String requestId, String loggedInStudentId)
            throws RequestNotFoundException, IllegalArgumentException {

        RoommateRequest request = findRequestById(requestId);
        if (!request.getReceiverId().equalsIgnoreCase(loggedInStudentId)) {
            throw new IllegalArgumentException("You are not authorized to reject this request as you are not the recipient.");
        }
        if (!request.isPending()) {
            throw new IllegalArgumentException("Request is not in PENDING status (Current: " + request.getStatus() + ").");
        }

        request.setStatus("REJECTED");
        saveAllRequests();
        return request;
    }

    public RoommateRequest cancelRequest(String requestId, String loggedInStudentId)
            throws RequestNotFoundException, IllegalArgumentException {

        RoommateRequest request = findRequestById(requestId);
        if (!request.getSenderId().equalsIgnoreCase(loggedInStudentId)) {
            throw new IllegalArgumentException("You are not authorized to cancel this request as you are not the sender.");
        }
        if (!request.isPending()) {
            throw new IllegalArgumentException("Only PENDING requests can be cancelled (Current: " + request.getStatus() + ").");
        }

        request.setStatus("CANCELLED");
        saveAllRequests();
        return request;
    }

    public RoommateRequest findRequestById(String requestId) throws RequestNotFoundException {
        if (requestId == null) {
            throw new RequestNotFoundException("Request ID cannot be null.");
        }
        for (RoommateRequest r : requestList) {
            if (r.getRequestId().equalsIgnoreCase(requestId.trim())) {
                return r;
            }
        }
        throw new RequestNotFoundException("Roommate request with ID '" + requestId + "' was not found.");
    }

    public List<RoommateRequest> getAllRequests() {
        return new ArrayList<>(requestList);
    }

    public int getPendingRequestCount() {
        int count = 0;
        for (RoommateRequest r : requestList) {
            if (r.isPending()) count++;
        }
        return count;
    }
}
