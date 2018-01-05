package pjatk.domain.data;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Class holds information about one consistent piece of data f.g. one article or one tweet.
 * @author Michał Dąbrowski
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DataBlock {
    private Long id;
    private DataSource origin;
    private LocalDate date;
    private String content;
    private List<Tag> tags;
}
