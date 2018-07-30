package pjatk.domain.crawler;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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
