package io.ten1010.coaster.auth.web.core.v1;

import io.ten1010.coaster.auth.common.PropertyUserVisibleException;
import io.ten1010.coaster.auth.domain.PasswordEncoder;
import io.ten1010.coaster.auth.domain.User;
import io.ten1010.coaster.auth.web.ApiResourceObjectNameMapping;
import io.ten1010.coaster.auth.web.updater.AbstractUpdater;
import org.springframework.data.jpa.repository.JpaRepository;

public class UserUpdater extends AbstractUpdater<User, UserApiResource> {

    private static final String OBJECT_NAME = ApiResourceObjectNameMapping.findByClass(UserApiResource.class).orElseThrow();

    private PasswordEncoder passwordEncoder;

    public UserUpdater(JpaRepository<User, Long> repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void updateInternal(User model, UserApiResource resource) {
        if (!model.getUserId().equals(resource.getUserId())) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/userId", "Can not be revised");
        }
        if (resource.getPassword() == null) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/password", PropertyUserVisibleException.MSG_NULL_NOT_ALLOWED);
        }
        model.patchPassword(this.passwordEncoder.encode(resource.getPassword()));
        model.patchKoreanName(resource.getKoreanName());
        model.patchPhoneNumber(resource.getPhoneNumber());
        model.patchEmail(resource.getEmail());
        model.patchDepartment(resource.getDepartment());
    }

}
