package com.acme.coldtrace.platform.iam.infrastructure.persistence.jpa.converters;

import com.acme.coldtrace.platform.iam.domain.model.valueobjects.RoleName;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for {@link RoleName} values.
 *
 * @since 1.0
 */
@Converter(autoApply = false)
public class RoleNamePersistenceConverter implements AttributeConverter<RoleName, String> {
    @Override
    public String convertToDatabaseColumn(RoleName attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public RoleName convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new RoleName(dbData);
    }
}
