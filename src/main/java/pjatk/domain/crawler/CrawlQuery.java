package pjatk.domain.crawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Michał Dąbrowski
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrawlQuery {
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
