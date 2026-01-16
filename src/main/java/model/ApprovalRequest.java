package model;

import java.time.LocalDate;

public abstract class ApprovalRequest {

    protected String requestId;
    protected String status;   // PENDING, APPROVED, REJECTED
    protected LocalDate createdAt;
    protected String createdByStaffId;
    protected String createdByRole;
    protected String reason;

    protected LocalDate processedAt;
    protected String processedByStaffId;
    protected String managerNote;

    public ApprovalRequest() {}

    public ApprovalRequest(String requestId, String staffId, String role, String reason) {
        this.requestId = requestId;
        this.createdByStaffId = staffId;
        this.createdByRole = role;
        this.reason = reason;
        this.status = "PENDING";
        this.createdAt = LocalDate.now();
    }

    // ===== Business =====
    public void approve(String managerId, String note) {
        this.status = "APPROVED";
        this.processedAt = LocalDate.now();
        this.processedByStaffId = managerId;
        this.managerNote = note;
    }

    public void reject(String managerId, String note) {
        this.status = "REJECTED";
        this.processedAt = LocalDate.now();
        this.processedByStaffId = managerId;
        this.managerNote = note;
    }

    // ===== GETTERS & SETTERS =====

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt.toString();
    }

    public void setCreatedAt(String createdAtString) {
        this.createdAt = LocalDate.parse(createdAtString);
    }

    public String getCreatedByStaffId() {
        return createdByStaffId;
    }

    public void setCreatedByStaffId(String createdByStaffId) {
        this.createdByStaffId = createdByStaffId;
    }

    public String getCreatedByRole() {
        return createdByRole;
    }

    public void setCreatedByRole(String createdByRole) {
        this.createdByRole = createdByRole;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getProcessedAt() {
          if (this.processedAt == null) {
            return "null"; // Hoặc trả về null nếu muốn
        }
        return this.processedAt.toString();
    }

    public void setProcessedAt(String processedAtString) {
        if (processedAtString.equals("null")){
            this.processedAt = null;
        }else{
            this.processedAt = LocalDate.parse(processedAtString);
        }
    }

    public String getProcessedByStaffId() {
        return processedByStaffId;
    }

    public void setProcessedByStaffId(String processedByStaffId) {
        this.processedByStaffId = processedByStaffId;
    }

    public String getManagerNote() {
        return managerNote;
    }

    public void setManagerNote(String managerNote) {
        this.managerNote = managerNote;
    }
}
