package com.jsolution.service.impl;

import com.jsolution.model.Menu;
import com.jsolution.repository.IGenericRepository;
import com.jsolution.repository.IMenuRepository;
import com.jsolution.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends CRUDImpl<Menu, String> implements IMenuService {

    @Autowired
    private IMenuRepository menuRepository;

    @Override
    protected IGenericRepository<Menu, String> getRepository() {
        return menuRepository;
    }
}
