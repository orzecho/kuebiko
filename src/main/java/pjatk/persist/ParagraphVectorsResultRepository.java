package pjatk.persist;

import org.springframework.data.jpa.repository.JpaRepository;

import pjatk.domain.data.ParagraphVectorsResult;

public interface ParagraphVectorsResultRepository extends JpaRepository<ParagraphVectorsResult, Long> {
}
