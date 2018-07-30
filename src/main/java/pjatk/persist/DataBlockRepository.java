package pjatk.persist;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pjatk.domain.data.DataBlock;

/**
 * @author Michał Dąbrowski
 */
public interface DataBlockRepository extends JpaRepository<DataBlock, Long> {
    List<DataBlock> findByWord2VecUnprocessedIsTrue();

    Optional<DataBlock> findByContentHash(String contentHash);

    Long countAllByWord2VecUnprocessedIsTrue();
}
