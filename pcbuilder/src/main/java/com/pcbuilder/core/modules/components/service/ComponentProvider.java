package com.pcbuilder.core.modules.components.service;

import com.pcbuilder.core.modules.components.model.Component;
import com.pcbuilder.core.modules.components.repository.ComponentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentProvider {

    private final ComponentRepository componentRepository;

    public Component getComponentById(Long id) {
        return componentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Component not found: " + id));
    }
    public List<Component> getComponentsByIds(List<Long> ids) {
        return componentRepository.findAllById(ids);
    }
}