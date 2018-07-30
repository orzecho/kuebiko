package pjatk.domain.data;

import lombok.RequiredArgsConstructor;

/**
 * @author Michał Dąbrowski
 */
@RequiredArgsConstructor
public enum DataSource {
    GUARDIAN(0L, "GUARDIAN", "Guardian data source"),
    TEST(1L, "TEST", "Test data source");

    private final Long id;
    private final String code;
    private final String name;
}
