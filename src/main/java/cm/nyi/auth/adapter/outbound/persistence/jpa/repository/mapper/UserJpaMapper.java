package cm.nyi.auth.adapter.outbound.persistence.jpa.repository.mapper;

import cm.nyi.auth.adapter.outbound.persistence.jpa.entity.UserJpa;
import cm.nyi.auth.domain.user.User;
import cm.nyi.auth.domain.user.UserId;
import cm.nyi.shared.utils.CreatedAt;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserJpaMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "createdAt", expression = "java(domain.getCreatedAt().value())")
    UserJpa toJpa(User domain);

    default User toDomain(UserJpa jpa) {
        return new User(
                new UserId(jpa.getId()),
                jpa.getEmail(),
                jpa.getPasswordHash(),
                jpa.getPhoneNumber(),
                new java.util.HashSet<>(jpa.getRoles()),
                jpa.isActive(),
                CreatedAt.reconstitute(jpa.getCreatedAt()));
    }

    default java.util.Set<cm.nyi.auth.domain.user.UserRole> mapRoles(
            java.util.Set<cm.nyi.auth.domain.user.UserRole> roles) {
        return new java.util.HashSet<>(roles);
    }
}
