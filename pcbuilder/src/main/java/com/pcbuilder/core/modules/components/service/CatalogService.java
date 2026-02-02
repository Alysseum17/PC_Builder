package com.pcbuilder.core.modules.components.service;

import com.pcbuilder.core.modules.components.repository.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final ComponentRepository componentRepository;


}
