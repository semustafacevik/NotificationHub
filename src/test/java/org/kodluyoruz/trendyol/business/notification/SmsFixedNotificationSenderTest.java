package org.kodluyoruz.trendyol.business.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.trendyol.datastructures.SmsFixedPackage;
import org.kodluyoruz.trendyol.exceptions.InvalidMessageContentException;
import org.kodluyoruz.trendyol.models.Company;
import org.kodluyoruz.trendyol.models.Sms;
import org.kodluyoruz.trendyol.models.dtos.NotificationSendDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SmsFixedNotificationSenderTest {

    SmsFixedNotificationSender sut = new SmsFixedNotificationSender();
    NotificationSendDTO notificationSendDTO;
    Company company;
    Sms sms;

    @BeforeEach
    public void setUp() {
        company = new Company("Comp1", 0, new SmsFixedPackage());

        sms = new Sms("Hello");

        notificationSendDTO = new NotificationSendDTO();
        notificationSendDTO.setCompany(company);
        notificationSendDTO.setMessage(sms);
        notificationSendDTO.setUserName("userTest");
    }

    @Test
    public void sendNotification_WhenSentSms_ShouldDecreaseSmsLimit() {
        // Arrange
        // defined at beforeEach

        // Act
        sut.sendNotification(notificationSendDTO);

        // Assert
        assertThat(company.getSmsPackage().limit).isEqualTo(999);
    }

    @Test
    public void sendNotification_WhenSmsLimitExceeded_ShouldDefineExtraPackage() {
        // Arrange
        // defined at beforeEach
        company.getSmsPackage().limit = 0;

        // Act
        sut.sendNotification(notificationSendDTO);

        // Assert
        assertThat(company.getSmsPackage().limit).isEqualTo(999);
        assertThat(company.getInvoice()).isEqualTo(40.00);
    }

    @Test
    public void sendNotification_WhenSmsLimitExceeded_ShouldDefineExtraPackageForEachLimitExcess() {
        // Arrange
        // defined at beforeEach
        company.getSmsPackage().limit = 0;

        // Act
        sut.sendNotification(notificationSendDTO);

        company.getSmsPackage().limit = 0;

        sut.sendNotification(notificationSendDTO);
        sut.sendNotification(notificationSendDTO);
        sut.sendNotification(notificationSendDTO);

        // Assert
        assertThat(company.getSmsPackage().limit).isEqualTo(997);
        assertThat(company.getInvoice()).isEqualTo(60.00);
    }

    @Test
    public void sendNotification_WhenInvalidSms_ShouldThrowInvalidMessageContentException() {
        // Arrange
        // defined at beforeEach
        sms = new Sms("H");
        notificationSendDTO.setMessage(sms);

        // Act
        Throwable throwable = catchThrowable(() -> sut.sendNotification(notificationSendDTO));

        // Assert
        assertThat(throwable).isInstanceOf(InvalidMessageContentException.class).hasMessage("Invalid Message Content");
    }

    @Test
    public void defineExtraPackage_TrueStory() {
        // Arrange
        // defined at beforeEach

        // Act
        sut.defineExtraPackage(company);

        // Assert
        assertThat(company.getSmsPackage().limit).isEqualTo(1000);
        assertThat(company.getInvoice()).isEqualTo(40.00);
    }
}
