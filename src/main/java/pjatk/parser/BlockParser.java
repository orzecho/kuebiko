package pjatk.parser;

import pjatk.domain.data.DataBlock;
import pjatk.domain.parser.ParsedBlock;

/**
 * @author Michał Dąbrowski
 */
public interface BlockParser {
    ParsedBlock parse(DataBlock dataBlock);
}
