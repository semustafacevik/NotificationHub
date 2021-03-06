package org.kodluyoruz.trendyol.business.notification;

import org.kodluyoruz.trendyol.business.notification.abstraction.ElasticNotificationSender;
import org.kodluyoruz.trendyol.business.validation.MessageContentValidation;
import org.kodluyoruz.trendyol.constants.ErrorMessage;
import org.kodluyoruz.trendyol.datastructures.EmailElasticPackage;
import org.kodluyoruz.trendyol.exceptions.InvalidMessageContentException;
import org.kodluyoruz.trendyol.models.Company;
import org.kodluyoruz.trendyol.models.Email;
import org.kodluyoruz.trendyol.models.dtos.NotificationSendDTO;

public class EmailElasticNotificationSender implements ElasticNotificationSender {
    @Override
    public void sendNotification(NotificationSendDTO notificationSendDTO) {
        Email email = (Email) notificationSendDTO.getMessage();
        Company company = notificationSendDTO.getCompany();

        boolean validContent = MessageContentValidation.checkMessageContent(email);

        if (!validContent) throw new InvalidMessageContentException(ErrorMessage.invalidMessageContent(company.getLanguage()));

        if (company.getEmailPackage().limit > 0) {
            company.getEmailPackage().limit--;
            System.out.println(company.getName() +
                    " - sent Email (ElasticPackage) -> " + notificationSendDTO.getUserName() +
                    " - subject : " + email.getSubject() +
                    " - content : " + email.getContent() +
                    " - remaining limit : " + company.getEmailPackage().limit);
        } else {
            System.out.printf("\n" + company.getName() + " - exceeded Email limit (ElasticPackage)" +
                    " - current invoice : %.2f \n", company.getInvoice());

            addUnitPriceToInvoice(company);

            System.out.printf(company.getName() +
                    " - sent Email (ElasticPackage) -> " + notificationSendDTO.getUserName() +
                    " - subject : " + email.getSubject() +
                    " - content : " + email.getContent() +
                    " - new invoice : %.2f \n", company.getInvoice());
        }
    }

    @Override
    public void addUnitPriceToInvoice(Company company) {
        EmailElasticPackage emailElasticPackage = (EmailElasticPackage) company.getEmailPackage();

        company.setInvoice(company.getInvoice() + emailElasticPackage.limitExcessUnitPrice);
    }
}
