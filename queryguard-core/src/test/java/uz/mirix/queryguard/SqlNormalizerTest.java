package uz.mirix.queryguard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlNormalizerTest {
    @Test void normalizesWhitespaceCommentsAndLiterals(){String sql="SELECT  *  FROM users /* x */ WHERE id = 42 AND email = 'a@b.com' -- tail";assertEquals("select * from users where id = ? and email = ?",SqlNormalizer.normalize(sql));}
}
