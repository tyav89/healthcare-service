package ru.netology.patient.service.medical;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;



class MedicalServiceImplTest {
    static MedicalService medicalService;
    static SendAlertServiceImpl sendAlertService;
    static String id = UUID.randomUUID().toString();
    static String message = String.format("Warning, patient with id: %s, need help", id);


    @BeforeEach
    void init() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(id)).thenReturn(new PatientInfo(id,"Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))));

        sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
        medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
    }
    @Test
    void checkBloodPressure() {
        medicalService.checkBloodPressure(id, new BloodPressure(120, 120));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals(message, argumentCaptor.getValue());
    }

    @Test
    void checkBloodPressureNotMessage(){
        medicalService.checkBloodPressure(id, new BloodPressure(120, 80));
        Mockito.verify(sendAlertService, Mockito.never()).send(message);
    }

    @Test
    void checkTemperature() {
        medicalService.checkTemperature(id, new BigDecimal("35.0"));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals(message, argumentCaptor.getValue());
    }
}