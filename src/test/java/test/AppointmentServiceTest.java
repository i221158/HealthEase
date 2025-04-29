package test;

import com.example.*;
import System.SystemManager;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class AppointmentServiceTest {

    @Test
    public void testGetAppointmentDateTimeFromSlot() {
        // Mock the dependencies
        Session mockSession = mock(Session.class);
        Transaction mockTransaction = mock(Transaction.class);
        DoctorAvailabilityHours mockAvailability = mock(DoctorAvailabilityHours.class);

        // Test data
        int doctorId = 1;
        int slotId = 101;
        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 5, 5, 10, 0);

        // Simulate behavior
        when(mockSession.get(DoctorAvailabilityHours.class, slotId)).thenReturn(mockAvailability);
        when(mockAvailability.getAppointmentDate()).thenReturn(expectedDateTime);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);

        // Create service with mocked session
        SystemManager service = new SystemManager() {
            @Override
            public Session getSession() {
                return mockSession;
            }
        };

        // Call and assert
        LocalDateTime actualDateTime = service.getAppointmentDateTimeFromSlot(doctorId, slotId);
        assertEquals(expectedDateTime, actualDateTime);

        // Verify
        verify(mockSession).get(DoctorAvailabilityHours.class, slotId);
        verify(mockTransaction).commit();
        verify(mockSession).close();
    }

    @Test
    public void testGetAppointmentDateTimeFromSlotWhenAvailabilityIsNull() {
        // Mock the dependencies
        Session mockSession = mock(Session.class);
        Transaction mockTransaction = mock(Transaction.class);

        // Simulate no availability
        when(mockSession.get(DoctorAvailabilityHours.class, 101)).thenReturn(null);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);

        // Create service with mocked session
        SystemManager service = new SystemManager() {
            @Override
            public Session getSession() {
                return mockSession;
            }
        };

        // Call and assert
        LocalDateTime result = service.getAppointmentDateTimeFromSlot(1, 101);
        assertNull(result);

        // Verify
        verify(mockSession).get(DoctorAvailabilityHours.class, 101);
        verify(mockTransaction).commit();
        verify(mockSession).close();
    }

    @Test
    public void testGetAppointmentDateTimeFromSlotExceptionHandling() {
        // Mock the dependencies
        Session mockSession = mock(Session.class);

        // Simulate an exception when beginning transaction
        when(mockSession.beginTransaction()).thenThrow(new RuntimeException("Database error"));

        // Create service with mocked session
        SystemManager service = new SystemManager() {
            @Override
            public Session getSession() {
                return mockSession;
            }
        };

        // Call and assert
        LocalDateTime result = service.getAppointmentDateTimeFromSlot(1, 101);
        assertNull(result);

        // Verify session close was called even after exception
        verify(mockSession).close();
    }


}
