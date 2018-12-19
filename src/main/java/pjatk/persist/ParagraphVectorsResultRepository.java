package pjatk.persist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pjatk.domain.data.ParagraphVectorsResult;

public interface ParagraphVectorsResultRepository extends JpaRepository<ParagraphVectorsResult, Long> {
    List<ParagraphVectorsResult> findByExperimentName(String experimentName);
}
