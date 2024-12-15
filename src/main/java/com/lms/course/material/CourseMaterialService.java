package com.lms.course.material;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseMaterialService {

    private final CourseMaterialRepository courseMaterialRepository;

    @Autowired
    public CourseMaterialService(CourseMaterialRepository courseMaterialRepository) {
        this.courseMaterialRepository = courseMaterialRepository;
    }

    public List<CourseMaterial> getAllMaterials() {
        return courseMaterialRepository.findAll();
    }

    public CourseMaterial uploadMaterial(CourseMaterial courseMaterial) {
        return courseMaterialRepository.save(courseMaterial);
    }

    public Optional<CourseMaterial> getMaterialById(Long id) {
        return courseMaterialRepository.findById(id);
    }

    public void deleteMaterial(Long materialId) {
        courseMaterialRepository.deleteById(materialId);
    }
}
