package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.converters;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.EmailAddress;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for {@link EmailAddress} values.
 *
 * @since 1.0
 */
@Converter(autoApply = false)
public class EmailAddressPersistenceConverter implements AttributeConverter<EmailAddress, String> {
    @Override
    public String convertToDatabaseColumn(EmailAddress attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public EmailAddress convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new EmailAddress(dbData);
    }
}
