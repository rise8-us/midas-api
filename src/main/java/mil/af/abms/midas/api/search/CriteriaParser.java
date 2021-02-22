package mil.af.abms.midas.api.search;

import org.springframework.data.jpa.domain.Specification;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class CriteriaParser<T> {

    private final RestQueryVisitorImpl<T> visitor = new RestQueryVisitorImpl<>();

    public Specification<T> parse(String searchParam) {
        RestQueryParser parser = getParser(searchParam);
        return visitor.visit(parser.input());
    }

    private RestQueryParser getParser(String queryString) {
        RestQueryLexer lexer = new RestQueryLexer(CharStreams.fromString(queryString));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new RestQueryParser(tokens);
    }
}
