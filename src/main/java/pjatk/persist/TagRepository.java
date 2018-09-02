package pjatk.persist;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pjatk.domain.data.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByContent(String content);
    List<Tag> findByParagraphVectorsProcessedIsTrue();
    List<Tag> findByIdIn(Set<Long>ids);
}
