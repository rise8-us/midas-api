package mil.af.abms.midas.api.search;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import mil.af.abms.midas.api.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

public class RestQueryParserTests {

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void shouldReturnTokens() {
        String expectedStringTree =
                "(input " +
                        "(query " +
                        "(query (criteria (key id) (op :) (value 1)))" +
                        " AND " +
                        "(query (criteria (key username) (op :) (value foo)))) <EOF>)";

        String urlPath = "id:1 AND username:foo";
        RestQueryLexer lexer = new RestQueryLexer(CharStreams.fromString(urlPath));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestQueryParser parser = new RestQueryParser(tokens);
        ParseTree tree = parser.input();

        assertThat(tree.toStringTree(parser)).isEqualTo(expectedStringTree);

    }

    @Test
    public void shouldReturnSpecs() {
        String urlPath = "id:1 AND username:foo";
        CriteriaParser<UserModel> parser = new CriteriaParser<>();
        Specification<UserModel> expectedSpecs = new NullSpecification<UserModel>();
        Specification<UserModel> specs = parser.parse(urlPath);

    }
}
