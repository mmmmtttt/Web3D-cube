package com.company.project.service;
import com.company.project.dto.*;
import com.company.project.model.Record;
import com.company.project.model.User;
import com.company.project.core.Service;

import java.util.List;


/**
 * Created by CodeGenerator on 2022/04/03.
 */
public interface UserService extends Service<User> {
    void register(User user);
    UserProfileDTO profile(String token);
    List<RecordDTO> record(String token);
    AttributeDTO attribute(String token);
}
