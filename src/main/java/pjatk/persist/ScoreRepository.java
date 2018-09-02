package pjatk.persist;

import org.springframework.data.jpa.repository.JpaRepository;

import pjatk.domain.data.Score;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
