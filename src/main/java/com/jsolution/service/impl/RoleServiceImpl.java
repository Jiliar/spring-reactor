package com.jsolution.service.impl;

import com.jsolution.model.Role;
import com.jsolution.repository.IGenericRepository;
import com.jsolution.repository.IRoleRepository;
import com.jsolution.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends CRUDImpl<Role, String> implements IRoleService {

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    protected IGenericRepository<Role, String> getRepository() {
        return roleRepository;
    }
}
