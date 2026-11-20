package com.apex.diagnostics.service;

import com.apex.diagnostics.entity.LabState;
import com.apex.diagnostics.repository.LabStateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LabStateService {
    private static final long SINGLETON_ID = 1L;
    private final LabStateRepository repository;
    private final ObjectMapper objectMapper;

    public LabStateService(LabStateRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getState() {
        return repository.findById(SINGLETON_ID).map(row -> {
            try {
                return objectMapper.readValue(row.getPayload(), new TypeReference<Map<String, Object>>() { });
            } catch (JsonProcessingException ex) {
                throw new IllegalStateException("Saved laboratory data is not valid JSON.", ex);
            }
        }).orElseGet(LinkedHashMap::new);
    }

    @Transactional
    public Map<String, Object> saveState(Map<String, Object> state) {
        validateState(state);
        try {
            String json = objectMapper.writeValueAsString(state);
            LabState row = repository.findById(SINGLETON_ID).orElseGet(() -> new LabState(SINGLETON_ID, json));
            row.setPayload(json);
            repository.save(row);
            return Map.of("saved", true, "updatedAt", row.getUpdatedAt().toString());
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to serialize laboratory data.", ex);
        }
    }

    private void validateState(Map<String, Object> state) {
        if (state == null) throw new IllegalArgumentException("Request body is required.");
        for (String key : new String[]{"patients", "tests", "samples", "results", "reports"}) {
            Object value = state.get(key);
            if (value != null && !(value instanceof java.util.List<?>)) {
                throw new IllegalArgumentException("Field '" + key + "' must be an array.");
            }
        }
    }
}
