package com.polyclinic.registry;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalCertificateDAO {
    private final DatabaseManager dbManager;

    public MedicalCertificateDAO() {
        this.dbManager = new DatabaseManager();
    }

    public boolean addCertificate(MedicalCertificate certificate) throws PolyclinicException {
        String sql = "INSERT INTO medical_certificates " +
                "(patient_policy, issue_date, start_date, end_date, diagnosis, recommendations, doctor_id, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, certificate.getPatientPolicy());
            pstmt.setDate(2, Date.valueOf(certificate.getIssueDate()));
            pstmt.setDate(3, Date.valueOf(certificate.getStartDate()));
            pstmt.setDate(4, Date.valueOf(certificate.getEndDate()));
            pstmt.setString(5, certificate.getDiagnosis());
            pstmt.setString(6, certificate.getRecommendations());
            pstmt.setInt(7, certificate.getDoctorId());
            pstmt.setString(8, certificate.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        certificate.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при добавлении справки: " + e.getMessage()
            );
        }
        return false;
    }

    public List<MedicalCertificate> getCertificatesByPatient(int patientPolicy) throws PolyclinicException {
        List<MedicalCertificate> certificates = new ArrayList<>();
        String sql = "SELECT * FROM medical_certificates WHERE patient_policy = ? ORDER BY issue_date DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientPolicy);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                certificates.add(mapResultSetToCertificate(rs));
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при получении справок: " + e.getMessage()
            );
        }
        return certificates;
    }

    public List<DiseaseStatistics> getDiseaseStatistics() throws PolyclinicException {
        List<DiseaseStatistics> stats = new ArrayList<>();
        String sql = "SELECT diagnosis, COUNT(*) as count " +
                "FROM medical_certificates " +
                "GROUP BY diagnosis " +
                "ORDER BY count DESC";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DiseaseStatistics stat = new DiseaseStatistics(
                        rs.getString("diagnosis"),
                        rs.getInt("count")
                );
                stats.add(stat);
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при получении статистики: " + e.getMessage()
            );
        }
        return stats;
    }
    public boolean deleteCertificate(int certificateId) throws PolyclinicException {
        String sql = "DELETE FROM medical_certificates WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnectionStatic();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, certificateId);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении справки: " + e.getMessage());
            throw new PolyclinicException(PolyclinicException.ErrorCode.DATABASE_ERROR, "Не удалось удалить справку", e);
        }
    }

    private MedicalCertificate mapResultSetToCertificate(ResultSet rs) throws SQLException {
        MedicalCertificate cert = new MedicalCertificate();
        cert.setId(rs.getInt("id"));
        cert.setPatientPolicy(rs.getInt("patient_policy"));
        cert.setIssueDate(rs.getDate("issue_date").toLocalDate());
        cert.setStartDate(rs.getDate("start_date").toLocalDate());
        cert.setEndDate(rs.getDate("end_date").toLocalDate());
        cert.setDiagnosis(rs.getString("diagnosis"));
        cert.setRecommendations(rs.getString("recommendations"));
        cert.setDoctorId(rs.getInt("doctor_id"));
        cert.setStatus(rs.getString("status"));
        return cert;
    }
}