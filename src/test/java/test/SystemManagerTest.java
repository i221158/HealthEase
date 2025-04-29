package test;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.hibernate.Session;
import org.hibernate.Transaction;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.hibernate.query.Query;  // This is the correct import for Hibernate 5 and later
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.example.*;
import System.SystemManager;

public class SystemManagerTest {

    private SystemManager systemManager;
    @Mock
    private Session mockSession;

    @Mock
    private Transaction mockTransaction;

    Query<User> mockQuery = mock(Query.class);  // Mock Query object


    @BeforeEach
    public void setUp() throws Exception {
        mockSession = mock(Session.class);
        mockTransaction = mock(Transaction.class);
        SessionFactory mockFactory = mock(SessionFactory.class);

        // Mock behavior
        when(mockSession.getTransaction()).thenReturn(mockTransaction);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        when(mockFactory.getCurrentSession()).thenReturn(mockSession);

        // Create the SystemManager instance
        systemManager = new SystemManager();

        // Inject mockFactory into the SystemManager instance using reflection
        Field factoryField = SystemManager.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(systemManager, mockFactory);
    }


    @Test
    public void testViewAllDoctors() {
        // Prepare data for test
        List<Doctor> mockDoctors = new ArrayList<>();
        Doctor doctor1 = new Doctor();
        doctor1.setUserId(1);
        doctor1.setName("Dr. Smith");
        doctor1.setSpecialization("Cardiologist");

        Doctor doctor2 = new Doctor();
        doctor2.setUserId(2);
        doctor2.setName("Dr. Jones");
        doctor2.setSpecialization("Dermatologist");

        mockDoctors.add(doctor1);
        mockDoctors.add(doctor2);
        when(mockSession.createQuery("FROM Doctor", Doctor.class).getResultList()).thenReturn(mockDoctors);

        // Call method
        systemManager.viewAllDoctors();

        // Verify interactions
        verify(mockSession).createQuery("FROM Doctor", Doctor.class);
        verify(mockSession).beginTransaction();
        verify(mockTransaction).commit();
        verify(mockSession).close();
    }

    // Additional test cases for other methods of SystemManager
    @Test
    public void testAddDoctor() {
        // Create a mock Doctor instance and set the necessary fields
        Doctor newDoctor = new Doctor();
        newDoctor.setUserId(3);
        newDoctor.setName("Dr. Williams");
        newDoctor.setSpecialization("Neurologist");

        // Mock session behavior
        when(mockSession.getTransaction()).thenReturn(mockTransaction);
        when(mockSession.save(newDoctor)).thenReturn(null); // Mock saving doctor

        // Call method
        systemManager.addDoctor(newDoctor);

        // Verify interactions
        verify(mockSession).beginTransaction();
        verify(mockSession).save(newDoctor);
        verify(mockTransaction).commit();
        verify(mockSession).close();
    }

    @Test
    public void testRemoveDoctor() {
        int doctorIdToRemove = 1;

        // Call method
        systemManager.removeDoctor(doctorIdToRemove);

        // Verify interactions
        verify(mockSession).delete(any(Doctor.class));
        verify(mockSession).beginTransaction();
        verify(mockTransaction).commit();
        verify(mockSession).close();
    }

    @Test
    public void testLoginUser_SuccessfulLogin() {
        // Mock the session and query behavior



        // Create and set mock user using getters and setters
        User mockUser = new User();
        mockUser.setUserId(1);  // Assuming the setter for userId exists
        mockUser.setEmail("test@example.com");
        mockUser.setPasswordHash("password");
        mockUser.setName("John Doe");
        mockUser.setRole("Admin");

        // Mock the behavior of the session and query
        when(mockSession.createQuery("FROM User WHERE email = :email AND passwordHash = :pass", User.class))
                .thenReturn(mockQuery);
        when(mockQuery.setParameter("email", "test@example.com")).thenReturn(mockQuery);
        when(mockQuery.setParameter("pass", "password")).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(mockUser);

        // Call the method to test the loginUser method
        systemManager.setSession(mockSession); // Assuming your systemManager has a method to set the session
        User result = systemManager.loginUser("test@example.com", "password");

        // Verify session interactions
        verify(mockSession).beginTransaction();
        verify(mockSession).createQuery("FROM User WHERE email = :email AND passwordHash = :pass", User.class);
        verify(mockSession).getTransaction().commit();
        verify(mockSession).close();

        // Assert that the user is returned
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    public void testLoginUser_FailedAuthentication() {
        // Mock the authenticateUser method to return false
        SystemManager systemManagerFailAuth = new SystemManager() {
            @Override
            public boolean authenticateUser(String email, String password) {
                return false; // Simulate failed authentication
            }

            @Override
            public Session getSession() {
                return mockSession; // Return mocked session
            }
        };

        // Test the loginUser method
        User result = systemManagerFailAuth.loginUser("test@example.com", "wrongpassword");

        // Verify session interactions (there should be no session query)
        verify(mockSession, times(0)).beginTransaction();
        verify(mockSession, times(0)).createQuery(anyString(), eq(User.class));
        verify(mockSession, times(0)).getTransaction();

        // Assert that null is returned and login failed
        assertNull(result);
    }

    @Test
    void testRegisterPatient_userDoesNotExist_registersSuccessfully() {
        SystemManager manager = Mockito.spy(new SystemManager());
        Mockito.doReturn(false).when(manager).checkIfUserExists("test@example.com");

        manager.registerPatient("Alice", "test@example.com", "1234567890", "pass", "No history");

        Mockito.verify(manager).storePatient(Mockito.any());
        Mockito.verify(manager).sendConfirmationEmail("test@example.com", "Alice", "You are registered as a patient.");
    }

    @Test
    void testRegisterPatient_userAlreadyExists_doesNotRegister() {
        SystemManager manager = Mockito.spy(new SystemManager());
        Mockito.doReturn(true).when(manager).checkIfUserExists("existing@example.com");

        manager.registerPatient("Bob", "existing@example.com", "9876543210", "pass", "History");

        Mockito.verify(manager, Mockito.never()).storePatient(Mockito.any());
        Mockito.verify(manager, Mockito.never()).sendConfirmationEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void testAuthenticateUser_validCredentials_returnsTrue() {
        Session session = Mockito.mock(Session.class);
        Query<Long> query = Mockito.mock(Query.class);
        SessionFactory mockFactory = Mockito.mock(SessionFactory.class);

        Mockito.when(mockFactory.openSession()).thenReturn(session);
        Mockito.when(session.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(query);
        Mockito.when(query.setParameter("email", "valid@example.com")).thenReturn(query);
        Mockito.when(query.setParameter("pass", "pass123")).thenReturn(query);
        Mockito.when(query.uniqueResult()).thenReturn(1L);

        SystemManager manager = new SystemManager();
        manager.factory = mockFactory;

        Mockito.doNothing().when(session).beginTransaction();
        Mockito.doNothing().when(session).getTransaction().commit();
        Mockito.doNothing().when(session).close();

        boolean result = manager.authenticateUser("valid@example.com", "pass123");

        Assertions.assertTrue(result);
    }

    @Test
    void testAuthenticateUser_invalidCredentials_returnsFalse() {
        Session session = Mockito.mock(Session.class);
        Query<Long> query = Mockito.mock(Query.class);
        SessionFactory mockFactory = Mockito.mock(SessionFactory.class);

        Mockito.when(mockFactory.openSession()).thenReturn(session);
        Mockito.when(session.createQuery(Mockito.anyString(), Mockito.eq(Long.class))).thenReturn(query);
        Mockito.when(query.setParameter("email", "invalid@example.com")).thenReturn(query);
        Mockito.when(query.setParameter("pass", "wrong")).thenReturn(query);
        Mockito.when(query.uniqueResult()).thenReturn(0L);

        SystemManager manager = new SystemManager();
        manager.factory = mockFactory;

        Mockito.doNothing().when(session).beginTransaction();
        Mockito.doNothing().when(session).getTransaction().commit();
        Mockito.doNothing().when(session).close();

        boolean result = manager.authenticateUser("invalid@example.com", "wrong");

        Assertions.assertFalse(result);
    }

    @Test
    void testRegisterDoctor_successfulRegistration() {
        SystemManager manager = Mockito.spy(new SystemManager());
        Session session = Mockito.mock(Session.class);
        Mockito.doReturn(session).when(manager).getSession();

        Transaction tx = Mockito.mock(Transaction.class);
        Mockito.when(session.beginTransaction()).thenReturn(tx);

        Mockito.doNothing().when(session).persist(Mockito.any(Doctor.class));
        Mockito.doNothing().when(tx).commit();

        manager.registerDoctor("Dr. Ali", "dr@example.com", "112233", "doc123", "Cardiology", "LIC123", true, "Mon-Fri 10:00-16:00");

        Mockito.verify(session).persist(Mockito.any(Doctor.class));
        Mockito.verify(manager).sendConfirmationEmail("dr@example.com", "Dr. Ali", "You are registered as a doctor.");
    }

    @Test
    void testRegisterDoctor_exceptionDuringTransaction_rollsBack() {
        SystemManager manager = Mockito.spy(new SystemManager());
        Session session = Mockito.mock(Session.class);
        Mockito.doReturn(session).when(manager).getSession();

        Transaction tx = Mockito.mock(Transaction.class);
        Mockito.when(session.beginTransaction()).thenReturn(tx);
        Mockito.doThrow(new RuntimeException("DB error")).when(session).persist(Mockito.any(Doctor.class));
        Mockito.doNothing().when(tx).rollback();

        manager.registerDoctor("Dr. Error", "error@example.com", "000000", "fail", "Surgery", "LIC999", true, "Mon 10:00-12:00");

        Mockito.verify(tx).rollback();
    }

    @Test
    void testIsTimeWithinAvailability_validMatch_returnsTrue() {
        SystemManager manager = new SystemManager();
        boolean result = manager.isTimeWithinAvailability("Mon-Fri 09:00-17:00", "Wed", LocalTime.of(10, 0));
        Assertions.assertTrue(result);
    }

    @Test
    void testIsTimeWithinAvailability_outOfTime_returnsFalse() {
        SystemManager manager = new SystemManager();
        boolean result = manager.isTimeWithinAvailability("Mon-Fri 09:00-17:00", "Sun", LocalTime.of(12, 0));
        Assertions.assertFalse(result);
    }

    @Test
    public void testUpdatePatientHistory_Success() {
        Session mockSession = mock(Session.class);
        Transaction mockTransaction = mock(Transaction.class);
        Patient mockPatient = mock(Patient.class);

        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        when(mockSession.get(Patient.class, 1)).thenReturn(mockPatient);

        // Create SystemManager with mock session
        SystemManager systemManager = new SystemManager(mockSession);

        // Act
        systemManager.updatePatientHistory(1, "New medical history");

        // Verify
        verify(mockSession).beginTransaction();
        verify(mockPatient).setMedicalHistory("New medical history");
        verify(mockSession).getTransaction().commit();
    }


    @Test
    public void testManagePatientHistory_PatientFound() {
        Session mockSession = mock(Session.class);
        Transaction mockTransaction = mock(Transaction.class);
        Patient mockPatient = mock(Patient.class);
        @SuppressWarnings("unchecked")
        Query<Patient> mockQuery = mock(Query.class); // 🔧 Define and mock the query

        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        when(mockSession.createQuery("FROM Patient", Patient.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.singletonList(mockPatient));
        when(mockSession.get(Patient.class, 1)).thenReturn(mockPatient);
        when(mockPatient.getName()).thenReturn("John Doe");
        when(mockPatient.getEmail()).thenReturn("johndoe@example.com");
        when(mockPatient.getPhone()).thenReturn("123-456-7890");
        when(mockPatient.getMedicalHistory()).thenReturn("No history");

        Scanner mockScanner = mock(Scanner.class);
        when(mockScanner.nextLine()).thenReturn("1"); // Simulates user selecting the patient

        // Create SystemManager with mock session
        SystemManager systemManager = new SystemManager(mockSession);

        // Inject the mock Scanner if SystemManager uses it
        // e.g., systemManager.setScanner(mockScanner); if there's a setter for it

        // Act
        systemManager.managePatientHistory();

        // Verify expected interactions
        verify(mockSession).beginTransaction();
        verify(mockSession).get(Patient.class, 1);
        verify(mockSession).getTransaction().commit();
    }
    @Test
    public void testViewNotifications_NotificationsFound() {
        Session mockSession = mock(Session.class);
        Transaction mockTransaction = mock(Transaction.class);
        Notification mockNotification = mock(Notification.class);
        @SuppressWarnings("unchecked")
        Query<Notification> mockQuery = mock(Query.class); // ✅ Declare and mock this

        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        when(mockSession.createQuery("FROM Notification WHERE user.id = :uid", Notification.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("uid", 1)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.singletonList(mockNotification));
        when(mockNotification.getTimestamp()).thenReturn(LocalDateTime.now());
        when(mockNotification.getMessage()).thenReturn("Sample notification");

        // Create SystemManager with mock session
        SystemManager systemManager = new SystemManager(mockSession);

        // Act
        systemManager.viewNotifications(1);

        // Verify interactions
        verify(mockSession).beginTransaction();
        verify(mockSession).getTransaction().commit();
    }

    @Test
    public void testCancelAppointment_Success() {
        Session mockSession = mock(Session.class);
        Transaction mockTransaction = mock(Transaction.class);
        Appointment mockAppointment = mock(Appointment.class);
        Patient mockPatient = mock(Patient.class);
        Doctor mockDoctor = mock(Doctor.class);
        @SuppressWarnings("unchecked")
        Query<Appointment> mockQuery = mock(Query.class); // ✅ Declare mockQuery

        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        when(mockSession.createQuery("FROM Appointment WHERE patient.id = :uid AND status = 'SCHEDULED'", Appointment.class))
                .thenReturn(mockQuery);
        when(mockQuery.setParameter("uid", 1)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.singletonList(mockAppointment));
        when(mockSession.get(Appointment.class, 1)).thenReturn(mockAppointment);
        when(mockAppointment.getStatus()).thenReturn("SCHEDULED");
        when(mockAppointment.getPatient()).thenReturn(mockPatient);
        when(mockAppointment.getDoctor()).thenReturn(mockDoctor);
        when(mockAppointment.getAppointmentDate()).thenReturn(LocalDateTime.now());

        // Create SystemManager with mock session
        SystemManager systemManager = new SystemManager(mockSession);

        // Act
        systemManager.cancelAppointment(1, "PATIENT");

        // Verify
        verify(mockSession).beginTransaction();
        verify(mockSession).getTransaction().commit();
        verify(mockAppointment).setStatus("CANCELED");
        verify(mockSession).persist(any(Notification.class));
    }

    @Test
    public void testViewAppointmentsForUser_Success() {
        Session mockSession = mock(Session.class);
        Transaction mockTransaction = mock(Transaction.class);
        Appointment mockAppointment = mock(Appointment.class);
        Patient mockPatient = mock(Patient.class); // ✅ Declare mockPatient
        Doctor mockDoctor = mock(Doctor.class);   // ✅ Declare mockDoctor
        @SuppressWarnings("unchecked")
        Query<Appointment> mockQuery = mock(Query.class); // ✅ Declare mockQuery

        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        when(mockSession.createQuery("FROM Appointment WHERE patient.id = :id AND status != 'CANCELED'", Appointment.class))
                .thenReturn(mockQuery);
        when(mockQuery.setParameter("id", 1)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.singletonList(mockAppointment));
        when(mockAppointment.getAppointmentId()).thenReturn(1);
        when(mockAppointment.getPatient()).thenReturn(mockPatient);
        when(mockAppointment.getDoctor()).thenReturn(mockDoctor);
        when(mockAppointment.getAppointmentDate()).thenReturn(LocalDateTime.now());

        // Create SystemManager with mock session
        SystemManager systemManager = new SystemManager(mockSession);

        // Act
        systemManager.viewAppointmentsForUser(1);

        // Verify
        verify(mockSession).beginTransaction();
        verify(mockSession).getTransaction().commit();
    }

    @Test
    public void testCheckIfUserExists_UserFound() {
        Session mockSession = mock(Session.class);
        Transaction mockTransaction = mock(Transaction.class);
        @SuppressWarnings("unchecked")
        Query<Long> mockQuery = mock(Query.class);  // ✅ Declare mockQuery

        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        when(mockSession.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class))
                .thenReturn(mockQuery);
        when(mockQuery.setParameter("email", "johndoe@example.com")).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(1L);

        // Create SystemManager with mock session
        SystemManager systemManager = new SystemManager(mockSession);

        // Act
        boolean exists = systemManager.checkIfUserExists("johndoe@example.com");

        // Verify
        verify(mockSession).beginTransaction();
        verify(mockSession).getTransaction().commit();
        assertTrue(exists);
    }

}
